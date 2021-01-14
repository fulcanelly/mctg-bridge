package me.fulcanelly.tgbridge.utils.async.tasks;

import java.util.function.Consumer;
import java.util.function.Function;

public interface AsyncTask<T> extends Task {
    T waitForResult();

    void andThenSilently(Consumer<T> consumer);
    
    <R>AsyncTask<R> andThen(Function<T, R> consumer);

 //   private AsyncRequest<T> addToQueue() {
    ///    worker.addOne(this);
      //  return this;
   // }
}