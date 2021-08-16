package me.fulcanelly.tgbridge.tools;

public interface MessageSender {
    void sendAsPlayer(String from, String text);
    void sendNote(String text);
}
