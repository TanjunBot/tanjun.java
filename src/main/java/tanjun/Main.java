package tanjun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import tanjun.api.Casino;
import tanjun.commands.CasinoCommands;
import tanjun.commands.FunCommands;
import tanjun.commands.UtilityCommands;
import tanjun.utilitys.DatabaseConnector;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
  public static void main(String[] args) throws Exception {
    Dotenv dotenv = Dotenv.load();
    final String token = dotenv.get("BotToken");

    JDA jda = JDABuilder.createDefault(token)
            .addEventListeners(new UtilityCommands())
            .addEventListeners(new FunCommands())
            .addEventListeners(new CasinoCommands())
            .setActivity(Activity.playing("Tanjun"))
            .build();

    Logger.addLog("Bot Started", "System");

    jda.updateCommands().addCommands(
            Commands.slash("ping", "Calculate ping of the bot"),
            Commands.slash("ban", "Ban a user from the server")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS))
                    .setGuildOnly(true) // Ban command only works inside a guild
                    .addOption(OptionType.USER, "user", "The user to ban", true)
                    .addOption(OptionType.STRING, "reason", "The ban reason"),
            Commands.slash("fun", "commands to interact with Users")
                    .addSubcommands(
                            new SubcommandData("hug", "hug a User")
                                    .addOption(OptionType.USER, "user", "The user to hug", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("eat", "eat a User")
                                    .addOption(OptionType.USER, "user", "The user to eat", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("wave", "wave to a User")
                                    .addOption(OptionType.USER, "user", "The user to wave to", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("kiss", "kiss a User")
                                    .addOption(OptionType.USER, "user", "The user to kiss", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("pat", "pat a User")
                                    .addOption(OptionType.USER, "user", "The user to pat", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("slap", "slap a User")
                                    .addOption(OptionType.USER, "user", "The user to slap", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false),
                            new SubcommandData("boop", "boop a User")
                                    .addOption(OptionType.USER, "user", "The user to boop", true)
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false)
                    ),
            Commands.slash("casino", "commands to interact with Users")
                    .addSubcommands(
                            new SubcommandData("info", "get the Casino Information of a User.")
                                    .addOption(OptionType.USER, "user", "The user you want to get the Information from.", false)
                    )
    ).queue();

    Logger.addLog("Slash Commands Initiated", "System");

    DatabaseConnector.connectToDatabase();
    DatabaseConnector.initiateDatabase();

    //for testing purposes
    System.out.println(Casino.checkIfPlayerExists("7"));
    System.out.println(Casino.getMoney("7"));

    Casino.playGame("7", 15);

    System.out.println(Casino.canRecieveDailyReward("7"));
    System.out.println(Casino.canRecieveDailyReward("10"));
    System.out.println("claimed Daily: " + Casino.claimDaily("12"));
  }
}
