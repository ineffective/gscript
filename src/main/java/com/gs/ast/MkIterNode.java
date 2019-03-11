package com.gs.ast;

import beaver.Symbol;

import com.gs.CMD;
import com.gs.Program;

public class MkIterNode extends Node {
	public MkIterNode(Symbol sym, Node lhs) {
		super(sym, "mk-iter", lhs, null);
	}
	public void emit(Program p) {
		lhs.emit(p);
		emit(p, CMD.MKITER);
	}
	public String toString() { return "MKITER"; }
}

