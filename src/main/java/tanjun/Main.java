package tanjun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import tanjun.events.UtilityCommands;
import tanjun.utilitys.Logger;

public class Main {
  public static void main(String[] args) throws Exception{
    JDA jda = JDABuilder.createDefault("ODMyMjk3MzIxNzkzMzIzMDI4.Gp7GKs.J6hSTiZxY9f4kW98UId8k039voWqsAC46C5xsI")
            .addEventListeners(new UtilityCommands())
            .setActivity(Activity.playing("Tanjun"))
            .build();

    Logger.addLog("Bot Started", "System");

    jda.updateCommands().addCommands(
            Commands.slash("ping", "Calculate ping of the bot"),
            Commands.slash("ban", "Ban a user from the server")
                    .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)) // only usable with ban permissions
                    .setGuildOnly(true) // Ban command only works inside a guild
                    .addOption(OptionType.USER, "user", "The user to ban", true) // required option of type user (target to ban)
                    .addOption(OptionType.STRING, "reason", "The ban reason") // optional reason
    ).queue();

    Logger.addLog("Slash Commands Initiated", "System");


  }
}