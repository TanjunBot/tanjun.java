package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.api.Casino;
import tanjun.commandHelper.EmbedCommands;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;
import tanjun.utilitys.cardGames.CasinoGames;

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
          embed.setDescription(EmbedCommands.infoCommandEmbedDescriptionGenerator(target));
          embed.setTitle("Casino Information from " + target.getName() + ".");
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "daily": {
          embed.setTitle("Daily Reward");
          embed.setDescription(EmbedCommands.dailyCommandEmbedDescriptionGenerator(event.getUser()));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "transfer": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          User sender = event.getUser();
          int amount = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle("Transfer Money");
          embed.setDescription(EmbedCommands.transferCommandEmbedDescriptionGenerator(sender, target, amount));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "slots": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle("Slots");
          embed.setDescription(EmbedCommands.slotsCommandEmbedDescriptionGenerator(user, bet));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }

        case "blackjack": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          try {
            CasinoGames.blackjack(bet, user, event);
            return;
          } catch (IOException e) {
            embed.setTitle("Blackjack Error");
            embed.setDescription("I was unable to run the Blackjack command because I could not write to the logs. You may want to report this Error: \n" + e);
            embed.setFooter("tanjun.java Casino");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
          } catch (SQLException e) {
            embed.setTitle("Blackjack Error");
            embed.setDescription("I was unable to run the Blackjack command because I can't do SQL. You may want to report this Error: \n" + e);
            embed.setFooter("tanjun.java Casino");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
          }
          break;
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