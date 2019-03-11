package com.gs.utils.security;

import java.lang.reflect.Method;

public class BlockReflectionMethodFinderPolicy implements MethodFinderPolicy {

	@Override
	public boolean checkMethod(Method method) {
		if (method.getName().equals("getClass")) {
			return false;
		}
		return true;
	}

}
