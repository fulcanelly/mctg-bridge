package me.fulcanelly.tgbridge.utils.databse.request;

import java.util.function.Consumer;

public interface AsyncRequest<T> extends Request {
    T waitForResult();

    default void onResult(Consumer<T> consumer) {
        consumer.accept(this.waitForResult());
    };
}