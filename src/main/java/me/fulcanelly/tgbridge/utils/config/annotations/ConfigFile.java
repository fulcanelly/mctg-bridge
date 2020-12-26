package me.fulcanelly.tgbridge.utils.config.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
    String file();
}