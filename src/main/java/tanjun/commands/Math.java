package tanjun.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import tanjun.Tanjun;

import java.util.Objects;

public class Math extends Command {

  public Math(Tanjun tanjun) {
    super(tanjun);
  }


  private boolean fibCommand(SlashCommandInteractionEvent event) {
    try {
      tanjun.addLog("Math Fib Command", event.getUser().getName() +
              "(" + event.getUser().getId() + ") has run the Math Fib command.");

      int n = Objects.requireNonNull(event.getOption("n")).getAsInt();
      boolean sumUp = event.getOption("sum_up") != null && Objects.requireNonNull(event.getOption("sum_up"))
              .getAsString().equals("sumup");

      long result = 0;

      // calculate result here.

      event.reply("Fib(" + n + ") = `" + result + "`").queue();
      tanjun.addLog("Math Fib Command", "Math Fib command has ran successfull.");
      return true;
    } catch (Exception e) {
      tanjun.addLog("Math Fib Command", "An Error has occured: " + e);
      return false;
    }
  }

  private boolean facCommand(SlashCommandInteractionEvent event) {
    try {
      tanjun.addLog("Math Fac Command", event.getUser().getName() +
              "(" + event.getUser().getId() + ") has run the Math Fac command.");

      int n = Objects.requireNonNull(event.getOption("n")).getAsInt();

      long result = 0;

      // calculate result here.

      event.reply(n + "! = `" + result + "`").queue();
      tanjun.addLog("Math Fac Command", "Math Fac command has ran successfull.");
      return true;
    } catch (Exception e) {
      tanjun.addLog("Math Fac Command", "An Error has occured: " + e);
      return false;
    }
  }
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals("math")) return;

    String subcommandName = event.getSubcommandName();
    switch(subcommandName) {
      case "fib":
        tanjun.getApi().addCommandUse("fib", event.getUser().getIdLong());
        if (!fibCommand(event)) {
          sendErrorMessage(event);
        }
        break;
      case "fac":
        tanjun.getApi().addCommandUse("fac", event.getUser().getIdLong());
        if (!facCommand(event)) {
          sendErrorMessage(event);
        }
        break;
      case null:
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + subcommandName);
    }
  }
}
