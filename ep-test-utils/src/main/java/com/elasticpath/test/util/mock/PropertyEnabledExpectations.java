package com.elasticpath.test.util.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.internal.InvocationExpectationBuilder;

import com.elasticpath.test.util.matchers.GetterSetterMatcherAction;

/**
 * Allows the creation of bean properties with state for mock objects.
 * <p>
 * Properties can then be defined as
 *
 * <pre>
 * SomeType mock = context.mock(SomeType.class);
 * allowingProperty(mock).getValue();
 * </pre>
 *
 * to allow for the invocation of the getter or setter of the property "value" of the {@code SomeType} class. This
 * property will also have state such that a get after a set will produce the value which was previously set. Initial
 * values of properties can be with {@link #with(Object)} as normal, i.e.
 *
 * <pre>
 * SomeType mock = context.mock(SomeType.class);
 * allowingProperty(mock).getValue();
 * will(returnValue("someInitialValue");
 * </pre>
 * <p>
 * Note: This class does <em>not</em> work with the {@link org.jmock.lib.legacy.ClassImposteriser ClassImposteriser}.
 */
public class PropertyEnabledExpectations extends Expectations {
	private static final Map<Class<?>, Object> BOX_TYPES = new HashMap<Class<?>, Object>();

	private InvocationExpectationBuilder currentBuilder;
	private GetterSetterMatcherAction<Object> lastPropertyAction;

	/**
	 * Property builder for allowing a property to be called.
	 *
	 * @param mock mock to operate on
	 * @param <T> type of mock
	 * @return the mock to call the property on
	 */
	public <T> T allowingProperty(final T mock) {
		return captureExpectedObject(atLeast(0).of(mock), mock);
	}

	private <T> T captureExpectedObject(final T capturingMock, final T mock) {
		Set<Class<?>> proxiedClasses = new LinkedHashSet<Class<?>>();
		proxiedClasses.addAll(Arrays.asList(capturingMock.getClass().getInterfaces()));
		Class<?>[] clazzes = proxiedClasses.toArray(new Class<?>[proxiedClasses.size()]);

		Object result = Proxy.newProxyInstance(capturingMock.getClass().getClassLoader(), clazzes, new InvocationHandler() {
			@Override
			public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
				Object result = method.invoke(capturingMock, args);

				if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
					GetterSetterMatcherAction<Object> matcherAction = new GetterSetterMatcherAction<Object>(null);
					currentBuilder().setAction(matcherAction);

					String property;
					if (method.getName().startsWith("is")) {
						property = method.getName().substring(2);
					} else {
						// CHECKSTYLE:OFF -- this constant is for "get" which won't change
						property = method.getName().substring(3);
						// CHECKSTYLE:ON
					}

					Method setter = findMethod(proxy.getClass(), "set" + property, method.getReturnType());
					if (setter == null) {
						// ignore no setters
						return result;
					}

					T capturingSetter = atLeast(0).of(mock);
					with(matcherAction);
					setter.invoke(capturingSetter, BOX_TYPES.get(method.getReturnType()));

					currentBuilder = currentBuilder();
					lastPropertyAction = matcherAction;
				}
				return result;
			}
		});

		return cast(result);
	}

	@SuppressWarnings("unchecked")
	private static <T> T cast(final Object capturingImposter) {
		return (T) capturingImposter;
	}

	@Override
	public void will(final Action action) {
		if (!currentBuilder().equals(currentBuilder)) {
			lastPropertyAction = null;
			currentBuilder = null;
			super.will(action);
			return;
		}

		try {
			Object value = action.invoke(new Invocation(null, null));
			lastPropertyAction.setValue(value);
		} catch (Throwable t) { // NOPMD - we must catch throwable here
			// must be an action which is dependent on the mock its called from or it throws an exception, use default
			super.will(action);
		}
	}

	/*
	 * This method is copied from the spring class org.springframework.util.ReflectionUtils#findMethod(Class<?>, String,
	 * Class<?>...). We don't want the spring dependency here, hence the copy.
	 */
	private static Method findMethod(final Class<?> clazz, final String name, final Class<?>... paramTypes) {
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods;
			if (searchType.isInterface()) {
				methods = searchType.getMethods();
			} else {
				methods = searchType.getDeclaredMethods();
			}
			for (Method method : methods) {
				if (name.equals(method.getName())
						&& (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/** Helper class to box primitives. */
	private static class PrimitiveBoxer {
		private boolean defaultBoolean;
		private byte defaultByte;
		private char defaultChar;
		private short defaultShort; // NOPMD -- we want short here
		private int defaultInt;
		private long defaultLong;
		private float defaultFloat;
		private double defaultDouble;
	}

	static {
		PrimitiveBoxer boxer = new PrimitiveBoxer();
		BOX_TYPES.put(boolean.class, boxer.defaultBoolean);
		BOX_TYPES.put(byte.class, boxer.defaultByte);
		BOX_TYPES.put(char.class, boxer.defaultChar);
		BOX_TYPES.put(short.class, boxer.defaultShort); // NOPMD -- we want short here
		BOX_TYPES.put(int.class, boxer.defaultInt);
		BOX_TYPES.put(long.class, boxer.defaultLong);
		BOX_TYPES.put(float.class, boxer.defaultFloat);
		BOX_TYPES.put(double.class, boxer.defaultDouble);
	}
}
