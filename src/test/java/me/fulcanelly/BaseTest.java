package me.fulcanelly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import me.fulcanelly.tgbridge.TelegramModule;
import me.fulcanelly.tgbridge.tools.twofactor.register.AccountDatabaseManager;
import me.fulcanelly.tgbridge.tools.twofactor.register.RegisterDatabaseManager;
import me.fulcanelly.tgbridge.tools.twofactor.register.SignupLoginReception;
import me.fulcanelly.tgbridge.utils.data.LazyValue;

public class BaseTest {
    

    static Injector obtainInjector() {
        var pluginMock = mock(Plugin.class);

        doReturn(mock(Logger.class)).when(pluginMock).getLogger();

        doReturn(new File(".")).when(pluginMock).getDataFolder();

        return Guice.createInjector(new TelegramModule(pluginMock));        
    }

    static RegisterDatabaseManager getDatabaseInstance() {
        return injector.get().getInstance(RegisterDatabaseManager.class);
    }

    static LazyValue<RegisterDatabaseManager> regdb = LazyValue.of(BaseTest::getDatabaseInstance);

    static LazyValue<Injector> injector = LazyValue.of(BaseTest::obtainInjector);

    static LazyValue<AccountDatabaseManager> accdb = LazyValue.of(() -> injector.get().getInstance(AccountDatabaseManager.class));

    static LazyValue<SignupLoginReception> signup = LazyValue.of(() ->  injector.get().getInstance(SignupLoginReception.class));
    
    @Test
    public synchronized void tableCreationTest() {
        regdb.get().setupTable();
    }

    @Test 
    public synchronized void checkAccountDatabase() {
        var aas = accdb.get();
    }   

    @Test 
    public synchronized void acregtest() {
        var reception = signup.get();
        var code = reception.requestRegistrationCodeFor("player");
        System.out.println("got code " + code);
        var result = reception.cofirmRegistration(123, "player", code.get());
        System.out.println(result);
    }

    @Test
    public synchronized void checkRegisterValidity() {
        var rdb = regdb.get();

        rdb.insertNew("lol", "131");
        rdb.insertNew("lol", "132");

        assertEquals(true, rdb.isValidCode("lol", "132"));
        assertEquals(true, rdb.isValidCode("lol", "131"));
        assertEquals(false, rdb.isValidCode("lol", "13221312"));   
    }


    
}
