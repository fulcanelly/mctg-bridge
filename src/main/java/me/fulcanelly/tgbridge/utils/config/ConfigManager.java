package me.fulcanelly.tgbridge.utils.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.*;

import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import lombok.SneakyThrows;

import me.fulcanelly.tgbridge.utils.config.annotations.ConfigFile;
import me.fulcanelly.tgbridge.utils.config.annotations.Nullable;
import me.fulcanelly.tgbridge.utils.config.annotations.Optional;
import me.fulcanelly.tgbridge.utils.config.annotations.Saveable;


public class ConfigManager<T> {

    List<FieldWrapper> fields;
    Map<String, Object> data = new HashMap<>();
    T instance;
    File file;
  
    public T getConfig() {
        return instance;
    }

    Runnable on_absent;
    Yaml yaml;

    class FieldWrapper {

        Field field;
        boolean optional;
        boolean nullable;
    
        FieldWrapper(Field field) {
            this.field = field;
            field.setAccessible(true);
            
            nullable = field.isAnnotationPresent(Nullable.class);
            optional = field.isAnnotationPresent(Optional.class);
        }
    
        Field unwrap() {
            return field; 
        }

        @SneakyThrows
        Object getFromInstance() {
            return field.get(instance);
        }

        @SneakyThrows
        void setInInstance(Object value) {
            field.set(instance, value);
        } 

        Object getFromData() {
            String name = field.getName();
            return data.get(name);
        }

        boolean isSetInData() {
            return data.containsKey(field.getName());
        }

        void setInData(Object for_set) {
            data.put(field.getName(), for_set);
        }

    }

    File findOrLoadFromResource(Plugin plugin, String fileName) {
        var file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        return file;
    }

    Yaml setUpYaml() {
        DumperOptions options = new DumperOptions();

        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        
        return new Yaml(options);
    }
    
    @SneakyThrows
    public ConfigManager(T config, Plugin plugin) {      
        instance = config;
  
        yaml = setUpYaml();

        Class<?> klass = config.getClass();
        ConfigFile cfile = klass.getAnnotation(ConfigFile.class);
        
        if (cfile == null) {
            throw new RuntimeException("Wrong class");
        }

        file = findOrLoadFromResource(plugin, cfile.file());

        fields = Stream.of(klass.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Saveable.class))
            .map(field -> new FieldWrapper(field))
            .collect(Collectors.toList());
    }

    public void setOnFileAbsence(Runnable oa) {
        this.on_absent = oa;
    }

    @SneakyThrows
    public T load() {

        if (!file.exists() && on_absent != null) {
            on_absent.run();
        }

        data = yaml.load(new FileReader(file));

        fields.forEach(this::fieldSetter);
        return instance;
    }

    void error(String template, Object... data) {
        throw new RuntimeException(
            String.format(template, data)
        );
    }

    @SneakyThrows
    void fieldSetter(FieldWrapper field) {
        Object value = field.getFromData();
        String name = field.unwrap().getName();

        if (!data.containsKey(name) && !field.optional) {
            error("%s file don't contains variable variable %s", file.toString(), name);
        }

        if (value != null) {
            field.setInInstance(value);
        } else if (!field.nullable) {
            error("variable %s in %s file is set to null", name, file.toString());
        }
    }

    @SneakyThrows
    void fieldGetter(FieldWrapper field) {
        field.setInData(field.getFromInstance());
    }

    @SneakyThrows
    public void save() {
        fields.forEach(this::fieldGetter);
        yaml.dump(data, new FileWriter(file));
    }

}
