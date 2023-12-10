package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Enables the use of the Tenor API in JAVA
 */
public class TenorApiWrapper{
  static Dotenv dotenv = Dotenv.load();
  private static final String API_KEY = dotenv.get("TenorApiKey");
  private static final String CLIENT_KEY = "my_test_app";

  /**
   * Get a specified number of Results from Tenor according to a Search Term. Returns the entire response from
   * the Tenor API.
   * @param searchTerm The term the Gif(s) should match.
   * @param limit The amount of Gifs you want to get. should be >= 1.
   * @return Response from the Tenor API in form of a JSONObject.
   */
  public static JSONObject getSearchResults(String searchTerm, int limit){
    final String url = String.format("https://tenor.googleapis.com/v2/search?q=%1$s&key=%2$s&client_key=%3$s&limit=%4$s",
            searchTerm, API_KEY, CLIENT_KEY, limit);
    try {
      return get(url);
    } catch (IOException | JSONException ignored) {
    }
    return null;

  }

  /**
   * Generates a String Array containing URLs of Tenor Gifs to a specific Topic.
   * @param searchTerm The term the Gif(s) should match.
   * @param limit The maximum amount of Gifs.
   * @return a String Array containing the URLs of the found Gifs.
   */
  public static String[] GetUrls(String searchTerm, int limit){
    List<String> urls = new ArrayList<>();
    JSONObject response = getSearchResults(searchTerm, limit);
    assert response != null;
    JSONArray results = (JSONArray) response.get("results");
    for (int i = 0; i < results.length(); i++) {
      JSONObject result = results.getJSONObject(i);
      JSONObject mediaFormats = result.getJSONObject("media_formats");
      JSONObject tinyGif = mediaFormats.getJSONObject("tinygif");
      urls.add((String) tinyGif.get("url"));
    }
    return urls.toArray(new String[0]);
  }

  private static JSONObject get(String url) throws IOException, JSONException {
    HttpURLConnection connection = null;
    try {
      // Get request
      connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("GET");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");
      connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

      // Handle failure
      int statusCode = connection.getResponseCode();
      if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
        String error = String.format("HTTP Code: '%1$s' from '%2$s'", statusCode, url);
        throw new ConnectException(error);
      }

      // Parse response
      return parser(connection);
    } catch (Exception ignored) {
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return new JSONObject("");
  }

  /**
   * Parse the response into JSONObject
   */
  private static JSONObject parser(HttpURLConnection connection) throws JSONException {
    char[] buffer = new char[1024 * 4];
    int n;
    InputStream stream = null;
    try {
      stream = new BufferedInputStream(connection.getInputStream());
      InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
      StringWriter writer = new StringWriter();
      while (-1 != (n = reader.read(buffer))) {
        writer.write(buffer, 0, n);
      }
      return new JSONObject(writer.toString());
    } catch (IOException ignored) {
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException ignored) {
        }
      }
    }
    return new JSONObject("");
  }


}
