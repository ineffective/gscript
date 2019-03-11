package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class IfNode extends Node {
	public IfNode(Symbol s, Object name, Node lhs, Node rhs) {
		super(s, name, lhs, rhs);
	}
	public IfNode(Symbol s, Object name) {
		this(s, name, null, null);
	}
	public void emit(Program p) {
		lhs.emit(p);
		emit(p, CMD.JMPFALSE);
		int locElseBlock = p.size();
		emit(p, "FILL-ME");
		rhs.lhs.emit(p);

		if (rhs.rhs != null) {
			emit(p, CMD.JMP);
			int locSkipElse = p.size();
			emit(p, "FILL-ME");
			p.set(locElseBlock, p.size() - locElseBlock);
			rhs.rhs.emit(p);
			p.set(locSkipElse, p.size() - locSkipElse);
		} else {
			p.set(locElseBlock, p.size() - locElseBlock);
		}
	}
	public String toString() { return "IF"; }
}

