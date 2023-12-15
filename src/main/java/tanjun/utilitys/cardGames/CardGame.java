package tanjun.utilitys.cardGames;

import tanjun.utilitys.Logger;

import java.io.IOException;
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
        beautifiedCards += "  _______\t";
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |%s     |\t", hand.get(i).substring(1));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |   %s   |\t", hand.get(i).charAt(0));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += " |       |  ";
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |   %s   |\t", hand.get(i).charAt(0));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += String.format(" |      %s|\t", hand.get(i).substring(1));
      }
      beautifiedCards += "\n";
      for (int i = 0; i < handLength; i++) {
        beautifiedCards += "  ‾‾‾‾‾‾‾\t";
      }
      beautifiedCards += "\n";
    }

    beautifiedCards += "```\n";


    return beautifiedCards;
  }
}