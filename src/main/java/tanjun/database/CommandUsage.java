package tanjun.database;

import tanjun.Tanjun;
import tanjun.util.DatabaseConnector;

import java.sql.*;

public class CommandUsage {

  int id;
  String commandName;
  long userId;
  Date useDate;

  public CommandUsage(int id, String commandName, long userId, Date useDate) {
    this.id = id;
    this.commandName = commandName;
    this.userId = userId;
    this.useDate = useDate;
  }

  public static void createTableIfNotExist(DatabaseConnector databaseConnector, Tanjun tanjun) {
    tanjun.addLog("Database", "trying to create Table commandUse");
    try (Connection connection = databaseConnector.getConnection()) {
      tanjun.addLog("Database", "Connection successfully established for creating commandUse table");
      String query = """
              CREATE TABLE IF NOT EXISTS commandUse (
                id INT AUTO_INCREMENT PRIMARY KEY,
                commandName VARCHAR(128),
                userId BIGINT,
                useTime DATETIME
              );
              """;
      PreparedStatement preparedStatement = connection.prepareStatement(query);

      int rowsAffected = preparedStatement.executeUpdate();
      tanjun.addLog("Database", "Successfully created Table commandUse. Affected "
              + rowsAffected + " Row(s)");
    } catch (SQLException e) {
      tanjun.addLog("Database", "Error creating Table commandUse: " + e);
      throw new RuntimeException("Error creating Table commandUse: " + e);
    }
  }

  public static CommandUsage addCommandUse(String commandName, long userId, Date useTime, DatabaseConnector databaseConnector,
                                   Tanjun tanjun) {
    tanjun.addLog("Database", "Establishing connection for addCommandUse");
    try (Connection connection = databaseConnector.getConnection()) {
      tanjun.addLog("Database", "Connection successfully established for addCommandUse");
      String query = "INSERT INTO commandUse (commandName, userId, useTime) VALUES (?, ?, ?)";

      PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      preparedStatement.setString(1, commandName);
      preparedStatement.setLong(2, userId);
      preparedStatement.setDate(3, useTime);

      int rowsAffected = preparedStatement.executeUpdate();
      tanjun.addLog("Database", "addCommandUse successfully finished. Added "
              + rowsAffected + " line(s)");

      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        int insertedId = generatedKeys.getInt(1);
        return new CommandUsage(insertedId, commandName, userId, useTime);
      }
    } catch (SQLException e) {
      tanjun.addLog("Database", "Error adding command use\n" + e);
      throw new RuntimeException("Error adding command use: " + e);
    }

    return null;
  }
}
