package me.fulcanelly;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import me.fulcanelly.tgbridge.TelegramBridge;

public class BaseTest {
    
    @Test
    public void test() {
        TelegramBridge bridge = mock(TelegramBridge.class);
        System.out.println("work!");
    }
}
