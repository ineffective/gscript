package com.gs.utils;

import java.util.ArrayDeque;

public abstract class VMCombinator {
	public static final VMPair fetch(ArrayDeque<Object> s) throws Exception {
		IAccessor acc1 = null;
		IAccessor acc2 = null;
		Object r = (acc1 = ((IAccessor)s.pop())).get();
		Object l = (acc2 = ((IAccessor)s.pop())).get();
		acc1.release();
		acc2.release();
		return new VMPair(l, r);
	}
	public Object Combine(ArrayDeque<Object> s) throws Exception {
		VMPair p = fetch(s);
		if (p.l instanceof String || p.r instanceof String) {
			return Combine(p.l.toString(), p.r.toString());
		}
		if (p.l instanceof Double || p.l instanceof Float || p.r instanceof Double || p.l instanceof Float) {
			return Combine(((Number)p.l).doubleValue(), ((Number)p.r).doubleValue());
		}
		if (p.l instanceof Number && p.r instanceof Number) { // integral types
			return Combine(((Number)p.l).longValue(), ((Number)p.r).longValue());
		}
		throw new RuntimeException("left or right argument not of Integer or Double type: " + p.l.getClass() + " and " + p.r.getClass());
	}

	public abstract Long Combine(Long l, Long r);
	public abstract Double Combine(Double l, Double r);

	public String Combine(String l, String r) throws Exception {
		throw new RuntimeException("Combinator not defined for strings");
	}
	public static final VMCombinator ADD = new VMCombinator() {
		public Long Combine(Long l, Long r) { return l + r; }
		public Double Combine(Double l, Double r) { return l + r; }
		public String Combine(String l, String r) { return l + r; }
	};
	public static final VMCombinator SUB = new VMCombinator() {
		public Long Combine(Long l, Long r) { return l - r; }
		public Double Combine(Double l, Double r) { return l - r; }
	};
	public static final VMCombinator MUL = new VMCombinator() {
		public Long Combine(Long l, Long r) { return l * r; }
		public Double Combine(Double l, Double r) { return l * r; }
	};
	public static final VMCombinator DIV = new VMCombinator() {
		public Long Combine(Long l, Long r) { return l / r; }
		public Double Combine(Double l, Double r) { return l / r; }
	};
	public static final VMCombinator MODULUS = new VMCombinator() {
		public Long Combine(Long l, Long r) { return l % r; }
		public Double Combine(Double l, Double r) { return l % r; }
	};
}

