package com.elasticpath.service.catalogview.filterednavigation.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.test.util.mock.PropertyEnabledExpectations;

/**
 * Test class for {@link FilteredNavigationConfigurationXmlParserImpl}.
 *
 */
@SuppressWarnings("PMD.NonStaticInitializer")
public class FilteredNavigationConfigurationXmlParserImplTest {

	private FilteredNavigationConfigurationXmlParserImpl filteredNavigationCoingurationXmlParserImpl;
	private FilteredNavigationConfiguration config;
	private FilterFactory filterFactory;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		this.filteredNavigationCoingurationXmlParserImpl = new FilteredNavigationConfigurationXmlParserImpl();
		this.config = new FilteredNavigationConfigurationImpl();

		filterFactory = context.mock(FilterFactory.class);
		filteredNavigationCoingurationXmlParserImpl.setFilterFactory(filterFactory);
	}

	/**
	 * Shouldn't fail to parse when there is not 'brands' element.
	 */
	@Test
	public void parserSkipsBrandsWhenNotSpecified() {		
		parseWithContent("");
		assertEquals(0, config.getAllBrandCodes().size());
	}
	
	/**
	 * Parse empty brands section.
	 */
	@Test
	public void testEmptyBrandsSection() {
		parseWithContent("<brands/>");
		assertEquals(0, config.getAllBrandCodes().size());
	}

	/** When parsing languages with a country code, the language and variant of a locale should be populated correctly. */
	@Test
	public void testLocaleCountryForAttributeRangeNode() {
		final String key = "key";
		final String attributeVal = "val";
		final String displayName = "display";
		final Locale locale = (new Locale("en", "US", "POSIX"));
		final String attributeFilterId = "01";

		final String content = String.format(
					"<attribute key=\"%s\" localized=\"true\">"
					+ 	"<simple id=\"" + attributeFilterId + "\" language=\"en_US_POSIX\" displayName=\"%s\" value=\"" + attributeVal + "\" />"
					+ "</attribute>"
				, key, displayName);

		final AttributeValueFilter filter = context.mock(AttributeValueFilter.class);
		final Attribute attribute = context.mock(Attribute.class);

		final Map<String, Object> filterInitMap = new HashMap<String, Object>();
		filterInitMap.put(AttributeFilter.ATTRIBUTE_PROPERTY, attribute);
		filterInitMap.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, attributeFilterId);
		filterInitMap.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, attributeVal);

		context.checking(new PropertyEnabledExpectations() {
			{
				allowing(filterFactory).getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
				will(returnValue(filter));
				allowingProperty(filter).getId();
				allowingProperty(filter).getAttributeKey();
				allowing(filter).getAttribute();
				will(returnValue(attribute));
				allowingProperty(filter).isLocalized();
				allowing(filter).initialize(filterInitMap);
				allowing(filter).setDisplayName(displayName);
				allowing(filter).setLocale(locale);
				allowing(filter).getLocale();
				will(returnValue(locale));
				allowing(filter).compareTo(filter);
				will(returnValue(0));
				allowing(filter).getAttributeValue();
			}
		});


		parseWithContent(content);
		assertEquals(1, config.getAllAttributeSimpleValues().size());
		assertEquals("Filter with our key not created", filter, config.getAllAttributeSimpleValues().get(key));
	}

	/**
	 * Parse two brand.
	 */
	@Test
	public void parseTwoBrands() {		
		parseWithContent("<brands><brand key='NIKE'/><brand key='PUMA'/></brands>");

		assertEquals(2, config.getAllBrandCodes().size());
		assertTrue(config.getAllBrandCodes().contains("NIKE"));
		assertTrue(config.getAllBrandCodes().contains("PUMA"));
	}
	
	/**
	 * A brand element with no 'key' attribute will throw an exception.
	 */
	@Test(expected = EpPersistenceException.class)
	public void parseBrandWithMalFormedAttribute() {
		parseWithContent("<brands><brand name='NIKE'/></brands>");
	}
	
	
	/**
	 * Duplicate brand entries should not result in multiple config elements.
	 */
	@Test
	public void duplicateBrandCodesNotDuplicatedInConfig() {		
		parseWithContent("<brands><brand key='NIKE'/><brand key='NIKE'/></brands>");
		
		assertEquals(1, config.getAllBrandCodes().size());
		assertEquals("NIKE", config.getAllBrandCodes().iterator().next());
	}	

	
	/**
	 * Config should be cleared before new config added.
	 */
	@Test
	public void configShouldBeClearedBeforeNewConfigIsAdded() {		
		parseWithContent("<brands><brand key='NIKE'/><brand key='PUMA'/></brands>");
		assertEquals(2, config.getAllBrandCodes().size());

		parseWithContent("<brands><brand key='ADIDAS'/></brands>");
		assertEquals(1, config.getAllBrandCodes().size());
	}	

	
	private void parseWithContent(final String content) {
		filteredNavigationCoingurationXmlParserImpl.parse(
				toStream("<?xml version='1.0' encoding='UTF-8'?>"
						+ "<IntelligentBrowsing>"
						+ content
						+ "</IntelligentBrowsing>"), config);
	}

	private InputStream toStream(final String string) {
			try {
				return new ByteArrayInputStream(string.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				fail("Unsupported Encoding in test" + e);
			}
			return null;
	}

}

