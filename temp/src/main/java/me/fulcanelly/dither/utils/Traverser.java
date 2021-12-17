package me.fulcanelly.dither.utils;

import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Traverser {

    File path;

    public Traverser(File path) {
        this.path = path;
    }

    public void traverse(Consumer<File> consumer) {
        Stream.of(path.listFiles())
            .parallel()
            .forEach(consumer::accept);
    }

}