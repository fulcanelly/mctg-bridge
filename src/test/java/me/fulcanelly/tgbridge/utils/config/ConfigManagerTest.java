package me.fulcanelly.tgbridge.utils.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import me.fulcanelly.tgbridge.utils.config.annotations.ConfigFile;
import me.fulcanelly.tgbridge.utils.config.annotations.Nullable;
import me.fulcanelly.tgbridge.utils.config.annotations.Optional;
import me.fulcanelly.tgbridge.utils.config.annotations.Saveable;

public class ConfigManagerTest {

    @TempDir
    Path tempDir;

    @ConfigFile(file = "config.yml")
    public static class TestConfig {

        @Saveable
        public String required;

        @Saveable @Nullable @Optional
        public String language;

        @Saveable @Nullable @Optional
        public Map<String, Map<String, String>> registration_messages;
    }

    @Test
    void loadMergesMissingTopLevelAndNestedResourceDefaults() throws Exception {
        Files.writeString(
            tempDir.resolve("config.yml"),
            """
            required: present
            registration_messages:
              eng:
                already_bound: custom
            """,
            UTF_8
        );

        var resource = """
            required: default
            language: eng
            registration_messages:
              eng:
                already_bound: default
                signup_success: ok
              ru:
                signup_success: да
            """;
        var plugin = pluginWithConfigResource(resource);

        var config = new ConfigManager<>(new TestConfig(), plugin).load();

        assertEquals("present", config.required);
        assertEquals("eng", config.language);
        assertEquals("custom", config.registration_messages.get("eng").get("already_bound"));
        assertEquals("ok", config.registration_messages.get("eng").get("signup_success"));
        assertEquals("да", config.registration_messages.get("ru").get("signup_success"));

        var saved = Files.readString(tempDir.resolve("config.yml"), UTF_8);
        assertTrue(saved.contains("language: eng"));
        assertTrue(saved.contains("signup_success"));
    }

    private Plugin pluginWithConfigResource(String resource) {
        return (Plugin) Proxy.newProxyInstance(
            Plugin.class.getClassLoader(),
            new Class<?>[] { Plugin.class },
            (proxy, method, args) -> switch (method.getName()) {
                case "getDataFolder" -> tempDir.toFile();
                case "getResource" -> new ByteArrayInputStream(resource.getBytes(UTF_8));
                default -> throw new UnsupportedOperationException(method.getName());
            }
        );
    }
}
