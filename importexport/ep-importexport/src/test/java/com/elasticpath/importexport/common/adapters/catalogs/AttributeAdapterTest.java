package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.AttributeTypeType;
import com.elasticpath.importexport.common.dto.catalogs.AttributeUsageType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Verify that AttributeAdapter (from Catalogs) populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class AttributeAdapterTest {

	private static final String ATTRIBUTE_KEY = "AttributeKey0";

	private static final String ATTRIBUTE_NAME = null;

	private static final AttributeUsageType ATTRIBUTE_USAGE = AttributeUsageType.Product;

	private static final AttributeTypeType ATTRIBUTE_TYPE = AttributeTypeType.Image;

	private static final Boolean ATTRIBUTE_LOCALE_DEPENDANT = Boolean.TRUE;

	private static final Boolean ATTRIBUTE_REQUIRED = Boolean.TRUE;

	private static final Boolean ATTRIBUTE_MULTI_VALUE_ENABLED = Boolean.FALSE;

	private static final Boolean ATTRIBUTE_IS_GLOBAL = Boolean.TRUE;

	private AttributeDTO attributeDto;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;
	
	private Attribute mockAttributeDomain;
	
	private CachingService mockCachingService;
	
	private AttributeAdapter attributeAdapter;

	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		
		attributeDto = new AttributeDTO();
		mockAttributeDomain = context.mock(Attribute.class);
		mockCachingService = context.mock(CachingService.class);
		
		attributeAdapter = new AttributeAdapter();
		attributeAdapter.setElasticPath(mockElasticPath);
		attributeAdapter.setCachingService(mockCachingService);

		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.ATTRIBUTE);
				will(returnValue(mockAttributeDomain));
			}
		});
	}
	
	private void setUpDefaultDto() {
		attributeDto.setKey(ATTRIBUTE_KEY);
		attributeDto.setName(ATTRIBUTE_NAME);
		attributeDto.setUsage(ATTRIBUTE_USAGE);
		attributeDto.setType(ATTRIBUTE_TYPE);
		attributeDto.setMultiLanguage(ATTRIBUTE_LOCALE_DEPENDANT);
		attributeDto.setRequired(ATTRIBUTE_REQUIRED);
		attributeDto.setMultivalue(ATTRIBUTE_MULTI_VALUE_ENABLED);
		attributeDto.setGlobal(ATTRIBUTE_IS_GLOBAL);
	}

	/**
	 * Check that all required fields for Dto object are being set during domain population.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockAttributeDomain).getKey();
				will(returnValue(ATTRIBUTE_KEY));
				oneOf(mockAttributeDomain).getName();
				will(returnValue(ATTRIBUTE_NAME));
				oneOf(mockAttributeDomain).getAttributeUsage();
				will(returnValue(ATTRIBUTE_USAGE.usage()));
				oneOf(mockAttributeDomain).getAttributeType();
				will(returnValue(ATTRIBUTE_TYPE.type()));
				oneOf(mockAttributeDomain).isLocaleDependant();
				will(returnValue(ATTRIBUTE_LOCALE_DEPENDANT));
				oneOf(mockAttributeDomain).isRequired();
				will(returnValue(ATTRIBUTE_REQUIRED));
				oneOf(mockAttributeDomain).isMultiValueEnabled();
				will(returnValue(ATTRIBUTE_MULTI_VALUE_ENABLED));
				oneOf(mockAttributeDomain).isGlobal();
				will(returnValue(ATTRIBUTE_IS_GLOBAL));
			}
		});

		AttributeDTO dto = attributeAdapter.createDtoObject();
		
		attributeAdapter.populateDTO(mockAttributeDomain, dto);
		
		assertEquals(ATTRIBUTE_KEY, dto.getKey());
		assertEquals(ATTRIBUTE_NAME, dto.getName());
		assertEquals(ATTRIBUTE_USAGE, dto.getUsage());
		assertEquals(ATTRIBUTE_TYPE, dto.getType());
		assertEquals(ATTRIBUTE_LOCALE_DEPENDANT, dto.getMultiLanguage());
		assertEquals(ATTRIBUTE_REQUIRED, dto.getRequired());
		assertEquals(ATTRIBUTE_MULTI_VALUE_ENABLED, dto.getMultivalue());
		assertEquals(ATTRIBUTE_IS_GLOBAL, dto.getGlobal());
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		setUpDefaultDto();

		context.checking(new Expectations() {
			{
				oneOf(mockAttributeDomain).setKey(ATTRIBUTE_KEY);
				oneOf(mockAttributeDomain).setName(ATTRIBUTE_NAME);
				oneOf(mockAttributeDomain).setAttributeUsage(ATTRIBUTE_USAGE.usage());
				oneOf(mockAttributeDomain).setAttributeType(ATTRIBUTE_TYPE.type());
				oneOf(mockAttributeDomain).setMultiValueEnabled(ATTRIBUTE_MULTI_VALUE_ENABLED);
				oneOf(mockAttributeDomain).setLocaleDependant(ATTRIBUTE_LOCALE_DEPENDANT);
				oneOf(mockAttributeDomain).setRequired(ATTRIBUTE_REQUIRED);
				oneOf(mockAttributeDomain).setGlobal(ATTRIBUTE_IS_GLOBAL);
			}
		});

		attributeAdapter.populateDomain(attributeDto, mockAttributeDomain);
	}	
	
	/**
	 * Check that all required fields for domain object are being set during domain population (unsuccessful situation).
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnMultilanguageCheck() {
		setUpDefaultDto();

		attributeDto.setType(AttributeTypeType.Integer);
		attributeDto.setMultiLanguage(true);
		attributeAdapter.populateDomain(attributeDto, mockAttributeDomain);
	}
	
	/**
	 * Check that all required fields for domain object are being set during domain population (unsuccessful situation).
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testPopulateDomainThrowsOnMultivalueCheck() {
		setUpDefaultDto();

		attributeDto.setMultiLanguage(false);
		attributeDto.setMultivalue(true);
		
		attributeAdapter.populateDomain(attributeDto, mockAttributeDomain);
	}

	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(attributeAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(attributeAdapter.createDtoObject());
	}

}
