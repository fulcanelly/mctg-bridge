package me.fulcanelly;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;

import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;
import me.fulcanelly.tgbridge.tools.stats.StatCollector;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.config.ConfigManager;
import me.fulcanelly.tgbridge.utils.database.SqliteConnectionProvider;
import me.fulcanelly.tgbridge.utils.events.pipe.EventPipe;
import me.fulcanelly.tgbridge.view.NamedTabExecutor;
import static me.fulcanelly.tgbridge.tools.command.mc.parser.CommandBuilder.*;

import me.fulcanelly.tgbridge.tools.command.mc.parser.ArgumentBuilder;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandParser;
import me.fulcanelly.tgbridge.tools.command.mc.parser.CommandSchema;
import me.fulcanelly.tgbridge.tools.mastery.ChatSettings;

import static me.fulcanelly.tgbridge.tools.command.mc.parser.EnumeratedCommandBuilder.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;

import me.fulcanelly.tgbridge.tools.twofactor.InGameReceptionUI;
import me.fulcanelly.tgbridge.utils.events.pipe.Listener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class ParserTest {
    
    CommandSchema providesDefaultSchema() {

        return create()
            .addCommand(
                named("chat")
                    .setDescription("controls telegram chat visibility")
                    .addCommand(
                        enumerated("show", "hide")
                            .setExecutor(args -> { 
                                System.out.println("show hide ok");
                            })
                            .done()
                    ),
                named("account")
                    .setDescription("controls telegram account")
                    .addCommand(
                        named("register")
                            .setExecutor(args -> {
                                System.out.println("register ok");
                            })
                    )
            )
            .generateHelpPage()
            .done();
    }
    

    LinkedList<String> makeList(String ... args) {
        return new LinkedList<>(List.of(args));
    }

    CommandSender mockSender() {
        var sendermock = mock(CommandSender.class);
        //doAnswer(an -> null).
        doAnswer(an -> {
            System.out.println(an.toString());
            return an;
        }).when(sendermock).sendMessage(any(String.class));
        return sendermock;
    }


    CommandParser getParser(String ...args) {
        return new CommandParser(providesDefaultSchema(), makeList(args),  mockSender());
    }

    @Test   
    public void testCaseOne() {
        getParser("chat", "show").evaluate();
        System.out.println(getParser("chat").parse().getExpected());
        System.out.println(getParser("cha").parse().getExpected());
        System.out.println(getParser("chat", "show").parse().getExpected());
        System.out.println(getParser().parse().getExpected());

    }
}
