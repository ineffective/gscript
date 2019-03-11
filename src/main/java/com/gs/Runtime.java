package com.gs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import com.gs.ast.FuncNode;
import com.gs.ast.Node;
import com.gs.ast.Type;
import com.gs.parser.GameScriptParser;
import com.gs.parser.GameScriptScanner;


public class Runtime {
	public static class CompilationOpts {
		private boolean optMakeDot = false;
		public void makeDot(boolean b) { optMakeDot = b; }
		public boolean getDot() { return optMakeDot; }

		private boolean disass = false;
		public void setDisassemble(boolean b) { disass = b; }
		public boolean getDisassemble() { return disass; }

		private String dotFile = "out"; // it will become "out.dot"
		public void makeDotFile(String s) { dotFile = s; }
		public String getDotFile() { return dotFile; }

		private String inFile = null;
		public void setInFile(String s) { inFile = s; }
		public String getInFile() { return inFile; }

		private String asmFile = null;
		public void setAsmFile(String s) { asmFile = s; }
		public String getAsmFile() { return asmFile; }

		private boolean genDbg = false;
		public void setGenDebug(boolean b) { genDbg = b; }
		public boolean getGenDebug() { return genDbg; }

		private boolean optimize = true;
		public void setOptimize(boolean b) { optimize = b; }
		public boolean getOptimize() { return optimize; }
	}
	private final HashMap<String, CallableObject> callables
		= new HashMap<String, CallableObject>();
	private final HashMap<String, Class<?>> typeAliases
		= new HashMap<String, Class<?>>();
	private void register(String name, CallableObject obj) {
		if (callables.get(name) != null) {
			throw new GSException("Function " + name + " already defined.");
		}
		callables.put(name, obj);
	}
	private final ArrayList<Object> constants = new ArrayList<Object>();
	public int getConstSlot(Object o) {
		int idx = constants.indexOf(o);
		if (idx == -1) {
			idx = constants.size();
			constants.add(o);
		}
		return idx;
	}
	public Object getConst(int idx) {
		return constants.get(idx);
	}
	private final GameScriptParser parser = new GameScriptParser();
	private GSVM virtualMachine = null;
	public Runtime() {
		virtualMachine = new GSVM(this);
		registerTypeAlias("Double", Double.class);
		registerTypeAlias("Float", Float.class);
		registerTypeAlias("Int", Integer.class);
		registerTypeAlias("String", String.class);
	}
	public boolean compileString(String s, CompilationOpts opts) {
		return compile(new StringReader(s), opts);
	}
	public boolean compileFile(String s, CompilationOpts opts) throws FileNotFoundException {
		return compile(new FileReader(s), opts);
	}
	public String getToolTip(Node n) {
		if (n == null) {
			return "NULL";
		}
		if (n instanceof FuncNode) {
			Scope s = ((FuncNode)n).scope;
			if (s != null) {
				return ((FuncNode)n).scope.toString();
			}
		}
		return "Just node";
	}
	public String getNodeId(Node n) {
		if (n == null) {
			return "{ null" + (int)(Math.random() * 10000000)
				+ " [shape=point label=\"null\"] }";
		}
		return "{ " + n.getClass().getSimpleName() + "_" + n.hashCode() + " [label=\""
			+ n.toString() + "\", tooltip=\"" + getToolTip(n) + "\"] }";
	}
	public void dumpNodesTree(Writer out, Node n) throws IOException {
		if (n != null) {
//			String name = getNodeId(n);
			// SHORT CIRCUIT IF BOTH CHILDREN ARE NULL
			if (n.lhs == null && n.rhs == null) {
				return;
			}
			out.write("    " + getNodeId(n) + " -> " + getNodeId(n.lhs));
			out.write("\n");
			dumpNodesTree(out, n.lhs);
			out.write("    " + getNodeId(n) + " -> " + getNodeId(n.rhs));
			out.write("\n");
			dumpNodesTree(out, n.rhs);
		}
	}
	public boolean compile(Reader r, CompilationOpts opts) {
		try {
			GameScriptScanner input = new GameScriptScanner(r);
			Node node = (Node) parser.parse(input);
			if (node == null) {
				System.err.println("parser.parse(input) returned null.");
			}
			Program program = new Program(this, opts);
			if (opts.getDot()) {
				PrintWriter out = new PrintWriter(opts.getDotFile() + ".dot");
				out.println("digraph BST { splines=line; ratio=1.0; ");
				out.println("    nodesep=0.3; ranksep=0.2;");
				dumpNodesTree(out, node);
				out.println("}");
				out.close();
			}
			if (opts.getOptimize()) {
				while (Optimizer.optimize(node)) {
//					System.out.println("Performing next optimizing loop");
				}
			}
			if (opts.getDot()) {
				PrintWriter out = new PrintWriter(opts.getDotFile() + ".opt.dot");
				out.println("digraph BST { splines=line; ratio=1.0; ");
				out.println("    nodesep=0.3; ranksep=0.2;");
				dumpNodesTree(out, node);
				out.println("}");
				out.close();
			}
			node.allocateVariables(program);
			node.emit(program);
			if (opts.getDot()) {
				PrintWriter out = new PrintWriter(opts.getDotFile() + ".opt.scps.dot");
				out.println("digraph BST { splines=line; ratio=1.0; ");
				out.println("    nodesep=0.3; ranksep=0.2;");
				dumpNodesTree(out, node);
				out.println("}");
				out.close();
			}
//			node.processTypes(program);
			PrintWriter out = null;
			if (opts.getDisassemble()) {
				out = new PrintWriter(opts.getAsmFile());
				for (int i = 0 ; i < constants.size() ; ++i) {
					out.println(".object.constant");
					if (constants.get(i) == null) {
						out.println("\tnull");
					} else {
						out.println("\t" + i + " " + constants.get(i).getClass().getCanonicalName()
							+ " " + constants.get(i));
					}
				}
				out.println();
			}
			for (CallableObject co: program.callables.values()) {
				if (opts.getDisassemble()) {
					co.dump(out);
				}
				register(co.name, co);
			}
			if (opts.getDisassemble()) {
				out.close();
			}
			return true;
		}
		catch (GSException e) {
			e.printStackTrace();
			System.err.println("ERROR: " + e.getMessage());
		}
		catch (Throwable e) {
			System.err.println("Compilation failed:");
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	public CallableObject getFunction(String s) {
		return callables.get(s);
	}
	public void put(String s, Object o) throws Exception {
		virtualMachine.putGlobal(s, o);
	}
	public GSVM getVM() {
		return virtualMachine;
	}
	public void registerTypeAlias(String s, Class<?> cls) {
		typeAliases.put(s, cls);
	}
	public Type solveTypeName(String s) {
		if (typeAliases.containsKey(s)) {
			return new Type.Simple(typeAliases.get(s));
		}
		try {
			Class<?> c = Class.forName(s);
			return new Type.Simple(c);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

