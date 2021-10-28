package me.fulcanelly.tgbridge.tools.hooks;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.fulcanelly.insyscore.InviteSysCore;
import me.fulcanelly.insyscore.database.InvitationsDatabase;
import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tools.command.tg.InvitePersonCommand;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class InviteSystemHook implements ForeignPluginHook {

    @Inject
    Plugin basePlugin;

    @Inject
    CommandManager cManager;

    @Inject 
    SignupLoginReception reception;

    LazyValue<Plugin> lazyPlugin = LazyValue.of(this::getPluginInstance);


    Plugin getPluginInstance() {
        return basePlugin.getServer()
            .getPluginManager().getPlugin("InviteSystemCore");
    }

    @Override
    public boolean isAvailable() {
        var plugin = lazyPlugin.get();

        if (plugin == null) {
            return false;
        }

        if (plugin instanceof InviteSysCore) {
            return true;
        }
        
        return false;
    }

    @Override
    public void setup() {
        InviteSysCore plugin = (InviteSysCore) lazyPlugin.get();

        new InvitePersonCommand(
            plugin.getDatabase(), reception
        ).registerCommand(cManager);
    }
       
    
}
