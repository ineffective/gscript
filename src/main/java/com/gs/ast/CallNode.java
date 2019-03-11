package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class CallNode extends Node {
	public CallNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public CallNode(Symbol sym, Object name, Node lhs) {
		this(sym, name, lhs, null);
	}
	public CallNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	protected int emit_args(Program p, ArgNode node)
	{
		int count = 0;
		if (node != null) {
			count = 1 + emit_args(p, (ArgNode)node.rhs);
			node.lhs.emit(p);
		}
		return count;
	}
	public void emit(Program p) {
		int arg_count = emit_args(p, (ArgNode)rhs);
		lhs.emit(p);
		emit(p, CMD.CALL);
		emit(p, null);
		emit(p, arg_count);
	}
	public String toString() { return "CALL"; }
}

