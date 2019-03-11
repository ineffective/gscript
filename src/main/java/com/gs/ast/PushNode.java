package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class PushNode extends Node {
	public PushNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public PushNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		emit(p, CMD.PUSH);
		emit(p, name);
	}
	public String toString() { return "PUSH " + name; }
}

