package tanjun.database.currencyconversion;

public class CurrencyConversionData {
  String code;
  double value;

  public CurrencyConversionData(String code, double value) {
    this.code = code;
    this.value = value;
  }

  public String getCode() {
    return this.code;
  }

  public double getValue() {
    return this.value;
  }
}
