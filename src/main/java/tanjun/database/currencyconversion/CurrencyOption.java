package tanjun.database.currencyconversion;

public class CurrencyOption {
  String symbol;
  String code;
  String name;

  // Constructor to initialize the fields
  public CurrencyOption(String symbol, String code, String name) {
    this.symbol = symbol;
    this.code = code;
    this.name = name;
  }

  // Optional: You can add getters for each field if needed
  public String getSymbol() {
    return symbol;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }
}
