package me.fulcanelly.tgbridge.utils.databse.request;

public interface RequestExecutor<T> {
    T process(String q, Object[] data);
}

