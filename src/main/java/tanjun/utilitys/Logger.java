package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
  private static final String LOG_FILE = "logs.log";
  static Dotenv dotenv = Dotenv.load();
  static final String maxFileSize = dotenv.get("MaxLogFileSize");
  private static final long MAX_FILE_SIZE_BYTES = Integer.parseInt(maxFileSize); // 1 MB
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  /**
   * Adds a Message to the Logs.
   *
   * @param information The Information that should be displayed in the Logs.
   * @param cause       The Cause of the Log. e.g. System, User, ...
   */
  public static void addLog(String information, String cause) throws IOException {
    Date date = new Date();

    long fileSize = Files.size(Paths.get(LOG_FILE));

    if (fileSize >= MAX_FILE_SIZE_BYTES) {
      truncateLogFile();
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
      writer.append(dateFormat.format(date)).append(" - ").append(cause).append(": ").append(information);
      writer.append('\n');
    }
  }

  /**
   * Truncate the log file.
   */
  private static void truncateLogFile() throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
      writer.write(""); // Truncate the file by writing an empty string
    }
  }
}
