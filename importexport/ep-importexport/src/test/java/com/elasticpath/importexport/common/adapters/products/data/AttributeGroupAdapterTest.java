package com.elasticpath.importexport.common.adapters.products.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueFactory;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.AttributeValuesDTO;
import com.elasticpath.validation.service.ValidatorUtils;

/**
 * Verify that AttributeGroupAdapterTest populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class AttributeGroupAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String KEY = "key";

	private static final String KEY_EN = KEY + "_en";
	
	private static final DisplayValue DISPLAY_VALUE = new DisplayValue("en", "value1");
	
	private static final Locale LOCALE = new Locale("en");

	private AttributeGroupAdapter attributeGroupAdapter;
	
	private CachingService cachingService;
	
	private ElasticPath elasticPath;
	
	private Attribute attribute;
	
	private ValidatorUtils validatorUtils;
	
	/**
	 * Setup required for each test.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		attribute = context.mock(Attribute.class);
		validatorUtils = context.mock(ValidatorUtils.class);
		elasticPath = context.mock(ElasticPath.class);
		cachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				allowing(cachingService).findAttribiteByKey(KEY); will(returnValue(attribute));
			}
		});

		AttributeValuesAdapter attributeValuessAdapter = new AttributeValuesAdapter();
		attributeValuessAdapter.setCachingService(cachingService);
		attributeValuessAdapter.setElasticPath(elasticPath);
		attributeValuessAdapter.setValidatorUtils(validatorUtils);
		
		attributeGroupAdapter = new AttributeGroupAdapter();
		attributeGroupAdapter.setCachingService(cachingService);
		attributeGroupAdapter.setElasticPath(elasticPath);
		attributeGroupAdapter.setAttributeValuesAdapter(attributeValuessAdapter);
	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void testPopulateDTO() {
		final AttributeValueGroup attributeValueGroup = context.mock(AttributeValueGroup.class);
		final AttributeValue attributeValue = context.mock(AttributeValue.class);
		
		final Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		
		attributeValueMap.put(KEY, attributeValue);
		context.checking(new Expectations() {
			{
				oneOf(attributeValueGroup).getAttributeValueMap(); will(returnValue(attributeValueMap));
				atLeast(1).of(attribute).getKey(); will(returnValue(KEY));
				atLeast(1).of(attributeValue).getAttribute(); will(returnValue(attribute));
				oneOf(attributeValue).getLocalizedAttributeKey(); will(returnValue(KEY_EN));
				allowing(attributeValue).getValue(); will(returnValue("Attribute value"));
				oneOf(attribute).isMultiValueEnabled(); will(returnValue(true));
				oneOf(attributeValue).getStringValue(); will(returnValue(DISPLAY_VALUE.getValue()));
			}
		});
		
		
		AttributeGroupDTO dto = new AttributeGroupDTO();
		attributeGroupAdapter.populateDTO(attributeValueGroup, dto);
		
		AttributeValuesDTO attributeValuesDTO = dto.getAttributeValuess().get(0);

		assertEquals(KEY, attributeValuesDTO.getKey());
		assertEquals(DISPLAY_VALUE.getLanguage(), attributeValuesDTO.getValues().get(0).getLanguage());
		assertEquals(DISPLAY_VALUE.getValue(), attributeValuesDTO.getValues().get(0).getValue());
		assertEquals(1, attributeValuesDTO.getValues().size());		
		assertEquals(1, dto.getAttributeValuess().size());
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		// prepare DTO :
		AttributeValuesDTO attributeValuesDTO = new AttributeValuesDTO();
		attributeValuesDTO.setKey(KEY);
		attributeValuesDTO.setValues(Arrays.asList(DISPLAY_VALUE));
		AttributeGroupDTO dto = new AttributeGroupDTO();		
		dto.setAttributeValues(Arrays.asList(attributeValuesDTO));

		// prepare Domain
		final Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		final AttributeValue attributeValue = context.mock(AttributeValue.class);
    	final AttributeValueFactory attributeValueFactory = context.mock(AttributeValueFactory.class);
		final AttributeValueGroup attributeValueGroup = context.mock(AttributeValueGroup.class);
		
		context.checking(new Expectations() {
			{
				oneOf(attributeValueGroup).getAttributeValueMap(); will(returnValue(attributeValueMap));
				oneOf(attributeValueGroup).getAttributeValue(KEY, LOCALE); will(returnValue(null));
				oneOf(attributeValueGroup).getAttributeValueFactory(); will(returnValue(attributeValueFactory));
				oneOf(attributeValueFactory).createAttributeValue(attribute, KEY_EN); will(returnValue(attributeValue));
				oneOf(attribute).getCatalog(); will(returnValue(null));
				oneOf(attributeValue).setStringValue(DISPLAY_VALUE.getValue());
				oneOf(attributeValue).getLocalizedAttributeKey(); will(returnValue(KEY_EN));
				
				oneOf(validatorUtils).validateAttributeValue(attributeValue);
			}
		});
		
		attributeGroupAdapter.populateDomain(dto, attributeValueGroup);
		
		assertEquals(attributeValue, attributeValueMap.get(KEY_EN));
		assertEquals(1, attributeValueMap.size());
	}

}
