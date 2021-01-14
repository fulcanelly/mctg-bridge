package me.fulcanelly.tgbridge.utils.async.tasks;

import java.util.function.Consumer;

public class VoidAwaitRequest<T> implements Task {

    final AsyncTask<T> data;
    final Consumer<T> consumer;

    VoidAwaitRequest(AsyncTask<T> data, Consumer<T> consumer) {
        this.data = data;
        this.consumer = consumer;
    }

    @Override
    public void execute() {
        consumer.accept(data.waitForResult());
    }
    
}
