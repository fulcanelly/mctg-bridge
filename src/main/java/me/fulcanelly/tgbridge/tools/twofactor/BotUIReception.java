package me.fulcanelly.tgbridge.tools.twofactor;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.StringUtils;

public class BotUIReception {

    @Inject
    SignupLoginReception reception;

    public boolean onPrivateStartCommand(long userId, String code) {
        var nameAndCode = new LinkedList<>(
            List.of(StringUtils.decodeBase64(code).split(":"))
        );
        var player = nameAndCode.getFirst();
        var clearcode = nameAndCode.getLast();

        return reception.cofirmRegistration(userId, player, clearcode);
    }
}
