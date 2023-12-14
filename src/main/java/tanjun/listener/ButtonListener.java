package tanjun.listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

public class ButtonListener extends ListenerAdapter  {
  public void onButtonClick(ButtonInteraction event) {
    if (event.getComponentId().equals("casino.blackjack.drawCard")) {
      event.editMessage("You clicked the button!").queue();
    }
  }
}
