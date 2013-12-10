package com.elasticpath.tools.sync.utils;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Provides java.lang.reflect.Method without consideration of declaring class.
 */
public class MethodComparator implements Comparator<Method> {

	@Override
	public int compare(final Method object1, final Method object2) {
		int result = 0;
		result = object1.getName().compareTo(object2.getName());
		if (result != 0) {
			return result;
		}
		if (object1.getReturnType() != object2.getReturnType()) {
			return -1;
		}
		/* Avoid unnecessary cloning */
		Class< ? >[] params1 = object1.getParameterTypes();
		Class< ? >[] params2 = object2.getParameterTypes();
		if (params1.length == params2.length) {
			for (int i = 0; i < params1.length; i++) {
				if (params1[i] != params2[i]) {
					return -1;
				}
			}
			return 0;
		}
		return -1;
	}
}