package me.fulcanelly.tgbridge.utils.databse.request;

public class VoidRequest extends GenericRequst<Object> {
    public VoidRequest(String query, Object[] args, RequestExecutor<Object> handler) {
        super(query, args, handler);
    }
}