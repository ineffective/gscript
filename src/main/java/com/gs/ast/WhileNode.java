package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

public class WhileNode extends Node {
	public WhileNode(Symbol sym, Object name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	public WhileNode(Symbol sym, Object name) {
		this(sym, name, null, null);
	}
// WHILE <BOOL-EXPR> <STATEMENT>
// label_expr:
// BOOL-EXPR
// JMPFALSE label_end:
// STATEMENT
// JMP label_expr:
// label_end:
	public void emit(Program p) {
		int locExprLoc = p.size();          // label_expr:
		lhs.emit(p);                        // BOOL-EXPR
		emit(p, CMD.JMPFALSE);              // JMPFALSE label_end:
		int locWhileEndLoc = p.size();      // label_end: location
		emit(p, "FILL-ME");                 // label_end: location store
		rhs.emit(p);                        // STATEMENT
		emit(p, CMD.JMP);                   // JMP label_expr:
		emit(p, "FILL-ME");                 // label_expr: location store
		p.set(p.size() - 1, locExprLoc - p.size() + 1);
		                                    // set last instr (label_expr: location store) to offset
		p.set(locWhileEndLoc, p.size() - locWhileEndLoc);
	}
	public String toString() { return "WHILE"; }
}

