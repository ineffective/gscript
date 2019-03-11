package com.gs.ast;

import beaver.Symbol;

import com.gs.Program;
import com.gs.GSException;

public class TypeNameNode extends Node {
	public TypeNameNode(Symbol sym, String name) {
		super(sym, name, null, null);
	}
	public void emit(Program p) {
		throw new GSException("TypeNameNode.emit() shouldn't be called.");
	}

	public void allocateVariables(Program p) {
		if (lhs != null) lhs.allocateVariables(p);
		if (rhs != null) rhs.allocateVariables(p);
	}
}

