package tanjun.database.currencyconversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CurrencyConversionResponse {
  Date lastUpdatedAt;
  CurrencyConversionData[] currencyConversionData;

  public CurrencyConversionResponse(Date lastUpdatedAt, CurrencyConversionData[] currencyConversionData) {
    this.lastUpdatedAt = lastUpdatedAt;
    this.currencyConversionData = currencyConversionData;
  }

  public CurrencyConversionData[] getCurrencyConversionData() {
    return currencyConversionData;
  }

  public static CurrencyConversionResponse fromString(String jsonString) throws JsonProcessingException {

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode rootNode = objectMapper.readTree(jsonString);

      String lastUpdateStr = rootNode.get("meta").get("last_updated_at").asText();
      Date lastUpdatedAt = new Date(java.time.Instant.parse(lastUpdateStr).toEpochMilli());

      JsonNode dataNode = rootNode.get("data");
      List<CurrencyConversionData> currencyList = new ArrayList<>();

      Iterator<String> fieldNames = dataNode.fieldNames();
      while (fieldNames.hasNext()){
        String currencyCode = fieldNames.next();
        double value = dataNode.get(currencyCode).get("value").asDouble();
        currencyList.add(new CurrencyConversionData(currencyCode, value));
      }

      return new CurrencyConversionResponse(lastUpdatedAt, currencyList.toArray(new CurrencyConversionData[0]));
  }
}
