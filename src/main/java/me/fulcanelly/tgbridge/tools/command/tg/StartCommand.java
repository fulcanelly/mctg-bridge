package me.fulcanelly.tgbridge.tools.command.tg;

import org.bukkit.Bukkit;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.tg.base.CommandRegister;
import me.fulcanelly.tgbridge.tools.command.tg.base.ReplierBuilder;
import me.fulcanelly.tgbridge.tools.twofactor.BotUIReception;
import me.fulcanelly.tgbridge.tools.twofactor.RegistrationMessageLocalizer;
import me.fulcanelly.tgbridge.tools.twofactor.register.RegistrationResult;

public class StartCommand implements CommandRegister {

    @Inject
    BotUIReception reception;

    @Inject
    RegistrationMessageLocalizer messages;

    @Inject
    void registerPrivateCodeListener(EventBus eventBus) {
        eventBus.register(this);
    }

    String onStartCommand(CommandEvent event) {
        var args = event.getArgs();
        if (args.size() == 1) {
            var tgUserId = event.getMessage().getFrom().getId();
            var code = args.get(0);

            return completeSignup(reception.onPrivateStartCommand(tgUserId, code), messages.englishPlain("signup_failed"));
        }

        return "hm?";
    }

    @Subscribe
    public void onPrivateCodeMessage(Message message) {
        var text = message.getText();
        if (text == null || text.startsWith("/") || !message.getChat().isPrivate()) {
            return;
        }

        var tgUserId = message.getFrom().getId();
        message.reply(completeSignup(reception.onPrivateCodeMessage(tgUserId, text), "hm?"));
    }

    private String completeSignup(RegistrationResult result, String fallback) {
        if (result.status() == RegistrationResult.Status.TOO_MANY_ACCOUNTS) {
            return messages.englishPlain("too_many_accounts");
        }

        if (result.status() != RegistrationResult.Status.SUCCESS) {
            return fallback;
        }

        result.player().stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null && player.isOnline())
                .forEach(player -> player.sendMessage(messages.format(player.getLocale(), "signup_success")));

        return messages.englishPlain("signup_success");
    }

    @Override
    public void registerCommand(CommandManager manager) {
        new ReplierBuilder("start", this::onStartCommand).registerCommand(manager);
    }

}
