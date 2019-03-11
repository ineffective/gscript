package com.gs;

import com.gs.GSVM.Variables;

public class LambdaObject extends CallableObject {
	private Object[] variables = null;
	public LambdaObject(CallableObject base, Variables scope) {
		super(base.name, base.argsCount, base.exec);
		variables = new Object[base.slotsCount];
	}
	@Override
	public String toString()
	{
		return "lambda " + name;
	}
	public void bind(int slot, Object o) {
		variables[slot] = o;
	}
	// this puts args on the stack and calls using provided VM
	// if called from vm, no arguments should be provided
	@Override
	public Object call(GSVM vm, Object... args) throws Exception {
		if (argsCount != args.length) {
			throw new GSException("Function " + name
				+ " called with invalid number of arguments: required: "
				+ argsCount + " provided: " + args.length);
		}
		return vm.run(this, variables, args);
	}
}

