package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class MapEntryNode extends Node {
	public MapEntryNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public void emit(Program p) {
		lhs.emit(p);
		rhs.emit(p);
		emit(p, CMD.MKMAPENTRY);
	}
}

