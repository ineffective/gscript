package com.gs;

import beaver.Symbol;

import java.util.HashMap;
import java.util.Stack;

import com.gs.ast.LambdaFuncNode;
import com.gs.ast.Type;
import com.gs.Runtime.CompilationOpts;

public class Program  {
	protected Stack<Scope> scopes = new Stack<Scope>();
	private CompilationOpts compOpts = null;
	public boolean isVarDefined(String name) {
		Stack<Scope> tmpScopes = new Stack<Scope>();
		boolean found = false;
		while (!scopes.empty()) {
			if (scopes.peek().isVarKnown(name)) {
				found = true;
				break;
			} else {
				tmpScopes.push(scopes.pop());
			}
		}
		while (!tmpScopes.empty()) {
			scopes.push(tmpScopes.pop());
		}
		if (found == false) {
			if (callables.containsKey(name)) {
				found = true;
			}
		}
		if (found == false && rt.getFunction(name) != null) {
			found = true;
		}
		if (found == false && rt.getVM().getGlobSlot(name) != null) {
			found = true;
		}
		return found;
	}
	public Integer declareVariable(String name) {
		Scope s = scopes.peek();
		if (s.isVarKnown(name)) {
			throw new GSException("Symbol '" + name + "' already defined.");
		}
		return s.allocateVarSlot(name);
	}
	public Integer getVariableSlot(String name) {
		Stack<Scope> tmpScopes = new Stack<Scope>();
		Integer slotNo = null;
		// check all scopes and see if variable exists there
		while (! scopes.empty()) {
			// if variable was not found at the top of the stack
			if (! scopes.peek().isVarKnown(name)) {
				// pop top scope and put it onto tmpScopes stack
				tmpScopes.push(scopes.pop());
			} else { // variable found in this scope
				slotNo = scopes.peek().getVarSlot(name);
				while (!tmpScopes.empty()) {
					scopes.push(tmpScopes.pop());
					int tmpSlotNo = scopes.peek().getSlotsCount();
					scopes.peek().setVarSlot(name, tmpSlotNo);
					scopes.peek().getLambdaObj().addBinding(tmpSlotNo, slotNo.intValue());
					scopes.peek().incSlotsCount();
					slotNo = tmpSlotNo;
				}
				break;
			}
		}
		while (!tmpScopes.empty()) {
			scopes.push(tmpScopes.pop());
		}
		if (slotNo == null) {
			if (callables.containsKey(name) || rt.getFunction(name) != null) {
				slotNo = -1;
			} else if (rt.getVM().getGlobSlot(name) != null) {
				slotNo = rt.getVM().getGlobSlot(name);
			}
		}
		return slotNo;
	}
	// holds functions - names mapped to bodies
	public HashMap<String, CallableObject> callables = new HashMap<String, CallableObject>();
	public HashMap<String, Type> callablesTypes = new HashMap<String, Type>();

	// reference to runtime that uses this Program object
	public Runtime rt;

	public Program(Runtime rt, CompilationOpts opts) {
		this.rt = rt;
		this.compOpts = opts;
	}

	// Starts new function compilation. Sets name and body so they can be easily
	// accessed
	public void CreateFunction(String name, LambdaFuncNode lambdaNode) {
		if (callables.get(name) != null) {
			throw new GSException("Function " + name + " is already defined.");
		}
		Scope s = new Scope(lambdaNode);
		s.setFuncName(name);//currentFuncName = name;
		pushScope(s);
		// this DECLARES function. This should be quite probably done in a more
		// fashionable way, but hey, currently it's "Whatever works".
		callables.put(s.getFuncName(), null);
	}
	public void SetFunctionArgsCount(int arg_count) {
//		System.out.println("Function " + scopes.peek().currentFuncName + " declared as having " + arg_count + " args");
		scopes.peek().setFuncArgsCount(arg_count);//currentFuncArgsCount = arg_count;
	}
	public void SetFuncReturnType(Type t) {
		scopes.peek().setReturnType(t); //returnType = t;
	}
	public void SetFunctionType(Type type) {
		callablesTypes.put(scopes.peek().getFuncName(), type);
		System.out.println("Function " + scopes.peek().getFuncName() + " has now type " + type);
	}
	public void EmitFunction() {
		Scope s = scopes.peek();
		CallableObject co = new CallableObject(s.getFuncName(), s.getFuncArgsCount(), s.getBodyAsArray());
		co.setSlotsCount(s.getSlotsCount());
		if (compOpts.getGenDebug()) {
			co.dbgInfo = new int[s.dbgInfo.size() * 4];
			for (int i = 0 ; i < s.dbgInfo.size() ; ++i) {
				Symbol sym = s.dbgInfo.get(i);
				co.dbgInfo[(i * 4) + 0] = Symbol.getLine  (sym.getStart());
				co.dbgInfo[(i * 4) + 1] = Symbol.getColumn(sym.getStart());
				co.dbgInfo[(i * 4) + 2] = Symbol.getLine  (sym.getEnd()  );
				co.dbgInfo[(i * 4) + 3] = Symbol.getColumn(sym.getEnd()  );
			}
			co.dbgFile = compOpts.getInFile();
		}
		callables.put(s.getFuncName(), co);
	}

	// generate name for anonymous function. This is done by concatenation of
	// current function's name, "$lam" string and next unused anonymous function number.
	// Example:
	// def func() { // function "func"
	//     x = \() { return 1; }(); // function "func$lam0"
	//     return \() { // function "func$lam1"
	//         return \() { return 3 ; } function "func$lam1$lam0
	//     }
	// }
	public String createLambdaName() {
		for (int i = 0 ; i < Integer.MAX_VALUE; ++i) {
			String candidate = scopes.peek().getFuncName() + "$lam" + i;
			if (!callables.containsKey(candidate))
				return candidate;
		}
		throw new GSException("couldn't create name for lambda, name on top: "
			+ scopes.peek().getFuncName());
	}

	public int size() {
		return scopes.peek().getBodySize();
	}

	// append o at the end of currently compiled function
	public void addLast(Object o) {
		scopes.peek().addLast(o); 
	}
	public void addLastDbg(Symbol s) {
		if (compOpts.getGenDebug()) {
			scopes.peek().dbgInfo.addLast(s);
		}
	}
	public void addLabel(String name) {
		scopes.peek().addLabel(name);
	}
	public int  getLabel(String name) {
		return scopes.peek().getLabel(name);
	}

	// put Object o at position 'pos' in current function's body
	public void set(int pos, Object o) {
		scopes.peek().set(pos, o); 
	}

	public int getConstSlot(Object name) {
		return rt.getConstSlot(name);
	}
	public void checkAndSetVarType(String name, Type rhs) {
		Type lhs = getVariableType(name);
		if (! lhs.containsType(rhs)) {
			throw new GSException("TypeError: can't assign " + rhs + " to " + lhs);
		}
	}
	public void setVariableType(String name, Type t) {
		Stack<Scope> tmpScopes = new Stack<Scope>();
		Boolean found = false;
		while (! scopes.empty()) {
			if (!scopes.peek().isVarKnown(name)) {
				tmpScopes.push(scopes.pop());
			} else {
				scopes.peek().varMapTypes.put(name, t);
				found = true;
				System.out.println("var " + name + " type set to " + t);
				while (! tmpScopes.empty()) {
					scopes.push(tmpScopes.pop());
				}
			}
			break;
		}
		if (!found) {
			if (callablesTypes.containsKey(name)) {
				callablesTypes.put(name, t);
				System.out.println("callable " + name + " type set to " + t);
				found = true;
			} else if (rt.getVM().getGlobSlot(name) != null) {
				System.out.println("Would set glob " + name + " type to " + t);
				found = true;
			}
		}
		if (!found) {
			System.out.println("Can't set type of variable " + name + ": not found");
			throw new GSException("Variable " + name + " not found for typeset.");
		}
	}
	public Type getVariableType(String name) {
		Stack<Scope> tmpScopes = new Stack<Scope>();
		Type type = null;
		while (! scopes.empty()) {
			if (! scopes.peek().isVarKnown(name)) {
				tmpScopes.push(scopes.pop());
			} else {
				type = scopes.peek().varMapTypes.get(name);
				while (! tmpScopes.empty()) {
					scopes.push(tmpScopes.pop());
				}
				break;
			}
		}
		while (!tmpScopes.empty()) {
			scopes.push(tmpScopes.pop());
		}
		if (type == null) {
			if (callables.containsKey(name)) {
				type = callablesTypes.get(name);
			} else if (rt.getVM().getGlobSlot(name) != null) {
				type = new Type.Simple(rt.getVM().getGlob(name).getClass());
			}
		}
		if (type == null) {
			throw new GSException("Variable " + name + " not found, can't get type");
		}
		return type;
	}
	public void pushScope(Scope s) {
		scopes.push(s);
	}
	public Scope popScope() {
		return scopes.pop();
	}
}

