package me.fulcanelly.tgbridge.utils.analyst;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.tapi.Message;

@RequiredArgsConstructor
public class ConstantMessageEditor {

    List<Runnable> forRun = new LinkedList<>();
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    int maxEditableMessages = 3;

    public void addToQueueMesaggeAndEditor(Message message, Supplier<String> supplier) {

        forRun.add(
            () -> message.edit(supplier.get())
        );

        if (forRun.size() > maxEditableMessages) {
            forRun = forRun.subList(1, forRun.size());
        }
    }

    int whichTurn = 0;

    public void start() {
        service.scheduleAtFixedRate(this::eachInterval, 0, 1500, TimeUnit.MILLISECONDS);
    }

    void safeRun(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int errorTimeout = 250;
    boolean timeoutEnabled = false;

    @SneakyThrows
    protected void eachInterval() {

        if (forRun.size() < 1 ) {
            return;
        }

        if (timeoutEnabled) {
            Thread.sleep(errorTimeout);
            errorTimeout = errorTimeout / 2;
            if (errorTimeout <= 250) {
                timeoutEnabled = false;
            }
        }

        try {
            forRun.get(whichTurn++ % forRun.size()).run();
        } catch (Exception e) {
       //     e.printStackTrace();
            errorTimeout = errorTimeout * 2;
            timeoutEnabled = true;
        }
    }
}
