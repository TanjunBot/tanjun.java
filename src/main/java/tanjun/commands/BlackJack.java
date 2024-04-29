package tanjun.commands;

import tanjun.api.Casino;
import tanjun.utilitys.Localizer;
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
  Localizer localizer;

  /**
   * Creates a new BlackJack game.
   *
   * @param decks the amount of decks.
   * @throws IOException if the Logger is not reachable.
   */
  public BlackJack(int decks, Localizer localizer) throws IOException {
    super(decks, 0, localizer);
    this.localizer = localizer;
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

        return localizer.localize("blackjack.startGame.notEnoughMoney");
      };

      try {
        Logger.addLog("Starting a new Blackjack game with " + bet + " Money bet.", "BlackJack");
      } catch (IOException e) {
        return Localizer.localize("blackjack.startGame.error", e);
      }
    } catch (SQLException | IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
    }
      try {
      playerId = addPlayerhand(2);
    } catch (IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
    }
    try {
      croupierId = addPlayerhand(2);
    } catch (IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
    }
    playerDiscordId = userId;
    bet = this.bet;

    List<String> croupierHand;
    try {
      croupierHand = getPlayerhand(croupierId);
    } catch (IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
    }
    int croupierHandValue = getCardValue(croupierHand);
    if (croupierHandValue == 21) {
      try {
        List<String> playerHand = getPlayerhand(playerId);
        int playerHandValue = getCardValue(playerHand);
        if (getCardValue(getPlayerhand(playerId)) == 21) {
          Casino.playGame(playerDiscordId, 0);
          gameIsOver = true;
          return Localizer.localize("blackjack.startGame.drawn", Casino.getMoney(playerDiscordId),
                  croupierHandValue, beautifyCards(croupierHand), playerHandValue, beautifyCards(getPlayerhand(playerId)));
        } else {
          Casino.playGame(playerDiscordId, bet * -1);
          gameIsOver = true;
          return Localizer.localize("blackjack.startGame.lose", bet, Casino.getMoney(playerDiscordId),
                  croupierHandValue, beautifyCards(croupierHand), playerHandValue, beautifyCards(getPlayerhand(playerId)));
        }
      } catch (SQLException | IOException e) {
        return Localizer.localize("blackjack.startGame.error", e);
      }
    } else {
      try {
        if (getCardValue(getPlayerhand(playerId)) == 21){
          Casino.playGame(playerDiscordId, bet * 2);
          gameIsOver = true;
          List<String> playerHand = getPlayerhand(playerId);
          int playerHandValue = getCardValue(playerHand);
          return Localizer.localize("blackjack.startGame.win", bet, Casino.getMoney(playerDiscordId),
                  croupierHandValue, beautifyCards(croupierHand), playerHandValue, beautifyCards(getPlayerhand(playerId)));
        }
      } catch (IOException | SQLException e) {
        return Localizer.localize("blackjack.startGame.error", e);
      }
    }
    try {
      return Localizer.localize("blackjack.startGame.started", beautifyCards(List.of(getPlayerhand(croupierId).getFirst(), "??")),
              getPlayerValue(), beautifyCards(getPlayerhand(playerId)));
    } catch (IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
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
        return Localizer.localize("blackjack.endGame.lose", bet, Casino.getMoney(playerDiscordId),
                croupierHandValue, beautifyCards(getPlayerhand(croupierId)), playerHandValue, beautifyCards(playerHand));
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
        return Localizer.localize("blackjack.endGame.win", bet, Casino.getMoney(playerDiscordId),
                croupierHandValue, beautifyCards(getPlayerhand(croupierId)), playerHandValue, beautifyCards(playerHand));
      }
      if(croupierHandValue > playerHandValue){
        Casino.playGame(playerDiscordId, bet * -1);
        Logger.addLog("The Croupier hand is higher than the Player hand. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return Localizer.localize("blackjack.endGame.lose", bet, Casino.getMoney(playerDiscordId),
                croupierHandValue, beautifyCards(getPlayerhand(croupierId)), playerHandValue, beautifyCards(playerHand));
      }
      if(croupierHandValue < playerHandValue){
        Casino.playGame(playerDiscordId, bet);
        Logger.addLog("The Player hand is higher than the Croupier hand. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return Localizer.localize("blackjack.endGame.win", bet, Casino.getMoney(playerDiscordId),
                croupierHandValue, beautifyCards(getPlayerhand(croupierId)), playerHandValue, beautifyCards(playerHand));
      }
        Casino.playGame(playerDiscordId, 0);
        Logger.addLog("The Croupier and the Player have the same hand value. Blackjack Game was ended successfully.", "BlackJack");
        gameIsOver = true;
        return Localizer.localize("blackjack.endGame.drawn", Casino.getMoney(playerDiscordId),
                croupierHandValue, beautifyCards(getPlayerhand(croupierId)), playerHandValue, beautifyCards(playerHand));

    } catch (IOException | SQLException e) {
      return Localizer.localize("blackjack.startGame.error", e);
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
      return Localizer.localize("blackjack.drawCard", drawnCard,
              beautifyCards(List.of(getPlayerhand(croupierId).getFirst(), "??")), playerHandValue, beautifyCards(getPlayerhand(playerId)));
    } catch (IOException e) {
      return Localizer.localize("blackjack.startGame.error", e);
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
