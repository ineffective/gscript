package com.gs.ast;

import beaver.Symbol;

import com.gs.Program;

public abstract class Node extends Symbol {
	public Type type = null;
	public Object name;
	public Node lhs, rhs;

	public Node(Symbol sym, Object name, Node lhs, Node rhs) {
		this.start = sym.getStart();
		this.end = sym.getEnd();
		this.lhs = lhs;
		this.rhs = rhs;
		this.name = name;
	}

	public Node(Symbol sym, Object name) {
		this(sym, name, null, null);
	}

	public final void emit(Program prog, Object o) {
		prog.addLast(o);
		prog.addLastDbg(this);
	}

	public abstract void emit(Program p);

	public final void ssae(Object... args) {
		for (Object o : args) {
			System.out.print(o);
			System.out.print(" ");
		}
		System.out.println();
	}

	public String getExtentAsString() {
		return getLine(start) + "," + getColumn(start) + "-" + getLine(end) + "," + getColumn(end);
	}

	public void allocateVariables(Program p) {
		if (lhs != null)
			lhs.allocateVariables(p);
		if (rhs != null)
			rhs.allocateVariables(p);
	}

	public void ssa() {
	}
}
