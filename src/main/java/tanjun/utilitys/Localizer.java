package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {
    private final ResourceBundle messages;

    public final LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction
            .fromBundles("Commands", DiscordLocale.ENGLISH_UK, DiscordLocale.ENGLISH_US)
            .build();


    public Localizer(Locale locale) {
        messages = ResourceBundle.getBundle("messages", locale);
    }

    public Localizer() {
        Dotenv dotenv = Dotenv.load();
        final String defaultLocale = dotenv.get("DefaultLocale");
        Locale locale = Locale.forLanguageTag(defaultLocale);
        messages = ResourceBundle.getBundle("messages", locale);
    }

    public String localize(String key) {
        return messages.getString(key);
    }

    public String localize(String key, Object... args) {
        String template = messages.getString(key);
        return String.format(template, args);
    }
}
