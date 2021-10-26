package me.fulcanelly.tgbridge.tools.twofactor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.bukkit.Color;
import org.bukkit.entity.Player;

import lombok.RequiredArgsConstructor;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.StringUtils;
import me.fulcanelly.tgbridge.utils.data.LazyValue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class InGameReceptionUI {
    
    @Inject
    SignupLoginReception reception;

    LazyValue<String> botname;

    @Inject
    void inject(TGBot bot) {
        botname = LazyValue.of(bot.getMe().getUsername());
    }

    public void onPlayerRegisterRequest(Player player) {
        var code = reception.requestRegistrationCodeFor(player.getName());

        if (code.isEmpty()) {
            player.sendMessage("It's seems like you can be registered, may be you already are.");
            return;
        }

        var fullcode = StringUtils.encodeBase64(player.getName() + ":" + code.get());


        TextComponent comp = new TextComponent();

        comp.addExtra("to end registration follow ");


        var link = new TextComponent(ChatColor.RED + "this");
        
        link.setClickEvent(
            new ClickEvent(
                ClickEvent.Action.OPEN_URL, 
                String.format(
                    "https://t.me/%s?start=%s", botname.get(), fullcode)
            )
        );
     
        link.setHoverEvent(
            new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, new Text("telegram - https://t.me/%s?start=%s")
            )
        );
        
        comp.addExtra(
            link
        );

        comp.addExtra(
            ChatColor.RESET + " link, don't share it to nobody"
        );


        player.spigot().sendMessage(comp);
    }

}
