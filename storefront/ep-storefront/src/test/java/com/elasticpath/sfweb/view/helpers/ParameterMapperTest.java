package com.elasticpath.sfweb.view.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.impl.AttributeValueFilterImpl;
import com.elasticpath.service.catalogview.FilterFactory;

/**
 * Test for ParameterMapper.
 */
public class ParameterMapperTest {
	
	private static final String STORE_CODE = "TESTSTORE";
	private static final String ATTRIBUTE_KEY = "A00001";
	private ParameterMapper parameterMapper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FilterFactory filterFactory;
	
	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		filterFactory = context.mock(FilterFactory.class);
		parameterMapper = new ParameterMapper();
		parameterMapper.setFilterFactory(filterFactory);
	}
	
	/**
	 * Test for converting an attribute to a parameter when there are no attribute value filters.
	 */
	@Test
	public void testConvertAttributeToParameterEmptyMap() {
		context.checking(new Expectations() { {															
			allowing(filterFactory).getAllSimpleValuesMap(STORE_CODE);
			will(returnValue(new HashMap<String, AttributeValueFilter>()));
			}
		});
		
		assertTrue(StringUtils.isEmpty(parameterMapper.convertAttributeToParameter(ATTRIBUTE_KEY, STORE_CODE)));
	}
	
	/**
	 * Test for converting an attribute to a parameter when there is a matching attribute value filter.
	 */
	@Test
	public void testConvertAttributeToParameterMapMatchingAttribute() {
		final Map<String, AttributeValueFilter> map = new HashMap<String, AttributeValueFilter>();
		AttributeValueFilterImpl attributeValueFilter = new AttributeValueFilterImplTesting();
		
		map.put(ATTRIBUTE_KEY, attributeValueFilter);
		
		context.checking(new Expectations() { {															
			allowing(filterFactory).getAllSimpleValuesMap(STORE_CODE);
			will(returnValue(map));
			}
		});
		
		assertEquals("atA00001", parameterMapper.convertAttributeToParameter(ATTRIBUTE_KEY, STORE_CODE));
	}
	/**
	 * Test for convertFromRangeAttributeToParameter.
	 */
	@Test
	public void testConvertFromRangeAttributeToParameter() {
		String attributeKey = ATTRIBUTE_KEY;		
		String parameterString = "nonPreDefinedAttributeRangeFilterMap[" + attributeKey + "]." + "fromField";
		assertEquals(parameterString, parameterMapper.convertFromRangeAttributeToParameter(attributeKey));
		
		attributeKey = "";		
		assertTrue(StringUtils.isEmpty(parameterMapper.convertFromRangeAttributeToParameter(attributeKey)));
		
		attributeKey = null;	
		assertTrue(StringUtils.isEmpty(parameterMapper.convertFromRangeAttributeToParameter(attributeKey)));
	}
	
	/**
	 * Test for convertToRangeAttributeToParameter.
	 */
	@Test
	public void testConvertToRangeAttributeToParameter() {
		String attributeKey = ATTRIBUTE_KEY;
		String parameterString = "nonPreDefinedAttributeRangeFilterMap[" + attributeKey  + "]." + "toField";
		assertEquals(parameterString, parameterMapper.convertToRangeAttributeToParameter(attributeKey));
		
		attributeKey = "";		
		assertTrue(StringUtils.isEmpty(parameterMapper.convertToRangeAttributeToParameter(attributeKey)));
		
		attributeKey = null;	
		assertTrue(StringUtils.isEmpty(parameterMapper.convertToRangeAttributeToParameter(attributeKey)));
	}
	
	/**
	 * Class for use in mocking the behaviour of getAttributeKey().
	 */
	private class AttributeValueFilterImplTesting extends AttributeValueFilterImpl {
		private static final long serialVersionUID = 4696405352751737197L;

		@Override
		public String getAttributeKey() {
			return ATTRIBUTE_KEY;
		}
	}
}
