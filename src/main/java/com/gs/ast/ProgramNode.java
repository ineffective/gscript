package com.gs.ast;

import com.gs.Program;
import beaver.Symbol;

public class ProgramNode extends Node {
	public ProgramNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public ProgramNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		lhs.emit(p);
		if (rhs != null) {
			rhs.emit(p);
		}
	}
	public String toString() { return "PROGRAM"; }
}

