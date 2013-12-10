package com.elasticpath.test.util.matchers;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link GetterSetterMatcherAction}.
 */
public class GetterSetterMatcherActionTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private GetterSetterMatcherAction<Object> matcherAction = new GetterSetterMatcherAction<Object>(null);

	/**
	 * Makes sure {@link GetterSetterMatcherAction} works with jmock.
	 */
	@Test
	public void testWithJMock() {
		final TestModel model = context.mock(TestModel.class);
		context.checking(new Expectations() {
			{
				allowing(model).setValue(with(matcherAction));
				allowing(model).getValue();
				will(matcherAction);
			}
		});

		Object value = new Object();
		model.setValue(value);
		assertEquals("matcherAction should have new value", value, matcherAction.getValue());
		assertEquals("Getter for model should have new value", value, model.getValue());
	}

	/**
	 * The initial value that you create the instance with should be the first one returned by a getter.
	 *
	 * @throws Throwable in case of errors
	 */
	@Test
	public void testInitialValue() throws Throwable {
		Object initialValue = new Object();
		matcherAction = new GetterSetterMatcherAction<Object>(initialValue);
		assertEquals("Initial value not setup", initialValue, matcherAction.getValue());
		assertEquals("Initial value not setup", initialValue, matcherAction.invoke(null));
	}

	/**
	 * Ensures that getter setter logic is working.
	 *
	 * @throws Throwable in case of errors
	 */
	@Test
	public void testGetSet() throws Throwable {
		Object value = new Object();
		matcherAction.matches(value);
		assertEquals("New value not changed", value, matcherAction.getValue());
		assertEquals("New value not changed", value, matcherAction.invoke(null));

		Object second = new Object();
		matcherAction.matches(second);
		assertEquals("Value not changed to second", second, matcherAction.getValue());
		assertEquals("Value not changed to second", second, matcherAction.invoke(null));
	}

	/** Test interface model for {@link GetterSetterMatcherAction}. */
	private interface TestModel {
		Object getValue();
		void setValue(Object value);
	}
}
