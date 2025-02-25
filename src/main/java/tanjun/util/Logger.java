package tanjun.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  int maxLogFileSize;
  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
  String logFileName = "tanjun.log";

  /**
   * Initializes a new Logger.
   * @param maxLogFileSize The maximum file size.
   */
  public Logger(int maxLogFileSize) {
    this.maxLogFileSize = maxLogFileSize;
  }

  /**
   * Sets the Date Format of the Logs.
   * @param pattern The Pattern of the Date Format. This can be 'yyyy/MM/dd HH:mm:ss'.
   */
  public void setDateFormat(String pattern) {
    this.dateFormat = new SimpleDateFormat(pattern);
  }

  /**
   * Sets the name of the Log file.
   * @param logFileName The Name of the Log file.
   */
  public void setLogFileName(String logFileName) {
    this.logFileName = logFileName;
  }

  /**
   * Adds a new Log to the Log file.
   * @param cause The cause of the Log. For example, this can be 'System', 'User', 'Server'
   * @param information The Log itself. This should contain information on what happened and who caused the log. For example
   *             this could be 'User xxx (id) Created Role xxx (id)'.
   */
  public void addLog(String cause, String information) {
    Date date = new Date();
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true));
      writer.append(dateFormat.format(date)).append(" - ").append(cause).append(": ").append(information);
      writer.append('\n');
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException("Log file could not be written to.");
    }
  }
}
