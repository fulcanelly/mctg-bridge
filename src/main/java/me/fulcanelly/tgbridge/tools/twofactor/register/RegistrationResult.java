package me.fulcanelly.tgbridge.tools.twofactor.register;

import java.util.Optional;

public record RegistrationResult(Status status, String playerName) {

    public enum Status {
        SUCCESS,
        CODE_NOT_FOUND,
        TOO_MANY_ACCOUNTS
    }

    public static RegistrationResult success(String playerName) {
        return new RegistrationResult(Status.SUCCESS, playerName);
    }

    public static RegistrationResult codeNotFound() {
        return new RegistrationResult(Status.CODE_NOT_FOUND, null);
    }

    public static RegistrationResult tooManyAccounts() {
        return new RegistrationResult(Status.TOO_MANY_ACCOUNTS, null);
    }

    public Optional<String> player() {
        return Optional.ofNullable(playerName);
    }
}
