package me.fulcanelly.tgbridge.utils.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import lombok.SneakyThrows;
//todo

public class ConfigManager<T> {

    ArrayList<Field> fields = new ArrayList<>();
    Map<Object, Object> middle_table;
    JSONObject data;
    T instance;
    File file;
    
    @SneakyThrows
    public ConfigManager(T config, File path) {
        Class<?> klass = config.getClass();
        ConfigFile cfile = klass.getAnnotation(ConfigFile.class);
        
        if (cfile == null) {
            throw new RuntimeException("Wrong class");
        }

        instance = config;

        if (path == null) {
            file = new File(cfile.file());
        } else {
            file = new File(path, cfile.file());
        }

        for (Field field: klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Saveable.class)) {
                fields.add(field);
                field.setAccessible(true);
            }
        }
    }

    Runnable on_absent = null;

    public void setOnAbsent(Runnable oa) {
        this.on_absent = oa;
    }

    @SneakyThrows
    public T load() {
        if (!file.exists() && on_absent != null) {
            System.out.println("on_absent called");
            on_absent.run();
        }

        FileReader reader = new FileReader(file);
        data = (JSONObject)new JSONParser().parse(reader);
        fields.forEach(this::fieldSetter);
        save();
        return instance;
    }

    @SneakyThrows
    void fieldSetter(Field field) {
        String name = field.getName();
        Object value = data.get(name);

        if (value != null) {
            field.set(instance, value);
        }
    }

    @SneakyThrows
    void fieldGetter(Field field) {
        String name = field.getName();
        middle_table.put(name, field.get(instance));
    }

    @SneakyThrows
    public void save() {
        middle_table = new HashMap<>();
        fields.forEach(this::fieldGetter);
        data = new JSONObject(middle_table);
        middle_table = null;
        PrintWriter pw = new PrintWriter(file);
        pw.write(data.toJSONString());
        pw.flush();
        pw.close();
    }
}