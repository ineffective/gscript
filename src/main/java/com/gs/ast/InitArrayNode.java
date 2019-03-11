package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class InitArrayNode extends Node {
	public InitArrayNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public InitArrayNode(Symbol sym, Object name) {
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
			emit(p, CMD.MKLIST);
			emit(p, count);
			new PushConstNode(new Symbol((short)0, 0, 0), count).emit(p);
			emit(p, CMD.MKARRAY);
			emit(p, CMD.LISTTOARRAY);
		} else if (lhs != null) {
			lhs.emit(p);
			emit(p, CMD.MKARRAY);
			// simply create array with `lhs` elements
		}
	}
}

