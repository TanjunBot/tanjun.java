package tanjun.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import tanjun.Tanjun;

public class DatabaseConnector {
  Connection connection;
  Tanjun tanjun;

  /**
   * Handles Connection and Communication with the Database.
   * @param tanjun the Tanjun object.
   * @param url The URL of the SQL Database.
   * @param username The name of the Database User.
   * @param password The password of the Database User.
   */
  public DatabaseConnector(Tanjun tanjun, String url, String username, String password) {
    this.tanjun = tanjun;
    connect(url, username, password);
  }

  /**
   * Establishes a Connection to the database.
   * @param url The URL of the Database.
   * @param username The name of the Database User.
   * @param password The password of the Database User.
   */
  private void connect(String url, String username, String password) {
    tanjun.logger.addLog("Database", "establishing connection to Database...");
    try {
      connection = DriverManager.getConnection(url, username, password);
      tanjun.logger.addLog("Database", "Database connection established successfully.");
    } catch (SQLException e) {
      throw new RuntimeException("Could not connect to database.");
    }
  }
}
