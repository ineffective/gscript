package com.gs.utils;

import com.gs.GSVM;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class Invoker {
    public abstract Object invoke(Object o, Object... objects) throws Throwable;
    protected MethodHandle mh = null;
    protected Method m = null;
    protected Invoker() { }
    protected Invoker(Method m) throws IllegalAccessException {
        mh = MethodHandles.lookup().unreflect(m);
        this.m = m;
    }
    public static final Invoker getInvoker(GSVM vm, String name) throws Throwable {
        return new CallableObjectInvoker(vm, name);
    }
    public static final Invoker getInvoker(Method m, int paramCount) throws IllegalAccessException {
        try {
        if (Modifier.isStatic(m.getModifiers())) {
            return new StaticInvoker(m, paramCount);
        }
        return new VirtualInvoker(m, paramCount);
        } catch (Exception e) {
            System.err.println(m);
            throw e;
        }
    }
    public String toString() {
        return this.getClass() + " MH: " + mh + " M: " + m;
    }
}
