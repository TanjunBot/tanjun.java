package tanjun.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tanjun.utilitys.Logger;

import java.io.IOException;

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
      event.reply("Pong!").setEphemeral(true).
              flatMap(v ->
                      event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time
                      )
              ).queue();
    }
  }
}