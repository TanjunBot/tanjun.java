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
import java.sql.Timestamp;

public class CasinoCommands extends ListenerAdapter {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    String eventName = event.getName();
    String subcommandName = event.getSubcommandName();
    if (eventName.equals("casino")) {
      EmbedBuilder embed = Helper.defaultEmbed();
      event.deferReply().queue();
      switch (subcommandName) {
        case "info": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          if (target == null) {
            target = event.getUser();
          }
          embed.setTitle("Casino Information from " + target.getName() + ".");
          try {
            ResultSet infoUser = Casino.getPlayerData(target.getId());
            assert infoUser != null;
            long money = infoUser.getLong("money");
            int gamesPlayed = infoUser.getInt("gamesPlayed");
            Timestamp lastPlayed = infoUser.getTimestamp("lastPlayed");
            int dailyStreak = infoUser.getInt("dailyStreak");
            int totalDaily = infoUser.getInt("totalDaily");
            Timestamp lastDaily = infoUser.getTimestamp("lastDaily");
            String embedDescription = "### Money ➤ " + money + "\n";
            embedDescription += "### games Played ➤ " + gamesPlayed + "\n";
            if (!(lastPlayed == null)) {
              embedDescription += "### last Played ➤ <t:" + (int) lastPlayed.getTime() + ":r>\n";
            } else {
              embedDescription += "### last Played ➤ never\n";
            }
            embedDescription += "### current Daily Streak ➤ " + dailyStreak + "\n";
            embedDescription += "### total Daily collected ➤ " + totalDaily + "\n";
            if (!(lastDaily == null)) {
              embedDescription += "### last Daily collected ➤ <t:" + (int) lastDaily.getTime() + ":r>";
            } else {
              embedDescription += "### last Daily collected ➤ never";
            }
            embed.setDescription(embedDescription);
          } catch (SQLException e) {
            embed.setDescription("I was unable to fetch the Information from " + target.getName() +
                    " from the Database. You may want to report this Error: \n" + e);
          } catch (IOException e) {
            embed.setDescription("I was unable to fetch the Information from " + target.getName() +
                    " because I could not write to the logs. You may want to report this Error: \n" + e);
          }
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
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
