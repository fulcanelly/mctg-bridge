package me.fulcanelly.tgbridge.utils.reflect;

import java.lang.reflect.Field;
import java.util.Map;
import lombok.SneakyThrows;

public class ObjectMapper {

    @SneakyThrows
	public <T> T convertValue(Map<String, Object> map, Class<T> klass) {
        T instance = klass.getConstructor().newInstance();
        for (Field field : klass.getDeclaredFields()) {
            String name = field.getName();
            if (map.containsKey(name)) {
                field.set(instance, map.get(name));
            } else {
                throw new RuntimeException("redundand field");
            }
        }
    
		return instance;
	}
    
}
