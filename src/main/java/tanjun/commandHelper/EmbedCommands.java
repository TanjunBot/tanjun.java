package tanjun.commandHelper;

import net.dv8tion.jda.api.entities.User;
import tanjun.api.Casino;
import tanjun.utilitys.Logger;
import tanjun.utilitys.cardGames.CasinoGames;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmbedCommands {
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

  public static String slotsCommandEmbedDescriptionGenerator(User user, int bet) {
    try {
      Logger.addLog("Slots command was run.", user.getId());
    } catch (IOException e) {
      return "I was unable to run the Slots command because I could not write to the logs. You may want to report this Error: \n" + e;
    }
    if (bet < 0) {
      return "Please specify a valid amount of Money you want to bet.";
    }
    try {
      long money = Casino.getMoney(user.getId());
      if (money < bet) {
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