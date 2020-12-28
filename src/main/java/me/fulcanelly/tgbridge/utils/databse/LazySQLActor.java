package me.fulcanelly.tgbridge.utils.databse;

import lombok.SneakyThrows;
import me.fulcanelly.tgbridge.utils.databse.request.AsyncRequest;
import me.fulcanelly.tgbridge.utils.databse.request.Request;
import me.fulcanelly.tgbridge.utils.databse.request.SpecificAsyncRequst;
import me.fulcanelly.tgbridge.utils.databse.request.VoidRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.ResultSet;

public class LazySQLActor extends Thread {

    final QueryHandler qhandler;  
    final BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    public LazySQLActor(QueryHandler handler) {
        this.qhandler = handler;
        start();
    }

    public void execute(String query, Object ...args) {
        queue.add(new VoidRequest(query, args, qhandler::execute));
    }

    public AsyncRequest<ResultSet> executeQuery(String query, Object ...args) {        
        var it = new SpecificAsyncRequst<>(query, args, qhandler::executeQuery);
        queue.add(it);
        return it;
    }

    @SneakyThrows
    void requestConsumer() {
        Request last = queue.take();
        last.execute();
    }

    public void run() {
        while (true) {
            requestConsumer();
        }
    }
    
}