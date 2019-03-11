package com.gs.ast;

import com.gs.CMD;
import com.gs.Program;

import java.util.LinkedList;

import beaver.Symbol;

public class LambdaFuncNode extends FuncNode {
	public static class Binding {
		public int to, from;
		public Binding(int aTo, int aFrom) {
			to = aTo;
			from = aFrom;
		}
	}
	public final LinkedList<Binding> bindings = new LinkedList<Binding>();
	public void addBinding(int to, int from) {
		bindings.add(new Binding(to, from));
	}
	public LambdaFuncNode(Symbol sym, String name, Node lhs, Node rhs) {
		super(sym, name, lhs, rhs);
	}
	// to learn how calling custom functions works, read note in FuncNode.java

	// lambda is created locally, so it must be created and pushed onto stack
	public void emit(Program p) {
		p.pushScope(scope);
		int args_count = bindName(p, (ArgNode)lhs);
		p.SetFunctionArgsCount(args_count);
		rhs.emit(p);
		emit(p, CMD.END);
		p.EmitFunction();
		p.popScope();
		emit(p, CMD.LAMBDA);
		emit(p, p.getConstSlot(this.name));
		while (!bindings.isEmpty()) {
			Binding b = bindings.pop();
			emit(p, CMD.BIND);
			emit(p, b.to);
			emit(p, b.from);
		}
	}
	public String toString() { return "LAMBDA"; }
	public void allocateVariables(Program p) {
		this.name = p.createLambdaName();
		p.CreateFunction((String)this.name, this);
		int args_count = declareArgName(p, (ArgNode)lhs);
		p.SetFunctionArgsCount(args_count);
		rhs.allocateVariables(p);
		scope = p.popScope();
	}
}

