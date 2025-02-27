package tanjun;

import tanjun.util.Api;
import tanjun.util.DatabaseConnector;
import tanjun.util.Logger;
import net.dv8tion.jda.api.JDA;

public class Tanjun {
  public Logger logger;
  DatabaseConnector databaseConnector;
  JDA jda;
  Api api;

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

  public void addLog(String cause, String message) {
    logger.addLog(cause, message);
  }

  public Api getApi() {
    return api;
  }
}
