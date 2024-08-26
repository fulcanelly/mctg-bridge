package me.fulcanelly.tgbridge.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Setting<T> {

    @Setter @Getter
    T value;
    final T original;

    void reset() {
        value = original;
    }

}
