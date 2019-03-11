package com.gs;

import beaver.Symbol;

import com.gs.ast.BinNode;
import com.gs.ast.IfNode;
import com.gs.ast.ListNode;
import com.gs.ast.Node;
import com.gs.ast.PushConstNode;

public class Optimizer {
	@SuppressWarnings("unused")
	private static boolean IsChildlessList(Node n) {
		if (n == null) { 
			return false;
		}
		if (n instanceof ListNode) {
			if (n.lhs == null && n.rhs == null) {
				return true;
			}
		}
		return false;
	}
	@SuppressWarnings("unused")
	private static boolean IsLeftOnlyList(Node n) {
		if (n == null) {
			return false;
		}
		if (n instanceof ListNode) {
			if (n.lhs != null && n.rhs == null) {
				return true;
			}
		}
		return false;
	}
	private static boolean IsLRSameClass(Node n, Class<?> cls) {
		if (n == null) { return false; }
		if (n.lhs == null || n.rhs == null) { return false; }
		if (cls.isInstance(n.lhs.name) && cls.isInstance(n.rhs.name)) { return true; }
		return false;
	}
	private static Node mkpn(Node n, Integer i) { return new PushConstNode(n, i); }
	private static Node mkpn(Node n, Double  d) { return new PushConstNode(n, d); }
	@SuppressWarnings("unused")
	private static Node mkpn(Node n, String  s) { return new PushConstNode(n, s); }
	private static Node mkpn(Node n, Boolean b) { return new PushConstNode(n, b); }

	private static Node ConstantInIfNode(Node n) {
		if (n == null) { return null; }
		if (! (n instanceof IfNode)) { return null; }
		if (n.lhs != null && n.lhs instanceof PushConstNode && n.lhs.name != null && n.lhs.name instanceof Boolean) {
			boolean b = (Boolean)n.lhs.name;
			// if b == true, then we won't use n.rhs.rhs, so set n.rhs to null;
			// if b == false, then we won't use n.rhs, we'll use n.rhs.rhs instead.
			if (b) {
				return n.rhs.lhs;     // return 'then' node
			} else {
				if (n.rhs.rhs == null) {
					// if there's nothing to return, return empty ListNode, it will be optimized out.
					return new ListNode(new Symbol((short)0, 0, 0), "", null, null);
				} else {
					return n.rhs.rhs; // there is actual "ELSE" node, use it.
				}
			}
		}
		return null;
	}
	private static Node FoldConstant(Node n) {
		if (n == null) { return null; }
		if (! (n instanceof BinNode)) { return null; }
		if (! (n.lhs instanceof PushConstNode && n.rhs instanceof PushConstNode) ) {
			return null;
		}
		BinNode b = (BinNode)n;
		if (IsLRSameClass(n, Integer.class)) {
			int il = (Integer)b.lhs.name;
			int ir = (Integer)b.rhs.name;
			switch (b.cmd) {
			case ADD: return mkpn(b, il + ir);
			case SUB: return mkpn(b, il - ir);
			case MUL: return mkpn(b, il * ir);
			case DIV: return mkpn(b, il / ir);
			case EQ: return mkpn(b, il == ir);
			case NOTEQ: return mkpn(b, il != ir);
			case LT: return mkpn(b, il < ir);
			case GT: return mkpn(b, il > ir);
			case LTEQ: return mkpn(b, il <= ir);
			case GTEQ: return mkpn(b, il >= ir);
			case MODULUS: return mkpn(b, il % ir);
			default:
			}
		}
		if (IsLRSameClass(n, Double.class)) {
			double il = (Double)b.lhs.name;
			double ir = (Double)b.rhs.name;
			switch (b.cmd) {
			case ADD: return mkpn(b, il + ir);
			case SUB: return mkpn(b, il - ir);
			case MUL: return mkpn(b, il * ir);
			case DIV: return mkpn(b, il / ir);
			case EQ: return mkpn(b, il == ir);
			case NOTEQ: return mkpn(b, il != ir);
			case LT: return mkpn(b, il < ir);
			case GT: return mkpn(b, il > ir);
			case LTEQ: return mkpn(b, il <= ir);
			case GTEQ: return mkpn(b, il >= ir);
			case MODULUS: return mkpn(b, il % ir);
			default:
			}
		}
		if (IsLRSameClass(n, String.class)) {
			switch (b.cmd) {
			case ADD: return new PushConstNode(b, b.lhs.name.toString() + b.rhs.name.toString());
			default:
			}
		}
		if (IsLRSameClass(n, Boolean.class)) {
			boolean il = (Boolean)b.lhs.name;
			boolean ir = (Boolean)b.rhs.name;
			switch (b.cmd) {
			case NOTEQ: return mkpn(b, il != ir);
			case EQ: return mkpn(b, il == ir);
			case OR: return mkpn(b, il || ir);
			case AND: return mkpn(b, il && ir);
			default:
			}
		}
		return null;
	}
	public static boolean optimize(Node n) {
		boolean anyOptsDone = false;
		if (n == null) { return anyOptsDone; }
		if (n.lhs != null) {
			if (optimize(n.lhs)) {
				anyOptsDone = true;
			}
			Node nc = null;
			do {
				nc = FoldConstant(n.lhs);
				if (nc != null) {
					n.lhs = nc;
					anyOptsDone = true;
				}
			} while (nc != null);
			nc = ConstantInIfNode(n.lhs);
			if (nc != null) {
				n.lhs = nc;
				anyOptsDone = true;
			}
			if (IsChildlessList(n.lhs)) {
				n.lhs = null;
				anyOptsDone = true;
			}
		}
		if (n.rhs != null) {
			if (optimize(n.rhs)) {
				anyOptsDone = true;
			}
			Node nc = null;
			do {
				nc = FoldConstant(n.rhs);
				if (nc != null) {
					n.rhs = nc;
					anyOptsDone = true;
				}
			} while (nc != null);
			nc = ConstantInIfNode(n.rhs);
			if (nc != null) {
				n.rhs = nc;
				anyOptsDone = true;
			}
			if (IsChildlessList(n.rhs)) {
				n.rhs = null;
				anyOptsDone = true;
			}
		}
		if (n instanceof ListNode && n.rhs != null && n.rhs instanceof ListNode && n.rhs.lhs == null && n.rhs.rhs != null) {
			n.rhs = n.rhs.rhs;
			anyOptsDone = true;
		}
		return anyOptsDone;
	}
}

