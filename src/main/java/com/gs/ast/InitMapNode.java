package com.gs.ast;

import beaver.Symbol;

import com.gs.CMD;
import com.gs.Program;

public class InitMapNode extends Node {
	public InitMapNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public InitMapNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	private int emitArgs(Program p, Node n) {
		int count = 0;
		if (n != null) {
			count += emitArgs(p, n.rhs);
			if (n.lhs != null) {
				n.lhs.emit(p);
				count++;
			}
		}
		return count;
	}
	public void emit(Program p) {
		int count = 0;
		if (rhs != null) {
			count = emitArgs(p, rhs);
		}
		emit(p, CMD.MKLIST);
		emit(p, count);
		emit(p, CMD.MKMAP);
		emit(p, CMD.LISTTOMAP);
	}
}

