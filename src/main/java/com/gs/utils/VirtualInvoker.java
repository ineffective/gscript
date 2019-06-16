package com.gs.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public final class VirtualInvoker extends Invoker {
    private final MethodHandle mh2;
    public VirtualInvoker(Method m, int paramCount) throws IllegalAccessException {
        super(m);
        MethodType invocationType = MethodType.genericMethodType(1 + paramCount);
        mh = mh.asType(invocationType);
        mh2 = MethodHandles.spreadInvoker(invocationType, 1);
    }
    @Override
    public final Object invoke(Object o, Object... objects) throws Throwable {
        return mh2.invokeExact(mh, o, objects);
    }
}
