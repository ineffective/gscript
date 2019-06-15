package com.gs;

import com.gs.CallableObject;
import com.gs.GSVM;
import com.gs.utils.Invoker;

import java.lang.reflect.Method;

public class CallableMethodObject extends CallableObject {
    private final Object target;

    public CallableMethodObject(Object obj, String name) {
        super(name, 0, null);
        this.target = obj;
    }

    public Object[] getCode() {
        return new Object[]{};
    }

    public String toString() {
        return "methref: " + target.getClass().getName() + "::" + name;
    }

    public Object call(GSVM vm, Object... args) throws Throwable {
        // but if vm returns null, this means that we weren't there yet, so try to
        // find matching function and remember it.
        // First, create an array storing types of all arguments
        Class<?>[] cls = new Class<?>[args == null ? 0 : args.length];
        // and store types of arguments in it
        for (int i = 0; i < (args == null ? 0 : args.length); ++i) {
            if (args[i] == null) {
                cls[i] = Object.class;
            } else {
                cls[i] = args[i].getClass();
            }
        }
        // and try to find matching method
        Method m = vm.getMethodFinder().findMethod(target.getClass(), name, cls);
        return Invoker.getInvoker(m, args.length).invoke(target, args);
    }
}
