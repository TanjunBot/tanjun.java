package tanjun.util;

import kotlin.text.Charsets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

  public void cleanLog() {
    try{
      Path path = Paths.get(logFileName);
      long size = Files.size(path);
      if (size > maxLogFileSize) {
        List<String> lines = Files.readAllLines(path, Charsets.UTF_8);
        int lineStart = 100; //Clean up at least 100 lines at once
        while (lines.get(lineStart).startsWith("\n ")){ // make shure to cut out multiline logs
          System.out.println("Oops! line starts with space. skipping line..");
          lineStart++;
        }
        lines.subList(0, lineStart).clear();
        String newContent = String.join("\n", lines);
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, false));
        writer.append(newContent);
        writer.append("\n");
        writer.close();
      }
    } catch (IOException e) {
      throw new RuntimeException("Error while cleaning up Log file.\n" + e);
    }

  }

  /**
   * Adds a new Log to the Log file.
   * @param cause The cause of the Log. For example, this can be 'System', 'User', 'Server'
   * @param information The Log itself. This should contain information on what happened and who caused the log.
   *                    or example this could be 'User xxx (id) Created Role xxx (id)'.
   */
  public void addLog(String cause, String information) {
    Date date = new Date();
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true));
      writer.append(dateFormat.format(date)).append(" - ").append(cause).append(": ").append(information);
      writer.append('\n');
      writer.close();
      cleanLog();
    } catch (IOException e) {
      throw new RuntimeException("Log file could not be written to.\n" + e);
    }
  }
}
