package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import beaver.Symbol;

import com.gs.Scope;

public class FuncNode extends Node {
	public Scope scope = null;
	public FuncNode(Symbol sym, String name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	// NOTE: calling custom functions works like this:
	// GSVM.run() directly copies its third parameter (args[]) into variables array
	public void emit(Program p) {
		/* NOTE: FuncNode can't exist inside FuncNode, so EndFunction() function
			must clear all Function/Lambda related structures (so free all scopes and
			bodies and so on) */
		p.pushScope(scope);
		int arg_count = bindName(p, (ArgNode)lhs);
		p.SetFunctionArgsCount(arg_count);
		rhs.emit(p);
		emit(p, CMD.END);
		p.EmitFunction();
		p.popScope();
	}
	protected String getVarName(Node n) {
		return (String)((VarNode)n).name;
	}
	protected int bindName(Program p, ArgNode n) {
		if (n != null) {
			return 1 + bindName(p, (ArgNode)n.rhs);
		}
		return 0;
	}
	public String toString() { return "FUNCTION " + name; }
	protected int declareArgName(Program p, ArgNode n) {
		if (n != null) {
			n.lhs.allocateVariables(p);
			return 1 + declareArgName(p, (ArgNode)n.rhs);
		}
		return 0;
	}
	public void allocateVariables(Program p) {
		p.CreateFunction((String)name, null);
		int args_count = declareArgName(p, (ArgNode)lhs);
		p.SetFunctionArgsCount(args_count);
		rhs.allocateVariables(p);
		scope = p.popScope();
	}
}

