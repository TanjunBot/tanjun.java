package tanjun.commands;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.Main;
import tanjun.api.Casino;
import tanjun.listener.ButtonListener;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Localizer;
import tanjun.utilitys.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

class CardGame {
  String[] symbols = {"♠", "♥", "♦", "♣"};
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
   * @param jokers the amount of jokers. Jokers currently don't work with the beautifier and should therefore not be used.
   * @throws IOException if the Logger is not reachable.
   */
  public CardGame(int decks, int jokers, Localizer localizer) throws IOException {
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

    beautifiedCards += "```ansi\n";
    int handLength = hand.toArray().length;
    int cardsPerLine = 4;
    for (int i = 0; i < handLength; i += cardsPerLine) {
      beautifiedCards += "\n";
      for (int j = i; j < Math.min(i + cardsPerLine, handLength); j++) {
        if(hand.get(j).substring(1).equals("10"))
          beautifiedCards += String.format("\u001B[0;2m\u001B[0m⠀\u001B[2;47m⠀\u001B[2;31m%s\u001B[0m\u001B[2;47m⠀⠀⠀⠀\u001B[0m⠀", hand.get(j).substring(1));
        else
          beautifiedCards += String.format("\u001B[0;2m\u001B[0m⠀\u001B[2;47m⠀\u001B[2;31m%s\u001B[0m\u001B[2;47m⠀⠀⠀⠀⠀\u001B[0m⠀", hand.get(j).substring(1));
      }
      beautifiedCards += "\n";
      for (int j = i; j < Math.min(i + cardsPerLine, handLength); j++) {
        beautifiedCards += String.format("⠀\u001B[2;47m⠀⠀⠀\u001B[0m\u001B[2;37m\u001B[2;47m\u001B[2;31m%s\u001B[0m\u001B[2;37m\u001B[2;47m⠀⠀⠀\u001B[0m\u001B[2;37m\u001B[0m⠀", hand.get(j).charAt(0));
      }
      beautifiedCards += "\n";
      for (int j = i; j < Math.min(i + cardsPerLine, handLength); j++) {
        beautifiedCards += "⠀\u001B[2;47m⠀⠀⠀ ⠀⠀⠀\u001B[0m⠀";
      }
      beautifiedCards += "\n";
      for (int j = i; j < Math.min(i + cardsPerLine, handLength); j++) {
        beautifiedCards += String.format("⠀\u001B[2;47m⠀⠀⠀\u001B[0m\u001B[2;47m\u001B[2;31m%s\u001B[0m\u001B[2;47m⠀⠀\u001B[0m\u001B[2;47m⠀\u001B[0m⠀", hand.get(j).charAt(0));
      }
      beautifiedCards += "\n";
      for (int j = i; j < Math.min(i + cardsPerLine, handLength); j++) {
        if(hand.get(j).substring(1).equals("10"))
          beautifiedCards += String.format("⠀\u001B[2;47m⠀⠀⠀⠀\u001B[2;31m%s\u001B[0m\u001B[2;47m⠀\u001B[0m⠀", hand.get(j).substring(1));
        else
        beautifiedCards += String.format("⠀\u001B[2;47m⠀⠀⠀⠀⠀\u001B[2;31m%s\u001B[0m\u001B[2;47m⠀\u001B[0m⠀", hand.get(j).substring(1));
      }
      beautifiedCards += "\n";
      beautifiedCards += "\n";
    }

    beautifiedCards += "```\n";


    return beautifiedCards;
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
  public static String slots(int bet, User user, boolean rigged, Localizer localizer) throws SQLException, IOException {
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

    int win = getWin(bet, slotsResult);
    Casino.playGame(user.getId(), win);

    String embedText = String.format(
            "```ansi\n" +
                    // Top border with stylized title
                    "\u001b[0;44m\u001b[31m" + "      𝓢𝓛𝓞𝓣 𝓜𝓐𝓒𝓗𝓘𝓝𝓔       " + "\u001b[0m\n" + // Adjusted spacing & color
                    // Slot line, adjusted for full width
                    "\u001b[0;44m\u001b[37m" + "      [%s]  [%s]  [%s]   ㅤ " + "\u001b[0m\n" +  // Ensured length consistency
                    // Bottom border, extended to match the top
                    "\u001b[0;44m\u001b[37m" + "                          ㅤ " + "\u001b[0m\n" +
                    "```",
            slot1Emoji, slot2Emoji, slot3Emoji
    );




    if (win > 0) {
      embedText += localizer.localize("commands.casino.slots.embed.description.moneyWon", win);
    } else {
      embedText += localizer.localize("commands.casino.slots.embed.description.moneyLost", bet);
    }

    embedText += "\n\n```\n🍒🍒 ➤ *2    🍒🍒🍒 ➤ *3\n" +
            "🍋🍋 ➤ *3    🍋🍋🍋 ➤ *5\n" +
            "🍊🍊 ➤ *4    🍊🍊🍊 ➤ *7\n" +
            "🍉🍉 ➤ *5    🍉🍉🍉 ➤ *10\n" +
            "🍇🍇 ➤ *6    🍇🍇🍇 ➤ *15\n" +
            "💎💎 ➤ *7    💎💎💎 ➤ *20\n```\n";

    embedText += "\n\n" + localizer.localize("commands.casino.slots.embed.description.newBalance", Casino.getMoney(user.getId()));

    return embedText;
  }

  private static int getWin(int bet, String[] slotsResult) {
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
    return win;
  }

  public static void blackjack(int bet, User user, SlashCommandInteractionEvent event, Localizer localizer) throws IOException, SQLException {
    long userMoney = Casino.getMoney(user.getId());

    if(userMoney < bet){
      EmbedBuilder embed = Helper.defaultEmbed();
      embed.setTitle(localizer.localize("commands.casino.blackJack.embed.title"));
      embed.setDescription(localizer.localize("commands.casino.blackJack.embed.description.error", userMoney));
      embed.setFooter("tanjun.java Casino");
      event.getHook().editOriginalEmbeds(embed.build()).queue();
      return;
    }

    BlackJack blackJackGame = new BlackJack(1, localizer);
    String result = blackJackGame.startGame(bet, user.getId());
    EmbedBuilder embed = Helper.defaultEmbed();
    embed.setTitle(localizer.localize("commands.casino.blackJack.embed.title"));
    embed.setDescription(result);
    embed.setFooter("tanjun.java Casino");
    List<Button> buttons = new ArrayList<>();
    if (blackJackGame.gameIsOver) {
      buttons.add(Button.primary("casino.blackjack.drawCard", localizer.localize("commands.casino.blackJack.buttons.draw")).asDisabled());
      buttons.add(Button.primary("casino.blackjack.stand", localizer.localize("commands.casino.blackJack.buttons.stand")).asDisabled());
    }else {
      buttons.add(Button.primary("casino.blackjack.drawCard", localizer.localize("commands.casino.blackJack.buttons.draw")));
      buttons.add(Button.primary("casino.blackjack.stand", localizer.localize("commands.casino.blackJack.buttons.stand")));
    }
    event.getHook().editOriginalEmbeds(embed.build()).setActionRow(buttons).queue();
    Main.addBlackJackInstance(event.getUser().getId(), blackJackGame);
  }
}

class Commands {
  public static String infoCommandEmbedDescriptionGenerator(User target, Localizer localizer) {
    try {
      ResultSet infoUser = Casino.getPlayerData(target.getId());
      assert infoUser != null;
      long money = infoUser.getLong("money");
      int gamesPlayed = infoUser.getInt("gamesPlayed");
      Timestamp lastPlayed = infoUser.getTimestamp("lastPlayed");
      int dailyStreak = infoUser.getInt("dailyStreak");
      int totalDaily = infoUser.getInt("totalDaily");
      Timestamp lastDaily = infoUser.getTimestamp("lastDaily");
      String embedDescription = "### " + Localizer.localize("commands.casino.info.embed.description.money", money) + "\n";
      embedDescription += "### " + Localizer.localize("commands.casino.info.embed.description.gamesPlayed", gamesPlayed) + "\n";
      if (!(lastPlayed == null)) {
        embedDescription += "### " + Localizer.localize("commands.casino.info.embed.description.lastPlayed.date", lastPlayed.getTime() / 1000) + "\n";
      } else {
        embedDescription += "### " + localizer.localize("commands.casino.info.embed.description.lastPlayed.never") + "\n";
      }
      embedDescription += "### " + Localizer.localize("commands.casino.info.embed.description.currentDailyStreak", dailyStreak) + "\n";
      embedDescription += "### " + Localizer.localize("commands.casino.info.embed.description.totalDailyCollected", totalDaily) + "\n";
      if (!(lastDaily == null)) {
        embedDescription += "### " + Localizer.localize("commands.casino.info.embed.description.lastDailyCollected.date", lastDaily.getTime() / 1000);
      } else {
        embedDescription += "### "+ localizer.localize("commands.casino.info.embed.description.lastDailyCollected.never");
      }
      return embedDescription;
    } catch (SQLException | IOException e) {
      return Localizer.localize("commands.casino.info.embed.description.error", target.getName(), e);
    }
  }

  public static String dailyCommandEmbedDescriptionGenerator(User user, Localizer localizer) {
    try {
      Logger.addLog("Daily command was run.", user.getId());
      long secondsUntilAllowedToCollectDaily = Casino.secondsUntilAllowedToCollectDaily(user.getId());
      if (secondsUntilAllowedToCollectDaily > 0) {
        long timeUntilCooldownOver = (System.currentTimeMillis() / 1000) + secondsUntilAllowedToCollectDaily;
        return Localizer.localize("commands.casino.daily.embed.description.error.notYetAllowed", timeUntilCooldownOver);
      } else {
        int reward = Casino.claimDaily(user.getId());
        return Localizer.localize("commands.casino.daily.embed.description.success", reward);
      }
    } catch (SQLException | IOException e) {
      return Localizer.localize("commands.casino.daily.embed.description.error", e);
    }
  }

  public static String transferCommandEmbedDescriptionGenerator(User sender, User receiver, int amount, Localizer localizer) {
    try {
      Logger.addLog("Transfer command was run.", sender.getId());
    } catch (IOException e) {
      return localizer.localize("commands.casino.transfer.embed.description.error");
    }
    if (receiver == null) {
      return localizer.localize("commands.casino.transfer.embed.description.error.noReceiver");
    }
    if (amount <= 0) {
      return localizer.localize("commands.casino.transfer.embed.description.error.amountInvalid");
    }
    try {
      boolean transferSuccessfully = Casino.transferMoney(sender.getId(), receiver.getId(), amount);
      if (!transferSuccessfully) {
        return localizer.localize("commands.casino.transfer.embed.description.error.transferNotSuccessfully");
      }
      return Localizer.localize("commands.casino.transfer.embed.description.success", amount, receiver.getAsMention());
    } catch (SQLException | IOException e) {
      return localizer.localize("commands.casino.transfer.embed.description.error");
    }
  }

  public static String slotsCommandEmbedDescriptionGenerator(User user, int bet, Localizer localizer) {
    try {
      Logger.addLog("Slots command was run.", user.getId());
    } catch (IOException e) {
      return Localizer.localize("commands.casino.slots.embed.description.error", e);
    }
    if (bet < 0) {
      return localizer.localize("commands.casino.slots.embed.description.amountInvalid");
    }
    try {
      long money = Casino.getMoney(user.getId());
      if (money < bet) {
        return localizer.localize("commands.casino.slots.embed.description.notEnoughMoney");
      }
      return CasinoGames.slots(bet, user, true, localizer);
    } catch (SQLException | IOException e) {
      return localizer.localize("commands.casino.slots.embed.description.error", e);
    }
  }
}

public class CasinoCommands extends ListenerAdapter {
  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Dotenv dotenv = Dotenv.load();
    final String usePersonalLocale = dotenv.get("usePersonalLocale");
    Locale locale = (usePersonalLocale.equals("yes")? event.getUserLocale(): event.getGuildLocale()).toLocale();
    Localizer localizer = new Localizer(locale);
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
          embed.setDescription(Commands.infoCommandEmbedDescriptionGenerator(target, localizer));
          embed.setTitle(Localizer.localize("commands.casino.info.embed.title", target.getName()));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "daily": {
          embed.setTitle(localizer.localize("commands.casino.daily.embed.title"));
          embed.setDescription(Commands.dailyCommandEmbedDescriptionGenerator(event.getUser(), localizer));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "transfer": {
          User target = event.getOption("user", OptionMapping::getAsUser);
          User sender = event.getUser();
          int amount = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle(localizer.localize("commands.casino.transfer.embed.title"));
          embed.setDescription(Commands.transferCommandEmbedDescriptionGenerator(sender, target, amount, localizer));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }
        case "slots": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          embed.setTitle(localizer.localize("commands.casino.slots.embed.title"));
          embed.setDescription(Commands.slotsCommandEmbedDescriptionGenerator(user, bet, localizer));
          embed.setFooter("tanjun.java Casino");
          event.getHook().editOriginalEmbeds(embed.build()).queue();
          break;
        }

        case "blackjack": {
          User user = event.getUser();
          int bet = event.getOption("amount", OptionMapping::getAsInt);
          try {
            CasinoGames.blackjack(bet, user, event, localizer);
            return;
          } catch (IOException | SQLException e) {
            embed.setTitle(localizer.localize("commands.casino.blackJack.error.embed.title"));
            embed.setDescription(localizer.localize("commands.casino.blackJack.error.embed.description", e));
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