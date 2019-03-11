package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class NegateNode extends Node {
	public NegateNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public NegateNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		lhs.emit(p);
		emit(p, CMD.NEG);
	}
}

