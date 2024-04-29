package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.api.Casino;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class CardGame {
  String[] symbols = {"♤", "♡", "♢", "♧"};
  String[] numbers = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10",
          "J", "Q", "K"};

  List<String> cardDeck = new ArrayList<>();

  ArrayList<List<String>> playerCards = new ArrayList<>();

  /**
   * Creates a new Card Game.
   * The Card Game is a Deck of Cards with the given amount of decks and jokers.
   * The Card Game can be used to play any Card Game.
   *
   * @param decks  the amount of decks.
   * @param jokers the amount of jokers.
   * @throws IOException if the Logger is not reachable.
   */
  public CardGame(int decks, int jokers) throws IOException {
    Logger.addLog("Creating card Game with " + decks + " decks and " + jokers + " jokers.", "CardGame");
    for (String s : symbols) {
      for (String number : numbers) {
        String symbol = s + number;
        if (cardDeck == null) {
          cardDeck = new ArrayList<>();
        }
        cardDeck.add(symbol);
      }
    }

    for (int i = 0; i < jokers; i++) {
      cardDeck.add("Joker");
    }
  }

  /**
   * Draws a card from the card deck.
   *
   * @return the card that was drawn.
   * @throws IOException if the Logger is not reachable.
   */
  public String drawCard() throws IOException {
    Logger.addLog("Drawing a card.", "CardGame");
    int card = new Random().nextInt(cardDeck.size());
    String cardDrawn = cardDeck.get(card);
    cardDeck.remove(card);
    return cardDrawn;
  }

  /**
   * Adds a new Playerhand to the Card Game.
   *
   * @param startingCards the amount of Cards the Playerhand should start with.
   * @return the id of the Playerhand.
   * @throws IOException if the Logger is not reachable.
   */
  public int addPlayerhand(int startingCards) throws IOException {
    Logger.addLog("Adding a new Playerhand with " + startingCards + " starting Cards.", "CardGame");
    int playerId = playerCards.size();
    playerCards.add(new ArrayList<>());
    for (int i = 0; i < startingCards; i++) {
      String drawnCard = drawCard();
      playerCards.get(playerId).add(drawnCard);
    }

    return playerId;
  }

  /**
   * Gets the Playerhand of a Player.
   *
   * @param playerId the id of the Playerhand.
   * @return the Playerhand of the Player.
   * @throws IOException if the Logger is not reachable.
   */
  public List<String> getPlayerhand(int playerId) throws IOException {
    Logger.addLog("Getting Playerhand of Player " + playerId + ".", "CardGame");
    return playerCards.get(playerId);
  }

  /**
   * Draws a Card for a Player.
   *
   * @param playerId the id of the Playerhand.
   * @throws IOException if the Logger is not reachable.
   */
  public void drawPlayerCard(int playerId) throws IOException {
    Logger.addLog("Drawing a Card for Player " + playerId + ".", "CardGame");
    String drawnCard = drawCard();
    playerCards.get(playerId).add(drawnCard);
  }

  /**
   * Gets the Card Deck.
   *
   * @return the Card Deck.
   * @throws IOException if the Logger is not reachable.
   */
  public List<String> getCardDeck() throws IOException {
    Logger.addLog("Getting the Card Deck.", "CardGame");
    return cardDeck;
  }

  /**
   * Gets the Cards of all Players.
   *
   * @return the Cards of all Players.
   * @throws IOException if the Logger is not reachable.
   */
  public List<List<String>> getPlayerCards() throws IOException {
    Logger.addLog("Getting the Player Cards.", "CardGame");
    return playerCards;
  }

  /**
   * Beautifies the Cards in a Hand.
   * The Cards are displayed in a way that they look like real Cards.
   * The Cards then can be displayed in an Embed.
   *
   * @param hand the Hand you want to beautify.
   * @return the beautified Cards.
   */
  public String beautifyCards(List<String> hand) {
    String beautifiedCards = "";

    beautifiedCards += "```\n";
    int handLength = hand.toArray().length;
    for (int j = 0; j < (handLength / 3) + 1; j++) {
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += "  _______  ";
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |%s      |  ", hand.get(i).substring(1));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |   %s  |  ", hand.get(i).charAt(0));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += " |      |  ";
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |   %s  |  ", hand.get(i).charAt(0));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |      %s|  ", hand.get(i).substring(1));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += "  ‾‾‾‾‾‾‾  ";
      }
      beautifiedCards += "\n";
    }

    beautifiedCards += "```\n";


    return beautifiedCards;
  }
}

class BlackJack extends CardGame {
  int playerId;
  int croupierId;
  String playerDiscordId;
  int bet;

  /**
   * Creates a new BlackJack game.
   *
   * @param decks the amount of decks.
   * @throws IOException if the Logger is not reachable.
   */
  public BlackJack(int decks) throws IOException {
    super(decks, 0);
    Logger.addLog("Creating a new BlackJack game.", "BlackJack");
  }

  /**
   * Starts a new BlackJack game.
   *
   * @param bet    the amount of Money the User bet.
   * @param userId the id of the User that started the game.
   * @return the Cards of the Player. If the Croupier has a Blackjack, the User loses the bet and the according message is returned.
   */
  public String startGame(int bet, String userId) {

    try {
      if(Casino.getMoney(userId) < bet) {

        return "You don't have enough Money you FaceHuman";
      };

      try {
        Logger.addLog("Starting a new Blackjack game with " + bet + " Money bet.", "BlackJack");
      } catch (IOException e) {
        return "I was unable to start a new Blackjack game. You may want to report this Error: \n" + e;
      }
    } catch (SQLException e) {
      return "I was unable to get the Money. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to get the Money. You may want to report this Error: \n" + e;
    }
    try {
      playerId = addPlayerhand(2);
    } catch (IOException e) {
      return "I was unable to add the Player hand. You may want to report this Error: \n" + e;
    }
    try {
      croupierId = addPlayerhand(2);
    } catch (IOException e) {
      return "I was unable to add the Croupier hand. You may want to report this Error: \n" + e;
    }
    playerDiscordId = userId;
    bet = this.bet;

    List<String> croupierHand;
    try {
      croupierHand = getPlayerhand(croupierId);
    } catch (IOException e) {
      return "I was unable to get the Croupier hand. You may want to report this Error: \n" + e;
    }
    int croupierHandValue = getCardValue(croupierHand);
    if (croupierHandValue == 21) {
      try {
        if (getCardValue(getPlayerhand(playerId)) == 21) {
          Casino.playGame(playerDiscordId, 0);
          return "The Croupier and you have a Blackjack. You get your Money back.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(getPlayerhand(playerId));
        } else {
          Casino.playGame(playerDiscordId, bet * -1);
          return "The Croupier has a Blackjack. You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(getPlayerhand(playerId));
        }
      } catch (SQLException e) {
        try {
          Logger.addLog("I was unable to play the game. You may want to report this Error: \n" + e, "BlackJack");
        } catch (IOException ex) {
          return "I was unable to play the game. You may want to report this Error: \n" + e;
        }
      } catch (IOException e) {
        return "I was unable to play the game. You may want to report this Error: \n" + e;
      }
    } else {
      try {
        if (getCardValue(getPlayerhand(playerId)) == 21){
          Casino.playGame(playerDiscordId, bet * 2);
          return "You have a Blackjack. You won " + bet * 2 + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(getPlayerhand(playerId));
        }
      } catch (IOException e) {
        return "I was unable to get the Player hand. You may want to report this Error: \n" + e;
      } catch (SQLException e) {
        return "I was unable to play the game. You may want to report this Error: \n" + e;
      }
    }
    try {
      return "The Blackjack Game was started.\n" + beautifyCards(getPlayerhand(playerId));
    } catch (IOException e) {
      return "I was unable to get the Player hand. You may want to report this Error: \n" + e;
    }
  }

  /**
   * Ends the Blackjack game.
   * If the Player hand is over 21, the User loses the bet.
   * If the Croupier hand is over 21, the User wins the bet.
   * If the Croupier hand is higher than the Player hand, the User loses the bet.
   * If the Player hand is higher than the Croupier hand, the User wins the bet.
   * If the Croupier and the Player have the same hand value, the User gets his Money back.
   * @return the result of the Blackjack game.
   */
  public String endGame(){
    try {
      Logger.addLog("Ending the Blackjack game.", "BlackJack");
      List<String> playerHand = getPlayerhand(playerId);
      int playerHandValue = getCardValue(playerHand);
      if(playerHandValue > 21){
        Casino.playGame(playerDiscordId, bet * -1);
        Logger.addLog("The Player hand is over 21. Blackjack Game was ended successfully.", "BlackJack");
        return "You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand:\n" + beautifyCards(getPlayerhand(croupierId)) + "\n\nYour Hand:\n" + beautifyCards(playerHand);
      }
      while(getCardValue(getPlayerhand(croupierId)) < 17){
        drawPlayerCard(croupierId);
      }
      List<String> croupierHand = getPlayerhand(croupierId);
      int croupierHandValue = getCardValue(croupierHand);
      if(croupierHandValue > 21){
        Casino.playGame(playerDiscordId, bet);
        Logger.addLog("The Croupier hand is over 21. Blackjack Game was ended successfully.", "BlackJack");
        return "You won " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue > playerHandValue){
        Casino.playGame(playerDiscordId, bet * -1);
        Logger.addLog("The Croupier hand is higher than the Player hand. Blackjack Game was ended successfully.", "BlackJack");
        return "You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue < playerHandValue){
        Casino.playGame(playerDiscordId, bet);
        Logger.addLog("The Player hand is higher than the Croupier hand. Blackjack Game was ended successfully.", "BlackJack");
        return "You won " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue == playerHandValue){
        Casino.playGame(playerDiscordId, 0);
        Logger.addLog("The Croupier and the Player have the same hand value. Blackjack Game was ended successfully.", "BlackJack");
        return "The Croupier and you have the same hand value. You get your Money back.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand:\n" + beautifyCards(croupierHand) + "\n\nYour Hand:\n" + beautifyCards(playerHand);
      }

      return "The Blackjack Game was ended. You are not supposed to see this. Please report this.";
    } catch (IOException e) {
      return "I was unable to get the Player hand. You may want to report this Error: \n" + e;
    } catch (SQLException e) {
      return "I was unable to play the game. You may want to report this Error: \n" + e;
    }

  }

  /**
   * Draws a Card for the Player.
   * If the Player hand is over 21, the User loses the bet.
   * @return the result of the Card draw.
   */
  public String drawBlackjackCard(){
    try {
      Logger.addLog("Drawing a Card for Player " + playerId + ".", "BlackJack");
      String drawnCard = drawCard();
      playerCards.get(playerId).add(drawnCard);
      if(getCardValue(getPlayerhand(playerId)) > 21){
        return endGame();
      }
      return "You drew a " + drawnCard + ".\n\nYour new Hand:\n" + beautifyCards(getPlayerhand(playerId));
    } catch (IOException e) {
      return "I was unable to draw a Card. You may want to report this Error: \n" + e;
    }
  }

  /**
   * gets the value of the Players Hand.
   *
   * @return the value of the Players Hand.
   */
  public int getPlayerValue() {
    try {
      return getCardValue(getPlayerhand(playerId));
    } catch (IOException e) {
      return -1;
    }
  }


  /**
   * gets the value of a Hand.
   *
   * @param cards the Cards you want to get the value from.
   * @return the value of the Hand.
   */
  public int getCardValue(List<String> cards) {
    int handValue = 0;
    int asses = 0;

    for (int i = 0; i < cards.toArray().length; i++) {
      String card = cards.get(i);

      String cardNumber = card.substring(1);
      System.out.println("Entering switch with card Value: " + cardNumber);
      switch (cardNumber) {
        case "A":
          handValue += 11;
          asses++;
          break;
        case "J":
        case "Q":
        case "K":
          handValue += 10;
          break;
        default:
          handValue += Integer.parseInt(cardNumber);
      }
    }
    while (handValue > 21 && asses > 0) {
      handValue -= 10;
      asses--;
    }

    return handValue;
  }


}

class CasinoGames {
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

class Commands {
  public static String infoCommandEmbedDescriptionGenerator(User target) {
    try {
      ResultSet infoUser = Casino.getPlayerData(target.getId());
      assert infoUser != null;
      long money = infoUser.getLong("money");
      int gamesPlayed = infoUser.getInt("gamesPlayed");
      Timestamp lastPlayed = infoUser.getTimestamp("lastPlayed");
      int dailyStreak = infoUser.getInt("dailyStreak");
      int totalDaily = infoUser.getInt("totalDaily");
      Timestamp lastDaily = infoUser.getTimestamp("lastDaily");
      String embedDescription = "### Money ➤ " + money + "\n";
      embedDescription += "### games Played ➤ " + gamesPlayed + "\n";
      if (!(lastPlayed == null)) {
        embedDescription += "### last Played ➤ <t:" + lastPlayed.getTime() / 1000 + ":R>\n";
      } else {
        embedDescription += "### last Played ➤ never\n";
      }
      embedDescription += "### current Daily Streak ➤ " + dailyStreak + "\n";
      embedDescription += "### total Daily collected ➤ " + totalDaily + "\n";
      if (!(lastDaily == null)) {
        embedDescription += "### last Daily collected ➤ <t:" + lastDaily.getTime() / 1000 + ":R>";
      } else {
        embedDescription += "### last Daily collected ➤ never";
      }
      return embedDescription;
    } catch (SQLException e) {
      return "I was unable to fetch the Information from " + target.getName() +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to fetch the Information from " + target.getName() +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String dailyCommandEmbedDescriptionGenerator(User user) {
    try {
      Logger.addLog("Daily command was run.", user.getId());
      long secondsUntilAllowedToCollectDaily = Casino.secondsUntilAllowedToCollectDaily(user.getId());
      if (secondsUntilAllowedToCollectDaily > 0) {
        long timeUntilCooldownOver = (System.currentTimeMillis() / 1000) + secondsUntilAllowedToCollectDaily;
        return "You are not yet allowed to collect the Daily reward. Try again in <t:" + timeUntilCooldownOver + ":R>";
      } else {
        int reward = Casino.claimDaily(user.getId());
        return "You successfully claimed your Daily reward. You gained " + reward + " Money.";
      }
    } catch (SQLException e) {
      return "I was unable to give you your Daily reward" +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to give you your Daily reward" +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String transferCommandEmbedDescriptionGenerator(User sender, User receiver, int amount) {
    try {
      Logger.addLog("Transfer command was run.", sender.getId());
    } catch (IOException e) {
      return "I was unable to run the Transfer command because I could not write to the logs. You may want to report this Error: \n" + e;
    }
    if (receiver == null) {
      return "Please specify a User you want to send the Money.";
    }
    if (amount <= 0) {
      return "Please specify a valid amount of Money you want to send.";
    }
    try {
      boolean transferSuccessfully = Casino.transferMoney(sender.getId(), receiver.getId(), amount);
      if (!transferSuccessfully) {
        return "The transfer was unsuccessful. Make shure you have enough Money.";
      }
      return "You successfully transferred " + amount + " Money to " + receiver.getAsMention() + ".";
    } catch (SQLException e) {
      return "I was unable to transfer the Money to " + receiver.getAsMention() +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to transfer the Money to " + receiver.getAsMention() +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }

  public static String slotsCommandEmbedDescriptionGenerator(User user, int bet) {
    try {
      Logger.addLog("Slots command was run.", user.getId());
    } catch (IOException e) {
      return "I was unable to run the Slots command because I could not write to the logs. You may want to report this Error: \n" + e;
    }
    if (bet < 0) {
      return "Please specify a valid amount of Money you want to bet.";
    }
    try {
      long money = Casino.getMoney(user.getId());
      if (money < bet) {
        return "You don't have enough Money to bet that much.";
      }
      return CasinoGames.slots(bet, user, true);
    } catch (SQLException e) {
      return "I was unable to run the Slots command" +
              " from the Database. You may want to report this Error: \n" + e;
    } catch (IOException e) {
      return "I was unable to run the Slots command" +
              " because I could not write to the logs. You may want to report this Error: \n" + e;
    }
  }
}

public class CasinoCommands extends ListenerAdapter {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    String eventName = event.getName();
    String subcommandName = event.getSubcommandName();
    if (eventName.equals("casino")) {
      EmbedBuilder embed = Helper.defaultEmbed();
      event.deferReply().queue();
      switch (subcommandName) {
        case "info": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          if (target == null) {
            target = event.getUser();
          }
          embed.setDescription(Commands.infoCommandEmbedDescriptionGenerator(target));
          embed.setTitle("Casino Information from " + target.getName() + ".");
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "daily": {
          embed.setTitle("Daily Reward");
          embed.setDescription(Commands.dailyCommandEmbedDescriptionGenerator(event.getUser()));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "transfer": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          User sender = event.getUser();
          int amount = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle("Transfer Money");
          embed.setDescription(Commands.transferCommandEmbedDescriptionGenerator(sender, target, amount));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "slots": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle("Slots");
          embed.setDescription(Commands.slotsCommandEmbedDescriptionGenerator(user, bet));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }

        case "blackjack": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          try {
            CasinoGames.blackjack(bet, user, event);
            return;
          } catch (IOException e) {
            embed.setTitle("Blackjack Error");
            embed.setDescription("I was unable to run the Blackjack command because I could not write to the logs. You may want to report this Error: \n" + e);
            embed.setFooter("tanjun.java Casino");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
          } catch (SQLException e) {
            embed.setTitle("Blackjack Error");
            embed.setDescription("I was unable to run the Blackjack command because I can't do SQL. You may want to report this Error: \n" + e);
            embed.setFooter("tanjun.java Casino");
            event.getHook().editOriginalEmbeds(embed.build()).queue();
          }
          break;
        }
        case null:
          try {
            Logger.addLog("Unknown Command " + eventName + " was run.", event.getUser().getId());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + subcommandName);
      }
    }
  }
}