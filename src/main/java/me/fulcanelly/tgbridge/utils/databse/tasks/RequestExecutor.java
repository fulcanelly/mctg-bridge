package me.fulcanelly.tgbridge.utils.databse.tasks;

public interface RequestExecutor<T> {
    T process(String q, Object[] data);
}

