package com.gs.utils;

import java.util.ArrayDeque;

import com.gs.*;

public final class ConstAccessor implements IAccessor {
	private Object value;
	private GSVM vm;
	private ConstAccessor() {
	}
	private static ArrayDeque<ConstAccessor> pool = new ArrayDeque<>();
	public static ConstAccessor getConstAccessor(GSVM vm, Object value) {
		ConstAccessor ca = null;
		if (pool.isEmpty()) {
			ca = new ConstAccessor();
		} else {
			ca = pool.pop();
		}
		ca.vm = vm;
		ca.value = value;
		return ca;
	}
	public void release() {
		pool.push(this);
	}
	public Object get() throws Throwable {
		return value;
	}
	public void set(Object o) {
		throw new RuntimeException("Can't assign to constant.");
	}
	// Ok, if invoke was called, it was most probably immediate application of lambda or call
	// of function returned from some other function.
	public Object invoke(Object[] args) throws Throwable {
		CallableObject co = (CallableObject)value;
		return co.call(vm, args);
	}
	@Override
	public String toString() {
		return "CA: " + value;
	}
}

