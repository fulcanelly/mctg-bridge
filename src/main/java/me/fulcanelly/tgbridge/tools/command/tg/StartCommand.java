package me.fulcanelly.tgbridge.tools.command.tg;

import java.util.Optional;

import org.bukkit.Bukkit;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.BotUIReception;
import me.fulcanelly.tgbridge.tools.twofactor.RegistrationMessageLocalizer;

public class StartCommand implements CommandRegister {

    @Inject
    BotUIReception reception;

    @Inject
    RegistrationMessageLocalizer messages;

    String onStartCommand(CommandEvent event) {
        var args = event.getArgs();
        if (args.size() == 1) {
            var tgUserId = event.getMessage().getFrom().getId();
            var code = args.get(0);

            return reception.onPrivateStartCommand(tgUserId, code)
                    .stream()
                    .map(playerName -> {
                        Optional.ofNullable(Bukkit.getPlayer(playerName))
                                .ifPresent(player -> player
                                        .sendMessage(messages.color(player.getLocale(), "signup_success")));

                        return messages.englishPlain("signup_success");
                    })
                    .findFirst()
                    .orElse(messages.englishPlain("signup_failed"));
        }

        return "hm?";
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("start", this::onStartCommand).registerCommand(manager);
    }

}
