package me.fulcanelly;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.mc.CommandProcessor;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.*;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.EnumeratedCommandBuilder.*;

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CommandProcessorSuggestionsTest extends BaseTest {

    @Inject
    CommandProcessor commandProcessor;

    @Override
    Module getModule() {
        return new AbstractModule() {
            @Provides
            @Singleton
            CommandSchema obtainCommandSchema() {
                return create()
                        .setName("tg")
                        .addCommand(
                                named("chat")
                                        .addCommand(
                                                enumerated("show", "hide").done()),
                                named("account")
                                        .addCommand(named("register")))
                        .done();
            }
        };
    }

    private Set<String> obtainUnorderedHintFor(String... arguments) {
        return commandProcessor.onTabComplete(null, null, null, arguments)
                .stream()
                .collect(Collectors.toSet());
    }

    @Test
    void testBaseSuggestion() {
        var suggestion = obtainUnorderedHintFor();
        assertEquals(
                "Must suggest 'chat', 'account'",
                Set.of("chat", "account"),
                suggestion);
    }

    @Test
    void testChildEnumSuggestion() {
        var suggestion = obtainUnorderedHintFor("chat");
        assertEquals(
                "Must suggest 'show', 'hide'",
                Set.of("show", "hide"),
                suggestion);
    }

    @Test
    void testEmptySuggestionInTheEnd() {
        var suggestion = obtainUnorderedHintFor("chat", "show");
        assertEquals(
                "Must no suggest",
                Set.of(),
                suggestion);
    }

    @Test
    void testHalfWritten() {
        var suggestion = obtainUnorderedHintFor("ch");
        assertEquals(
                "Must suggest relevant: 'chat'",
                Set.of("chat"),
                suggestion);
    }

    @Test()
    void testBrokenCommand() {
        var suggestion = obtainUnorderedHintFor("chr");
        assertEquals(
                "Must suggest nothing",
                Set.of(),
                suggestion);
    }

    @Test()
    void testBrokenCommandWithSpace() {
        var suggestion = obtainUnorderedHintFor("ch", "ch");
        assertEquals(
                "Must suggest nothing",
                Set.of(),
                suggestion);
    }
}
