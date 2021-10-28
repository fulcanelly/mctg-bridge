package me.fulcanelly.tgbridge.tools.hooks;

import com.google.inject.Inject;

import org.bukkit.plugin.Plugin;

import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class LoginSecurityHook implements ForeignPluginHook {

    @Inject
    Plugin basePlugin;

    LazyValue<Plugin> lazyPlugin = LazyValue.of(this::getPluginInstance);

    Plugin getPluginInstance() {
        return basePlugin.getServer()
            .getPluginManager().getPlugin("LoginSecurity");
    }

    @Override
    public boolean isAvailable() {
        // TODO Auto-generated method stub
        return lazyPlugin.get() != null;
    }

    @Override
    public void setup() {
        lazyPlugin.get();
        // TODO Auto-generated method stub
        
    }
    
    
}
