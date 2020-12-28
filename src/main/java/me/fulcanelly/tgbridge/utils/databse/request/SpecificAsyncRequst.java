package me.fulcanelly.tgbridge.utils.databse.request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lombok.SneakyThrows;

public class SpecificAsyncRequst<T> extends GenericRequst<T> implements AsyncRequest<T> {

    final BlockingQueue<T> result = new ArrayBlockingQueue<>(1);

    public SpecificAsyncRequst(String query, Object[] args, RequestExecutor<T> handler) {
        super(query, args, handler);
    }

    public  void execute() {
        result.add(super.run());
    }
    
    @SneakyThrows
    public T waitForResult() {
        return result.take();
    }

}