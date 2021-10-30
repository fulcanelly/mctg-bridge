package me.fulcanelly.tgbridge.tools.hooks.loginsec;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;

import me.fulcanelly.tgbridge.tapi.CommandManager;

import me.fulcanelly.tgbridge.tools.hooks.ForeignPluginHook;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class LoginSecurityFacade implements ForeignPluginHook {

    @Inject
    Plugin basePlugin;
    
    @Inject
    CommandManager cManager;

    @Inject 
    SignupLoginReception reception;

    LazyValue<Plugin> lazyPlugin = LazyValue.of(this::getPluginInstance);

    Plugin getPluginInstance() {
        return basePlugin.getServer()
            .getPluginManager().getPlugin("LoginSecurity");
    }

    @Override
    public boolean isAvailable() {
        System.out.println(lazyPlugin.get());
        return lazyPlugin.get() != null;
    }


    @Override
    public void setup() {
        new ActualLoginHook(reception, basePlugin, cManager).start();
    }
    
    
}
