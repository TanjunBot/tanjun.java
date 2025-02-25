package tanjun;

import tanjun.util.DatabaseConnector;
import tanjun.util.Logger;
import net.dv8tion.jda.api.JDA;

public class Tanjun {
  public Logger logger;
  DatabaseConnector databaseConnector;
  JDA jda;

  public Tanjun(JDA jda, String databaseUrl, String databaseUsername, String databasePassword) {
    // The Max File Size of the Log file; 50MB
    int maxLogFileSize = 1028*1028*50;
    logger = new Logger(maxLogFileSize);
    this.jda = jda;
    DatabaseConnector databaseconnector = new DatabaseConnector(
      this,
            databaseUrl,
            databaseUsername,
            databasePassword
    );
  }
}
