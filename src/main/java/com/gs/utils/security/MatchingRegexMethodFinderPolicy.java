package com.gs.utils.security;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class MatchingRegexMethodFinderPolicy implements MethodFinderPolicy {

	private Pattern p;

	public MatchingRegexMethodFinderPolicy(String regex) {
		p = Pattern.compile(regex);
	}

	@Override
	public boolean checkMethod(Method method) {
		if (p.matcher(method.getName()).matches()) {
			return true;
		}
		return false;
	}

}
