package me.fulcanelly;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tools.command.mc.CommandProcessor;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.*;

import me.fulcanelly.tgbridge.tools.command.mc.parser.ArgumentBuilder;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.EnumeratedCommandBuilder.*;

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class CommandProcessorSuggestionsTest extends BaseTest {

    @Inject
    CommandProcessor commandProcessor;

    @Provides
    @Singleton
    CommandSchema obtainCommandSchema() {
        return create()
                .setName("tg")
                .addCommand(
                        named("a")
                                .addCommand(
                                        named("func")
                                                .addArgument(ArgumentBuilder.create().setName("name"))
                                                .addArgument(ArgumentBuilder.create().setName("age")))
                                .addCommand(named("c").addCommand(named("l")))
                                .addCommand(named("d")),
                        named("chat")
                                .addCommand(
                                        enumerated("show", "hide").done()),
                        named("account")
                                .addCommand(named("register")))
                .done();
    }

    @Override
    Module getModule() {
        return this;
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
                Set.of("a", "chat", "account"),
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

    @Test() @Disabled
    void testBrokenCommandFirstMatchWithSpace() {
        var suggestion = obtainUnorderedHintFor("ch", "ch");
        assertEquals(
                "Must suggest nothing",
                Set.of(),
                suggestion);
    }

    @Test @Disabled
    void testBrokenCommandWithSpace() {
        var suggestion = obtainUnorderedHintFor("sdfksd9349543", "ch");
        assertEquals(
                "Must suggest nothing",
                Set.of(),
                suggestion);
    }

    @Test
    void testDeepSuggest() {
        var suggestion = obtainUnorderedHintFor("tg", "a", "c");
        assertEquals(
                "Must suggest l",
                Set.of("l"),
                suggestion);
    }

    @Test
    void testArgumentSuggest() {
        var suggestion = obtainUnorderedHintFor("tg", "a", "func");
        assertEquals(
                "Must suggest l",
                Set.of("name:", "age:"),
                suggestion);
    }

    @Test @Disabled
    void testArgumentHalsWritten()  {
        var suggestion = obtainUnorderedHintFor("tg", "a", "func", "a");
        assertEquals(
                "Must suggest relevant: 'age:'",
                Set.of("age:"),
                suggestion);
    }

}
