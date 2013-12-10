package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl;
import com.elasticpath.domain.catalogview.impl.AttributeValueFilterImpl;
import com.elasticpath.domain.catalogview.search.AdvancedSearchConfigurationProvider;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;

/**
 * Test for Advanced Search controller.
 *
 */
public class AdvancedSearchControllerImplTest {

	private static final String STORE_CODE = "testStore";
	private AdvancedSearchControllerImpl advancedSearchController;
	private ParameterMapper parameterMapper;
	private MockHttpServletRequest request;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		advancedSearchController = new AdvancedSearchControllerImpl();
		parameterMapper = new ParameterMapper();
		advancedSearchController.setParameterMapper(parameterMapper);
		request = new MockHttpServletRequest();
	}
	
	/**
	 * Test for form submission when only filter parameter is set.
	 */
	@Test
	public void testIsFormSubmissionFilterParameterOnly() {		
		request.setParameter(parameterMapper.getFiltersParameter(), "arA02638");
		assertTrue(advancedSearchController.isFormSubmission(request));
	}
	
	/**
	 * Test for form submission when only search form parameter is set.
	 */
	@Test
	public void testIsFormSubmissionSearchFormParameterOnly() {	
		request.setParameter(parameterMapper.getSearchFormButtonName(), "search");
		assertTrue(advancedSearchController.isFormSubmission(request));
	}
	
	/**
	 * Test when there are no parameters set initially.
	 */
	@Test
	public void testNoParametersSet() {
		assertFalse(advancedSearchController.isFormSubmission(request));
	}
	
	/**
	 * One attribute in the filter map will result in only 1 form bean.
	 */
	@Test
	public void testConstructMapWithOneElement() {
		final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider = 
			context.mock(AdvancedSearchConfigurationProvider.class);
		
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		
		final List<AttributeRangeFilter> attributeRangeFilters = new ArrayList<AttributeRangeFilter>();
		attributeRangeFilters.add(createAttributeRangeFilter("arA00001"));
		
		
		advancedSearchController.setRequestHelper(requestHelper);
		advancedSearchController.setAdvancedSearchConfigurationProvider(advancedSearchConfigurationProvider);
		
		context.checking(new Expectations() {
			{
				oneOf(advancedSearchConfigurationProvider).getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE);
				will(returnValue(attributeRangeFilters));	
				oneOf(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));
				oneOf(storeConfig).getStoreCode();
				will(returnValue(STORE_CODE));
			}
		});
		
		Map<String, NonPreDefinedAttributeRangeFieldFormBean> resultMap = 
			advancedSearchController.constructNonPreDefinedAttributeRangeFilterMap();
		
		assertEquals("Should have only 1 formbean in the map since there is only 1 range filter", 1, resultMap.size());
		
		NonPreDefinedAttributeRangeFieldFormBean nonPreDefinedAttributeRangeFieldFormBean =
			resultMap.entrySet().iterator().next().getValue();
		
		//initially the from and to fields are null
		assertNull(nonPreDefinedAttributeRangeFieldFormBean.getFromField());
		assertNull(nonPreDefinedAttributeRangeFieldFormBean.getToField());
	}
	
	/**
	 * Test that an empty advanced search configuration returned will return an empty map.
	 */
	@Test
	public void testConstructEmptyMap() {
		final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider = 
			context.mock(AdvancedSearchConfigurationProvider.class);
		
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		
		final List<AttributeRangeFilter> attributeRangeFilters = new ArrayList<AttributeRangeFilter>();		
		
		advancedSearchController.setRequestHelper(requestHelper);
		advancedSearchController.setAdvancedSearchConfigurationProvider(advancedSearchConfigurationProvider);
		
		context.checking(new Expectations() {
			{
				oneOf(advancedSearchConfigurationProvider).getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE);
				will(returnValue(attributeRangeFilters));	
				oneOf(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));
				oneOf(storeConfig).getStoreCode();
				will(returnValue(STORE_CODE));
			}
		});
		
		Map<String, NonPreDefinedAttributeRangeFieldFormBean> resultMap = 
			advancedSearchController.constructNonPreDefinedAttributeRangeFilterMap();
		
		assertTrue("Should be empty since no AttributeRangeFilters were defined", resultMap.isEmpty());
	}

	/**
	 * One attribute in the filter map will result in a map with one element.
	 */
	@Test
	public void testConstructShortTextAttributeValueMapWithOneElement() {
		final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider = 
			context.mock(AdvancedSearchConfigurationProvider.class);
		
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		final AttributeType attributeType = AttributeType.SHORT_TEXT;
		
		final Map<Attribute, List<AttributeValueFilter>> attributeValueMap = new HashMap<Attribute, List<AttributeValueFilter>>();
		Attribute attribute = new AttributeImpl() {
			private static final long serialVersionUID = -4942454239742980508L;

			@Override
			public AttributeType getAttributeType() {
				return attributeType;
			}
		};
		attribute.setKey("A00001");
		List<AttributeValueFilter> filterList = new ArrayList<AttributeValueFilter>();
		filterList.add(createAttributeValueFilter("A00001", "atA00001_05", "Something"));
		attributeValueMap.put(attribute, filterList);
		
		advancedSearchController.setRequestHelper(requestHelper);
		advancedSearchController.setAdvancedSearchConfigurationProvider(advancedSearchConfigurationProvider);
		
		context.checking(new Expectations() {
			{
				oneOf(advancedSearchConfigurationProvider).getAttributeValueFilterMap(STORE_CODE, Locale.ENGLISH);
				will(returnValue(attributeValueMap));
				oneOf(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));
				oneOf(storeConfig).getStoreCode();
				will(returnValue(STORE_CODE));
				
			}
		});
		
		Map<String, Map<String, String>> resultMap = 
			advancedSearchController.constructShortTextAttributeValueMap(Locale.ENGLISH);
		
		assertEquals("Should have only 1 formbean in the map since there is only 1 range filter", 1, resultMap.size());
		
		Map<String, String> map =
			resultMap.entrySet().iterator().next().getValue();
		
		assertEquals(map.get("atA00001_05"), "Something");
	}		
	
	
	/**
	 * Test that an empty advanced search configuration returned will return an empty map.
	 */
	@Test
	public void testConstructEmptyShortTextAttributeValueMap() {
		final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider = 
			context.mock(AdvancedSearchConfigurationProvider.class);
		
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final StoreConfig storeConfig = context.mock(StoreConfig.class);
		
		final Map<Attribute, List<AttributeValueFilter>> attributeValueMap = new HashMap<Attribute, List<AttributeValueFilter>>();
		
		advancedSearchController.setRequestHelper(requestHelper);
		advancedSearchController.setAdvancedSearchConfigurationProvider(advancedSearchConfigurationProvider);
		
		context.checking(new Expectations() {
			{
				oneOf(advancedSearchConfigurationProvider).getAttributeValueFilterMap(STORE_CODE, Locale.ENGLISH);
				will(returnValue(attributeValueMap));
				oneOf(requestHelper).getStoreConfig();
				will(returnValue(storeConfig));
				oneOf(storeConfig).getStoreCode();
				will(returnValue(STORE_CODE));
			}
		});
		
		Map<String, Map<String, String>> resultMap = 
			advancedSearchController.constructShortTextAttributeValueMap(Locale.ENGLISH);
		
		assertTrue("Should be empty since no AttributeValueFilters were defined", resultMap.isEmpty());
	}	
	
	private AttributeRangeFilter createAttributeRangeFilter(final String key) {
		AttributeRangeFilter filter = new AttributeRangeFilterTesting();
		filter.setAttributeKey(key);
		
		return filter;
	}

	private AttributeValueFilter createAttributeValueFilter(final String key, final String seoId, final String displayName) {
		AttributeValueFilter filter = new AttributeValueFilterTesting();
		filter.setAttributeKey(key);
		filter.setId(seoId);
		filter.setDisplayName(displayName);
		
		return filter;
	}	
	
	/**
	 * Testing class for AttributeRangeFilter.
	 *
	 */
	public class AttributeRangeFilterTesting extends AttributeRangeFilterImpl {
		private static final long serialVersionUID = -819571626001728328L;

		private String attributeKey;
		
		@Override
		public void setAttributeKey(final String attributeKey) {
			this.attributeKey = attributeKey;
		}
		
		@Override
		public String getAttributeKey() {
			return attributeKey;
		}
	}
	
	/**
	 * Testing class for AttributeValueFilterImpl.
	 *
	 */
	public class AttributeValueFilterTesting extends AttributeValueFilterImpl {
		private static final long serialVersionUID = -1215845731688486306L;

		private String attributeKey;
		
		@Override
		public void setAttributeKey(final String attributeKey) {
			this.attributeKey = attributeKey;
		}
		
		@Override
		public String getAttributeKey() {
			return attributeKey;
		}

		@Override
		public String getSeoId() {
			return getId();
		}
		
	}	
}
