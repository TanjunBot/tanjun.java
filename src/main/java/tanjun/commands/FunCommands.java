package tanjun.commands;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import tanjun.utilitys.Helper;
import tanjun.utilitys.Localizer;
import tanjun.utilitys.Logger;
import tanjun.utilitys.TenorApiWrapper;

import java.io.IOException;
import java.util.Locale;

public class FunCommands extends ListenerAdapter {
  String[] huggingGifs = TenorApiWrapper.GetUrls("hug", 15);
  String[] eatGifs = TenorApiWrapper.GetUrls("eat", 15);
  String[] waveGifs = TenorApiWrapper.GetUrls("wave", 15);
  String[] kissGifs = TenorApiWrapper.GetUrls("kiss", 15);
  String[] patGifs = TenorApiWrapper.GetUrls("patpat", 15);
  String[] slapGifs = TenorApiWrapper.GetUrls("slap", 15);
  String[] boopGifs = TenorApiWrapper.GetUrls("boop", 15);

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    Dotenv dotenv = Dotenv.load();
    final String usePersonalLocale = dotenv.get("usePersonalLocale");
    Locale locale = (usePersonalLocale.equals("yes")? event.getUserLocale(): event.getGuildLocale()).toLocale();
    Localizer localizer = new Localizer(locale);
    String eventName = event.getName();
    String subcommandName = event.getSubcommandName();
    if (eventName.equals("fun")) {
      switch (subcommandName) {
        case "hug":
        case "eat":
        case "wave":
        case "kiss":
        case "pat":
        case "slap":
        case "boop":
        case "cry":
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
              embed.setTitle(localizer.localize("commands.utility.fun.hug.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "eat":
              try {
                Logger.addLog("eat command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(eatGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.eat.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "wave":
              try {
                Logger.addLog("wave command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(waveGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.wave.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "kiss":
              try {
                Logger.addLog("kiss command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(kissGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.kiss.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "pat":
              try {
                Logger.addLog("pat command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(patGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.pat.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "slap":
              try {
                Logger.addLog("slap command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(slapGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.slap.embed.title", event.getUser().getName(), target.getName()));
              break;
            case "boop":
              try {
                Logger.addLog("boop command was run.", event.getUser().getId());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              url = Helper.getRandom(boopGifs);
              embed.setTitle(localizer.localize("commands.utility.fun.boop.embed.title", event.getUser().getName(), target.getName()));
              break;
          }


          embed.setDescription(message);
          embed.setImage(url);
          embed.setFooter("Via Tenor");
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
