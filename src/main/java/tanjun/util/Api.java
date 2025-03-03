package tanjun.util;

import tanjun.Tanjun;
import tanjun.database.CommandUsage;
import tanjun.database.CurrencyConversion;

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
    CurrencyConversion.createTableIfNotExist(databaseConnector, tanjun);
  }

  public void addCommandUse(String commandName, long userId) {
    Date currentDate = new Date(System.currentTimeMillis());
    CommandUsage.addCommandUse(commandName, userId, currentDate, databaseConnector, tanjun);
  }
}
