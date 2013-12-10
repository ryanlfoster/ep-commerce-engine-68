package com.elasticpath.tools.sync.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * Test for MethodComparator.
 */
public class MethodComparatorTest {

	/**
	 * Test Data Interface for getting Methods form it.
	 */
	private interface TestInterface {
		void testMethod1(String arg1); 
		
		int testMethod1();
		int testMethod2();
		
		int testMethod2(String arg1);

		void testMethod3();
		void testMethod3(int arg1);
		void testMethod3(String arg1);
	}
	
	private Method getMethodByName(final String name, final Class< ? >...params) {
		try {
			return TestInterface.class.getMethod(name, params);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return null;
	}
	
	/**
	 * Tests compare of the MethodComparator.
	 */
	@Test
	public void testCompare() {
		final MethodComparator methodComparator = new MethodComparator();
		
		assertEquals(0,  methodComparator.compare(getMethodByName("testMethod1"), getMethodByName("testMethod1"))); // NOPMD
		assertEquals(-1, methodComparator.compare(getMethodByName("testMethod1"), getMethodByName("testMethod2")));
		assertEquals(-1, methodComparator.compare(getMethodByName("testMethod1"), getMethodByName("testMethod1", String.class)));
		assertEquals(-1, methodComparator.compare(getMethodByName("testMethod2"), getMethodByName("testMethod2", String.class)));
		assertEquals(-1, methodComparator.compare(getMethodByName("testMethod3", int.class), getMethodByName("testMethod3", String.class)));
	}
}
