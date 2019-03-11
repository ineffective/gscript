package com.gs.ast;

import beaver.Symbol;

import com.gs.GSException;
import com.gs.Program;

public class IfBlockNode extends Node {
	public IfBlockNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public IfBlockNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		throw new GSException("IfBlockNode.emit() shouldn't be called.");
	}
	public String toString() { return "IF-BLOCK"; }
}

