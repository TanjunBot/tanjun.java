package tanjun.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tanjun.Tanjun;

public class Command extends ListenerAdapter {
  Tanjun tanjun;

  public Command(Tanjun tanjun) {this.tanjun = tanjun;}

  public void sendErrorMessage(SlashCommandInteractionEvent event) {
    tanjun.addLog("Error message", "Unknown Error occured during execution of command.");
    event.reply("An error has occured! Please try again later.").queue();
  }
}
