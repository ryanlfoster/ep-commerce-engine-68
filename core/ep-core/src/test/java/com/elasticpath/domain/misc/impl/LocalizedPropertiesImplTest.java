package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>LocalizedPropertiesImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class LocalizedPropertiesImplTest {

	private static final String NULL_VALUES_PROHIBITED_MSG = "Null values are prohibited";
	private static final String EPDOMAIN_EXCEPTION_EXPECTED_MSG = "EpDomainException expected";

	private LocalizedPropertiesImpl localizedPropertiesImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of any error happens
	 */
	@Before
	public void setUp() throws Exception {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("utility", UtilityImpl.class);

		this.localizedPropertiesImpl = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -7100252602367051323L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that getValue() and SetValue() methods cannot be called with null parameters;
	 * EpDomainException will be thrown.
	 */
	@Test
	public void testGetValueWithNullsForbidden() {
		try {
			localizedPropertiesImpl.getValue(null, Locale.US);
			fail(NULL_VALUES_PROHIBITED_MSG);
		} catch (EpDomainException ex) {
			assertNotNull(EPDOMAIN_EXCEPTION_EXPECTED_MSG, ex);
		}

		try {
			localizedPropertiesImpl.getValue("", null);
			fail(NULL_VALUES_PROHIBITED_MSG);
		} catch (EpDomainException ex) {
			assertNotNull(EPDOMAIN_EXCEPTION_EXPECTED_MSG, ex);
		}

		try {
			localizedPropertiesImpl.setValue(null, Locale.US, "");
			fail(NULL_VALUES_PROHIBITED_MSG);
		} catch (EpDomainException ex) {
			assertNotNull(EPDOMAIN_EXCEPTION_EXPECTED_MSG, ex);
		}

		try {
			localizedPropertiesImpl.setValue("", null, "");
			fail(NULL_VALUES_PROHIBITED_MSG);
		} catch (EpDomainException ex) {
			assertNotNull(EPDOMAIN_EXCEPTION_EXPECTED_MSG, ex);
		}
	}

	/**
	 * Tests that setting null value to a property gets null value afterwards.
	 */
	@Test
	public void testSetNullValueLocalizedProperty() {
		String propertyName = "myprop1";
		String value = "test1";
		localizedPropertiesImpl.setValue(propertyName, Locale.CANADA, value);
		assertEquals(value, localizedPropertiesImpl.getValue(propertyName, Locale.CANADA));

		localizedPropertiesImpl.setValue(propertyName, Locale.CANADA, null);
		assertNull(localizedPropertiesImpl.getValue(propertyName, Locale.CANADA));
	}

	/**
	 * Tests that setting empty string value to a property gets null value afterwards.
	 */
	@Test
	public void testSetEmptyStringValueLocalizedProperty() {
		String propertyName = "myprop2";
		String value = "test2";
		localizedPropertiesImpl.setValue(propertyName, Locale.CANADA, value);
		assertEquals(value, localizedPropertiesImpl.getValue(propertyName, Locale.CANADA));

		localizedPropertiesImpl.setValue(propertyName, Locale.CANADA, "");
		assertNull(localizedPropertiesImpl.getValue(propertyName, Locale.CANADA));
	}

	/**
	 * Test that GetValue() succeeds.
	 */
	@Test
	public void testGetAndSet() {
		final String propertyName1 = "testProperty";
		final Locale locale1 = Locale.US;
		final Locale locale2 = Locale.CANADA;
		assertNull(this.localizedPropertiesImpl.getValue(propertyName1, locale1));

		// Set the value to a property name
		String value1 = "testValue";
		this.localizedPropertiesImpl.setValue(propertyName1, locale1, value1);
		assertEquals(value1, this.localizedPropertiesImpl.getValue(propertyName1, locale1));
		assertNull(this.localizedPropertiesImpl.getValueWithoutFallBack(propertyName1, locale2));
		// When the value of the given locale doesn't exist, should not fallback to the value of the default locale
		assertEquals(null, this.localizedPropertiesImpl.getValue(propertyName1, locale2));

		// Set the value to the same property name with another locale
		String value2 = "testValue2";
		this.localizedPropertiesImpl.setValue(propertyName1, locale2, value2);
		assertEquals(value1, this.localizedPropertiesImpl.getValue(propertyName1, locale1));
		assertEquals(value2, this.localizedPropertiesImpl.getValue(propertyName1, locale2));

		// Set the value to the another property name
		final String propertyName2 = "testProperty2";
		this.localizedPropertiesImpl.setValue(propertyName2, locale2, value2);
		assertEquals(value1, this.localizedPropertiesImpl.getValue(propertyName1, locale1));
		assertEquals(value2, this.localizedPropertiesImpl.getValue(propertyName1, locale2));
		assertEquals(value2, this.localizedPropertiesImpl.getValue(propertyName2, locale2));
	}

	/**
	 * Test that LocalizedPropertiesMap is never null.
	 */
	@Test
	public void testGetLocalizedPropertiesMap() {
		assertNotNull(this.localizedPropertiesImpl.getLocalizedPropertiesMap());
	}

	/**
	 * Test that you can set the LocalizedPropertiesMap.
	 */
	@Test
	public void testSetLocalizedPropertiesMap() {
		final Map<String, LocalizedPropertyValue> map = new HashMap<String, LocalizedPropertyValue>();
		final String beanId = ContextIdNames.TAX_CATEGORY_LOCALIZED_PROPERTY_VALUE;
		this.localizedPropertiesImpl.setLocalizedPropertiesMap(map, beanId);
		assertSame(map, this.localizedPropertiesImpl.getLocalizedPropertiesMap());
		assertEquals(beanId, this.localizedPropertiesImpl.getLocalizedPropertyValueBean());
	}

	/**
	 * Tests that equals works as expected.
	 */
	@Test
	public void testEquals() {

		assertTrue("Every object should be equal to itself", localizedPropertiesImpl.equals(localizedPropertiesImpl)); // NOPMD
		LocalizedProperties otherLocalizedPropertiesImpl = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 7825564499802504460L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		assertFalse(localizedPropertiesImpl.equals(null)); // NOPMD
		assertEquals(localizedPropertiesImpl, otherLocalizedPropertiesImpl);

		otherLocalizedPropertiesImpl.setValue("test", Locale.CANADA, "testValue");

		assertFalse(localizedPropertiesImpl.equals(otherLocalizedPropertiesImpl));
	}

	/**
	 * Tests two equal/non-equal objects having equal/non-equal hash code values.
	 */
	@Test
	public void testHashCode() {
		LocalizedProperties otherLocalizedPropertiesImpl = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 5677652994143700940L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};

		assertEquals(localizedPropertiesImpl.hashCode(), otherLocalizedPropertiesImpl.hashCode());

		otherLocalizedPropertiesImpl.setValue("test", Locale.CANADA, "testValue");

		assertFalse(localizedPropertiesImpl.hashCode() == otherLocalizedPropertiesImpl.hashCode());
	}

	/**
	 * Tests that toString() returns non-null value.
	 */
	@Test
	public void testToString() {
		assertNotNull(localizedPropertiesImpl.toString());
	}

}
