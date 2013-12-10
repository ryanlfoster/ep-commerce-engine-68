package com.elasticpath.commons.util;

/**
 * Interface for mocking methods.
 * 
 * TODO: good comment here, how to use it.
 */
public interface MockInterface {
	
	/**
	 * The mock method.
	 * 
	 * @param objects the array of objects (arguments)
	 * @param <T> template arg.
	 * @return object return value
	 */
	<T> T method(Object ... objects);
}
