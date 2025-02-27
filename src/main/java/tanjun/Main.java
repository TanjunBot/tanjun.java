package tanjun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import tanjun.commands.Utility;
import tanjun.commands.Math;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    final String token = dotenv.get("BotToken");

    final String databaseUrl = dotenv.get("databaseUrl");
    final String databasePassword = dotenv.get("databasePassword");
    final String databaseUsername = dotenv.get("databaseUsername");


    JDA jda = JDABuilder.createDefault(token)
            .setActivity(Activity.playing("Tanjun"))
            .build();

    Tanjun tanjun = new Tanjun(jda, databaseUrl, databaseUsername, databasePassword);

    jda.addEventListener(new Utility(tanjun));
    jda.addEventListener(new Math(tanjun));

    tanjun.addLog("System", "Bot Started.");
    tanjun.addLog("System", "Initializing slash commands...");

    jda.updateCommands().addCommands(
            Commands.slash("ping", "Calculate ping of the bot"),
            Commands.slash("math", "Math Commands to do some Math things!")
                    .addSubcommands(
                            new SubcommandData("fib", "Calculate Fib(n)")
                                    .addOptions(
                                            new OptionData(OptionType.INTEGER, "n",
                                                    "The number fib you want to calculate. e.g. fib(n)",
                                                    true).setRequiredRange(0, 100),
                                            new OptionData(
                                                    OptionType.STRING,
                                                    "sum_up",
                                                    "rather you want to sum up the results from fib(0) to fib(n)"
                                            ).addChoice("Sum Up", "sumup")
                                                    .addChoice("Don't Sum up", "nosumup")
                                    ),
                            new SubcommandData("fac", "Calculate the Faculty of n => n!")
                                    .addOptions(
                                            new OptionData(OptionType.INTEGER, "n",
                                                    "The number that you want to calculate the Faculty of. e.g. n!",
                                                    true).setRequiredRange(0, 100)
                                    ),
                            new SubcommandData("calc", "Calculate a expression")
                                    .addOptions(
                                            new OptionData(
                                                    OptionType.STRING,
                                                    "expression",
                                                    "The Expression you want to calculate",
                                                    true
                                            ).setRequiredLength(1, 100)
                                    ),
                            new SubcommandData("calculator", "Opens the Calculator")
                                    .addOptions(
                                            new OptionData(
                                                    OptionType.STRING,
                                                    "expression",
                                                    "The Expression you want to start the Calculator with",
                                                    false
                                            ).setRequiredLength(1, 100)
                                    )
                    )
    ).queue();

    tanjun.addLog("System", "Initialized slash commands");
  }
}
