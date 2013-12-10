package com.elasticpath.test.util.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


/**
 * Creates and manages {@link MockWrapper}s.
 */
public class MockWrapperFactory {

	
	/**
	 * Creates a proxy object that can wrap a mock of type T. Calling methods on the result will result in either delegating to an
	 * instance of {@link MockWrapper}, or the wrapped mock itself. The return value will not have a mock set on the wrapper.
	 * 
	 * If no mock is set on the MockWrapper, the {@link Object#equals(Object)} and {@link Object#hashCode()} will run gracefully. 
	 * Any other method call to the proxy while the mock is not set will result in a {@link NullPointerException}. 
	 *   
	 * @param <T> the target type of the mock to be wrapped
	 * @param targetType the Class of the target type
	 * @return the proxy
	 */
	@SuppressWarnings("unchecked")
	public <T> T wrap(final Class<T> targetType) {
	    Enhancer enhancer = new Enhancer();
	    Class<?>[] interfaces;
	    if (targetType.isInterface()) {
	        interfaces = new Class<?>[] {targetType, MockWrapper.class};
	    } else {
            interfaces = new Class<?>[] {MockWrapper.class};
            enhancer.setSuperclass(targetType);
	    }
	    enhancer.setInterfaces(interfaces);
	    enhancer.setCallback(new MockWrapperInvocationHandler<T>());
        return (T) enhancer.create();
	}
	
	
	/**
	 * @param <T> mock's target type
	 * @param mockWrapper the mock wrapper object
	 * @return a {@link MockWrapper} instance
	 */
	@SuppressWarnings("unchecked")
	public <T> MockWrapper<T> toWrapper(final T mockWrapper) {
		return (MockWrapper<T>) mockWrapper;
	}
	
	/**
	 * Puts the given mock on the given wrapper.
	 * @param <T> the target type of the mock to be wrapped
	 * @param wrapper the wrapper 
	 * @param mock the mock to be put on the wrapper
	 */
	public <T> void setMockOnWrapper(final T wrapper, final T mock) {
		toWrapper(wrapper).setMock(mock);
	}
	
	/**
	 * Gets the mock from a wrapper.
	 * @param <T> the target type of the mock
	 * @param wrapper the wrapper to be unwrapped
	 * @return the mock that was set on the wrapper using {@link #setMockOnWrapper(Object, Object)} or
	 * {@link MockWrapper#setMock(Object)}
	 */
	public <T> T unwrap(final T wrapper) {
		return toWrapper(wrapper).getMock();
	}
	
	/**
	 * Handles invocations on the proxy object created by {@link MockWrapperFactory#wrap(Class)}.
	 */
	private class MockWrapperInvocationHandler<T> implements MethodInterceptor {

		private final MockWrapper<T> mockWrapper = new MockWrapperImpl<T>();

		@Override
        public Object intercept(final Object proxy, final Method method,
                final Object[] args, final MethodProxy proxyMethod) throws Throwable {
            if (method.getDeclaringClass().equals(MockWrapper.class)) {
                return method.invoke(mockWrapper, args);
            }
            T mock = mockWrapper.getMock();
            if (mock == null && method.getDeclaringClass().equals(Object.class)) {
                if (method.getName().equals("equals")) {
                    return proxy == args[0];
                }

                if (method.getName().equals("hashCode")) {
                    return mockWrapper.hashCode();
                }
            }
            try {
                return method.invoke(mock, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
	
	}

}
