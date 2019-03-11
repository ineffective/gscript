package com.gs.ast;

import beaver.Symbol;

import com.gs.CMD;
import com.gs.Program;

public class BinNode extends Node {
	public final CMD cmd;
	public final String rep;
	public BinNode(Symbol sym, Object name, String rep, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
		cmd = (CMD)name;
		this.rep = rep;
	}
	public void emit(Program p) {
		if (cmd == CMD.SET) {
			rhs.emit(p);
			lhs.emit(p);
		} else {
			lhs.emit(p);
			rhs.emit(p);
		}
		emit(p, cmd);
	}
	public void ssa() {
		if(lhs != null) lhs.ssa();
		if(rhs != null) rhs.ssa();
		ssae("tmp = lhs", cmd, "rhs");
	}
	public String toString() { return cmd.toString() + " (" + rep + ")"; }
	public void allocateVariables(Program p) {
		lhs.allocateVariables(p);
		rhs.allocateVariables(p);
	}
}

