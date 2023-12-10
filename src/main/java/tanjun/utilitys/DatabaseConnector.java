package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
  static Dotenv dotenv = Dotenv.load();
  static final String url = dotenv.get("DatabaseUrl");
  static final String username = dotenv.get("DatabaseUsername");
  static final String password = dotenv.get("DatabasePassword");

  public static Connection connection;

  /**
   * Establishes the connection to the database.
   */
  public static void connectToDatabase(){
    try {
      Logger.addLog("Attempting to establish connection to Database.", "Database");
      connection = DriverManager.getConnection(url, username, password);
      Logger.addLog("Database connection established successfully.", "Database");
    } catch (SQLException | IOException e) {
      try {
        Logger.addLog("Failed to establish connection to Database: " + e, "Database");
        return;
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Should rarely be used. Rather use the API. to be used with caution.
   *
   * @param query the Query that should be executed.
   */
  public static void runUpdateSQLQuery(String query) throws IOException {
    Statement statement;
    Logger.addLog("Trying to run Query " + query, "Database");

    try {
      statement = connection.createStatement();
      statement.executeUpdate(query);
    } catch (SQLException e) {
      Logger.addLog("Failed to Run Query " + query + " Error: " + e, "Database");
      return;
    }
    Logger.addLog("Successfully executed the query " + query, "Database");
  }

  /**
   * creates all the needed Tables for the bot, if they don't exist yet.
   */
  public static void initiateDatabase() throws IOException {
    Logger.addLog("Initiating Database.", "Database");
    String[] querys = new String[1];
    querys[0] = """
            CREATE TABLE IF NOT EXISTS casinoUser(
              id VARCHAR(32),
              money BIGINT DEFAULT 100,
              gamesPlayed MEDIUMINT UNSIGNED DEFAULT 0,
              lastPlayed DATETIME,
              dailyStreak SMALLINT UNSIGNED DEFAULT 0,
              totalDaily SMALLINT UNSIGNED DEFAULT 0,
              lastDaily DATETIME,
              PRIMARY KEY(id)
            );
            """;

    for (String query : querys) {
      runUpdateSQLQuery(query);
    }
    Logger.addLog("Database Initiated.", "Database");
  }
}
