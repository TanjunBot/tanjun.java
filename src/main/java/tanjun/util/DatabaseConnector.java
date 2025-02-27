package tanjun.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import tanjun.Tanjun;

public class DatabaseConnector {
  private final HikariDataSource dataSource;
  private final Tanjun tanjun;

  /**
   * Handles Connection and Communication with the Database.
   * @param tanjun the Tanjun object.
   * @param url The URL of the SQL Database.
   * @param username The name of the Database User.
   * @param password The password of the Database User.
   */
  public DatabaseConnector(Tanjun tanjun, String url, String username, String password) {
    this.tanjun = tanjun;
    this.dataSource = createDataSource(url, username, password);
  }

  /**
   * Creates and configures the HikariCP connection pool.
   * @param url The URL of the Database.
   * @param username The database username.
   * @param password The database password.
   * @return A configured HikariDataSource.
   */
  private HikariDataSource createDataSource(String url, String username, String password) {
    tanjun.addLog("Database", "Setting up connection pool...");

    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(password);
    config.setMaximumPoolSize(10);  // Max 10 connections in the pool
    config.setMinimumIdle(2);        // Minimum idle connections
    config.setIdleTimeout(30000);    // 30 seconds idle timeout
    config.setMaxLifetime(600000);   // 10 minutes max lifetime
    config.setConnectionTimeout(3000); // 3 seconds timeout for new connections

    HikariDataSource ds = new HikariDataSource(config);
    tanjun.addLog("Database", "Connection pool initialized successfully.");
    return ds;
  }

  /**
   * Retrieves a connection from the connection pool.
   * @return A database connection.
   * @throws SQLException If unable to get a connection.
   */
  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * Closes the connection pool when shutting down.
   */
  public void close() {
    tanjun.addLog("Database", "Shutting down database connection pool...");
    if (dataSource != null) {
      dataSource.close();
    }
  }
}
