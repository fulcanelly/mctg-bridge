package me.fulcanelly.tgbridge.tools.hooks.invites;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.fulcanelly.tgbridge.tapi.CommandManager;
import me.fulcanelly.tgbridge.tools.command.tg.InvitePersonCommand;
import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class InviteSystemFacade implements ForeignPluginHook {

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
        return (plugin != null);
    }

    @Override
    public void setup() {
        new InviteSystemHook(lazyPlugin.get(), cManager, reception)
            .start();
    }
       
    
}
