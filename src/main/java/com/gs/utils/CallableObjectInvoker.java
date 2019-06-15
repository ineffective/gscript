package com.gs.utils;

import com.gs.CallableObject;
import com.gs.GSVM;

public final class CallableObjectInvoker extends Invoker {
    private final String name;
    private final GSVM vm;
    public CallableObjectInvoker(GSVM vm, String name) throws Throwable {
        this.name = name;
        this.vm = vm;
    }
    public final Object invoke(Object o, Object... objects) throws Throwable {
        CallableObject co = (CallableObject)o.getClass().getField(name).get(o);
        return co.call(vm, objects);
    }
}
