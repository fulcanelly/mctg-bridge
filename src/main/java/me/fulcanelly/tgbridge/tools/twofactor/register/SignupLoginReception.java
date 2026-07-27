package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.MainConfig;

public class SignupLoginReception {

    private static final int CODE_LENGTH = 5;
    private static final int CODE_GENERATION_ATTEMPTS = 10;
    
    @Inject 
    RegisterDatabaseManager rgdb;

    @Inject 
    AccountDatabaseManager acdb;

    @Inject
    MainConfig config;

    private String generateSecretCode() {
        var num = Integer.toHexString(ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE));
        return String.format("%" + CODE_LENGTH + "s", num).replace(' ', '0').substring(0, CODE_LENGTH);
    }

    public Optional<String> requestRegistrationCodeFor(String player) {
        if (getTgByUser(player).isPresent()) {
            return Optional.empty();
        }

        for (int attempt = 0; attempt < CODE_GENERATION_ATTEMPTS; attempt++) {
            var code = generateSecretCode();
            if (rgdb.insertNewIfCodeIsFree(player, code)) {
                return Optional.of(code);
            }
        }

        return Optional.empty();
    }

    public RegistrationResult confirmRegistration(long userId, String code) {
        var player = rgdb.getPlayerByCode(code);
        if (player.isEmpty()) {
            return RegistrationResult.codeNotFound();
        }

        int maxAccounts = config.getMaxMcAccountsPerTg();
        if (maxAccounts > 0 && acdb.countUsernamesByTg(userId) >= maxAccounts) {
            return RegistrationResult.tooManyAccounts();
        }

        rgdb.delete(code);
        acdb.insertNew(userId, player.get());
        return RegistrationResult.success(player.get());
    }

    public Optional<String> getPlayerByTg(long userId) {
        return acdb.getUsernameByTg(userId);
    }

    public Optional<Long> getTgByUser(String user) {
        return acdb.getTgByUsername(user);
    }



}
