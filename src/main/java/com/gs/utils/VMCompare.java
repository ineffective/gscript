package com.gs.utils;

import java.util.ArrayDeque;

public class VMCompare {
	public int Compare(ArrayDeque<Object> s) throws Throwable {
		VMPair p = fetch(s);
		return Compare(p.l, p.r);
	}
	public VMPair fetch(ArrayDeque<Object> s) throws Throwable {
		IAccessor acc1 = ((IAccessor)s.pop());
		IAccessor acc2 = ((IAccessor)s.pop());
		Object r = acc1.get();
		Object l = acc2.get();
		acc1.release();
		acc2.release();
		return new VMPair(l, r);
	}
	@SuppressWarnings("unchecked")
	public int Compare(Object l, Object r) throws Throwable {
		if (l instanceof Number && r instanceof Number) {
			double lhs = ((Number)l).doubleValue();
			double rhs = ((Number)r).doubleValue();
			return ((lhs < rhs) ? - 1 : (lhs > rhs) ? 1 : 0);
		}
		if (l instanceof Comparable<?> && r instanceof Comparable<?>) {
			return ((Comparable<Comparable<?>>)l).compareTo((Comparable<?>)r);
		}
		throw new Exception("objects incomparable: " + l + " vs " + r);
	}
	public static final VMCompare EQUAL = new VMCompare() {
		@SuppressWarnings("unchecked")
		public int Compare(ArrayDeque<Object> s) throws Throwable {
			VMPair p = fetch(s);
			if (p.l == null || p.r == null) {
				if (p.l == null && p.r == null) {
					return 0;
				} else {
					return 1;
				}
			}
			if (p.l instanceof Number && p.r instanceof Number) {
				double lhs = ((Number)p.l).doubleValue();
				double rhs = ((Number)p.r).doubleValue();
				return ((lhs < rhs) ? - 1 : (lhs > rhs) ? 1 : 0);
			}
			if (p.l instanceof Comparable<?> && p.r instanceof Comparable<?>) {
				return ((Comparable<Comparable<?>>)p.l).compareTo((Comparable<?>)p.r);
			}
			// ok, relation should be returned, but if they are not equal,
			// then that's all that we know
			return p.l.equals(p.r) ? 0 : 1; 
		}
	};
	public static final VMCompare REL = new VMCompare();
}
