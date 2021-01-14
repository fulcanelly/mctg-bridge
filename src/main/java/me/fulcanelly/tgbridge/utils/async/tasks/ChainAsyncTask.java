package me.fulcanelly.tgbridge.utils.async.tasks;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


import me.fulcanelly.tgbridge.utils.async.ActorTemplate;
import me.fulcanelly.tgbridge.utils.container.BlockingVariable;

public class ChainAsyncTask<K> implements AsyncTask<K> {

    final Supplier<K> supplier;
    final BlockingVariable<K> result = new BlockingVariable<>();
    final ActorTemplate<Task> worker;
    
    public <T> ChainAsyncTask(AsyncTask<T> data, Function<T, K> func, ActorTemplate<Task> worker) {
        this(() -> func.apply(data.waitForResult()), worker);
    }

    public ChainAsyncTask(Supplier<K> supplier, ActorTemplate<Task> worker) {
        this.supplier = supplier;
        this.worker = worker;
    }

    @Override
    strictfp public void execute() {
        result.setValue(supplier.get());
    }

    @Override
    public K waitForResult() {
        return result.getValue();
    }

    @Override
    public void andThenSilently(Consumer<K> consumer) {
        worker.addOne(new VoidAwaitRequest<>(this, consumer));
    }
    
    public ChainAsyncTask<K> addToQueue() {
        worker.addOne(this);
        return this;
    }
    
    @Override
    public <A> AsyncTask<A> andThen(Function<K, A> func) {
        return new ChainAsyncTask<>(this, func, worker).addToQueue();
    }
    
}