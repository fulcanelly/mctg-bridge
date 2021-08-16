package me.fulcanelly;

import static org.mockito.Mockito.doAnswer;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.mockito.Mockito.*;

import org.junit.Before;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.tapi.Message;
import me.fulcanelly.tgbridge.tapi.TGBot;
import me.fulcanelly.tgbridge.tools.compact.MessageCompactableSender;

public class MessageCompactorTest {
    
    
    TGBot botmock = mock(TGBot.class);
    long chat_id = 2415;


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    MessageCompactableSender mcp = new MessageCompactableSender(botmock, chat_id);;

    static <T> T profile(InvocationOnMock invocation) {
        
        System.out.printf("call %s%s with arguments %s\n\n", ANSI_GREEN + invocation.getMethod().getName(), ANSI_RESET, ANSI_PURPLE + List.of(invocation.getArguments()));
        System.out.println(ANSI_RESET);
        return null;
    }

    void a() {
        System.out.println("a");
    }

    void b() {
        System.out.println("b");
    }

   
    {

        var profile = Optional.<Function<InvocationOnMock, ?>>of(MessageCompactorTest::profile).get();
        
        Answer<Message> messageSupplierTracer = invoc -> 
            profile.andThen(it -> mock(Message.class))
                .apply(invoc);

        doAnswer(messageSupplierTracer).when(botmock)
            .sendMessage(any(), any());
        
        doAnswer(messageSupplierTracer).when(botmock)
            .sendMessage(any(), any(), any());

        doAnswer(messageSupplierTracer).when(botmock)
            .editMessage(any(), any(), any());

        System.out.println("ujj");
    }

    @Test
    public void testSimpleMerge() {
        System.out.println(mcp);

        mcp.sendAsPlayer("pida", "harukja");
        mcp.sendAsPlayer("pida", "asdf");
        
        StringJoiner joiner = new StringJoiner("\n\n");
        System.out.println(mcp.getActualLast());
        joiner.add("*<pida>*\nharukja");
        joiner.add("asdf");

        var result = mcp.getLastSent()
            .orElseThrow()
            .getText();
        System.out.println(result);
        System.out.println(joiner.toString());

        assert joiner.toString().equals(result) : "strings don't match";
    }


    @Test 
    public void testInteruptedMerge() {
        mcp.setLastSent(Optional.empty());

        mcp.sendAsPlayer("pida", "harukja");
        mcp.setActualLast(-235213);
        mcp.sendAsPlayer("pida", "bhvhgvh");

        System.out.println( ANSI_RED +
            mcp.getLastSent()
                .orElseThrow()
                .getText()
        );
    }


    @Test
    public void testSimpleNoteMerge() {
        System.out.println(this);
        mcp.setLastSent(Optional.empty());
        for (int i = 0; i < 23; i++) {
          //  mcp.sendNote("dasd");
           // mcp.sendNote("dasddsfsd");
        }
         
   

    }


    @Test
    public void testNoteIntruption() {
        System.out.println(this);
        mcp.sendAsPlayer("pida", "ksdk");

      //  throw new RuntimeException("not implemented");
    }

    @Test @SneakyThrows
    public void testPause() {
        System.out.println(this);
        mcp.sendAsPlayer("pida", "jango");

        Thread.sleep(TimeUnit.SECONDS.toMillis(35));
        mcp.sendAsPlayer("pida", "zip zip adsf");

      //  throw new RuntimeException("not implemented");
    }

}
