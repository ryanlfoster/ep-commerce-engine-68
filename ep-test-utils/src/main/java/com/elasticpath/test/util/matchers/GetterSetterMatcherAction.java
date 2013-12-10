package com.elasticpath.test.util.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

/**
 * Implements the {@link Matcher} and {@link Action} jmock API to create trivial getter/setter logic.
 * <p>
 * By using the {@link Action} in a getter and the {@link Matcher} in a setter, this can be used to implement domain
 * logic on an interface. You can use this class as follows in an expectation:
 *
 * <pre>
 * GetterSetterMatcherAction&lt;Integer&gt; value = new GetterSetterMatcherAction&lt;Integer&gt;();
 * one(object).getValue();
 * will(value);
 *
 * one(object).setValue(with(value));
 * </pre>
 *
 * @param <T> type of data to store
 */
public class GetterSetterMatcherAction<T> extends TypeSafeMatcher<T> implements Action, Matcher<T> {
	private T value;

	/**
	 * Creates a new {@link GetterSetterMatcherAction} instance.
	 *
	 * @param initialValue an initial value
	 */
	public GetterSetterMatcherAction(final T initialValue) {
		this.value = initialValue;
	}

	/**
	 * Creates a new {@link GetterSetterMatcherAction}.
	 *
	 * @param initialValue initial value
	 * @return new {@link GetterSetterMatcherAction}
	 * @param <T> type of data to store
	 */
	@Factory
	public static <T> GetterSetterMatcherAction<T> getterSetterMatcherAction(final T initialValue) {
		return new GetterSetterMatcherAction<T>(initialValue);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("Getter/setter logic");
	}

	@Override
	public Object invoke(final Invocation invocation) throws Throwable {
		return value;
	}

	@Override
	public boolean matchesSafely(final T object) {
		value = object;
		return true;
	}

	public void setValue(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
