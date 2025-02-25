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

  public DatabaseConnector(Tanjun tanjun, String url, String username, String password) {
    this.tanjun = tanjun;
  }

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
