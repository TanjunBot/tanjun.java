package tanjun.utilitys;

import java.util.Random;

public class Helper {
  public static String getRandom(String[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
  }

}
