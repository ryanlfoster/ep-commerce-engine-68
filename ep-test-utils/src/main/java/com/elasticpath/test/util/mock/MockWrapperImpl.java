package com.elasticpath.test.util.mock;


/**
 * Wraps a mock with target type of T.
 * @param <T> the target type of the mock to be wrapped
 */
public class MockWrapperImpl<T> implements MockWrapper<T> {
	
	private T mock;
	
	public void setMock(final T mock) {
		this.mock = mock;
	}

	public T getMock() {
		return this.mock;
	}
	
}
