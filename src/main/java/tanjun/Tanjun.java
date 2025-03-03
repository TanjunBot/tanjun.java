package tanjun;

import tanjun.util.Api;
import tanjun.util.DatabaseConnector;
import tanjun.util.Logger;
import net.dv8tion.jda.api.JDA;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class Tanjun {
  public Logger logger;
  DatabaseConnector databaseConnector;
  JDA jda;
  Api api;
  Date lastCurrencyConversionFetched;

  public Tanjun(JDA jda, String databaseUrl, String databaseUsername, String databasePassword) {
    // The Max File Size of the Log file; 1MB
    int maxLogFileSize = 1028*1028;
    logger = new Logger(maxLogFileSize);
    this.jda = jda;
    this.databaseConnector = new DatabaseConnector(
            this,
            databaseUrl,
            databaseUsername,
            databasePassword
    );
    this.api = new Api(this, databaseConnector);
    this.api.createAllTables();
  }

  public DatabaseConnector getDatabaseConnector() {
    return this.databaseConnector;
  }

  public void addLog(String cause, String message) {
    logger.addLog(cause, message);
  }

  public Api getApi() {
    return api;
  }

  public boolean currencyConversionShouldBeFetched() {
    if (lastCurrencyConversionFetched == null) {
      return true; // Never fetched before
    }

    // Convert last fetch time to ZonedDateTime
    ZonedDateTime lastFetchedTime = lastCurrencyConversionFetched.toInstant().atZone(ZoneId.systemDefault());
    ZonedDateTime fourHoursAgo = ZonedDateTime.now().minusHours(4);

    return lastFetchedTime.isBefore(fourHoursAgo);
  }

  public void updateCurrencyConversionFetchTime(Date fetchTime) {
    this.lastCurrencyConversionFetched = fetchTime;
  }
}
