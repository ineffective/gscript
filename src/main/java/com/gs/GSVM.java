package com.gs;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;

import com.gs.utils.IAccessor;
import com.gs.utils.MethodFinder;
import com.gs.utils.security.AllowAllMethodFinderPolicy;
import com.gs.utils.security.BlockReflectionMethodFinderPolicy;
import com.gs.utils.security.MethodFinderPolicy;

public final class GSVM {
	public static void dumpExecArray(Object[] exec, int[] d, PrintWriter out) {
		int i = 0;
		while (i < exec.length) {
			CMD c = (CMD) exec[i];
			String line = String.format("%8d ", i) + c;
			String dbg = "";
			for (int j = 0; j < c.getArgs(); ++j) {
				line += " " + exec[i + 1 + j];
			}
			if (d != null) {
				dbg = "// DBG: " + d[i * 4 + 0] + "," + d[i * 4 + 1] + "-" + d[i * 4 + 2] + "," + d[i * 4 + 3];
			}
			out.println(String.format("%-45.45s%s", line, dbg));
			i += 1 + c.args;
		}
	}

	@SuppressWarnings("serial")
	static class Variables extends HashMap<String, Object> {
	}

	private final ArrayDeque<Object[]> prog_stack = new ArrayDeque<Object[]>();
	Object[] prog = null;
	private final Variables globs = new Variables();
	private final ArrayDeque<Object[]> vars = new ArrayDeque<Object[]>();
	private final ArrayDeque<Integer> instr_ptrs_stack = new ArrayDeque<Integer>();
	final ArrayDeque<Object> stack = new ArrayDeque<Object>();
	int instrPtr = 0;

	private void pushCode(Object[] code) {
		prog_stack.push(code);
		instr_ptrs_stack.push(instrPtr);
		prog = prog_stack.peek();
		instrPtr = 0;
	}

	private void popCode() {
		instrPtr = instr_ptrs_stack.pop();
		prog_stack.pop();
		prog = prog_stack.size() == 0 ? null : prog_stack.peek();
	}

	public void putGlobal(String name, Object var) throws Exception {
		globs.put(name, var);
	}

	public Integer getGlobSlot(String name) {
		if (globs.containsKey(name)) {
			return -2;
		}
		return null;
	}

	public Object getGlob(String name) {
		return globs.get(name);
	}

	public void putVar(int slot, Object var) throws Exception {
		vars.peek()[slot] = var;
	}

	public Object getVar(int slot) {
		return vars.peek()[slot];
	}

	private void pushVariablesVector(Object[] variables) {
		vars.push(variables);
	}

	private void popVariablesVector() {
		vars.pop();
	}

	Object getFunc(String name) {
		return rt.getFunction(name);
	}

	final Runtime rt;

	public GSVM(Runtime rt) {
		this(rt, new AllowAllMethodFinderPolicy());
	}

	public GSVM(Runtime rt, MethodFinderPolicy mfp) {
		this.rt = rt;
		methodFinder = new MethodFinder(mfp);
	}

	public Object run(CallableObject function, Object[] variables, Object... args) throws Exception {
		pushCode(function.getCode());
		pushVariablesVector(variables);
		System.arraycopy(args, 0, variables, 0, args.length);
		instrPtr = 0;
		try {
			while (prog[instrPtr] != CMD.END) {
				CMD command = (CMD) prog[instrPtr];
				cmdStats[command.getNum()]++;
				command.exec(this);
				instrPtr++;
			}
			if (stack.isEmpty()) {
				return null; // if function returns nothing, it can as well return null, so Void
			}
			IAccessor acc = ((IAccessor) stack.pop());
			Object o = acc.get();
			acc.release();
			return o; // do it always. all functions must return something. all.
		} catch (GSVMJustUnwindStackException e) {
			e.gsvmStackTrace.addLast(generateStackFrameInfo(function, instrPtr));
			throw e;
		} catch (Throwable e) {
			GSVMJustUnwindStackException juse = new GSVMJustUnwindStackException(e);
			juse.gsvmStackTrace.addLast(generateStackFrameInfo(function, instrPtr));
			throw juse;
		} finally {
			popCode();
			popVariablesVector();
		}
	}

	private static final String generateStackFrameInfo(CallableObject function, int instrPtr) {
		String s = "  @ " + function;
		if (function.dbgInfo != null) {
			s += " (file: " + function.dbgFile + " at " + function.dbgInfo[instrPtr * 4 + 0] + ","
					+ function.dbgInfo[instrPtr * 4 + 1] + "-" + function.dbgInfo[instrPtr * 4 + 2] + ","
					+ function.dbgInfo[instrPtr * 4 + 3] + ")";
		}
		return s;
	}

	public static final class GSVMJustUnwindStackException extends Exception {
		private static final long serialVersionUID = 1L;
		public LinkedList<String> gsvmStackTrace = new LinkedList<String>();

		public GSVMJustUnwindStackException(Throwable e) {
			super(e);
		}
	}

	private long[] cmdStats = new long[CMD.values().length];
	private MethodFinder methodFinder;

	public void resetStats() {
		for (int i = 0; i < cmdStats.length; ++i) {
			cmdStats[i] = 0;
		}
	}

	public void printStats() {
		long total = 0;
		for (CMD c : CMD.values()) {
			total += cmdStats[c.getNum()];
			System.out.println(c.toString() + "\t" + cmdStats[c.getNum()]);
		}
		System.out.println("TOTAL: " + total);

	}

	// returns count of executed instructions
	public long getIECount() {
		long total = 0;
		for (CMD c : CMD.values()) {
			total += cmdStats[c.getNum()];
		}
		return total;
	}

	public void memoize(Object o) {
		prog[instrPtr - 1] = o;
	}

	public Object recall() {
		return prog[instrPtr - 1];
	}

	public void sanitize() {
		if (stack.size() > 0) {
			System.err.println("GSVM.Sanitize: stack still contains " + stack.size() + " elements:");
			int i = 0;
			for (Object o : stack) {
				System.err.println(String.format("%3d %s", i, o.toString()));
			}
		}
	}

	public MethodFinder getMethodFinder() {
		return methodFinder;
	}
}
