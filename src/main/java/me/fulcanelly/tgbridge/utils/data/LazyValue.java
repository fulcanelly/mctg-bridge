package me.fulcanelly.tgbridge.utils.data;

import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

public class LazyValue<T> {
    
    Supplier<T> supplier;

    T obtained; 


    LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }


    public static <R>LazyValue<R> of(R value) {
        return new LazyValue<R>(() -> value);
    }


    public static <R>LazyValue<R> of(Supplier<R> supplier) {
        return new LazyValue<R>(supplier);
    }

    public synchronized T get() {
        if (supplier == null) {
            return obtained;
        } else {
            obtained = supplier.get();
            supplier = null;
            return obtained;
        }
    }
}
