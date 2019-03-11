package com.gs;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import com.gs.ast.LambdaFuncNode;
import com.gs.ast.Type;

import beaver.Symbol;

public class Scope {
	private String currentFuncName = null;
	private int currentFuncArgsCount = 0;
	private HashMap<String, Integer> varMap = new HashMap<String, Integer>();
	public HashMap<String, Type> varMapTypes = new HashMap<String, Type>();
//	private LinkedList<String> argsNames = new LinkedList<String>();
	private HashMap<String, Integer> labels = new HashMap<>();
	private Type returnType;
	// number of slots that this function/lambda uses.
	private int slotsCount = 0;
	// lambda object that will be bound
	private LambdaFuncNode lamObj = null;
	public Scope() {
		this(null);
	}
	public Scope(LambdaFuncNode lambda) {
		lamObj = lambda;
	}
	// keeps track of compiled function bodies. This is required since when we compile function
	// we can compile lambda inside it, and then lambda inside this lambda and so on, and
	// after this is done we must complete functions that weren't procesed yet.
	private LinkedList<Object> body = new LinkedList<Object>();
	public LinkedList<Symbol> dbgInfo = new LinkedList<Symbol>();
	@Override
	public String toString() {
		String desc = "Scope: { name: " + currentFuncName + ", varMap: { ";
		String varmap = "";
		String catchar = "";
		for (Map.Entry<String, Integer> e: varMap.entrySet()) {
			varmap += catchar + "{ " + e.getKey() + " -> " + e.getValue() + " }";
			catchar = ", ";
		}
		desc += varmap;
		desc += " } slotsCount: " + slotsCount + " }";
		return desc;
	}
	public void addLabel(String name) {
		if (labels.containsKey(name)) {
			throw new RuntimeException("Label " + name + " already exists");
		}
		labels.put(name, body.size());
	}
	public int getLabel(String name) {
		return labels.get(name);
	}
	public boolean isVarKnown(String name) {
		return varMap.containsKey(name);
	}
	public Integer allocateVarSlot(String name) {
		Integer slotNum = slotsCount++;
		varMap.put(name, slotNum);
		return slotNum;
	}
	public Integer getVarSlot(String name) {
		return varMap.get(name);
	}
	public Integer getSlotsCount() {
		return slotsCount;
	}
	public void setVarSlot(String name, Integer slot) {
		varMap.put(name, slot);
	}
	public LambdaFuncNode getLambdaObj() {
		return lamObj;
	}
	public void incSlotsCount() {
		slotsCount++;
	}
	public void setFuncName(String name) {
		currentFuncName = name;
	}
	public String getFuncName() {
		return currentFuncName;
	}
	public void setFuncArgsCount(int count) {
		currentFuncArgsCount = count;
	}
	public int getFuncArgsCount() {
		return currentFuncArgsCount;
	}
	public void setReturnType(Type t) {
		returnType = t;
	}
	public Object[] getBodyAsArray() {
		return body.toArray(new Object[body.size()]);
	}
	public int getBodySize() {
		return body.size();
	}
	public void addLast(Object o) {
		body.addLast(o);
	}
	public void set(int pos, Object o) {
		body.set(pos, o);
	}
}

