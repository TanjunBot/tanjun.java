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
import java.util.Objects;
import java.util.Random;

class CasinoGames {
  /**
   * Calculates the win of the Slots game.
   * @param bet the amount of Money the User bet.
   * @param fruit the fruit that was rolled.
   * @param threeTimes if the fruit was rolled three times.
   * @return the amount of Money the User won.
   */
  public static int getSlotswin(int bet, String fruit, boolean threeTimes){
    if(threeTimes){
      switch (fruit){
        case "🍒":
          return bet * 3;
        case "🍋":
          return bet * 5;
        case "🍊":
          return bet * 7;
        case "🍉":
          return bet * 10;
        case "🍇":
          return bet * 15;
        case "💎":
          return bet * 20;
      }
    } else{
      switch (fruit){
        case "🍒":
          return bet * 2;
        case "🍋":
          return bet * 3;
        case "🍊":
          return bet * 4;
        case "🍉":
          return bet * 5;
        case "🍇":
          return bet * 6;
        case "💎":
          return bet * 7;
      }
    }
    return bet;
  }

  /**
   * Runs the Slots game.
   * @param bet the amount of Money the User bet.
   *            If the User wins, he gets the bet * the win.
   *            If the User loses, he loses the bet.
   * @param user the User that runs the command.
   *             Used to get the Money from the Database.
   * @param rigged if the game is rigged.
   *               If the game is rigged, the User statistically will lose money.
   *               If the game is not rigged, the User statistically will win money.
   * @return the result of the Slots game.
   * @throws SQLException if the Database is not reachable.
   * @throws IOException if the Logger is not reachable.
   */
  public static String slots(int bet, User user, boolean rigged) throws SQLException, IOException {
    String[] slots;
    String[] slots2;
    String[] slots3;

    if(rigged){
      slots = new String[]{"🗑️", "🍒", "🍒", "🍊", "🍉", "🍇", "💎" };
      slots2 = new String[]{"🗑️", "🍒", "🍒", "🍋", "🍉", "🍇", "💎" };
      slots3 = new String[]{"🗑️", "🍒", "🍒", "🍋", "🍊", "🍉", "🍇" };
      Logger.addLog("Rigged Slots game was run.", user.getId());
    } else {
      slots = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎" };
      slots2 = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎" };
      slots3 = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎" };
      Logger.addLog("Slots game was run.", user.getId());
    }


    int slot1 = new Random().nextInt(slots.length);
    int slot2 = new Random().nextInt(slots2.length);
    int slot3 = new Random().nextInt(slots3.length);

    String slot1Emoji = slots[slot1];
    String slot2Emoji = slots2[slot2];
    String slot3Emoji = slots3[slot3];

    String[] slotsResult = {slot1Emoji, slot2Emoji, slot3Emoji};

    int win = bet * -1;

    //at least first two are the same
    if(slotsResult[0].equals(slotsResult[1])){
        //all three are the same
        if(slotsResult[1].equals(slotsResult[2])){
          win = getSlotswin(bet, slotsResult[0], true);
        } else {
          //only first two are the same
          win = getSlotswin(bet, slotsResult[0], false);
        }
    }
    //first and last are the same
    else if(slotsResult[0].equals(slotsResult[2])){
      win = getSlotswin(bet, slotsResult[0], false);
    }
    Casino.playGame(user.getId(), win);

    String embedText = "```\n―――――――――――――――\n|" + slot1Emoji + " | " + slot2Emoji + " | " + slot3Emoji +
            "|\n―――――――――――――――\n```\n";

    if(win > 0){
      embedText += "You won " + win + " Money.";
    } else {
      embedText += "You lost " + bet + " Money.";
    }

    embedText += "\n\n```\n🍒🍒 ➤ *2    🍒🍒🍒 ➤ *3\n" +
            "🍋🍋 ➤ *3    🍋🍋🍋 ➤ *5\n" +
            "🍊🍊 ➤ *4    🍊🍊🍊 ➤ *7\n" +
            "🍉🍉 ➤ *5    🍉🍉🍉 ➤ *10\n" +
            "🍇🍇 ➤ *6    🍇🍇🍇 ➤ *15\n" +
            "💎💎 ➤ *7    💎💎💎 ➤ *20\n```\n";

    embedText += "\n\nYour new Balance is " + Casino.getMoney(user.getId()) + " Money.";

    return embedText;
  }
}

class Commands {
  public static String infoCommandEmbedDescriptionGenerator(User target) {
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
        embedDescription += "### last Played ➤ <t:" + lastPlayed.getTime() / 1000 + ":R>\n";
      } else {
        embedDescription += "### last Played ➤ never\n";
      }
      embedDescription += "### current Daily Streak ➤ " + dailyStreak + "\n";
      embedDescription += "### total Daily collected ➤ " + totalDaily + "\n";
      if (!(lastDaily == null)) {
        embedDescription += "### last Daily collected ➤ <t:" + lastDaily.getTime() / 1000 + ":R>";
      } else {
        embedDescription += "### last Daily collected ➤ never";
      }
      return embedDescription;
    } catch (SQLException e) {
      return "I was unable to fetch the Information from " + target.getName() +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to fetch the Information from " + target.getName() +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String dailyCommandEmbedDescriptionGenerator(User user) {
    try {
      Logger.addLog("Daily command was run.", user.getId());
      long secondsUntillAllowedToCollectDaily = Casino.secondsUntillAllowedToCollectDaily(user.getId());
      if (secondsUntillAllowedToCollectDaily > 0) {
        return "You are not yet allowed to collect the Daily reward. Try again in <t:" + secondsUntillAllowedToCollectDaily + ":r>";
      } else {
        int reward = Casino.claimDaily(user.getId());
        return "You successfully claimed your Daily reward. You gained " + reward + " Money.";
      }
    } catch (SQLException e) {
      return "I was unable to give you your Daily reward" +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to give you your Daily reward" +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String transferCommandEmbedDescriptionGenerator(User sender, User receiver, int amount) {
    try {
      Logger.addLog("Transfer command was run.", sender.getId());
    } catch (IOException e) {
      return "I was unable to run the Transfer command because I could not write to the logs. You may want to report this Error: \n" + e;
    }
    if (receiver == null) {
      return "Please specify a User you want to send the Money.";
    }
    if (amount <= 0) {
      return "Please specify a valid amount of Money you want to send.";
    }
    try {
      boolean transferSuccessfully = Casino.transferMoney(sender.getId(), receiver.getId(), amount);
      if (!transferSuccessfully) {
        return "The transfer was unsuccessful. Make shure you have enough Money.";
      }
      return "You successfully transferred " + amount + " Money to " + receiver.getAsMention() + ".";
    } catch (SQLException e) {
      return "I was unable to transfer the Money to " + receiver.getAsMention() +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to transfer the Money to " + receiver.getAsMention() +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String slotsCommandEmbedDescriptionGenerator(User user, int bet){
    try {
      Logger.addLog("Slots command was run.", user.getId());
    } catch (IOException e) {
      return "I was unable to run the Slots command because I could not write to the logs. You may want to report this Error: \n" + e;
    }
    if(bet < 0){
        return "Please specify a valid amount of Money you want to bet.";
    }
    try {
      long money = Casino.getMoney(user.getId());
      if(money < bet){
        return "You don't have enough Money to bet that much.";
      }
      return CasinoGames.slots(bet, user, true);
    } catch (SQLException e) {
      return "I was unable to run the Slots command" +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to run the Slots command" +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }
}

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
          embed.setDescription(Commands.infoCommandEmbedDescriptionGenerator(target));
          embed.setTitle("Casino Information from " + target.getName() + ".");
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "daily": {
          embed.setTitle("Daily Reward");
          embed.setDescription(Commands.dailyCommandEmbedDescriptionGenerator(event.getUser()));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "transfer": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          User sender = event.getUser();
          int amount = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle("Transfer Money");
          embed.setDescription(Commands.transferCommandEmbedDescriptionGenerator(sender, target, amount));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "slots": {
          User user = event.getUser();
            int bet = event.getOption("amount", OptionMapping::getAsInt);
            embed.setTitle("Slots");
            embed.setDescription(Commands.slotsCommandEmbedDescriptionGenerator(user, bet));
            embed.setFooter("tanjun.java Casino");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
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
