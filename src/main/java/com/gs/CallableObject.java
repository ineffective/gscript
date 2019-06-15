package com.gs;

import java.io.IOException;
import java.io.PrintWriter;
import com.gs.GSException;

public class CallableObject {
	protected String name;
	int slotsCount;
	int argsCount;
	protected Object[] exec;
	public String dbgFile;
	public int[] dbgInfo;
	public CallableObject(String name, int argsCount, Object[] exec) {
		this.name = name;
		this.exec = exec;
		this.argsCount = argsCount;
	}
	public void setSlotsCount(int count) {
		slotsCount = count;
	}
	public Object[] getCode()
	{
		return exec;
	}
	@Override
	public String toString()
	{
		return "func " + name;
	}
	public void dump(PrintWriter out) throws IOException {
		out.println(".object.function " + name);
		if (dbgFile != null) {
			out.println("// DBG: .file " + dbgFile);
		}
		GSVM.dumpExecArray(exec, dbgInfo, out);
		out.println();
	}
	// this puts args on the stack and calls using provided VM
	// if called from vm, no arguments should be provided
	public Object call(GSVM vm, Object... args) throws Throwable {
		if (argsCount != args.length) {
			throw new GSException("Function " + name
				+ " called with invalid number of arguments: required: "
				+ argsCount + " provided: " + args.length);
		}
		return vm.run(this, new Object[this.slotsCount], args);
	}
}
