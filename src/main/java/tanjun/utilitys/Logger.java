package tanjun.utilitys;

import java.io.*;
import java.nio.Buffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Scanner;

public abstract class Logger {
  static File log = new File("logs.log");
  public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


  /**
   * Adds a Message to the Logs.
   *
   * @param information The Information that should be displayed in the Logs.
   * @param cause       The Cause of the Log. e.g. System, User, ...
   */
  public static void addLog(String information, String cause) throws IOException {
    Date date = new Date();
    BufferedWriter writer = new BufferedWriter(new FileWriter("logs.log", true));
    if(checkFileSize(log)){
      removeFirstLine(log);
    }
    writer.append(dateFormat.format(date)).append(" - ").append(cause).append(": ").append(information);
    writer.append('\n');
    writer.close();
  }

  /**
   * Checks the fileSize
   *
   * @param log The File you want to check
   */
  public static boolean checkFileSize(File log) {

    double fileSize = log.length() / Math.pow(2, 20);
    //System.out.println(fileSize);
    return fileSize > (1048576.0 * 10); // 1048576.0 * 10
  }

  /**
   *
   * @param log The File you want to check
   */
  public static void removeFirstLine(File log) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(log, "rw");

    long writePosition = raf.getFilePointer();
    raf.readLine();

    long readPosition = raf.getFilePointer();

    byte[] buff = new byte[1024];
    int n;
    while(-1 != (n = raf.read(buff))){
      raf.seek(writePosition);
      raf.write(buff, 0, n);
      readPosition += n;
      writePosition += n;
      raf.seek(readPosition);
    }
    raf.setLength(writePosition);
    raf.close();
  }



/*
  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10_000; i++) {
      addLog("Testing", "Test");
    }
    long end = System.currentTimeMillis();

    System.out.println((int)(end - start) / 1000);
  }
*/
}