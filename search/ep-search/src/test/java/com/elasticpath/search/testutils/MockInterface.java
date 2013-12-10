package com.elasticpath.search.testutils;

/**
 * Helper interface for better testing.
 */
public interface MockInterface {
	/**
	 * Mock method with two parameters.
	 * 
	 * @param parameter1 first parameter
	 * @param parameter2 second parameter
	 * @return result
	 */
	Object mockMethod(Object parameter1, Object parameter2);

	/**
	 * Mock method with three parameter.
	 * 
	 * @param parameter1 first parameter
	 * @param parameter2 second parameter
	 * @param parameter3 third parameter
	 * @return result
	 */
	Object mockMethod(Object parameter1, Object parameter2, Object parameter3);
}
