package tanjun.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UtilityCommands extends ListenerAdapter {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    switch (event.getName()) {
      //Sends a Message containing the current Ping.
      case "ping":
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true).
                flatMap(v ->
                  event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time
                )
        ).queue();
    }
  }
}