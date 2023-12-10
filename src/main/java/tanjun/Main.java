package tanjun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import tanjun.commands.FunCommands;
import tanjun.commands.UtilityCommands;
import tanjun.utilitys.Logger;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
  public static void main(String[] args) throws Exception {
    Dotenv dotenv = Dotenv.load();
    String token = dotenv.get("BotToken");

    JDA jda = JDABuilder.createDefault(token)
            .addEventListeners(new UtilityCommands())
            .addEventListeners(new FunCommands())
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
                                    .addOption(OptionType.STRING, "message", "A Additional Message you want to Send.", false)
                    )
    ).queue();

    Logger.addLog("Slash Commands Initiated", "System");


  }
}