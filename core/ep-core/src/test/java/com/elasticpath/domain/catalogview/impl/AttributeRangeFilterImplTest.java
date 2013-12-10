package com.elasticpath.domain.catalogview.impl;

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

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.domain.attribute.impl.TransientAttributeValueImpl;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>AttributeRangeFilterImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class AttributeRangeFilterImplTest {

	private static final String LESSTHAN_2 = "A00001__2.0";

	private static final String MORETHAN_3 = "A00001_3.0_";

	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";

	private static final String BETWEEN_2_AND_3 = "A00001_2.0_3.0";

	private static final String EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED = "EpBindExceptionSearchRequest expected.";

	private AttributeRangeFilter rangeFilter;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private AttributeService attributeService;

    private BeanFactoryExpectationsFactory expectationsFactory;

    private BeanFactory beanFactory;

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
	    expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER, AttributeRangeFilterImpl.class);

		context.checking(new Expectations() {
			{
				allowing(attributeService).findByKey("A00001"); will(returnValue(getAttribute("A00001")));
			}
		});
		this.rangeFilter = getRangeFilter();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(rangeFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayName() {
		final String testId = SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + BETWEEN_2_AND_3;
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);

		this.rangeFilter.initialize(testId);
		assertEquals("2.0 - 3.0", this.rangeFilter.getDisplayName(Locale.US));

        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + LESSTHAN_2);
		assertEquals("< 2.0", this.rangeFilter.getDisplayName(Locale.US));

        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + MORETHAN_3);
		assertEquals("> 3.0", this.rangeFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitializetion() {
		try {
			this.rangeFilter.getDisplayName(Locale.US);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.initialize()'.
	 */
	@Test
	public void testInitializeWithBadId() {
		try {
			this.rangeFilter.initialize("bad range filter id");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "between-50-and-100");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "between-USD-50");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "lessthan-USD-aade");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "bad-USD-aade");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.rangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "between-USD-150-and-100");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for
	 * 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.getParent()'.
	 */
	@Test
	public void testGetParent() {
		assertNull(rangeFilter.getParent());
		assertEquals(0, rangeFilter.getChildren().size());

		AttributeRangeFilter childRangeFilter = getRangeFilter();
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		childRangeFilter.initialize(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + "A00001_2.0_2.3");
		this.rangeFilter.addChild(childRangeFilter);
		assertTrue(rangeFilter.getChildren().contains(childRangeFilter));
		assertSame(rangeFilter, childRangeFilter.getParent());
		assertSame(rangeFilter, childRangeFilter.getParent());
	}

	private AttributeRangeFilter getRangeFilter() {
	    return beanFactory.getBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.equals()'.
	 */
	@Test
	public void testEquals() {
		final String testId = SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + BETWEEN_2_AND_3;
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		this.rangeFilter.initialize(testId);

		final AttributeRangeFilter anotherFilter = getRangeFilter();
		anotherFilter.initialize(testId);

		assertEquals(rangeFilter, anotherFilter);
		assertEquals(rangeFilter.hashCode(), anotherFilter.hashCode());
		assertFalse(rangeFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl.getSeoId()'.
	 */
	@Test
	public void testGetSeoId() {
		String testId = SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX + BETWEEN_2_AND_3;
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		this.rangeFilter.initialize(testId);
		assertEquals(testId, rangeFilter.getSeoId());
	}

	/**
	 * Test that when we set an Id on the filter it is used in the SEO Id as an alias for the range.
	 */
	@Test
	public void testGetAliasedSeoId() {
		AttributeRangeFilter newFilter = getRangeFilter();
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
        expectationsFactory.oneBeanFactoryGetBean(ContextIdNames.ATTRIBUTE_VALUE, TransientAttributeValueImpl.class);
		context.checking(new Expectations() {
			{
				oneOf(attributeService).findByKey("A00002"); will(returnValue(getAttribute("A00002")));
			}
		});
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_KEY_PROPERTY, "A00002");
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "3_4");
		filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, "3");
		filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, "3.9x");
		newFilter.initialize(filterProperties);
		assertEquals("Seo ID should use the provided alias", "arA00002_3_4", newFilter.getSeoId());
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
		AttributeRangeFilter newFilter = getRangeFilter();

		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_PROPERTY, getAttribute("A00001"));
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, "01");
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, "SD Memory Card");
		newFilter.initialize(filterProperties);
		assertEquals("Attribute ID should use the Attribute prefix plus the attribute key", "arA00001", newFilter.getAttributePrefixAndKey());
	}

}
