package tanjun.utilitys;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  public static void addLog(String information, String cause) throws IOException {
    Date date = new Date();
    BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true));
    writer.append(dateFormat.format(date)).append(" - ").append(cause).append(": ").append(information);
    writer.append('\n');
    writer.close();
  }
}
