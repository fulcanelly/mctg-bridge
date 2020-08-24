package me.fulcanelly.tgbridge.utils.config;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
    String file();
}