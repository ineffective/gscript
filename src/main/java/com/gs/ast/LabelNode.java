package com.gs.ast;

import beaver.Symbol;

import com.gs.Program;

public class LabelNode extends Node {
	public LabelNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public void emit(Program p) {
		p.addLabel((String)name);
	}
}

