package tanjun.utilitys.cardGames;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.api.Casino;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CasinoGames {
  /**
   * Calculates the win of the Slots game.
   *
   * @param bet        the amount of Money the User bet.
   * @param fruit      the fruit that was rolled.
   * @param threeTimes if the fruit was rolled three times.
   * @return the amount of Money the User won.
   */
  public static int getSlotswin(int bet, String fruit, boolean threeTimes) {
    if (threeTimes) {
      switch (fruit) {
        case "🍒":
          return bet * 3;
        case "🍋":
          return bet * 5;
        case "🍊":
          return bet * 7;
        case "🍉":
          return bet * 10;
        case "🍇":
          return bet * 15;
        case "💎":
          return bet * 20;
      }
    } else {
      switch (fruit) {
        case "🍒":
          return bet * 2;
        case "🍋":
          return bet * 3;
        case "🍊":
          return bet * 4;
        case "🍉":
          return bet * 5;
        case "🍇":
          return bet * 6;
        case "💎":
          return bet * 7;
      }
    }
    return bet;
  }

  /**
   * Runs the Slots game.
   *
   * @param bet    the amount of Money the User bet.
   *               If the User wins, he gets the bet * the win.
   *               If the User loses, he loses the bet.
   * @param user   the User that runs the command.
   *               Used to get the Money from the Database.
   * @param rigged if the game is rigged.
   *               If the game is rigged, the User statistically will lose money.
   *               If the game is not rigged, the User statistically will win money.
   * @return the result of the Slots game.
   * @throws SQLException if the Database is not reachable.
   * @throws IOException  if the Logger is not reachable.
   */
  public static String slots(int bet, User user, boolean rigged) throws SQLException, IOException {
    String[] slots;
    String[] slots2;
    String[] slots3;

    if (rigged) {
      slots = new String[]{"🗑️", "🍒", "🍒", "🍊", "🍉", "🍇", "💎"};
      slots2 = new String[]{"🗑️", "🍒", "🍒", "🍋", "🍉", "🍇", "💎"};
      slots3 = new String[]{"🗑️", "🍒", "🍒", "🍋", "🍊", "🍉", "🍇"};
      Logger.addLog("Rigged Slots game was run.", user.getId());
    } else {
      slots = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎"};
      slots2 = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎"};
      slots3 = new String[]{"🍒", "🍋", "🍊", "🍉", "🍇", "💎"};
      Logger.addLog("Slots game was run.", user.getId());
    }


    int slot1 = new Random().nextInt(slots.length);
    int slot2 = new Random().nextInt(slots2.length);
    int slot3 = new Random().nextInt(slots3.length);

    String slot1Emoji = slots[slot1];
    String slot2Emoji = slots2[slot2];
    String slot3Emoji = slots3[slot3];

    String[] slotsResult = {slot1Emoji, slot2Emoji, slot3Emoji};

    int win = bet * -1;

    //at least first two are the same
    if (slotsResult[0].equals(slotsResult[1])) {
      //all three are the same
      if (slotsResult[1].equals(slotsResult[2])) {
        win = getSlotswin(bet, slotsResult[0], true);
      } else {
        //only first two are the same
        win = getSlotswin(bet, slotsResult[0], false);
      }
    }
    //first and last are the same
    else if (slotsResult[0].equals(slotsResult[2])) {
      win = getSlotswin(bet, slotsResult[0], false);
    }
    Casino.playGame(user.getId(), win);

    String embedText = "```\n―――――――――――――――\n|" + slot1Emoji + " | " + slot2Emoji + " | " + slot3Emoji +
            "|\n―――――――――――――――\n```\n";

    if (win > 0) {
      embedText += "You won " + win + " Money.";
    } else {
      embedText += "You lost " + bet + " Money.";
    }

    embedText += "\n\n```\n🍒🍒 ➤ *2    🍒🍒🍒 ➤ *3\n" +
            "🍋🍋 ➤ *3    🍋🍋🍋 ➤ *5\n" +
            "🍊🍊 ➤ *4    🍊🍊🍊 ➤ *7\n" +
            "🍉🍉 ➤ *5    🍉🍉🍉 ➤ *10\n" +
            "🍇🍇 ➤ *6    🍇🍇🍇 ➤ *15\n" +
            "💎💎 ➤ *7    💎💎💎 ➤ *20\n```\n";

    embedText += "\n\nYour new Balance is " + Casino.getMoney(user.getId()) + " Money.";

    return embedText;
  }

  public static void blackjack(int bet, User user, SlashCommandInteractionEvent event) throws IOException, SQLException {
    long userMoney = Casino.getMoney(user.getId());

    if(userMoney < bet){
      EmbedBuilder embed = Helper.defaultEmbed();
      embed.setTitle("Blackjack");
      embed.setDescription("You don't have enough Money. You only have " + userMoney +".");
      embed.setFooter("tanjun.java Casino");
      event.getHook().editOriginalEmbeds(embed.build()).queue();
      return;
    }

    BlackJack blackJackGame = new BlackJack(1);
    String result = blackJackGame.startGame(bet, user.getId());
    EmbedBuilder embed = Helper.defaultEmbed();
    embed.setTitle("Blackjack");
    embed.setDescription(result);
    List<Button> buttons = new ArrayList<>();
    buttons.add(Button.primary("casino.blackjack.drawCard", "Draw a Card"));
    event.getHook().editOriginalEmbeds(embed.build()).setActionRow(buttons).queue();
  }
}