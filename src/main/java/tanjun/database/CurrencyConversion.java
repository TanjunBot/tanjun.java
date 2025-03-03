package tanjun.database;

import tanjun.Tanjun;
import tanjun.database.currencyconversion.CurrencyConversionData;
import tanjun.database.currencyconversion.CurrencyConversionResponse;
import tanjun.database.currencyconversion.CurrencyOption;
import tanjun.util.DatabaseConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyConversion {

  String name;
  String fullName;
  String symbol;
  double valueAsDollar;
  Date lastUpdate;
  public static final List<CurrencyOption> CURRENCIES = Arrays.asList(
          new CurrencyOption("$", "USD", "United States Dollar"),
          new CurrencyOption("€", "EUR", "Euro"),
          new CurrencyOption("AU$", "AUD", "Australian Dollar"),
          new CurrencyOption("R$", "BRL", "Brazilian Real"),
          new CurrencyOption("C$", "CAD", "Canadian Dollar"),
          new CurrencyOption("Fr", "CHF", "Swiss Franc"),
          new CurrencyOption("CN¥", "CNY", "Chinese Yuan Renminbi"),
          new CurrencyOption("kr", "DKK", "Danish Krone"),
          new CurrencyOption("£", "GBP", "Great British Pound"),
          new CurrencyOption("HK$", "HKD", "Hong Kong Dollar"),
          new CurrencyOption("Rp", "IDR", "Indonesian Rupiah"),
          new CurrencyOption("₹", "INR", "Indian Rupee"),
          new CurrencyOption("¥", "JPY", "Japanese Yen"),
          new CurrencyOption("₩", "KRW", "South Korean Won"),
          new CurrencyOption("M$", "MXN", "Mexican Peso"),
          new CurrencyOption("nkr", "NOK", "Norwegian Krone"),
          new CurrencyOption("NZ$", "NZD", "New Zealand Dollar"),
          new CurrencyOption("Zł", "PLN", "Polish Zloty"),
          new CurrencyOption("₽", "RUB", "Russian Ruble"),
          new CurrencyOption("Ft", "HUF", "Hungarian Forint"),
          new CurrencyOption("Kč", "CZK", "Czech Koruna"),
          new CurrencyOption("S$", "SGD", "Singapore Dollar"),
          new CurrencyOption("฿", "THB", "Thai Baht"),
          new CurrencyOption("₺", "TRY", "Turkish Lira"),
          new CurrencyOption("NT$", "TWD", "Taiwan New Dollar")
  );

  public CurrencyConversion(String name, String fullName, String symbol, double valueAsDollar, Date lastUpdate) {
    this.name = name;
    this.fullName = fullName;
    this.symbol = symbol;
    this.valueAsDollar = valueAsDollar;
    this.lastUpdate = lastUpdate;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {return this.fullName;}

  public String getSymbol() {
    return this.symbol;
  }

  public double getValue() {
    return this.valueAsDollar;
  }

  public Date getLastUpdate() {
    return this.lastUpdate;
  }

  public static List<CurrencyOption> getAllCurrencyOptions() {return CURRENCIES;}

  public static void createTableIfNotExist(DatabaseConnector databaseConnector, Tanjun tanjun) {
    tanjun.addLog("Database", "trying to create Table currencyConversion");
    try (Connection connection = databaseConnector.getConnection()) {
      tanjun.addLog("Database", "Connection successfully established for creating currencyConversion " +
              "table");
      String query = """
              CREATE TABLE IF NOT EXISTS currencyConversion (
                name VARCHAR(128) PRIMARY KEY,
                fullName VARCHAR(128),
                symbol VARCHAR(128),
                valueAsDollar DECIMAL(55, 35),
                lastUpdate DATETIME
              );
              """;
      PreparedStatement preparedStatement = connection.prepareStatement(query);

      int rowsAffected = preparedStatement.executeUpdate();
      tanjun.addLog("Database", "Successfully created Table currencyConversion. Affected "
              + rowsAffected + " Row(s)");
    } catch (SQLException e) {
      tanjun.addLog("Database", "Error creating Table commandUse: " + e);
      throw new RuntimeException("Error creating Table commandUse: " + e);
    }
  }

  public static void updateCurrencyConversions(Tanjun tanjun, DatabaseConnector databaseConnector) throws URISyntaxException, IOException {

      URL url = new URI("https://api.currencyapi.com/v3/latest?apikey=cur_live_SdroQpuQMMariYfenbu1gyudCZJv41ksmsSRGJhr&currencies=EUR%2CUSD%2CJPY%2CGBP%2CAUD%2CCAD%2CCHF%2CCNY%2CHKD%2CNZD%2CHUF%2CKRW%2CSGD%2CNOK%2CMXN%2CINR%2CRUB%2CCZK%2CTRY%2CBRL%2CTWD%2CDKK%2CPLN%2CTHB%2CIDR").toURL();
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");

      int status = con.getResponseCode();

      if (status != 200) {
        throw new RuntimeException("Could not fetch currencyConversion data. Make shure that the currencyConversion " +
                "URL is working.");
      }

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuilder content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }

      CurrencyConversionResponse currencyConversionResponse = CurrencyConversionResponse.fromString(content.toString());

      CurrencyConversionData[] currencyConversionData = currencyConversionResponse.getCurrencyConversionData();

      Date nowDate = new Date(System.currentTimeMillis());

    for (CurrencyConversionData conversionData : currencyConversionData) {
      switch (conversionData.getCode()) {
        case "AUD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "AUD", "Australian Dollar", "AU$", conversionData.getValue(), nowDate);
          break;
        case "BRL":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "BRL", "Brazilian real", "R$", conversionData.getValue(), nowDate);
          break;
        case "CAD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "CAD", "Canadian Dollar", "C$", conversionData.getValue(), nowDate);
          break;
        case "CHF":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "CHF", "Swiss franc", "Fr", conversionData.getValue(), nowDate);
          break;
        case "CNY":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "CNY", "Chinese yuan renminbi", "CN¥", conversionData.getValue(), nowDate);
          break;
        case "DKK":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "DKK", "Danish krone", "kr", conversionData.getValue(), nowDate);
          break;
        case "EUR":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "EUR", "Euro", "€", conversionData.getValue(), nowDate);
          break;
        case "GBP":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "GBP", "Great British Pound", "£", conversionData.getValue(), nowDate);
          break;
        case "HKD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "HKD", "Hong Kong dollar", "HK$", conversionData.getValue(), nowDate);
          break;
        case "IDR":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "IDR", "Indonesian Rupiah", "Rp", conversionData.getValue(), nowDate);
          break;
        case "INR":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "INR", "Indian Rupee", "₹", conversionData.getValue(), nowDate);
          break;
        case "JPY":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "JPY", "Japanese yen", "¥", conversionData.getValue(), nowDate);
          break;
        case "KRW":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "KRW", "South Korean won", "₩", conversionData.getValue(), nowDate);
          break;
        case "MXN":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "MXN", "Mexican peso", "M$", conversionData.getValue(), nowDate);
          break;
        case "NOK":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "NOK", "Norwegian krone", "nkr", conversionData.getValue(), nowDate);
          break;
        case "NZD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "NZD", "New Zealand Dollar", "NZ$", conversionData.getValue(), nowDate);
          break;
        case "PLN":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "PLN", "Polish zloty", "Zł", conversionData.getValue(), nowDate);
          break;
        case "RUB":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "RUB", "Russian Ruble", "₽", conversionData.getValue(), nowDate);
          break;
        case "HUF":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "HUF", "Hungarian Forint", "Ft", conversionData.getValue(), nowDate);
          break;
        case "CZK":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "CZK", "Czech koruna", "Kč", conversionData.getValue(), nowDate);
          break;
        case "SGD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "SGD", "The Singapore dollar", "S$", conversionData.getValue(), nowDate);
          break;
        case "THB":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "THB", "Thai baht", "฿", conversionData.getValue(), nowDate);
          break;
        case "TRY":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "TRY", "Turkish lira", "₺", conversionData.getValue(), nowDate);
          break;
        case "TWD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "TWD", "Taiwan New Dollar", "NT$", conversionData.getValue(), nowDate);
          break;
        case "USD":
          CurrencyConversion.add(tanjun, databaseConnector,
                  "USD", "United States dollar", "$", conversionData.getValue(), nowDate);
          break;
      }
    }
      con.disconnect();
  }

  public static CurrencyConversion get(Tanjun tanjun, DatabaseConnector databaseConnector, String name) throws
          URISyntaxException, IOException {
    tanjun.addLog("Database", "Getting currencyConversion..");
    if (tanjun.currencyConversionShouldBeFetched()) {
      CurrencyConversion.updateCurrencyConversions(tanjun, databaseConnector);
      tanjun.updateCurrencyConversionFetchTime(Date.from(Instant.now()));
    }

    ArrayList<String> allowedNames = new ArrayList<>(List.of("EUR", "USD", "JPY", "GBP", "AUD", "CAD", "CHF",
            "CNY", "HKD", "NZD", "SEK", "KRW", "SGD", "NOK", "MXN", "INR", "RUB", "ZAR", "TRY", "BRL", "TWD", "DKK",
            "PLN", "THB", "IDR"));

    if (!allowedNames.contains(name)) {
      tanjun.addLog("Database", "Could not find Currency with Name " + name );
      return null;
    }
    try (Connection connection = databaseConnector.getConnection()) {
      tanjun.addLog("Database", "Connection successfully established for getCurrencyConversion");
      String query = "SELECT symbol, fullName, valueAsDollar, lastUpdate FROM currencyConversion WHERE name = ?";

      PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, name);
      ResultSet rs = preparedStatement.executeQuery();
      rs.next();
      String symbol = rs.getString(1);
      String fullName = rs.getString(2);
      double valueAsDollar = rs.getDouble(3);
      Date lastUpdate = rs.getDate(4);

      tanjun.addLog("Database", "Successfully got currencyConversion for " + name);

      return new CurrencyConversion(name, fullName, symbol, valueAsDollar, lastUpdate);

    } catch (SQLException e) {
      tanjun.addLog("Database", "Error updating currency conversion\n" + e);
      throw new RuntimeException("Error adding command use: " + e);
    }
  }

  public static CurrencyConversion add(Tanjun tanjun, DatabaseConnector databaseConnector, String name, String fullName,
                                       String symbol, double valueAsDollar, java.sql.Date lastUpdate) {
    tanjun.addLog("Database", "Establishing connection for setCurrencyConversion");

    try (Connection connection = databaseConnector.getConnection()) {
      if (connection == null) {
        tanjun.addLog("Database", "ERROR: Database connection is null!");
        throw new RuntimeException("Database connection failed!");
      }

      tanjun.addLog("Database", "Connection successfully established for setCurrencyConversion");

      String query = "INSERT INTO currencyConversion (name, fullName, symbol, valueAsDollar, lastUpdate) " +
              "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `symbol` = ?, `valueAsDollar` = ?, `lastUpdate` = ?";

      PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

      tanjun.addLog("Database", "Executing query: " + query);

      if (preparedStatement == null) {
        tanjun.addLog("Database", "ERROR: PreparedStatement is null!");
        throw new RuntimeException("Failed to create PreparedStatement!");
      }

      preparedStatement.setString(1, name);
      preparedStatement.setString(2, fullName);
      preparedStatement.setString(3, symbol);
      preparedStatement.setDouble(4, valueAsDollar);
      preparedStatement.setDate(5, lastUpdate);
      preparedStatement.setString(6, symbol);
      preparedStatement.setDouble(7, valueAsDollar);
      preparedStatement.setDate(8, lastUpdate);

      int rowsAffected = preparedStatement.executeUpdate();
      tanjun.addLog("Database", "update currency conversion successfully finished. Added/Updated " + rowsAffected + " line(s)");

      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys != null && generatedKeys.next()) {
          tanjun.addLog("Database", "Generated key found, returning new CurrencyConversion.");
        } else {
          tanjun.addLog("Database", "No generated key found, returning object from input.");
        }
      }

      return new CurrencyConversion(name, fullName, symbol, valueAsDollar, lastUpdate);

    } catch (SQLException e) {
      tanjun.addLog("Database", "Error updating currency conversion\n" + e);
      throw new RuntimeException("Error adding currency conversion: " + e);
    }
  }
}

