package com.gs.utils;

import java.util.ArrayDeque;

import com.gs.*;

public final class VarAccessor implements IAccessor {
	private Integer name;
	private GSVM vm;
	private VarAccessor() {
	}
	private static ArrayDeque<VarAccessor> pool = new ArrayDeque<>();
	public static VarAccessor getVarAccessor(GSVM vm, Integer name) {
		VarAccessor va = null;
		if (pool.isEmpty()) {
			va = new VarAccessor();
		} else {
			va = pool.pop();
		}
		va.vm = vm;
		va.name = name;
		return va;
	}
	public void release() {
		pool.push(this);
	}
	public Object get() throws Throwable {
		return vm.getVar(name);
	}
	public void set(Object o) throws Throwable {
		vm.putVar(name, o);
	}
	public Object invoke(Object[] args) throws Throwable {
		CallableObject co = (CallableObject)vm.getVar(name);
		return co.call(vm, args);
	}
	@Override
	public String toString() {
		String s = "";
		try {
		s = "VA: " + name;
		} catch (Exception e) {
			s = "VA: " + name + " (EXCEPTION CAUGHT)";
			e.printStackTrace();
		}
		return s;
	}
}

