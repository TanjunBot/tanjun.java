package tanjun.listener;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.commands.BlackJack;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Localizer;

import java.util.*;

public class ButtonListener extends ListenerAdapter {
  private final Map<String, BlackJack> blackJackMap = new HashMap<>();

  public void addBlackJackInstance(String blackjackId, BlackJack blackJackInstance) {
    blackJackMap.put(blackjackId, blackJackInstance);
  }

    @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
      Dotenv dotenv = Dotenv.load();
      final String usePersonalLocale = dotenv.get("usePersonalLocale");
      Locale locale = (usePersonalLocale.equals("yes")? event.getUserLocale(): event.getGuildLocale()).toLocale();
      Localizer localizer = new Localizer(locale);
    if (event.getButton().getId() == null) {
      event.editMessage("The button you clicked does not exist or has no ID. Please report this.").queue();
      return;
    }

    switch (event.getButton().getId()) {
      case "casino.blackjack.drawCard": {
        BlackJack blackJack = blackJackMap.get(event.getUser().getId());
        if (blackJack == null) {
          event.reply(localizer.localize("commands.casino.blackjack.buttons.belongsNotToYou")).setEphemeral(true).queue();
          return;
        }
        String result = blackJack.drawBlackjackCard();
        EmbedBuilder eb = Helper.defaultEmbed();
        eb.setTitle("Blackjack");
        eb.setDescription(result);
        eb.setFooter("Tanjun.java Blackjack");
        List<Button> buttons = new ArrayList<>();
        if (blackJack.gameIsOver) {
          buttons.add(Button.primary("casino.blackjack.drawCard", localizer.localize("commands.casino.blackJack.buttons.draw")).asDisabled());
          buttons.add(Button.primary("casino.blackjack.stand", localizer.localize("commands.casino.blackJack.buttons.stand")).asDisabled());
        } else {
          buttons.add(Button.primary("casino.blackjack.drawCard", localizer.localize("commands.casino.blackJack.buttons.draw")));
          buttons.add(Button.primary("casino.blackjack.stand", localizer.localize("commands.casino.blackJack.buttons.stand")));
        }
        event.getHook().editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
      }
        break;

      case "casino.blackjack.stand": {
        BlackJack blackJack = blackJackMap.get(event.getUser().getId());
        if (blackJack == null) {
          event.reply(localizer.localize("commands.casino.blackjack.buttons.belongsNotToYou")).setEphemeral(true).queue();
          return;
        }
        String result = blackJack.endGame();
        EmbedBuilder eb = Helper.defaultEmbed();
        eb.setTitle("Blackjack");
        eb.setDescription(result);
        eb.setFooter("Tanjun.java Blackjack");
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("casino.blackjack.drawCard", localizer.localize("commands.casino.blackJack.buttons.draw")).asDisabled());
        buttons.add(Button.primary("casino.blackjack.stand", localizer.localize("commands.casino.blackJack.buttons.stand")).asDisabled());
        event.getHook().editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
      }
        break;
      default:
        event.editMessage("The button you clicked does not exist. Please report this.").queue();
        break;
    }
  }
}
