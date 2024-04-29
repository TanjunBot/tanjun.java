package tanjun.api;

import tanjun.utilitys.DatabaseConnector;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class Casino {
  /**
   * Changed the data of a Casino Player, so he has played a game. If the Casino Player does not exist in the Database,
   * the player gets created.
   *
   * @param userid      the ID of the user that played the game.
   * @param earnedMoney the Money the user earned. If the user lost money, this values has to be less than 0.
   * @return rather the Data was updated successfully or not.
   */
  public static boolean playGame(String userid, int earnedMoney) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("Changing database Data because Player played in Casino", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String query = "UPDATE casinoUser SET money = casinoUser.money + " + earnedMoney + ", gamesPlayed = " +
            "casinoUser.gamesPlayed + 1, lastPlayed = '" + timestamp + "' WHERE " +
            "id = " + userid;
    int result = statement.executeUpdate(query);
    boolean successfully = result != 0;
    if (successfully) {
      Logger.addLog("Data changed Successfully.", "API");
    } else {
      Logger.addLog("Failed to change the Casino Data from " + userid + ".", "API");
    }
    return successfully;
  }

  /**
   * Fetches the Data of a specified Player from the Casino-
   *
   * @param userid the ID of the User you want to fetch.
   * @return the Player Data from the specified user. May be null.
   */
  public static ResultSet getPlayerData(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("trying to get the User Data from " + userid + ".", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT * FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    if (!result.next()) {
      Logger.addLog("Was not able to get Player Data from " + userid + ".", "API");
      return null;
    }
    Logger.addLog("Successfully fetched Player Data form " + userid + ".", "API");
    return result;
  }

  /**
   * Rather the time a user last collected the daily reward is a day ago or not.
   *
   * @param userid the ID you want to check if he is allowed to collect the daily reward.
   * @return rather the user is allowed to collect the daily reward or not.
   */
  public static boolean canRecieveDailyReward(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("trying to find out if " + userid + " is allowed to receive Casino Daily reward.", "API");
    Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT lastDaily FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    if (!result.next()) {
      Logger.addLog("could not retrieve lastDaily from the Database where userid = " + userid + ".", "API");
      return false;
    }
    Timestamp timestampLastDaily = result.getTimestamp(1);
    if (timestampLastDaily == null) {
      return true;
    }
    Timestamp oneDayAfterLastDaily = Helper.addDays(timestampLastDaily, 1);
    System.out.println("Timestamp last Daily: " + timestampLastDaily);
    System.out.println("Timestamp in a Day: " + oneDayAfterLastDaily);
    return timestampNow.after(oneDayAfterLastDaily);
  }

  /**
   * Returns the seconds until a user is allowed to retrieve the daily Casino Reward.
   *
   * @param userid the ID of the user you want to retrieve the seconds until they're allowed to retrieve
   *               the Casino daily Reward.
   * @return the amount of Seconds until the user is allowed. if the user is allowed to collect the daily reward, this
   * will be a negative Number.
   */
  public static long secondsUntilAllowedToCollectDaily(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("trying to find out how many Seconds until " + userid + " is allowed to receive Casino " +
            "Daily reward.", "API");
    Timestamp timestampNow = new Timestamp(System.currentTimeMillis());
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDate midnightTomorrow = tomorrow.atStartOfDay().toLocalDate();
    Timestamp timestampTomorrowMidnight = Timestamp.valueOf(midnightTomorrow.atStartOfDay());
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT lastDaily FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    if (!result.next()) {
      Logger.addLog("Could not find lastDaily from " + userid + ".", "API");
      return 9999;
    }
    Timestamp timestampLastDaily = result.getTimestamp(1);
    if (timestampLastDaily == null) {
      Logger.addLog("The user with ID " + userid + " has never collected their daily.", "API");
      return -999999999;
    }
    System.out.println("Timestamp last Daily: " + timestampLastDaily);
    long secondsUntilAllowed = (timestampTomorrowMidnight.getTime() - timestampNow.getTime()) / 1000;
    Logger.addLog(userid + " is allowed to Collect their Daily reward in " + secondsUntilAllowed + "s.", "API");
    return secondsUntilAllowed;
  }


  /**
   * gets the current Casino Daily stream of a Casino Player.
   *
   * @param userid the ID of the person you want to get the daily Stream from.
   * @return the current Daily Streak.
   */
  public static int dailyStreak(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("Fetching the daily Streak from " + userid + ".", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT dailyStreak FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    if (!result.next()) {
      Logger.addLog("could not retrieve Daily Streak from " + userid + ".", "API");
      return 0;
    }
    return result.getInt(1);
  }

  /**
   * Gives the Daily reward to a Casino Player, if they're allowed to claim the Daily reward.
   *
   * @param userid the ID of the Casino Player that shall claim the Daily reward.
   * @return the reward the user got. If 0, then the Daily Reward was not given.
   */
  public static int claimDaily(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("giving the Casino Daily reward to " + userid + ".", "API");
    if (!canRecieveDailyReward(userid)) {
      Logger.addLog(userid + " is not allowed to retrieve the Daily reward yet.", "API");
      return 0;
    }
    int currentStreak = dailyStreak(userid);
    currentStreak += 1;
    if (secondsUntilAllowedToCollectDaily(userid) <= -(24 * 60 * 60 * 60)) {
      currentStreak = 1;
    }
    int reward = ((int) Helper.log(currentStreak, 2) + 1) * 100;
    System.out.println("Reward: " + reward);
    Statement statement = DatabaseConnector.connection.createStatement();
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    String query = "UPDATE casinoUser SET money = casinoUser.money + " + reward + ", lastDaily = '" + timestamp +
            "', dailyStreak = " + currentStreak + ", totalDaily = casinoUser.totalDaily + 1 WHERE id = " + userid;
    System.out.println("Query: " + query);
    int result = statement.executeUpdate(query);
    System.out.println("result: " + result);
    boolean successfully = result != 0;
    if (successfully) {
      Logger.addLog("Daily Reward was successfully given to " + userid + ".", "API");
      return reward;
    } else {
      Logger.addLog("Was not able to give Daily Reward to " + userid + ".", "API");
      return 0;
    }
  }

  /**
   * Transfers Money from one person to another. Rather use this method than giveMoney because this also checks if
   * the sender has enough money.
   *
   * @param sender   the ID of the user that sends the money.
   * @param receiver the ID of the user that receives the money.
   * @param amount   the amount of Money you want to transfer.
   * @return rather the transfer was successfully or not.
   */
  public static boolean transferMoney(String sender, String receiver, int amount) throws SQLException, IOException {
    Logger.addLog("trying to transfer " + amount + " Casino Money from " + sender + " to " + receiver, "API");
    addCasinoPlayerIfNotExists(sender);
    addCasinoPlayerIfNotExists(receiver);
    int senderMoney = getMoney(sender);
    if (amount <= senderMoney) {
      boolean takingMoneySuccessfully = giveMoney(sender, amount * -1);
      if (!takingMoneySuccessfully) {
        Logger.addLog("Failed to transfer Casino money from " + sender + " to " + receiver + ". Was not able " +
                "to take money from " + sender + ".", "API");
        return false;
      }
      boolean givingMoneySuccessfully = giveMoney(receiver, amount);
      if (!givingMoneySuccessfully) {
        Logger.addLog("Failed to transfer Casino money from " + sender + " to " + receiver + ". Was not able " +
                "to give money to " + receiver + ". I will try to give the taken Money back to " + sender, "API");
        giveMoney(sender, amount);
        return false;
      }
      Logger.addLog("transferred Casino money from " + sender + " to " + receiver + " Successfully.", "API");
      return true;
    } else {
      Logger.addLog("Failed to transfer Casino money from " + sender + " to " + receiver + ". Sender does not " +
              "have enough Money.", "API");
      return false;
    }
  }

  /**
   * Gives a Casino Player money. Keep in mind that this does not check anything whatsoever. In theory, you can give or
   * take someone as much money as you want. If you want to take someone money, amount has to be < 0.
   *
   * @param userid the ID of the user you want to give money
   * @param amount the amount of money you want to give
   * @return rather the Money was given successfully.
   */
  public static boolean giveMoney(String userid, long amount) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("Trying to give " + userid + " " + amount + " Casino Money", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "UPDATE casinoUser SET money = casinoUser.money + " + amount + " WHERE id = " + userid;
    int result = statement.executeUpdate(query);
    boolean successfully = result != 0;
    if (successfully) {
      Logger.addLog("Gave " + userid + " successfully " + amount + " Casino Money.", "API");
    } else {
      Logger.addLog("Failed to give " + userid + " successfully " + amount + " Casino Money.", "API");
    }
    return successfully;
  }

  /**
   * Fetches the Money from the given Casino Player. If for whatever reason the query fails, 0 gets returned.
   *
   * @param userid The ID of the user you want to get the money from.
   * @return The amount of Money the user has. 0 if the fetch fails.
   */
  public static int getMoney(String userid) throws SQLException, IOException {
    addCasinoPlayerIfNotExists(userid);
    Logger.addLog("Fetching the Casino Money from " + userid, "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT money FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    if (!result.next()) {
      Logger.addLog("could not fetch money from " + userid, "API");
      return 0;
    }
    int money = result.getInt(1);
    Logger.addLog("Successfully fetched Casino Money from " + userid + ". They have " + money + " money.", "API");
    return money;
  }

  /**
   * Inserts a user in the Casino Player Table.
   *
   * @param userid the ID of the User that should be inserted.
   * @return rather the User was successfully added or not.
   */
  public static Boolean addCasinoPlayer(String userid) throws IOException, SQLException {
    Logger.addLog("Adding User " + userid + " as a Casino Player.", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "INSERT INTO casinoUser (id) VALUES (" + userid + ")";
    int result = statement.executeUpdate(query);
    boolean successfully = result != 0;
    if (successfully) {
      Logger.addLog("Successfully added CasinoPlayer.", "API");
    } else {
      Logger.addLog("CasinoPlayer was not added.", "API");
    }
    return successfully;
  }

  /**
   * Creates a Casino Player if he does not yet Exist in the database.
   *
   * @param userid the ID of the user you want to check.
   * @return rather the operation was successfully. (not if the player was inserted to the database)
   */
  public static Boolean addCasinoPlayerIfNotExists(String userid) throws SQLException, IOException {
    if (!checkIfPlayerExists(userid)) {
      return addCasinoPlayer(userid);
    } else {
      return true;
    }
  }

  /**
   * checks rather a User is registered in the Casino or not.
   *
   * @param userid the ID of the User that you want to check.
   * @return rather the User is registered or not.
   */
  public static boolean checkIfPlayerExists(String userid) throws SQLException, IOException {
    Logger.addLog("Checking if User " + userid + " Exists as a Casino Player.", "API");
    Statement statement = DatabaseConnector.connection.createStatement();
    String query = "SELECT id FROM casinoUser WHERE id = " + userid;
    ResultSet result = statement.executeQuery(query);
    boolean existent = result.next();
    Logger.addLog("Result of check if user " + userid + " Exists: " + existent, "API");
    return existent;
  }
}
