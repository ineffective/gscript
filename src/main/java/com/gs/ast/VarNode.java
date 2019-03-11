package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

import com.gs.GSException;

public class VarNode extends Node {
	public VarNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public VarNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public VarNode(Symbol sym, Object name, Node type) {
		this(sym, name, type, null);
	}
	public void emit(Program p) {
		// FIXME: this does not work if name referes to function
		Integer slot = p.getVariableSlot((String)name);
		if (slot == null) {
			String pos = getExtentAsString();
			throw new GSException(pos + " - symbol " + (String)name + " is undefined.");
		}
		if (slot == -1) { // so it's a function!
			emit(p, CMD.GETFUNC);
			emit(p, p.getConstSlot((String)name));
		} else if (slot == -2) {
			emit(p, CMD.GETGLOB);
			emit(p, p.getConstSlot((String)name));
		} else {
			emit(p, CMD.GETVAR);
			emit(p, slot);
		}
	}
	public String toString() { return "VAR: " + name; }
	public void allocateVariables(Program p) {
		Integer slot = p.getVariableSlot((String)name);
		if (slot == null) {
			String pos = getExtentAsString();
			throw new GSException(pos + " - symbol " + (String)name + " is undefined.");
		}
	}
}

