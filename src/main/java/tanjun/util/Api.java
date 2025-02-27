package tanjun.util;

import tanjun.Tanjun;
import tanjun.database.CommandUsage;

import java.sql.Date;

public class Api {
  Tanjun tanjun;
  DatabaseConnector databaseConnector;

  public Api(Tanjun tanjun, DatabaseConnector databaseConnector) {
    this.tanjun = tanjun;
    this.databaseConnector = databaseConnector;
  }

  public void createAllTables() {
    CommandUsage.createTableIfNotExist(databaseConnector, tanjun);
  }

  public CommandUsage addCommandUse(String commandName, long userId) {
    Date currentDate = new Date(System.currentTimeMillis());
    return CommandUsage.addCommandUse(commandName, userId, currentDate, databaseConnector, tanjun);
  }
}
