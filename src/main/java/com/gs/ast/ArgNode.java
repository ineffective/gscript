package com.gs.ast;

import com.gs.Program;
import beaver.Symbol;

public class ArgNode extends Node {
	public ArgNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public ArgNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		System.out.println("WARN: this shouldn't be called. List should be traversed by CallNode or similar");
	}
	public String toString() { return "ARG"; }
}

