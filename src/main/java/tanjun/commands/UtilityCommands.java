package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;

public class UtilityCommands extends ListenerAdapter {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getName().equals("ping")) {
      long time = System.currentTimeMillis();
      try {
        Logger.addLog("Ping command was run.", event.getUser().getId());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      event.reply("Pong!").
              flatMap(v ->
                      event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time
                      )
              ).queue();
    }
    if (event.getName().equals("usage")) {
      try {
        Logger.addLog("usage command was run.", event.getUser().getId());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      EmbedBuilder eb = Helper.defaultEmbed();
      eb.setTitle("System Information");

      String cpuInfo = getCPUInfo();
      String memoryInfo = getMemoryInfo();

      RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
      long uptime = rb.getUptime();
      String formattedUptime = Helper.formatUptime(uptime);


      eb.setDescription("```ansi\n\u001b[1;32mUptime: " + formattedUptime + "\n\n" + cpuInfo + "\n" + memoryInfo + "\n```");

      event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }
  }

  private String getCPUInfo() {
    StringBuilder cpuInfo = new StringBuilder();
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    int logicalProcessors = Runtime.getRuntime().availableProcessors();
    double[] cpuUsage = new double[logicalProcessors];

    for (int i = 0; i < logicalProcessors; i++) {
      long[] threadIds = threadMXBean.getAllThreadIds();
      double totalCpuTime = 0;
      for (long threadId : threadIds) {
        long cpuTime = threadMXBean.getThreadCpuTime(threadId);
        if (cpuTime != -1) {
          totalCpuTime += cpuTime;
        }
      }
      long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
      cpuUsage[i] = (totalCpuTime / (uptime * 1000000.0)) * 100 / logicalProcessors;
    }

    cpuInfo.append("\u001b[1;32mCPU\u001b[0m\n");
    double totalUsage = 0;
    for (int i = 0; i < logicalProcessors; i++) {
      totalUsage += cpuUsage[i];
      cpuInfo.append("\u001b[1;34mLogical Processor ").append(i + 1).append("\u001b[0m Usage: \u001b[1;36m").append(String.format("%.2f", cpuUsage[i])).append("%\u001b[0m\n");
    }
    cpuInfo.append("\u001b[1;34mTotal Usage\u001b[0m: \u001b[1;36m").append(String.format("%.2f", totalUsage)).append("%\u001b[0m\n");
    cpuInfo.append("\u001b[1;34mNumber of Logical Processors\u001b[1;36m: ").append(logicalProcessors).append("\n");

    return cpuInfo.toString();
  }


  private static String getMemoryInfo() {
    StringBuilder memoryInfo = new StringBuilder();
    try {
      Process process = Runtime.getRuntime().exec("wmic MEMORYCHIP get BankLabel,Speed,Capacity");
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        memoryInfo.append(line.trim()).append("\n");
      }
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return "\u001b[1;32mMemory\u001b[0m\n" + formatMemoryInfo(memoryInfo.toString());
  }

  private static String formatMemoryInfo(String memoryInfo) {
    String[] lines = memoryInfo.split("\n");
    StringBuilder formattedMemoryInfo = new StringBuilder();
    formattedMemoryInfo.append(lines[0]).append("\n"); // Append the header
    for (int i = 1; i < lines.length; i++) {
      String[] tokens = lines[i].split("\\s+");
      if (tokens.length >= 3) {
        long capacityBytes = Long.parseLong(tokens[2]);
        String formattedCapacity = Helper.formatBytes(capacityBytes);
        formattedMemoryInfo.append(tokens[0]).append("\t").append(formattedCapacity).append("\t")
                .append(tokens[3]).append(" MHz").append("\n");
      } else {
        formattedMemoryInfo.append(lines[i]).append("\n");
      }
    }
    return formattedMemoryInfo.toString();
  }

}
