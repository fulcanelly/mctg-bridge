package me.fulcanelly.tgbridge.utils.stop;

enum Type {
    SIGNAL,
    DATA
}

public class StopSignalOrData<T> {
    final T value;
    final Type singal;

    static public <T> StopSignalOrData<T> getStopSignal() {
        return new StopSignalOrData<T>();
    }

    public boolean isSignal() {
        return singal == Type.SIGNAL;
    }

    public StopSignalOrData(T value) {
        this.singal = Type.DATA;
        this.value = value;
    }

    StopSignalOrData() {
        this.singal = Type.SIGNAL;
        value = null;
    }

    public T get() {
        return value;
    }


}
