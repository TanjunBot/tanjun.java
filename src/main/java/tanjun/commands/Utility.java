package tanjun.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tanjun.Tanjun;

public class Utility extends Command {
  public Utility(Tanjun tanjun) {
    super(tanjun);
  }

  private boolean pingCommand(SlashCommandInteractionEvent event) {
    try {
      long time = System.currentTimeMillis();
      tanjun.addLog("Ping command", event.getUser().getName() +
              "(" + event.getUser().getId() + ") has run the ping command.");
      event.reply("Ping: ...").setEphemeral(true).
              flatMap(v ->
                      event.getHook().editOriginalFormat("Ping: %d ms | Websocket: %d ms",
                              System.currentTimeMillis() - time,
                              event.getJDA().getGatewayPing()
                      )
              ).queue();
      tanjun.addLog("Ping command", "Ping command has ran successfull.");
      return true;
    } catch (Exception e) {
      tanjun.addLog("Ping command", "An Error has occured: " + e);
      return false;
    }
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (event.getName().equals("ping")) {
      tanjun.getApi().addCommandUse("ping", event.getUser().getIdLong());
      if (!pingCommand(event)) {
        sendErrorMessage(event);
      }
    }
  }
}
