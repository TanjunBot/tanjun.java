package tanjun.commands;

import tanjun.api.Casino;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BlackJack extends CardGame {
  int playerId;
  int croupierId;
  String playerDiscordId;
  int bet;
  public boolean gameIsOver = false;

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
  this.bet = bet;
    try {
      if(Casino.getMoney(userId) < bet) {

        return "You don't have enough Money.";
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
        List<String> playerHand = getPlayerhand(playerId);
        int playerHandValue = getCardValue(playerHand);
        if (getCardValue(getPlayerhand(playerId)) == 21) {
          Casino.playGame(playerDiscordId, 0);
          gameIsOver = true;
          return "The Croupier and you have a Blackjack. You get your Money back.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand (" + croupierHandValue + "):" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):\n" + beautifyCards(getPlayerhand(playerId));
        } else {
          Casino.playGame(playerDiscordId, bet * -1);
          gameIsOver = true;
          return "The Croupier has a Blackjack. You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(getPlayerhand(playerId));
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
          gameIsOver = true;
          List<String> playerHand = getPlayerhand(playerId);
          int playerHandValue = getCardValue(playerHand);
          return "You have a Blackjack. You won " + bet * 2 + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                  + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(getPlayerhand(playerId));
        }
      } catch (IOException e) {
        return "I was unable to get the Player hand. You may want to report this Error: \n" + e;
      } catch (SQLException e) {
        return "I was unable to play the game. You may want to report this Error: \n" + e;
      }
    }
    try {
      return "The Blackjack Game was started.\nCroupier Hand:\n" + beautifyCards(List.of(getPlayerhand(croupierId).getFirst(), "??")) + "\nYour Hand (" + getPlayerValue() + "):\n" + beautifyCards(getPlayerhand(playerId));
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
        List<String> croupierHand = getPlayerhand(croupierId);
        int croupierHandValue = getCardValue(croupierHand);
        Casino.playGame(playerDiscordId, bet * -1);
        Logger.addLog("The Player hand is over 21. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return "You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(getPlayerhand(croupierId)) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(playerHand);
      }
      while(getCardValue(getPlayerhand(croupierId)) < 17){
        drawPlayerCard(croupierId);
      }
      List<String> croupierHand = getPlayerhand(croupierId);
      int croupierHandValue = getCardValue(croupierHand);
      if(croupierHandValue > 21){
        Casino.playGame(playerDiscordId, bet);
        Logger.addLog("The Croupier hand is over 21. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return "You won " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue > playerHandValue){
        Casino.playGame(playerDiscordId, bet * -1);
        Logger.addLog("The Croupier hand is higher than the Player hand. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return "You lost " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue < playerHandValue){
        Casino.playGame(playerDiscordId, bet);
        Logger.addLog("The Player hand is higher than the Croupier hand. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return "You won " + bet + " Money.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(playerHand);
      }
      if(croupierHandValue == playerHandValue){
        Casino.playGame(playerDiscordId, 0);
        Logger.addLog("The Croupier and the Player have the same hand value. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return "The Croupier and you have the same hand value. You get your Money back.\n\nYour new Balance is " + Casino.getMoney(playerDiscordId) + " Money."
                + "\n\nCroupier Hand (" + croupierHandValue + "):\n" + beautifyCards(croupierHand) + "\n\nYour Hand (" + playerHandValue + "):⠀\n" + beautifyCards(playerHand);
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
      List<String> playerHand = getPlayerhand(playerId);
      int playerHandValue = getCardValue(playerHand);
      System.out.println(getPlayerhand(playerId));
      return "You drew a " + drawnCard + ".\n\nCroupier Hand:\n" + beautifyCards(List.of(getPlayerhand(croupierId).getFirst(), "??")) + "\nYour new Hand (" + playerHandValue + "):\n" + beautifyCards(getPlayerhand(playerId));
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
