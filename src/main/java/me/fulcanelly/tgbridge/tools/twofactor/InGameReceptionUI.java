package me.fulcanelly.tgbridge.tools.twofactor;

import com.google.inject.Inject;
import org.bukkit.entity.Player;

import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.StringUtils;
import me.fulcanelly.tgbridge.utils.data.LazyValue;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class InGameReceptionUI {

    @Inject
    SignupLoginReception reception;

    @Inject
    RegistrationMessageLocalizer messages;

    LazyValue<String> botname;

    @Inject
    void inject(TGBot bot) {
        botname = LazyValue.of(bot.getMe().getUsername());
    }

    public void onPlayerRegisterRequest(Player player) {
        var code = reception.requestRegistrationCodeFor(player.getName());

        if (code.isEmpty()) {
            player.sendMessage(messages.color(player.getLocale(), "already_bound"));
            return;
        }

        var fullcode = StringUtils.encodeBase64(player.getName() + ":" + code.get());
        var url = String.format("https://t.me/%s?start=%s", botname.get(), fullcode);


        TextComponent comp = new TextComponent();

        comp.addExtra(messages.color(player.getLocale(), "register_prefix"));


        var link = new TextComponent(messages.color(player.getLocale(), "register_link"));

        link.setClickEvent(
            new ClickEvent(
                ClickEvent.Action.OPEN_URL,
                url
            )
        );

        link.setHoverEvent(
            new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new Text(messages.color(player.getLocale(), "link_hover", url))
            )
        );

        comp.addExtra(
            link
        );

        comp.addExtra(
            messages.color(player.getLocale(), "register_suffix")
        );


        player.spigot().sendMessage(comp);
    }

}
