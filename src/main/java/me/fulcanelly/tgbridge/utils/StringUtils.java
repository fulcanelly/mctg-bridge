package me.fulcanelly.tgbridge.utils;

import java.util.Base64;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public String encodeBase64(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes()));
    }

    public String decodeBase64(String text) {
        return new String(Base64.getDecoder().decode(text.getBytes()));
    }

}