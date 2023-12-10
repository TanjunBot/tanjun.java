package tanjun.utilitys;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class Helper {
  public static String getRandom(String[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
  }

  public static EmbedBuilder defaultEmbed(){
    EmbedBuilder embed = new EmbedBuilder();
    embed.setColor(new Color(203, 51, 245));
    return embed;
  }

}
