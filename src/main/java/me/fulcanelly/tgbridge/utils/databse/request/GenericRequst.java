package me.fulcanelly.tgbridge.utils.databse.request;

public class GenericRequst<T> implements Request {
    final Object[] args;
    final String query;

    final RequestExecutor<T> handler;

    public GenericRequst(String query, Object[] args, RequestExecutor<T> handler) {
        this.args = args;
        this.query = query;
        this.handler = handler;
    }

    T run() {
        return handler.process(query, args);
    }

    public void execute() {
        run();
    }

}