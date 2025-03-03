package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.jetbrains.annotations.NotNull;
import tanjun.Tanjun;
import tanjun.util.Calculator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

public class Math extends Command {

  HashMap<String, Calculator> calculatorHashMap = new HashMap<String, Calculator>();

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

  private boolean calcCommand(SlashCommandInteractionEvent event) {
    try {
      tanjun.addLog("Math calc command", event.getUser().getName() +
              "(" + event.getUser().getId() + ") has run the Math calc command.");

      String expression = Objects.requireNonNull(event.getOption("expression")).getAsString();

      Expression expr = new ExpressionBuilder(expression).build();

      double result = expr.evaluate();

      event.reply(expression + " = `" + result + "`").queue();
      tanjun.addLog("Math calc Command", "Math calc command has ran successfull.");
      return true;
    } catch (Exception e) {
      tanjun.addLog("Math calc Command", "An Error has occured: " + e);
      return false;
    }
  }

  private boolean calculatorCommand(SlashCommandInteractionEvent event) {
    try {
      tanjun.addLog("Math calculator command", event.getUser().getName() +
              "(" + event.getUser().getId() + ") has run the Math calculator command.");
      event.deferReply().queue();
      event.getHook().sendMessage("loading calculator...").queue((message) -> {
        String expression = event.getOption("expression") != null
                ? Objects.requireNonNull(event.getOption("expression")).getAsString()
                : "";

        Calculator calculator = new Calculator(tanjun, expression, message);

        EmbedBuilder embedBuilder = calculator.generateEmbed();

        event.getHook().editOriginal("").setEmbeds(embedBuilder.build())
                .setComponents(
                        calculator.actionRow1(),
                        calculator.actionRow2(),
                        calculator.actionRow3(),
                        calculator.actionRow4(),
                        calculator.actionRow5()
                )
                .queue();

        System.out.println(message.getIdLong());

        calculatorHashMap.put(message.getId(), calculator);
      });

      tanjun.addLog("Math calculator Command", "Math calculator command has ran successfull.");
      return true;
    } catch (Exception e) {
      tanjun.addLog("Math calculator Command", "An Error has occured: " + e);
      return false;
    }
  }

  @Override
  public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
    super.onButtonInteraction(event);

    String messageId = event.getMessageId();

    Calculator calculator = calculatorHashMap.get(messageId);

    if (calculator == null) return;

    calculator.callback(event);
  }

  @Override
  public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
    super.onStringSelectInteraction(event);

    String messageId = event.getMessageId();

    Calculator calculator = calculatorHashMap.get(messageId);

    if (calculator == null) return;

    try {
      calculator.selectCallback(event);
    } catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
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
      case "calc":
        tanjun.getApi().addCommandUse("calc", event.getUser().getIdLong());
        if (!calcCommand(event)) {
          sendErrorMessage(event);
        }
        break;
      case "calculator":
        tanjun.getApi().addCommandUse("calculator", event.getUser().getIdLong());
        if (!calculatorCommand(event)) {
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
