package tanjun.utilitys;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localizer {
    private static ResourceBundle messages = null;

    /**
     * The localizeFunction used to localize Discord SlashCommands.
     */
    public final LocalizationFunction localizationFunction = ResourceBundleLocalizationFunction
            .fromBundles("Commands", DiscordLocale.ENGLISH_UK, DiscordLocale.ENGLISH_US)
            .build();

    /**
     * Creates a new localizer with a specified locale. Used to translate Text.
     * @param locale the Locale you want to create the localizer with
     */
    public Localizer(Locale locale) {
        messages = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Creates a new localizer with the default locale of the bot.
     */
    public Localizer() {
        Dotenv dotenv = Dotenv.load();
        final String defaultLocale = dotenv.get("DefaultLocale");
        Locale locale = Locale.forLanguageTag(defaultLocale);
        messages = ResourceBundle.getBundle("messages", locale);
    }

    /**
     * Returns the localisation of a specified key. May be null if the key is not found.
     * @param key the Key of the localisation
     * @return the localised String
     */
    public String localize(String key) {
        return messages.getString(key);
    }

    /**
     * Returns the localisation of a specified key. May be null if the key is not found. Allows for variables inside
     *  the localisation.
     * @param key the Key of the localisation
     * @param args the variables that should be inserted to the localisation
     * @return the localised String
     */
    public static String localize(String key, Object... args) {
        String template = messages.getString(key);
        return MessageFormat.format(template, args);
    }
}
