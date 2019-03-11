package com.gs.utils.security;

import java.lang.reflect.Method;

public class AllowAllMethodFinderPolicy implements MethodFinderPolicy {

	@Override
	public boolean checkMethod(Method method) {
		return true;
	}
}
