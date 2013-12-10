package com.elasticpath.test.util.mock;

/**
 * Wraps a mock object of type T.
 * @param <T> the target type of the mock
 */
public interface MockWrapper<T> {
	
	/**
	 * Sets the mock on the wrapper.
	 * @param mock the mock to be set
	 */
	void setMock(T mock);
	
	/**
	 * @return the mock that was set on the wrapper
	 */
	T getMock();
}
