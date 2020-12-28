package me.fulcanelly.tgbridge.tools;

import me.fulcanelly.tgbridge.utils.config.annotations.ConfigFile;
import me.fulcanelly.tgbridge.utils.config.annotations.Optional;
import me.fulcanelly.tgbridge.utils.config.annotations.Saveable;
import me.fulcanelly.tgbridge.utils.config.annotations.Nullable;

@ConfigFile(file = "config.yml")
public class MainConfig {
    
    @Saveable
    public String api_token;

    @Saveable @Nullable
    public String chat_id;

    @Saveable
    public Boolean login_manger = false;

    @Saveable @Nullable @Optional
    public String test_field = "works";
    
}