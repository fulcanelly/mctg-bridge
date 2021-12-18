package me.fulcanelly.tgbridge.tapi;

import org.json.simple.JSONObject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TgFile {

    JSONObject object;

    public String getFilePath() {
        return (String) object.get("file_path");
    }
}