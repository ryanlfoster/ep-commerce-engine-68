package com.elasticpath.sfweb.test;

import org.jmock.api.Action;
import org.jmock.api.Invocation;

import org.hamcrest.Description;

import com.elasticpath.base.Initializable;

/**
 * Custom JMock Stub for creating new bean instances.
 */
public class PrototypeBeanCustomStub implements Action {

	private final Class<?> beanClass;

	/**
	 * Create a stub for the specified bean class.
	 *
	 * @param beanClass the bean class to stub.
	 */
	public PrototypeBeanCustomStub(final Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Creates the actual bean instance.
	 *
	 * @param invocation the invocation to respond to.
	 * @return the bean instance.
	 * @throws Throwable if something goes wrong.
	 */
	@Override
	public Object invoke(final Invocation invocation) throws Throwable {
		final Object object = beanClass.newInstance();
		if (object instanceof Initializable) {
			Initializable initializable = (Initializable) object;
			initializable.initialize();
		}
		return object;
	}

	/**
	 * Describe the action of the stub to the description.
	 *
	 * @param description the description to describe this stub to.
	 */
	@Override
	public void describeTo(final Description description) {
		description.appendText("creates new instances of " + beanClass);
	}

}
