package com.gs.ast;

import beaver.Symbol;

import com.gs.Program;

public class DefaultNode extends Node {
	public DefaultNode(Symbol sym, Object name, Node lhs) {
		super(sym, name, lhs, null);
	}
	public void emit(Program p) {
	}
}

