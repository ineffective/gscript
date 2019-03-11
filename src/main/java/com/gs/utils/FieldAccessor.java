package com.gs.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;

import com.gs.CallableObject;
import com.gs.GSVM;

public final class FieldAccessor implements IAccessor {
	protected IAccessor object;
	protected String name;
	protected GSVM vm;
	private FieldAccessor() {
	}
	private static ArrayDeque<FieldAccessor> pool = new ArrayDeque<>();
	public static FieldAccessor getFieldAccessor(GSVM vm, String name, IAccessor o) {
		FieldAccessor fa = null;
		if (pool.isEmpty()) {
			fa = new FieldAccessor();
		} else {
			fa = pool.pop();
		}
		fa.vm = vm;
		fa.name = name;
		fa.object = o;
		return fa;
	}
	public void release() {
		object.release();
		pool.push(this);
	}
	public static FieldAccessor getFieldAccessor(FieldAccessor fa) {
		FieldAccessor acc = null;
		if (pool.isEmpty()) {
			acc = new FieldAccessor();
		} else {
			acc = pool.pop();
		}
		acc.vm = fa.vm;
		acc.name = fa.name;
		acc.object = fa.object;
		return acc;
	}
	@Override
	public String toString() {
		return "FA: " + object + ": " + name;
	}
	public Object get() throws Exception {
		Object o = object.get();
		if (o instanceof Class<?> && ((Class<?>)o).isEnum()) {
			Class<?> cls = ((Class<?>) o); 
			for(Object x: cls.getEnumConstants()) {
				if (((Enum<?>)x).name().equals(name)) {
					return x;
				}
			}
		}
		if (o.getClass().isArray()) {
			if (name.equals("length")) {
				return ((Object[])o).length;
			}
		}
		Field field = o.getClass().getField(name);
		return field.get(object.get());
	}
	public void set(Object o) throws Exception {
		Field field = ((Object)object.get()).getClass().getField(name);
		field.set(object.get(), o);
	}
	public static abstract class Invoker {
		abstract Object invoke(Object o, Object...objects) throws Throwable;
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
	public static final class CallableObjectInvoker extends Invoker {
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
	public static final class StaticInvoker extends Invoker {
		private final MethodHandle mh2;
		public StaticInvoker(Method m, int paramCount) throws IllegalAccessException {
			super(m);
			MethodType invocationType = MethodType.genericMethodType(paramCount);
			mh = mh.asType(invocationType);
			mh2 = MethodHandles.spreadInvoker(invocationType, 0);
		}
		@Override
		public final Object invoke(Object o, Object... objects) throws Throwable {
			return mh2.invokeExact(mh, objects);
		}
	}
	public static final class VirtualInvoker extends Invoker {
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
	public Object invoke(Object[] args) throws Throwable {
//		System.out.println("INVOKING WITH ARGS: ");
//		for (Object o: args) {
//			System.out.println(o.getClass());
//		}
		Object target = (Object)object.get();
		// Try to recall remembered invoker.
		Invoker invoker = (Invoker)vm.recall();
		// If it succeeded, try to invoke() it
		if (invoker != null) {
			try {
				Object o = invoker.invoke(target, args);
				return o;
			} catch (Throwable e) {
				System.err.println("MEMOIZATION FAILED: " + target.getClass() + " " + invoker);
				e.printStackTrace();
			}
		}
		// but if vm returns null, this means that we weren't there yet, so try to
		// find matching function and remember it.
		// First, create an array storing types of all arguments
		Class<?>[] cls = new Class<?>[args == null ? 0 : args.length];
		// and store types of arguments in it
		for (int i = 0 ; i < (args == null ? 0 : args.length) ; ++i) {
			if (args[i] == null) {
				cls[i] = Object.class;
			} else {
				cls[i] = args[i].getClass();
			}
		}
		// and try to find matching method
		Method m = vm.getMethodFinder().findMethod(target.getClass(), name, cls);
		// If method wasn't found, look for a field with this name.
		// If it is instance of CallableObject, create invoker and memoize it
		if (m == null) {
			@SuppressWarnings("unused") // this is here so we can use Java's mechanism for checking if it is instance of CallableObject
			CallableObject co = (CallableObject)target.getClass().getField(name).get(target);
			vm.memoize(Invoker.getInvoker(vm, name));
		} else {
			vm.memoize(Invoker.getInvoker(m, args.length));
		}
		invoker = (Invoker)vm.recall();
		return invoker.invoke(target, args);
	}
}

