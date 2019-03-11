package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class StatementEndNode extends Node {
	public StatementEndNode(Symbol sym, Object name, Node lhs/*, Node rhs*/) {
		super(sym, name, lhs, null);
	}
	public StatementEndNode(Symbol sym, Object name) {
		this(sym, name, null);
	}
	public void emit(Program p) {
		lhs.emit(p);
		emit(p, CMD.POP);
	}
	public String toString() { return "STATEMENT-END"; }
}

