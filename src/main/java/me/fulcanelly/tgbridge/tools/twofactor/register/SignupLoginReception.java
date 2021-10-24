package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import javax.management.RuntimeErrorException;

import com.google.inject.Inject;

import lombok.val;
import me.fulcanelly.tgbridge.tapi.Message;

public class SignupLoginReception {
    
    @Inject 
    RegisterDatabaseManager rgdb;

    @Inject 
    AccountDatabaseManager acdb;

    private String generateSecretCode() {
        var num = Integer.toHexString(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        return String.format("%8s", num).replace(' ', '0');
    }

    public Optional<String> requestRegistrationCodeFor(String player) {
        if (getTgByUser(player).isPresent()) {
            return Optional.empty();
        } else {
            var code = generateSecretCode();
            rgdb.insertNew(player, code);
            return Optional.of(code);
        }
    }

    public boolean cofirmRegistration(long userId, String player, String code) {  
        var valid = rgdb.isValidCode(player, code);
        if (valid) {
            rgdb.delete(player, code);
            acdb.insertNew(userId, player);
        };
        return valid;        
    }

    public Optional<String> getPlayerByTg(long userId) {
        return acdb.getUsernameByTg(userId);
    }

    public Optional<Long> getTgByUser(String user) {
        return acdb.getTgByUsername(user);
    }



}
