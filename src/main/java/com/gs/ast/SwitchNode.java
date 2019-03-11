package com.gs.ast;

import beaver.Symbol;

import com.gs.Program;

public class SwitchNode extends Node {
	public SwitchNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public void emit(Program p) {
	}
}

