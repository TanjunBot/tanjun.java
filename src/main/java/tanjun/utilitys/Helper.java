package tanjun.utilitys;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class Helper {
  /**
   * Chooses a random entry of a String Array.
   * @param array The Array containing the Strings from which a random string shall be chosen.
   * @return a random entry of the Array.
   */
  public static String getRandom(String[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
  }

  /**
   * Creates the default Embed builder. Used so that every Embed looks the same e.g. have the same color.
   * @return an EmbedBuilder with the correct base settings.
   */
  public static EmbedBuilder defaultEmbed(){
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(new Color(203, 51, 245));
    return embed;
  }

}
