package com.gs;

import static com.gs.utils.CompoundAccessor.getCompoundAccessor;
import static com.gs.utils.ConstAccessor.getConstAccessor;
import static com.gs.utils.FieldAccessor.getFieldAccessor;
import static com.gs.utils.VarAccessor.getVarAccessor;

import java.util.HashMap;
import java.util.LinkedList;

import com.gs.utils.ConstAccessor;
import com.gs.utils.IAccessor;
import com.gs.utils.VMCombinator;
import com.gs.utils.VMCompare;
import com.gs.utils.VMIterators;
import com.gs.utils.VMPair;

public enum CMD {
	END(0, 0) {
		public final void exec(GSVM vm) throws Throwable {
		}
	},
	SET(1, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor tgt = ((IAccessor) vm.stack.pop());
			IAccessor val = ((IAccessor) vm.stack.pop());
			tgt.set(val.get());
			vm.stack.push(tgt);
			val.release();
		}
	},
	PUSH(2, 1) {
		public final void exec(GSVM vm) throws Throwable {
			vm.instrPtr++;
			vm.stack.push(getConstAccessor(vm, vm.prog[vm.instrPtr]));
		}
	},
	POP(3, 0) {
		public final void exec(GSVM vm) throws Throwable {
			((IAccessor)vm.stack.pop()).release();
		}
	},
	DOT(4, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc1 = ((IAccessor) vm.stack.pop());
			String fld = (String) acc1.get();
			acc1.release();
			IAccessor o = (IAccessor) vm.stack.pop();
			vm.stack.push(getFieldAccessor(vm, fld, o));
		}
	},
	GETVAR(5, 1) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getVarAccessor(vm, (Integer) vm.prog[++vm.instrPtr]));
		}
	},
	CALL(6, 2) {
		public final void exec(GSVM vm) throws Throwable {
			// CALL is followed by the number of arguments, so get it
			// it is also immediately followed by memoized "Invoker", so skip this slot
			vm.instrPtr += 2;
			int arg_count = (Integer) vm.prog[vm.instrPtr];
			IAccessor target = ((IAccessor) vm.stack.pop());
			// pop all args and use them to construct "formal-args"
			// FIXME: what to do with nulls. They should be allowed, and now
			// they're not
			Object[] args = new Object[arg_count];
			for (int arg = 0; arg < arg_count; ++arg) {
				IAccessor acc = ((IAccessor) vm.stack.pop());
				args[arg] = acc.get();
				acc.release();
			}
			Object rv = target.invoke(args);
			target.release();
			vm.stack.push(getConstAccessor(vm, rv));
		}
	},
	ADD(7, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCombinator.ADD
					.Combine(vm.stack)));
		}
	},
	SUB(8, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCombinator.SUB
					.Combine(vm.stack)));
		}
	},
	MUL(9, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCombinator.MUL
					.Combine(vm.stack)));
		}
	},
	NEG(10, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			Object o = (Object) acc.get();
			acc.release();
			if (o instanceof Integer) {
				vm.stack.push(getConstAccessor(vm, -(Integer) o));
			} else if (o instanceof Double) {
				vm.stack.push(getConstAccessor(vm, -(Double) o));
			} else { // float or bust
				vm.stack.push(getConstAccessor(vm, -(Float) o));
			}
		}
	},
	DIV(11, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCombinator.DIV
					.Combine(vm.stack)));
		}
	},
	MODULUS(12, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCombinator.MODULUS
					.Combine(vm.stack)));
		}
	},
	AND(13, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc1 = ((ConstAccessor) vm.stack.pop());
			IAccessor acc2 = ((ConstAccessor) vm.stack.pop());
			vm.stack.push(getConstAccessor(vm, (Boolean) acc1.get() && (Boolean) acc2.get()));
			acc1.release();
			acc2.release();
		}
	},
	OR(14, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc1 = ((ConstAccessor) vm.stack.pop());
			IAccessor acc2 = ((ConstAccessor) vm.stack.pop());
			vm.stack.push(getConstAccessor(vm, (Boolean) acc1.get() || (Boolean) acc2.get()));
			acc1.release();
			acc2.release();
		}
	},
	EQ(15, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCompare.EQUAL
					.Compare(vm.stack) == 0));
		}
	},
	LT(16, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm,
					VMCompare.REL.Compare(vm.stack) < 0));
		}
	},
	GT(17, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm,
					VMCompare.REL.Compare(vm.stack) > 0));
		}
	},
	LTEQ(18, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm,
					VMCompare.REL.Compare(vm.stack) <= 0));
		}
	},
	GTEQ(19, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm,
					VMCompare.REL.Compare(vm.stack) >= 0));
		}
	},
	NOTEQ(20, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, VMCompare.EQUAL
					.Compare(vm.stack) != 0));
		}
	},
	JMP(21, 1) {
		public final void exec(GSVM vm) throws Throwable {
			int reach = (Integer) vm.prog[vm.instrPtr + 1];
			vm.instrPtr += reach;
		}
	},
	JMPFALSE(22, 1) {
		public final void exec(GSVM vm) throws Throwable {
			int reach = (Integer) vm.prog[vm.instrPtr + 1];
			IAccessor acc = ((IAccessor) vm.stack.pop());
			Boolean b = (Boolean) acc.get();
			acc.release();
			if (!b) {
				vm.instrPtr += reach;
			} else {
				vm.instrPtr++;
			}
		}
	},
	LAMBDA(23, 1) {
		public final void exec(GSVM vm) throws Throwable {
			String name = (String) vm.rt
					.getConst((Integer) vm.prog[++vm.instrPtr]);
			LambdaObject lo = new LambdaObject(
					(CallableObject) vm.getFunc(name), null);
			vm.stack.push(getConstAccessor(vm, lo));
		}
	},
	BIND(24, 2) {
		public final void exec(GSVM vm) throws Throwable {
			int bindTo = (Integer) vm.prog[++vm.instrPtr];
			int bindFrom = (Integer) vm.prog[++vm.instrPtr];
			LambdaObject lo = (LambdaObject) ((ConstAccessor) vm.stack.peek())
					.get();
			// FIXME: actually instead of "from" we should do something like vm:
			lo.bind(bindTo, vm.getVar(bindFrom));
		}
	},
	MKLIST(25, 1) {
		public final void exec(GSVM vm) throws Throwable {
			vm.instrPtr++;
			int arg_count = (Integer) vm.prog[vm.instrPtr];
			LinkedList<Object> list = new LinkedList<Object>();
			for (int arg = 0; arg < arg_count; ++arg) {
				IAccessor acc = ((IAccessor) vm.stack.pop()); 
				list.add(0, acc.get());
				acc.release();
			}
			vm.stack.push(getConstAccessor(vm, list));
		}
	},
	PUSHCONST(26, 1) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, vm.rt
					.getConst((Integer) vm.prog[++vm.instrPtr])));
		}
	},
	GETGLOB(27, 1) {
		public final void exec(GSVM vm) throws Throwable {
			String name = (String) vm.rt
					.getConst((Integer) vm.prog[++vm.instrPtr]);
			vm.stack.push(getConstAccessor(vm, vm.getGlob(name)));
		}
	},
	GETFUNC(28, 1) {
		public final void exec(GSVM vm) throws Throwable {
			String name = (String) vm.rt
					.getConst((Integer) vm.prog[++vm.instrPtr]);
			vm.stack.push(getConstAccessor(vm, vm.getFunc(name)));
		}
	},
	SUBSCRIPT(29, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor at = ((IAccessor) vm.stack.pop());
			IAccessor from = ((IAccessor) vm.stack.pop());
			vm.stack.push(getCompoundAccessor(vm, at.get(), from));
			at.release();
		}
	},
	MKARRAY(30, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			int arg_count = (Integer) acc.get();
			acc.release();
			Object[] arr = new Object[arg_count];
			vm.stack.push(getConstAccessor(vm, arr));
		}
	},
	LISTTOARRAY(32, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			Object[] arr = (Object[]) acc.get();
			acc.release();
			acc = ((IAccessor) vm.stack.pop());
			@SuppressWarnings("unchecked")
			LinkedList<Object> list = (LinkedList<Object>) acc.get();
			acc.release();
			for (int i = 0; i < arr.length; ++i) {
				arr[i] = list.get(i);
			}
			vm.stack.push(getConstAccessor(vm, arr));
		}
	},
	MKMAP(31, 0) {
		public final void exec(GSVM vm) throws Throwable {
			vm.stack.push(getConstAccessor(vm, new HashMap<Object, Object>()));
		}
	},
	MKMAPENTRY(33, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			Object v = acc.get();
			acc.release();
			acc = ((IAccessor) vm.stack.pop());
			Object k = acc.get();
			acc.release();
			vm.stack.push(getConstAccessor(vm, new VMPair(k, v)));
		}
	},
	LISTTOMAP(34, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			@SuppressWarnings("unchecked")
			HashMap<Object, Object> map = (HashMap<Object, Object>) acc.get();
			acc.release();
			acc = ((IAccessor) vm.stack.pop());
			@SuppressWarnings("unchecked")
			LinkedList<Object> list = (LinkedList<Object>) acc.get();
			acc.release();
			for (Object obj : list) {
				VMPair p = (VMPair) obj;
				map.put(p.l, p.r);
			}
			vm.stack.push(getConstAccessor(vm, map));
		}
	},
	MKITER(35, 0) {
		public final void exec(GSVM vm) throws Throwable {
			IAccessor acc = ((IAccessor) vm.stack.pop());
			Object o = acc.get();
			acc.release();
			vm.stack.push(getConstAccessor(vm, VMIterators.getIterable(o)));
		}
	};

	private final int num;
	final int args;

	private CMD(int num, int args) {
		this.num = num;
		this.args = args;
	}

	public int getNum() {
		return num;
	}

	public int getArgs() {
		return args;
	}

	public abstract void exec(GSVM vm) throws Throwable;
}
