package com.gs.ast;

import java.util.HashMap;
import java.util.LinkedList;

import com.gs.CMD;
import com.gs.GSException;

public class Type {
	public Boolean containsType(Type t) {
		return true;
	}
	public static class Undefined extends Type {
		
	}
	public static final Type UNDEFINED = new Undefined();
	public static class Simple extends Type {
		public final Class<?> cls;
		public Simple(Class<?> cls) {
			this.cls = cls;
		}
		@Override
		public String toString() {
			return cls.getName();
		}
		public Boolean containsType(Type t) {
			if (! (t instanceof Simple)) {
				return false;
			}
			Simple s = (Simple)t;
			if (s.cls != cls) {
				return false;
			}
			return true;
		}
	}

	public static class AnyOf extends Type {
		protected final LinkedList<Type> types = new LinkedList<Type>();
		public AnyOf() {
		}
		public AnyOf(Class<?> cl, Class<?>... types) {
			this.types.add(new Simple(cl));
			for (Class<?> c: types) {
				this.types.add(new Simple(c));
			}
		}
		public AnyOf(Type tp, Type... types) {
			this.types.add(tp);
			for (Type t: types) {
				this.types.add(t);
			}
		}
		public AnyOf(Type t, AnyOf a) {
			types.add(t);
			if (a != null) {
				for (Type tp: a.types) {
					types.add(tp);
				}
			}
		}
		@Override
		public String toString() {
			String s = "AnyOf[";
			for (Type t: types) {
				s += t.toString() + ", ";
			}
			return s + "]";
		}
		public Boolean containsType(Type t) {
			if (t instanceof AnyOf) {
				for (Type chk: types) {
					if (t.containsType(chk)) {
						return true;
					}
				}
				return false;
			}
			for (Type chk: types) {
				if (chk.containsType(t)) {
					return true;
				}
			}
			return false;
		}
	}
	public static class List extends Type {
		protected final LinkedList<Type> types = new LinkedList<Type>();
		public List() {
		}
		public List(Type t, List rest) {
			types.add(t);
			if (rest != null) {
				types.addAll(rest.types);
			}
		}
	}
	public static class Function extends Type {
		private final Type returnType;
		private final List args;
		public Function(Type rt, List lst) {
			returnType = rt;
			args = lst;
		}
		public Type getReturnType() {
			return returnType;
		}
		public List getArgsTypes() {
			return args;
		}
		@Override
		public String toString() {
			String s = "F(";
			for (Type t: args.types) {
				s += t.toString() + ", ";
			}
			return s + ") => " + returnType.toString();
		}
	}
	public static class Any extends Type {
		@Override
		public String toString() {
			return "ANY";
		}
	}
	public static class Error extends Type {
		@Override
		public String toString() {
			return "ERROR";
		}
	}
	public static class Mock extends Type {
		private final String mockName;
		public Mock(String s) {
			mockName = s;
		}
		@Override
		public String toString() {
			return mockName;
		}
	}
	public static abstract class Combine extends Type {
		public final Type lhs, rhs;
		public Combine(Type lhs, Type rhs) {
			this.lhs = lhs;	
			this.rhs = rhs;
		}
		public abstract Type getResultType(Type t1, Type t2);
	}
	public static final Combine ADDType = new Combine(new AnyOf(Integer.class, Double.class, Float.class, String.class), new AnyOf(Integer.class, Double.class, Float.class, String.class)) {
		public Type getResultType(Type t1, Type t2) {
			if (!lhs.containsType(t1)) {
				throw new GSException("ADDType: " + lhs + " does not contain " + t1);
			}
			if (!rhs.containsType(t2)) {
				throw new GSException("ADDType: " + rhs + " does not contain " + t2);
			}
			Class<?> c1 = ((Simple)t1).cls;
			Class<?> c2 = ((Simple)t2).cls;
			if (c1 == String.class || c2 == String.class) {
				return new Simple(String.class);
			}
			if (c1 == Double.class || c2 == Double.class) {
				return new Simple(Double.class);
			}
			if (c1 == Float.class || c2 == Float.class) {
				return new Simple(Float.class);
			}
			return new Simple(Integer.class);
		}
	};
	public static final Combine MULType = new Combine(new AnyOf(Integer.class, Double.class, Float.class), new AnyOf(Integer.class, Double.class, Float.class)) {
		public Type getResultType(Type t1, Type t2) {
			if (!lhs.containsType(t1)) {
				throw new GSException("MULType: " + lhs + " does not contain " + t1);
			}
			if (!rhs.containsType(t2)) {
				throw new GSException("MULType: " + rhs + " does not contains " + t2);
			}
			Class<?> c1 = ((Simple)t1).cls;
			Class<?> c2 = ((Simple)t2).cls;
			if (c1 == Double.class || c2 == Double.class) {
				return new Simple(Double.class);
			}
			if (c1 == Float.class || c2 == Float.class) {
				return new Simple(Float.class);
			}
			return new Simple(Integer.class);
		}
	};
	public static final Combine COMPAREType = new Combine(new AnyOf(Integer.class, Double.class, Float.class, String.class), new AnyOf(Integer.class, Double.class, Float.class, String.class)) {
		public Type getResultType(Type t1, Type t2) {
			if (!lhs.containsType(t1)) {
				throw new GSException("COMPAREType: " + lhs + " does not contain " + t1);
			}
			if (!rhs.containsType(t2)) {
				throw new GSException("COMPAREType: " + rhs + " does not contains " + t2);
			}
			return new Simple(Boolean.class);
		}
	};
	public static final Combine EQUALType = new Combine(new AnyOf(Integer.class, Double.class, Float.class, String.class, Boolean.class), new AnyOf(Integer.class, Double.class, Float.class, String.class, Boolean.class)) {
		public Type getResultType(Type t1, Type t2) {
			if (!lhs.containsType(t1)) {
				throw new GSException("EQUALType: " + lhs + " does not contain " + t1);
			}
			if (!rhs.containsType(t2)) {
				throw new GSException("EQUALType: " + rhs + " does not contains " + t2);
			}
			Class<?> c1 = ((Simple)t1).cls;
			Class<?> c2 = ((Simple)t2).cls;
			if (c1 == Boolean.class && c2 == Boolean.class) {
				return new Simple(Boolean.class);
			}
			if (c1 == String.class && c2 == String.class) {
				return new Simple(Boolean.class);
			}
			if (c1 == String.class || c2 == String.class) {	 // both strings served above, so only one is stringa
				throw new GSException("EQUALType: can't compare " + c1.getName() + " and " + c2.getName());
			}
			return new Simple(Boolean.class);
		}
	};
	public static final Combine BOOLType = new Combine(new Simple(Boolean.class), new Simple(Boolean.class)) {
		public Type getResultType(Type t1, Type t2) {
			if (!lhs.containsType(t1)) {
				throw new GSException("BOOLType: " + lhs + " does not contain " + t1);
			}
			if (!rhs.containsType(t2)) {
				throw new GSException("BOOLType: " + rhs + " does not contains " + t2);
			}
			return new Simple(Boolean.class);
		}
	};
	public static final HashMap<CMD, Combine> operatorTypes;
	static {
		operatorTypes = new HashMap<CMD, Combine>();
		operatorTypes.put(CMD.ADD, ADDType);
		operatorTypes.put(CMD.MUL, MULType);
		operatorTypes.put(CMD.DIV, MULType);
		operatorTypes.put(CMD.SUB, MULType); // no string support, so identical to MUL
		operatorTypes.put(CMD.MODULUS, MULType);
		operatorTypes.put(CMD.EQ, EQUALType);
		operatorTypes.put(CMD.NOTEQ, EQUALType);
		operatorTypes.put(CMD.LT, COMPAREType);
		operatorTypes.put(CMD.GT, COMPAREType);
		operatorTypes.put(CMD.LTEQ, COMPAREType);
		operatorTypes.put(CMD.GTEQ, COMPAREType);
		operatorTypes.put(CMD.AND, BOOLType);
		operatorTypes.put(CMD.OR, BOOLType);
	}
}

