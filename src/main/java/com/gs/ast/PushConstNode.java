package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class PushConstNode extends Node {
	public PushConstNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public PushConstNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public void emit(Program p) {
		emit(p, CMD.PUSHCONST);
		int constSlot = p.getConstSlot(name);
		emit(p, constSlot);
	}
	public String toString() { return "PUSHCONST " + name; }
}

