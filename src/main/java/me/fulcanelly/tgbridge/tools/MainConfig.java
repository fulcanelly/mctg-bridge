package me.fulcanelly.tgbridge.tools;

import lombok.ToString;
import me.fulcanelly.tgbridge.utils.config.annotations.ConfigFile;
import me.fulcanelly.tgbridge.utils.config.annotations.Optional;
import me.fulcanelly.tgbridge.utils.config.annotations.Saveable;
import me.fulcanelly.tgbridge.utils.config.annotations.Nullable;

@ConfigFile(file = "config.yml") @ToString
public class MainConfig {

    @Saveable
    public String api_token;

    @Saveable @Nullable
    public String chat_id;

    @Saveable
    public Boolean login_manger = false;

    @Saveable @Nullable @Optional
    public String test_field = "works";

    @Saveable
    public Boolean log_status;

    @Saveable
    public Boolean enable_chat = true;

    @Saveable @Nullable
    public String ngrok_auth;

    public String getNgrokAuthToken() {
        return ngrok_auth;
    }

    public String getApiToken() {
        return api_token;
    }

    public String getChatId() {
        return chat_id;
    }

    public boolean isLoginManagerEnabled() {
        return login_manger;
    }

    public <T>void setChatId(T chat_id) {
        this.chat_id = chat_id.toString();
    }

}
