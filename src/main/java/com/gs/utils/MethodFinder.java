package com.gs.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.gs.utils.security.MethodFinderPolicy;

public class MethodFinder {
	private MethodFinderPolicy mfp;

	public MethodFinder(MethodFinderPolicy mfp) {
		this.mfp = mfp; 
	}

	/**
	 * Scans class and it's super class and implemented interfaces for matching
	 * method.
	 * 
	 * As long as method is found in currently scanned cls, check again in
	 * superclass and implemented interfaces. We do it because first found method
	 * could be defined in an inner class, where we can't access it. Simplest case
	 * is one that forced me to do it this way, namely calling "size()" method on
	 * value returned from HashMap.keySet() method. HashMap$KeySet class has
	 * "package" visibility, so it's method aren't available to us directly, we have
	 * to call it using via base class' (AbstractSet or some such) method which is
	 * named identically and basically COULD be called this way, if only returned
	 * method wasn't so damn specific.
	 */
	public Method findMethod(Class<?> cls, String name, Class<?>[] fa) {
		// System.out.println("Checking class " + cls + " for method " + name);
		// since it is called recursively, we want to stop some day, and when we are
		// passed null (so most getSuperclass was called on Object.class or something
		// similar)
		if (cls == null) {
			return null;
		}
		Method m = null;
		if ((m = findMethod(cls.getSuperclass(), name, fa)) != null) {
			if (Modifier.isPublic(cls.getSuperclass().getModifiers())) {
				return m;
			}
			m = null;
		}
		// ok, if we're here, then m is null. so check if cls is public. it must
		// be public, because otherwise we won't be able to call it - we are definitely
		// in different package. if class isn't public, then check interfaces.
		if (!Modifier.isPublic(cls.getModifiers())) {
			// System.out.println("Class is not public, and superclasses do not contain
			// method " + name);
			// System.out.println("Checking all interfaces");
			for (Class<?> iface : cls.getInterfaces()) {
				if ((m = findMethod(iface, name, fa)) != null) {
					return m;
				}
			}
		}
		return findMethodInClass(cls, name, fa);
	}

	private Method findMethodInClass(Class<?> cls, String name, Class<?>[] fa) {
		Method m = null;
		// scan all methods and move plausible candidates to the start of an
		// array
		Method[] mm = cls.getMethods();
		int n = 0;
		for (int i = 0; i < mm.length; ++i) {
			if (checkMethod(mm[i], name, fa)) {
				mm[n++] = mm[i];
			}
		}
		if (n > 1) {
			m = selectBestCandidate(mm, n, fa);
			// return mm[0];
			// System.out.println("Caveat: we have to perform more specific test. n == " +
			// n);
			// System.out.println("class: " + cls + "\nname: " + name);
			// for (int i = 0; i < n; ++i) {
			// System.out.println(mm[i]);
			// }
		} else if (n > 0) {
			m = mm[0];
		}
		return m;
	}

	private static final Method selectBestCandidate(Method[] mm, int n, Class<?>[] fa) {
		// first assumption is that n > 1, so at least 2, so we can safely
		// do the following:
		Method m = mm[0];
		for (int i = 1; i < n; ++i) {
			m = selectBestCandidate(m, mm[i], fa);
		}
		return m;
	}

	private static final Method selectBestCandidate(Method lhs, Method rhs, Class<?>[] formalArgs) {
		// System.out.println("compare:");
		// System.out.println(lhs);
		// System.out.println(rhs);
		// now, compare these two methods.
		Class<?> lhsFormalArgs[] = new Class<?>[formalArgs.length];
		Class<?> rhsFormalArgs[] = new Class<?>[formalArgs.length];
		expandArgs(lhs, lhsFormalArgs);
		expandArgs(rhs, rhsFormalArgs);
		Method rv = lhs; // by default, select left hand side method
		for (int i = 0; i < lhsFormalArgs.length; ++i) {
			// now, since fa[i] can be assigned to both types (we checked it earlier), we
			// don't have to
			// check it now. what we want, it to select MORE SPECIFIC TYPE. This means that
			// one of
			// types can be assigned from the other one, but not the other way around. So
			// here it comes:
			if (assignableFrom(lhsFormalArgs[i], rhsFormalArgs[i]) && !assignableFrom(rhsFormalArgs[i], lhsFormalArgs[i])) {
				// so we can assign rhs to lhs, but not the other way around, this means that
				// RHS is an answer
				return rhs; // short-circuit
			}
			if (assignableFrom(rhsFormalArgs[i], lhsFormalArgs[i]) && !assignableFrom(lhsFormalArgs[i], rhsFormalArgs[i])) {
				// check the opposite
				return lhs;
			}
		}
		return rv;

	}

	// copy parameter types to lhsfa array, if more parameters requested than
	// method has and it is varArgs method (and because of earlier checks we're
	// pretty sure it is), clone type of trailing array type.
	private static final void expandArgs(Method method, Class<?>[] formalArgsTypes) {
		int paramCount = method.getParameterCount();
		Class<?>[] paramTypes = method.getParameterTypes();

		for (int i = 0; i < formalArgsTypes.length; ++i) {
			if (method.isVarArgs() && i >= paramCount) {
				formalArgsTypes[i] = paramTypes[paramCount - 1].getComponentType();
			} else {
				formalArgsTypes[i] = paramTypes[i];
			}
		}
	}

	private boolean checkMethod(Method method, String name, Class<?>[] fa) {
		// if method has different name, no match
		if (!method.getName().equals(name)) {
			return false;
		}
		if (!mfp.checkMethod(method)) {
			return false;
		}
		// so method is either varargs or parameter count matches. check if
		// types are ok.
		int argcount = method.getParameterCount();
		Class<?> pt[] = method.getParameterTypes();
		if (method.isVarArgs()) {
			// argcount - 1, because there is an array at the end, so we can
			// call method like
			// m(Object... objects) with no parameters, but we can't call
			// m(String s, Object... o)
			// with no parameters, at least <s> must be provided.
			if (argcount - 1 > fa.length) {
				return false;
			}
			// this is varargs method, so check args from 0 to n - 1, and then
			// check if remaining ones qualify
			for (int i = 0; i < argcount - 1; ++i) {
				if (!assignableFrom(pt[i], fa[i])) {// !pt[i].isAssignableFrom(fa[i]))
													// {
					return false;
				}
			}
			// if we are here, then all required parameters are fine, check last
			// parameter
			Class<?> lastparam = pt[pt.length - 1].getComponentType();
			for (int i = argcount - 1; i < fa.length; ++i) {
				if (!lastparam.isAssignableFrom(fa[i])) {
					return false;
				}
			}
			return true;
		} else {
			if (argcount != fa.length) {
				return false; // too much or too few parameters, no match
			}
			for (int i = 0; i < argcount; ++i) {
				if (!assignableFrom(pt[i], fa[i])) {// pt[i].isAssignableFrom(fa[i]))
													// {
					return false;
				}
			}
			return true;
		}
	}

	private static boolean assignableFrom(Class<?> to, Class<?> from) {
		if (to.isAssignableFrom(from)) {
			return true;
		}
		if (to.isPrimitive()) {
			// byte, chart, short, int, long
			// float, double
			// boolean
			if (to == boolean.class) {
				if (from == Boolean.class)
					return true;
				return false; // and we're done with boolean
			}
			if (!Number.class.isAssignableFrom(from)) {
				return false;
			}
			if (to == long.class) {
				if (from == Long.class || from == Integer.class || from == Short.class || from == Byte.class
						|| from == Character.class) {
					return true;
				}
			}
			if (to == int.class) {
				if (from == Integer.class || from == Short.class || from == Byte.class || from == Character.class) {
					return true;
				}
			}
			if (to == short.class) {
				if (from == Short.class || from == Byte.class || from == Character.class) {
					return true;
				}
			}
			if (to == char.class) {
				if (from == Short.class || from == Byte.class || from == Character.class) {
					return true;
				}
			}
			if (to == byte.class) {
				if (from == Byte.class) {
					return true;
				}
			}
			if (to == double.class) {
				if (from == Float.class || from == Double.class) {
					return true;
				}
			}
			if (to == float.class) {
				if (from == Float.class) {
					return true;
				}
			}
		}
		return false;
	}
}
