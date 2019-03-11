package com.gs.ast;

import com.gs.Program;
import beaver.Symbol;

public class ListNode extends Node {
	public ListNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public ListNode(Symbol sym, Object name, Node lhs) {
		this(sym, name, lhs, null);
	}
	public ListNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public ListNode(Symbol sym) {
		this(sym, "", null, null);
	}
	public void emit(Program p) {
		if (lhs != null) {
			lhs.emit(p);
		}
		if (rhs != null) {
			rhs.emit(p);
		}
	}
	public String toString() { return "LST"; }
}

