package me.fulcanelly.tgbridge.utils.async;

import me.fulcanelly.tgbridge.utils.async.tasks.*;


public class AsyncActorEngine extends ActorTemplate<Task> {
    
    public AsyncActorEngine() {
        this.start();
    }

    public void addRequest(Task req) {
        addOne(req);
    }

    @Override
    public void consume(Task task) {
        task.execute();
    }

    
}