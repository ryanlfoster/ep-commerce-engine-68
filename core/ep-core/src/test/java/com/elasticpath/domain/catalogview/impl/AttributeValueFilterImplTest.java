package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.TransientAttributeValueImpl;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>AttributeValueFilterImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class AttributeValueFilterImplTest {

	private static final String A00001_JPEG = "A00001_JPEG";

	private static final String A00001_RAW = "A00001_RAW";

	private static final String EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED = "EpBindExceptionSearchRequest expected.";

	private AttributeValueFilterImpl attributeFilter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
    private BeanFactoryExpectationsFactory expectationsFactory;
    private BeanFactory beanFactory;

	private AttributeService attributeService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
        beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
        attributeService = context.mock(AttributeService.class);

        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_SERVICE, attributeService);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);

		context.checking(new Expectations() {
			{
				allowing(attributeService).findByKey("A00001"); will(returnValue(getAttribute("A00001")));
			}
		});
		this.attributeFilter = getAttributeFilter();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	private AttributeValueFilterImpl getAttributeFilter() {
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_FILTER, AttributeValueFilterImpl.class);

	    return beanFactory.getBean(ContextIdNames.ATTRIBUTE_FILTER);
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(attributeFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayName() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId);
		assertEquals(filterId, attributeFilter.getId());
		assertEquals("JPEG", attributeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.getDisplayName(String)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitialization() {
		assertNull(attributeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeFilter overrides the
	 * default behavior.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverride() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
		attributeFilter.setDisplayName("test display name");
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId);
		assertEquals("test display name", attributeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeFilter to
	 * null doesn't cause a failure, and therefore keeps the storefront
	 * resilient.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverrideOfNull() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
		attributeFilter.setDisplayName(null);
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId);
		assertEquals("Null display name was set, fallback should have been attribute value", "JPEG", attributeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test that explicitly setting the display name of an attributeFilter to
	 * blank doesn't cause a failure, and therefore keeps the storefront
	 * resilient.
	 */
	@Test
	public void testGetDisplayNameWithExplicitDispayNameOverrideOfBlank() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
		attributeFilter.setDisplayName("   ");
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId);
		assertEquals("Blank display name was set, null should have been returned", "JPEG", attributeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.initialize(String)'.
	 */
	@Test
	public void testInitializeWithBadId() {
		try {
			this.attributeFilter.initialize("bad filter id");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.attributeFilter.initialize("A00001-aaa");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.attributeFilter.initialize("A00001");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.attributeFilter.initialize("A00001-333");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.equals()'.
	 */
	@Test
	public void testEquals() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId);

		final AttributeValueFilterImpl anotherattributeFilter = getAttributeFilter();
		anotherattributeFilter.initialize(filterId);

		assertEquals(attributeFilter, anotherattributeFilter);
		assertEquals(attributeFilter.hashCode(), anotherattributeFilter.hashCode());
		assertFalse(attributeFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.compare(Object)'.
	 */
	@Test
	public void testCompare() {
		final String filterId1 = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
		final String filterId2 = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_RAW;
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		attributeFilter.initialize(filterId1);

		AttributeValueFilterImpl anotherattributeFilter = getAttributeFilter();
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		anotherattributeFilter.initialize(filterId1);

		assertEquals(0, attributeFilter.compareTo(anotherattributeFilter));

        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		anotherattributeFilter.initialize(filterId2);
		assertTrue(attributeFilter.compareTo(anotherattributeFilter) < 0);
		assertTrue(anotherattributeFilter.compareTo(attributeFilter) > 0);
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.AttributeValueFilterImpl.getSeoId()'.
	 */
	@Test
	public void testGetSeoId() {
		final String filterId = SeoConstants.ATTRIBUTE_FILTER_PREFIX + A00001_JPEG;
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.UTILITY, UtilityImpl.class);
		attributeFilter.initialize(filterId);
		assertEquals(filterId, attributeFilter.getSeoId());
		assertEquals("jpeg", attributeFilter.getSeoName(Locale.US));

	}

	/**
	 * Test that when we set an Id on the filter it is used in the SEO Id as an alias for the value.
	 */
	@Test
	public void testGetAliasedSeoId() {
		AttributeValueFilter newFilter = getAttributeFilter();
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, getAttribute("A00002"));
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "01");
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, "SD Memory Card");
		newFilter.initialize(filterProperties);
		newFilter.setDisplayName("display name");
		assertEquals("Seo ID should use the provided alias", "atA00002_01", newFilter.getSeoId());
	}

	private Attribute getAttribute(final String key) {
		Attribute attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		attribute.setMultiValueEnabled(false);
		attribute.setKey(key);
		return attribute;
	}

	/**
	 * Test that when we set the attribute Id on the filter it only returns the prefix of the attribute,
	 * excluding the actual attribute value.
	 */
	@Test
	public void testGetAttributePrefixAndKey() {
		AttributeValueFilter newFilter = getAttributeFilter();
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, getAttribute("A00002"));
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "01");
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, "SD Memory Card");
		newFilter.initialize(filterProperties);
		assertEquals("Attribute ID should use the Attribute prefix plus the attribute key", "atA00002", newFilter.getAttributePrefixAndKey());
	}
}
