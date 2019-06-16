package com.gs.utils;

import java.util.ArrayDeque;

import com.gs.CallableObject;
import com.gs.GSVM;

final class ArrayUtil {
public static final void set(Object array, int index, Object o) {
    Class<?> c = array.getClass();
    if (int[].class == c) {
	(((int[])array)[index]) = (int)o;
    } else if (float[].class == c) {
	(((float[])array)[index]) = (float)o;
    } else if (boolean[].class == c) {
	(((boolean[])array)[index]) = (boolean)o;
    } else if (char[].class == c) {
	(((char[])array)[index]) = (char)o;
    } else if (double[].class == c) {
	(((double[])array)[index]) = (double)o;
    } else if (long[].class == c) {
	(((long[])array)[index]) = (long)o;
    } else if (short[].class == c) {
	(((short[])array)[index]) = (short)o;
    } else if (byte[].class == c) {
	(((byte[])array)[index]) = (byte)o;
    }
    (((Object[])array)[index]) = o;
}
public static final Object get(Object array, int index){
    Class<?> c = array.getClass();
    if (int[].class == c) {
	return ((int[])array)[index];
    } else if (float[].class == c) {
	return ((float[])array)[index];
    } else if (boolean[].class == c) {
	return ((boolean[])array)[index];
    } else if (char[].class == c) {
	return ((char[])array)[index];
    } else if (double[].class == c) {
	return ((double[])array)[index];
    } else if (long[].class == c) {
	return ((long[])array)[index];
    } else if (short[].class == c) {
	return ((short[])array)[index];
    } else if (byte[].class == c) {
	return ((byte[])array)[index];
    }
    return ((Object[])array)[index];
}
}
public final class CompoundAccessor implements IAccessor {
	private IAccessor object;
	private Object designator;
	private GSVM vm;
	private CompoundAccessor() {
	}
	private static ArrayDeque<CompoundAccessor> pool = new ArrayDeque<>();
	public static CompoundAccessor getCompoundAccessor(GSVM vm, Object designator, IAccessor object) {
		CompoundAccessor ca = null;
		if (pool.isEmpty()) {
			ca = new CompoundAccessor();
		} else {
			ca = pool.pop();
		}
		ca.vm = vm;
		ca.designator = designator;
		ca.object = object;
		return ca;
	}
	public void release() {
		object.release();
		pool.push(this);
	}
	@Override
	public String toString() {
		return "CMPA: " + object + ": " + designator;
	}
	@SuppressWarnings("unchecked")
	public Object get() throws Throwable {
		Class<?> fromCls = object.get().getClass();
		if (fromCls.isArray()) {
			return ArrayUtil.get(object.get(), ((Number)designator).intValue());
		}
		if (java.util.AbstractCollection.class.isAssignableFrom(fromCls)) {
			if (java.util.AbstractList.class.isAssignableFrom(fromCls)) {
				return ((java.util.AbstractList<Object>)object.get()).get(((Number)designator).intValue());
			}
		}
		if (java.util.AbstractMap.class.isAssignableFrom(fromCls)) {
			return ((java.util.AbstractMap<Object, Object>)object.get()).get(designator);
		}
		throw new RuntimeException("object " + object.get() + " [" + fromCls + "] is not an array or list");
	}
	@SuppressWarnings("unchecked")
	public void set(Object o) throws Throwable {
		Class<?> fromCls = object.get().getClass();
		if (fromCls.isArray()) {
			ArrayUtil.set(object.get(), ((Number)designator).intValue(), o);
			return;
		}
		if (java.util.AbstractCollection.class.isAssignableFrom(fromCls)) {
			if (java.util.AbstractList.class.isAssignableFrom(fromCls)) {
				java.util.AbstractList<Object> l = (java.util.AbstractList<Object>)object.get();
				l.set(((Number)designator).intValue(), o);
				return;
			}
		}
		if (java.util.AbstractMap.class.isAssignableFrom(fromCls)) {
			java.util.AbstractMap<Object, Object> m = (java.util.AbstractMap<Object, Object>)object.get();
			m.put(designator, o);
			return;
		}
		throw new RuntimeException("object " + object.get() + " [" + fromCls + "] is not an array, linkedlist nor abstractmap instance");
	}
	public Object invoke(Object[] args) throws Throwable {
		CallableObject co = (CallableObject)get();
		return co.call(vm, args);
	}
}

