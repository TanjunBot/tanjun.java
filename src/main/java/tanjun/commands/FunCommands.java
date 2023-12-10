package tanjun.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Logger;
import tanjun.utilitys.TenorApiWrapper;

import java.io.IOException;
import java.util.Collection;

public class FunCommands extends ListenerAdapter {
  String[] huggingGifs = TenorApiWrapper.GetUrls("hug", 15);
  String[] eatGifs = TenorApiWrapper.GetUrls("eat", 15);
  String[] waveGifs = TenorApiWrapper.GetUrls("wave", 15);
  String[] kissGifs = TenorApiWrapper.GetUrls("kiss", 15);
  String[] patGifs = TenorApiWrapper.GetUrls("patpat", 15);
  String[] slapGifs = TenorApiWrapper.GetUrls("slap", 15);
  String[] boopGifs = TenorApiWrapper.GetUrls("boop", 15);
  String[] cryGifs = TenorApiWrapper.GetUrls("cry", 15);
  String[] highfiveGifs = TenorApiWrapper.GetUrls("highFive", 15);

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    String eventName = event.getName();
    String subcommandName = event.getSubcommandName();
    switch (eventName) {
      case "fun":
        switch (subcommandName) {
          case "hug":
          case "eat":
          case "wave":
            event.deferReply().queue();
            User target = event.getOption("user", OptionMapping::getAsUser);
            String message = event.getOption("message", OptionMapping::getAsString);
            EmbedBuilder embed = Helper.defaultEmbed();
            assert target != null;
            String url = "https://placehold.co/600x400?text=404";
            switch (subcommandName) {
              case "hug":
                try {
                  Logger.addLog("hug command was run.", event.getUser().getId());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                url = Helper.getRandom(huggingGifs);
                embed.setTitle(event.getUser().getName() + " is hugging " + target.getName());
                break;
              case "eat":
                try {
                  Logger.addLog("eat command was run.", event.getUser().getId());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                url = Helper.getRandom(eatGifs);
                embed.setTitle(event.getUser().getName() + " is eating " + target.getName());
                break;
              case "wave":
                try {
                  Logger.addLog("wave command was run.", event.getUser().getId());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
                url = Helper.getRandom(waveGifs);
                embed.setTitle(event.getUser().getName() + " is waving to " + target.getName());
                break;
            }


            embed.setDescription(message);
            embed.setImage(url);
            event.getHook().editOriginalEmbeds(embed.build()).queue();

            break;
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
