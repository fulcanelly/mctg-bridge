package me.fulcanelly.tgbridge.tools.twofactor;

import java.util.Locale;
import java.util.Optional;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.StringUtils;

public class BotUIReception {

    @Inject
    SignupLoginReception reception;

    public Optional<String> onPrivateStartCommand(long userId, String payload) {
        try {
            return confirm(userId, StringUtils.decodeBase64(payload));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<String> onPrivateCodeMessage(long userId, String code) {
        return confirm(userId, code);
    }

    private Optional<String> confirm(long userId, String code) {
        return reception.confirmRegistration(userId, normalizeCode(code));
    }

    private String normalizeCode(String code) {
        var normalized = code.trim();
        if (normalized.contains(":")) {
            normalized = normalized.substring(normalized.lastIndexOf(':') + 1);
        }

        return normalized.toLowerCase(Locale.ROOT);
    }
}
