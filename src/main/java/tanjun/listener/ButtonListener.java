package tanjun.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import tanjun.commands.BlackJack;
import tanjun.utilitys.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonListener extends ListenerAdapter {
  private final Map<String, BlackJack> blackJackMap = new HashMap<>();

  public void addBlackJackInstance(String blackjackId, BlackJack blackJackInstance) {
    blackJackMap.put(blackjackId, blackJackInstance);
  }

    @Override
  public void onButtonInteraction(ButtonInteractionEvent event) {
    if (event.getButton().getId() == null) {
      event.editMessage("The button you clicked does not exist or has no ID. Please report this.").queue();
      return;
    }

    switch (event.getButton().getId()) {
      case "casino.blackjack.drawCard": {
        BlackJack blackJack = blackJackMap.get(event.getUser().getId());
        if (blackJack == null) {
          event.reply("The BlackJack game does not belong to you.").setEphemeral(true).queue();
          return;
        }
        String result = blackJack.drawBlackjackCard();
        EmbedBuilder eb = Helper.defaultEmbed();
        eb.setTitle("Blackjack");
        eb.setDescription(result);
        eb.setFooter("Tanjun.java Blackjack");
        List<Button> buttons = new ArrayList<>();
        if (blackJack.gameIsOver) {
          buttons.add(Button.primary("casino.blackjack.drawCard", "Draw a Card").asDisabled());
          buttons.add(Button.primary("casino.blackjack.stand", "Stand").asDisabled());
        } else {
          buttons.add(Button.primary("casino.blackjack.drawCard", "Draw a Card"));
          buttons.add(Button.primary("casino.blackjack.stand", "Stand"));
        }
        event.getHook().editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
        event.editMessage("You clicked the button!").queue();
      }
        break;

      case "casino.blackjack.stand": {
        BlackJack blackJack = blackJackMap.get(event.getUser().getId());
        if (blackJack == null) {
          event.reply("The BlackJack game does not belong to you.").setEphemeral(true).queue();
          return;
        }
        String result = blackJack.endGame();
        EmbedBuilder eb = Helper.defaultEmbed();
        eb.setTitle("Blackjack");
        eb.setDescription(result);
        eb.setFooter("Tanjun.java Blackjack");
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("casino.blackjack.drawCard", "Draw a Card").asDisabled());
        buttons.add(Button.primary("casino.blackjack.stand", "Stand").asDisabled());
        event.getHook().editOriginalEmbeds(eb.build()).setActionRow(buttons).queue();
        event.editMessage("You clicked the button!").queue();
      }
        break;
      default:
        event.editMessage("The button you clicked does not exist. Please report this.").queue();
        break;
    }
  }
}
