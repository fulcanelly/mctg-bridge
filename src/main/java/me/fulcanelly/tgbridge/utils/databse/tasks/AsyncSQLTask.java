package me.fulcanelly.tgbridge.utils.databse.tasks;

import me.fulcanelly.tgbridge.utils.async.ActorTemplate;
import me.fulcanelly.tgbridge.utils.async.tasks.*;

public class AsyncSQLTask<T> extends ChainAsyncTask<T> {

    public AsyncSQLTask(String query, Object[] args, RequestExecutor<T> executor, ActorTemplate<Task> worker) {
        super(() -> executor.process(query, args), worker);
    }

}