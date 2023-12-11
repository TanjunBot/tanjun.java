package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import tanjun.api.Casino;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CasinoCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        String eventName = event.getName();
        String subcommandName = event.getSubcommandName();
        if (eventName.equals("casino")) {
            EmbedBuilder embed = Helper.defaultEmbed();
            event.deferReply().queue();
            switch (subcommandName){
                case "info":
                    User target = event.getOption("user", OptionMapping::getAsUser);
                    assert  target != null;
                    embed.setTitle("Casino Information from " + target.getName() + ".");
                    try {
                        ResultSet infoUser = Casino.getPlayerData(target.getId());
                        embed.setDescription("__Moro__");
                    } catch (SQLException e) {
                        embed.setDescription("I was unable to fetch the Information from " + target.getName() +
                                " from the Database. You may want to report this Error: \n" + e);
                    } catch (IOException e) {
                        embed.setDescription("I was unable to fetch the Information from " + target.getName() +
                                " because I could not write to the logs. You may want to report this Error: \n" + e);
                    }
                case null:
                    try {
                        Logger.addLog("Unknown Command " + eventName + " was run.", event.getUser().getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + subcommandName);
            }
        }
    }
}
