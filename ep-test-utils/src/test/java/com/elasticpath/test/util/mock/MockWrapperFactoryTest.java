package com.elasticpath.test.util.mock;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests {@link MockWrapperFactory}.
 */
@SuppressWarnings("unchecked")
public class MockWrapperFactoryTest {
	
	private final MockWrapperFactory factory = new MockWrapperFactory();
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * C'tor.
	 */
	public MockWrapperFactoryTest() {
	    context.setImposteriser(ClassImposteriser.INSTANCE);
	}
	
	/**
	 * Tests setting a mock to the wrapper and getting it back.
	 * @see MockWrapperFactory#setMockOnWrapper(Object, Object) 
	 * @see MockWrapperFactory#unwrap(Object)
	 */
	@Test
	public void testCreateMock() {
		List<String> wrapper = factory.wrap(List.class);

		Assert.assertTrue(wrapper instanceof MockWrapper);
		
		List<String> mock1 = context.mock(List.class, "mock1");
		factory.setMockOnWrapper(wrapper, mock1);
		Assert.assertSame(mock1, factory.unwrap(wrapper));
		
		List<String> mock2 = context.mock(List.class, "mock2");
		factory.setMockOnWrapper(wrapper, mock2);
		Assert.assertSame(mock2, factory.unwrap(wrapper));
	}
	
	/**
	 * Tests the wrapper's behaviour on delegating to the wrapped mock.
	 */
	@Test
	public void testMocking() {
		final List<String> wrapper = factory.wrap(List.class);
		final List<String> mock1 = context.mock(List.class, "mock1");
		final List<String> mock2 = context.mock(List.class, "mock2");
		
		context.checking(new Expectations() { { 
			oneOf(mock1).size(); will(returnValue(1));
			oneOf(mock2).size(); will(returnValue(2));
		} });
		
		factory.setMockOnWrapper(wrapper, mock1);
		Assert.assertEquals(1, wrapper.size());

		factory.setMockOnWrapper(wrapper, mock2);
		Assert.assertEquals(2, wrapper.size());
		
		factory.setMockOnWrapper(wrapper, new ArrayList<String>());
		Assert.assertEquals(0, wrapper.size());
	}
	
	/**
	 * Tests the wrapper's behaviour on delegating to the wrapped mock.
	 */
	@SuppressWarnings({"PMD.LooseCoupling"})
	@Test
	public void testMockingClasses() {
		final ArrayList<String> wrapper = factory.wrap(ArrayList.class);
		final ArrayList<String> mock1 = context.mock(ArrayList.class, "mock1");
		final ArrayList<String> mock2 = context.mock(ArrayList.class, "mock2");

		context.checking(new Expectations() {
			{
				oneOf(mock1).size();
				will(returnValue(1));
				oneOf(mock2).size();
				will(returnValue(2));
			}
		});

		factory.setMockOnWrapper(wrapper, mock1);
		Assert.assertEquals(1, wrapper.size());

		factory.setMockOnWrapper(wrapper, mock2);
		Assert.assertEquals(2, wrapper.size());

		factory.setMockOnWrapper(wrapper, new ArrayList<String>());
		Assert.assertEquals(0, wrapper.size());
	}

	/**
	 * Tests for graceful hashCode and equals on null mocks.
	 */
	@SuppressWarnings({"ResultOfMethodCallIgnored"})
	@Test
	public void testEqualsHashCodeForNull() {
		final List<String> wrapper = factory.wrap(List.class);
		
		Assert.assertNull(factory.toWrapper(wrapper).getMock());
		Assert.assertEquals(wrapper, wrapper);
		wrapper.hashCode();
		
		final List<String> wrapper2 = factory.wrap(List.class);
		Assert.assertFalse(wrapper.equals(wrapper2));
	}

	/**
	 * Tests for hashCode and equals on non-null mocks.
	 */
	@Test
	public void testEqualsHashCodeForNotNull() {
		final List<String> wrapper1 = factory.wrap(List.class);

		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("hello");
		Assert.assertFalse(wrapper1.equals(list1));

		factory.setMockOnWrapper(wrapper1, list1);
		Assert.assertEquals(wrapper1, list1);
		Assert.assertEquals(wrapper1.hashCode(), list1.hashCode());
	}
	
}
