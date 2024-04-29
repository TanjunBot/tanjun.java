package tanjun.utilitys;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;

public class Helper {
  /**
   * Chooses a random entry of a String Array.
   *
   * @param array The Array containing the Strings from which a random string shall be chosen.
   * @return a random entry of the Array.
   */
  public static String getRandom(String[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
  }

  /**
   * Creates the default Embed builder. Used so that every Embed looks the same e.g. have the same color.
   *
   * @return an EmbedBuilder with the correct base settings.
   */
  public static EmbedBuilder defaultEmbed() {
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(new Color(203, 51, 245));
    return embed;
  }

  /**
   * Adds n days to a Timestamp. Code from <a href="http://www.java2s.com/example/java-utility-method/timestamp/adddays-timestamp-date-int-days-dff0f.html">...</a>
   * These accounts for change in TimeZones e.g. the german summer and winter time.
   *
   * @param date The timestamp you want to add the days to.
   * @param days The amount of days you want to add.
   * @return the Timestamp with n days added.
   */
  public static Timestamp addDays(Timestamp date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, days); //minus number would decrement the days
    return new Timestamp(cal.getTime().getTime());
  }

  /**
   * Calculates the Log of the number n to a specified Base.
   *
   * @param n    the Number you want to calculate the log from.
   * @param base the Base of the log.
   * @return the log of the number n to the given base.
   */
  public static double log(int n, int base) {
    return Math.log(n) / Math.log(base);
  }

  /**
   * Adds a String to a String Array.
   * @param array the Array you want to add the String to.
   * @param element the String you want to add to the Array.
   * @return the Array with the String added.
   */
  public static String[] addToArray(String[] array, String element) {
    String[] newArray = new String[array.length + 1];
    System.arraycopy(array, 0, newArray, 0, array.length);
    newArray[array.length] = element;
    return newArray;
  }

}
