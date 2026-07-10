package me.fulcanelly.tgbridge.tools.twofactor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.StringUtils;

public class BotUIReception {

    @Inject
    SignupLoginReception reception;

    public Optional<String> onPrivateStartCommand(long userId, String code) {
        var nameAndCode = new LinkedList<>(
            List.of(StringUtils.decodeBase64(code).split(":"))
        );
        var player = nameAndCode.getFirst();
        var clearcode = nameAndCode.getLast();

        if (reception.cofirmRegistration(userId, player, clearcode)) {
            return Optional.of(player);
        } else {
            return Optional.empty();
        }
    }
}
