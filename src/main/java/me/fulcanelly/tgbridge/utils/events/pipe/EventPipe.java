package me.fulcanelly.tgbridge.utils.events.pipe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EventPipe {
    public EventPipe() {}

    boolean isRightHandler(Method method) {
        
        if(method.getAnnotation(EventReactor.class) == null) {
            return false;
        }

        Class<?>[] parameters = method.getParameterTypes();
        
        if(parameters.length != 1) {
            return false;
        }  
        
        Class<?> parameter = parameters[0];
        
        if(parameter.isInterface()) {
            return false;
        }

        return EventObject.class.isAssignableFrom(parameter);
    }
    
    List<Reactor> getHandlers(Listener object) {
        return Arrays.asList(object.getClass().getDeclaredMethods()).stream()
        .filter(method -> this.isRightHandler(method))
        .map(method -> new Reactor(method, object))
        .collect(Collectors.toList());
    }

    class Reactor {
        Method method;
        Object instance;

        <T>void call(T args) {
            try {
                method.invoke(instance, args); 
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
       
        Reactor(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        public Class<?> getType() {
            Class<?>[] types = method.getParameterTypes();
            return Arrays.asList(types).get(0);
        }
    }

    //HashMap<Class<Listener>, List<Reactor>> reactors = new HashMap<>();
    HashMap<Class<?>, List<Reactor>> reactors = new HashMap<>();

    public void registerListener(Listener listener) {
        getHandlers(listener).stream()
            .forEach(reactor -> registerReactor(reactor));
    }

    public void registerReactor(Reactor reactor) {
        Class<?> type = reactor.getType();
        reactors.putIfAbsent(type, new ArrayList<Reactor>());

        List<Reactor> listeners = reactors.get(type);
        listeners.add(reactor);
    }

    public void emit(EventObject object) {
   //     new Thread(() -> 
        reactors.get(object.getClass())
            .forEach(reactor -> reactor.call(object));
       // ).start(); 
    }
}