package me.fulcanelly.tgbridge.tools.twofactor;

import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import org.bukkit.ChatColor;

import me.fulcanelly.tgbridge.tools.MainConfig;

public class RegistrationMessageLocalizer {

    private static final String DEFAULT_LANGUAGE = "eng";

    private final MainConfig config;

    @Inject
    public RegistrationMessageLocalizer(MainConfig config) {
        this.config = config;
    }

    public String color(String locale, String key) {
        return ChatColor.translateAlternateColorCodes('&', raw(locale, key));
    }

    public String color(String locale, String key, Object... args) {
        return String.format(color(locale, key), args);
    }

    public String englishPlain(String key) {
        return ChatColor.stripColor(color(DEFAULT_LANGUAGE, key));
    }

    private String raw(String locale, String key) {
        var messages = config.registration_messages;
        if (messages == null) {
            return key;
        }

        var language = language(locale);
        var localized = messages.get(language);
        if (localized == null) {
            localized = messages.get(DEFAULT_LANGUAGE);
        }

        return getOrDefault(localized, messages.get(DEFAULT_LANGUAGE), key);
    }

    private String language(String locale) {
        var language = languageFromLocale(locale);
        if (language != null) {
            return language;
        }

        language = languageFromLocale(config.language);
        if (language != null) {
            return language;
        }

        return DEFAULT_LANGUAGE;
    }

    private String languageFromLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            return null;
        }

        var normalized = locale.toLowerCase(Locale.ROOT).replace('-', '_');

        if (normalized.startsWith("en") || normalized.startsWith("eng")) {
            return "eng";
        }
        if (normalized.startsWith("ru")) {
            return "ru";
        }
        if (normalized.startsWith("ua") || normalized.startsWith("uk")) {
            return "ua";
        }

        return null;
    }

    private String getOrDefault(Map<String, String> messages, Map<String, String> defaults, String key) {
        if (messages != null && messages.containsKey(key)) {
            return messages.get(key);
        }
        if (defaults != null && defaults.containsKey(key)) {
            return defaults.get(key);
        }

        return key;
    }
}
