package com.elasticpath.tools.sync.utils.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.MapKey;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Tests synchronization utility methods.
 */

public class SyncUtilsImplTest {

	private static final String DATA_KEY_VALUE = "dataKeyValue";

	private SyncUtilsImpl syncUtils;

	/**
	 * Test Data Class.
	 */
	@SuppressWarnings("unused")
	private class TestData {
		private String value;

		@MapKey(name = "dataKey")
		public String getDataKey() {
			return DATA_KEY_VALUE;
		}

		public String getValue() {
			return value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

		private void secretPrivateMethod() { // this method is used for fallback call (throw reflection)
			// empty
		}
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		syncUtils = new SyncUtilsImpl();
	}

	/**
	 * Tests getMapKey.
	 *
	 * @throws Exception if any problem with reflection
	 */
	@Test
	public void testGetMapKey() throws Exception {
		assertEquals(DATA_KEY_VALUE, syncUtils.getMapKey(
				TestData.class.getMethod("getDataKey").getAnnotation(MapKey.class), new TestData()));
	}

	/**
	 * Tests getMapKey failing.
	 *
	 * @throws Exception every time
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testMapKeyFail() throws Exception {
		assertEquals(DATA_KEY_VALUE, new SyncUtilsImpl() {
			@Override
			public Object invokeGetterMethod(final Object source, final Method getterMethod) {
				throw new SyncToolRuntimeException("Test Exception");
			}
		} .getMapKey(
				TestData.class.getMethod("getDataKey").getAnnotation(MapKey.class), new TestData()));
	}

	private Method getClassMethod(final String methodName, final Class< ? >...params) {
		try {
			return TestData.class.getMethod(methodName, params);
		} catch (Exception e) {
			fail("Could not get method " + methodName + ", " + e);
		}
		return null;
	}

	/**
	 * Tests invokeCopyMethod.
	 */
	@Test
	public void testInvokeCopyMethod() {
		TestData source = new TestData();
		TestData target = new TestData();

		source.setValue("source");
		target.setValue("target");

		Entry<Method, Method> accessors = new Map.Entry<Method, Method>() {

			public Method getKey() {
				return getClassMethod("getValue");
			}

			public Method getValue() {
				return getClassMethod("setValue", String.class);
			}

			public Method setValue(final Method value) {
				return null;
			}
		};
		syncUtils.invokeCopyMethod(source, target, accessors);

		assertEquals("source", source.getValue());
		assertEquals("source", target.getValue());

		// test failing
		try {
		new SyncUtilsImpl() {
			@Override
			public Object invokeGetterMethod(final Object source, final Method getterMethod) {
				throw new SyncToolRuntimeException("Expected Exception");
			}
		} .invokeCopyMethod(source, target, accessors);
			fail("SyncToolRuntimeException should be thrown");
		} catch (SyncToolRuntimeException expected) {
			assertNotNull(expected);
		}
	}

	/**
	 * Tests invokeSetterMethod fail.
	 *
	 * @throws Exception always
	 */
	@Test (expected = SyncToolRuntimeException.class)
	public void testInvokeSetterMethodFail() throws Exception {
		syncUtils.invokeSetterMethod(new TestData(), new TestData() {
			@SuppressWarnings("unused")
			public void setKey(final int index) {
				// empty
			}
		} .getClass().getMethod("setKey", int.class), 1);
	}

	/**
	 * Tests invokeGetterMethod fail.
	 *
	 * @throws Exception always
	 */
	@Test (expected = SyncToolRuntimeException.class)
	public void testInvokeGetterMethodFail() throws Exception {
		syncUtils.invokeGetterMethod(new TestData(), new TestData() {
			@SuppressWarnings("unused")
			public void getKey() {
				// empty
			}
		} .getClass().getMethod("getKey"));
	}

	/**
	 * Tests findDeclaredMethodWithFallback Fail.
	 */
	@Test (expected = SyncToolRuntimeException.class)
	public void testFindDeclaredMethodWithFallbackFail() {
		syncUtils.findDeclaredMethodWithFallback(TestData.class, "newMethod");
	}

	/**
	 * Tests findDeclaredMethodWithFallback.
	 */
	@Test
	public void testFindDeclaredMethodWithFallback() {
		Method method = syncUtils.findDeclaredMethodWithFallback(new TestData() {
			// Derived class which to find in superclass not Object
		} .getClass(), "secretPrivateMethod"); // NOPMD

		assertEquals("secretPrivateMethod", method.getName());
	}

	/**
	 * Tests createSetterName.
	 */
	@Test
	public void testCreateSetterName() {
		assertEquals("setSuperValueKey", syncUtils.createSetterName("getSuperValueKey"));
		assertEquals("setEnabled", syncUtils.createSetterName("isEnabled"));
		assertEquals("badGetter", syncUtils.createSetterName("badGetter"));
	}

}
