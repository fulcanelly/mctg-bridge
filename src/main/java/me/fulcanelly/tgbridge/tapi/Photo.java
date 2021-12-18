package me.fulcanelly.tgbridge.tapi;

import org.json.simple.JSONObject;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


@AllArgsConstructor
public class Photo {

    JSONObject object;

    public String getFileId() {
        return (String)object.get("file_id"); 
    }

    @SneakyThrows
    public BufferedImage load(TGBot bot) {
        var file = bot.getFile(this.getFileId());
        return ImageIO.read(
            bot.loadFile(file.get().getFilePath())
        );
    }
}
