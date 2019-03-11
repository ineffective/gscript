package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class EndNode extends Node {
	public EndNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public EndNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public EndNode(Symbol sym)
	{
		this(sym, "END", null, null);
	};
	public void emit(Program p) {
		emit(p, CMD.END);
	}
}

