package me.fulcanelly.tgbridge.tools;

import java.util.Random;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.md_5.bungee.api.ChatColor;

public class SecretCodeMediator {

    int secretTempCode;
    Logger logger;

    @Inject
    public SecretCodeMediator(Logger logger) {
        this.logger = logger;
    }

    Random random = new Random();

    public synchronized int generateSecretTempCode() {
        secretTempCode = random.nextInt() % 100000;
        logger.info( 
            ChatColor.GREEN + "secretTempCode is set to " + secretTempCode);
        return secretTempCode;
    }
    
    public boolean isSecretCodeMatch(int code) {
        return secretTempCode == code;
    } 
    
}