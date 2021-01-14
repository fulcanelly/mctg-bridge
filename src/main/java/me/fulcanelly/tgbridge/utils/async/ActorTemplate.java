package me.fulcanelly.tgbridge.utils.async;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.utils.stop.*;

public abstract class ActorTemplate<T> extends Thread implements Stopable {

    @Override
    public void stopIt() {
        queue.add(StopSignalOrData.getStopSignal());
    }

    final BlockingQueue<StopSignalOrData<T>> queue = new LinkedBlockingQueue<>();

    abstract public void consume(T data);

    public void addOne(T data) {
        queue.add(new StopSignalOrData<>(data));
    }

    @SneakyThrows
    public void run() {
        while (true) {
            StopSignalOrData<T> last = queue.take();
            if (last.isSignal()) {
                return;
            } else {
                consume(last.get());
            }
        }
    }
    
}
