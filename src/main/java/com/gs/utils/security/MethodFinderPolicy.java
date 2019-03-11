package com.gs.utils.security;

import java.lang.reflect.Method;

public interface MethodFinderPolicy {

	boolean checkMethod(Method method);

}
