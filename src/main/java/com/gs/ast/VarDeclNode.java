package com.gs.ast;

import beaver.Symbol;

import com.gs.GSException;
import com.gs.Program;

public class VarDeclNode extends VarNode {
	public VarDeclNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public VarDeclNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
	public VarDeclNode(Symbol sym, Object name, Node type) {
		this(sym, name, type, null);
	}
	public String toString() { return "VAR: " + name; }
	public void allocateVariables(Program p) {
		p.declareVariable((String)name);
		if (lhs != null) {
			Type t = p.rt.solveTypeName((String)lhs.name);
			if (t == null) {
				throw new GSException(lhs.getExtentAsString() + ": Unknown type name (" + (String)lhs.name + ")");
			}
			type = t;
		}
	}
}

