package tanjun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import tanjun.api.Casino;
import tanjun.commands.CasinoCommands;
import tanjun.commands.FunCommands;
import tanjun.commands.UtilityCommands;
import tanjun.listener.ButtonListener;
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
            .addEventListeners(new ButtonListener())
            .setActivity(Activity.playing("Tanjun"))
            .build();

    Tanjun tanjun = new Tanjun(jda);

    tanjun.logger.addLog("System", "Bot Started.");
    tanjun.logger.addLog("System", "Initializing slash commands...");

    jda.updateCommands().addCommands(
            Commands.slash("ping", "Calculate ping of the bot")
    ).queue();

    tanjun.logger.addLog("System", "Initialized slash commands");

    DatabaseConnector.connectToDatabase();
    DatabaseConnector.initiateDatabase();
  }
}
