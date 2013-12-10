package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.MultiSkuDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Verify that ProductTypeAdapterTest populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class ProductTypeAdapterTest {

	private static final String ASSIGNED_SKU_OPTION = "assigned_sku_option";

	private static final String ASSIGNED_SKU_ATTRIBUTE = "assigned_sku_attribute";

	private static final String ASSIGNED_ATTRIBUTE = "assigned_attribute";

	private static final String PRODUCT_TYPE_NAME = "product_name";
	
	private static final String PRODUCT_TYPE_TEMPLATE = "product_template";

	private static final String PRODUCT_TAX_CODE = "tax_code";

	private static final String SKUOPTION_KEY = "sku_key";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;
	
	private SkuOption mockSkuOption;
	
	private TaxCode mockTaxCode;
	
	private AttributeGroupHelper mockAttributeGroupHelper;

	@Before
	public void setUp() throws Exception {
		mockSkuOption = context.mock(SkuOption.class);
		mockTaxCode = context.mock(TaxCode.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTaxCode).getCode();
				will(returnValue(PRODUCT_TAX_CODE));

				allowing(mockElasticPath).getBean(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(new ProductTypeImpl()));

				allowing(mockCachingService).findSkuOptionByKey(SKUOPTION_KEY);
				will(returnValue(mockSkuOption));
				allowing(mockCachingService).findTaxCodeByCode(PRODUCT_TAX_CODE);
				will(returnValue(mockTaxCode));
			}
		});
				
		mockAttributeGroupHelper = context.mock(AttributeGroupHelper.class);

		//setUpAdapter();
	}

	/**
	 * Tests PopulateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final ProductType mockDomain = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
                oneOf(mockDomain).getGuid();
                will(returnValue("AFD5EF35-F98F-4AC3-8893-8107D1EB0CEA"));
				oneOf(mockDomain).getName();
				will(returnValue(PRODUCT_TYPE_NAME));
				oneOf(mockDomain).getTemplate();
				will(returnValue(PRODUCT_TYPE_TEMPLATE));
				oneOf(mockDomain).getTaxCode();
				will(returnValue(mockTaxCode));
				oneOf(mockDomain).isWithMultipleSkus();
				will(returnValue(Boolean.TRUE));
				oneOf(mockDomain).isExcludedFromDiscount();
				will(returnValue(Boolean.FALSE));
			}
		});
		
		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter() {
			@Override
			List<String> createAssignedSkuOptions(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Arrays.asList(ASSIGNED_SKU_OPTION);
			}
			@Override
			List<String> createAssignedSkuAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Arrays.asList(ASSIGNED_SKU_ATTRIBUTE);
			}
			@Override
			List<String> createAssignedAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Arrays.asList(ASSIGNED_ATTRIBUTE);
			}
		};
		setUpProductTypeAdapter(productTypeAdapter);

		ProductTypeDTO productTypeDTO = productTypeAdapter.createDtoObject();
		
		productTypeAdapter.populateDTO(mockDomain, productTypeDTO);
		
		assertEquals(PRODUCT_TYPE_NAME, productTypeDTO.getName());
		assertEquals(PRODUCT_TYPE_TEMPLATE, productTypeDTO.getTemplate());
		assertEquals(PRODUCT_TAX_CODE, productTypeDTO.getDefaultTaxCode());
		
		assertEquals(Arrays.asList(ASSIGNED_SKU_ATTRIBUTE), productTypeDTO.getMultiSku().getAssignedAttributes());
		assertEquals(Arrays.asList(ASSIGNED_SKU_OPTION), productTypeDTO.getMultiSku().getAssignedSkuOptions());
		assertEquals(Arrays.asList(ASSIGNED_ATTRIBUTE), productTypeDTO.getAssignedAttributes());
	}

	private void setUpProductTypeAdapter(
			final ProductTypeAdapter productTypeAdapter) {
		productTypeAdapter.setElasticPath(mockElasticPath);
		productTypeAdapter.setCachingService(mockCachingService);
		productTypeAdapter.setAttributeGroupHelper(mockAttributeGroupHelper);
	}

	private ProductTypeAdapter createDefaultProductTypeAdapter() {
		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter();
		setUpProductTypeAdapter(productTypeAdapter);
		return productTypeAdapter;
	}

	private ProductTypeDTO createProductTypeDTO() {
		final MultiSkuDTO multiSku = new MultiSkuDTO();
		multiSku.setAssignedAttributes(Collections.<String> emptyList());
		multiSku.setAssignedSkuOptions(Arrays.asList(SKUOPTION_KEY));
		
		final ProductTypeDTO dto = new ProductTypeDTO();
		dto.setName(PRODUCT_TYPE_NAME);
		dto.setTemplate(PRODUCT_TYPE_TEMPLATE);
		dto.setAssignedAttributes(Collections.<String> emptyList());
		dto.setDefaultTaxCode(PRODUCT_TAX_CODE);
		dto.setMultiSku(multiSku);
		dto.setNoDiscount(Boolean.FALSE);
		return dto;
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		final ProductType mockDomain = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).setName(PRODUCT_TYPE_NAME);
				oneOf(mockDomain).setTemplate(PRODUCT_TYPE_TEMPLATE);
				oneOf(mockDomain).setTaxCode(mockTaxCode);
			}
		});

		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter() {
			@Override
			Set<AttributeGroupAttribute> createSkuAttributeGroupAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.emptySet();
			}
			@Override
			Set<AttributeGroupAttribute> createAttributeGroupAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.emptySet();
			}			
			@Override
			TaxCode findDefaultTaxCode(final String defaultTaxCode) {
				assertEquals(PRODUCT_TAX_CODE, defaultTaxCode);
				return mockTaxCode;
			}
			@Override
			SkuOption findSkuOption(final String skuOptionCode) {
				assertEquals(SKUOPTION_KEY, skuOptionCode);
				return mockSkuOption;
			}
		};
		setUpProductTypeAdapter(productTypeAdapter);

		
		final ProductTypeDTO dto = createProductTypeDTO();

		context.checking(new Expectations() {
			{
				oneOf(mockAttributeGroupHelper).populateAttributeGroupAttributes(
						Collections.<AttributeGroupAttribute>emptySet(), dto.getAssignedAttributes(), ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);

				oneOf(mockAttributeGroupHelper).populateAttributeGroupAttributes(
						Collections.<AttributeGroupAttribute>emptySet(),
						dto.getMultiSku().getAssignedAttributes(),
						ContextIdNames.PRODUCT_TYPE_SKU_ATTRIBUTE);

				oneOf(mockDomain).setWithMultipleSkus(Boolean.TRUE);
				oneOf(mockDomain).addOrUpdateSkuOption(mockSkuOption);
				oneOf(mockDomain).setExcludedFromDiscount(Boolean.FALSE);
			}
		});
		
		productTypeAdapter.populateDomain(dto, mockDomain);
	}


	/**
	 * Tests findDefaultTaxCode.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void ensureFindDefaultTaxCodeThrowsExceptionForNull() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		// Throw on null code
		productTypeAdapter.findDefaultTaxCode(null);
	}
	@Test(expected = PopulationRollbackException.class)
	public void ensureFindDefaultTaxCodeThrowsExceptionForBadTaxCode() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		// Throw on bad code
		final String badTaxCode = "bad_tax_code";
		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findTaxCodeByCode(badTaxCode);
				will(returnValue(null));
			}
		});
		productTypeAdapter.findDefaultTaxCode(badTaxCode);
	}

	@Test

	public void testFindDefaultTaxCode() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		assertEquals("Should work fine.", mockTaxCode, productTypeAdapter.findDefaultTaxCode(PRODUCT_TAX_CODE));
	}
	
	/**
	 * Tests FindSkuOption.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testFindSkuOptionThrowsExceptionForBadSku() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final String badSkuOptionKey = "bad_sku_option_key";

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findSkuOptionByKey(badSkuOptionKey);
				will(returnValue(null));
			}
		});
		
		productTypeAdapter.findSkuOption(badSkuOptionKey);
	}

	@Test

	public void testFindSkuOption() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		final String goodSkuOptionKey = "good_sku_option_key";
		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findSkuOptionByKey(goodSkuOptionKey);
				will(returnValue(mockSkuOption));
			}
		});
		productTypeAdapter.findSkuOption(goodSkuOptionKey);
	}
	
	/**
	 * Tests createAssignedSkuOptions.
	 */
	@Test
	public void testCreateAssignedSkuOptions() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
				
		final Set<SkuOption> skuOptions = new HashSet<SkuOption>();
		skuOptions.add(mockSkuOption);
		
		final ProductType mockDomain = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getSkuOptions();
				will(returnValue(skuOptions));
				oneOf(mockSkuOption).getOptionKey();
				will(returnValue(SKUOPTION_KEY));
			}
		});
		
		List<String> result = productTypeAdapter.createAssignedSkuOptions(mockDomain);
		
		assertEquals(Arrays.asList(SKUOPTION_KEY), result);
	}
	
	/**
	 * Tests createAttributeGroupAttributes.
	 */
	@Test
	public void testCreateAttributeGroupAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final ProductType mockDomain = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getProductAttributeGroupAttributes();
				will(returnValue(null));
				oneOf(mockDomain).setProductAttributeGroupAttributes(attributeGroupAttributes);
			}
		});
		
		productTypeAdapter.createAttributeGroupAttributes(mockDomain);
	}
	
	/**
	 * Tests createSkuAttributeGroupAttributes.
	 */
	@Test
	public void testCreateSkuAttributeGroupAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final ProductType mockDomain = context.mock(ProductType.class);
		final AttributeGroup mockAttributeGroup = context.mock(AttributeGroup.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getSkuAttributeGroup();
				will(returnValue(mockAttributeGroup));
				oneOf(mockAttributeGroup).getAttributeGroupAttributes();
				will(returnValue(null));
				oneOf(mockAttributeGroup).setAttributeGroupAttributes(attributeGroupAttributes);
				oneOf(mockDomain).setSkuAttributeGroup(mockAttributeGroup);
			}
		});
		
		productTypeAdapter.createSkuAttributeGroupAttributes(mockDomain);
	}

	/**
	 * Tests createAssignedAttributes.
	 */
	@Test
	public void testCreateAssignedAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final ProductType mockDomain = context.mock(ProductType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getProductAttributeGroupAttributes();
				will(returnValue(attributeGroupAttributes));

				oneOf(mockAttributeGroupHelper).createAssignedAttributes(attributeGroupAttributes);
				will(returnValue(Collections.emptyList()));
			}
		});
		
		List<String> result = productTypeAdapter.createAssignedAttributes(mockDomain);
		
		assertEquals(Collections.<String>emptyList(), result);
	}
	
	/**
	 * Tests createAssignedSkuAttributes.
	 */
	@Test
	public void testCreateAssignedSkuAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final ProductType mockDomain = context.mock(ProductType.class);
		final AttributeGroup mockAttributeGroup = context.mock(AttributeGroup.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getSkuAttributeGroup();
				will(returnValue(mockAttributeGroup));
				oneOf(mockAttributeGroup).getAttributeGroupAttributes();
				will(returnValue(attributeGroupAttributes));

				oneOf(mockAttributeGroupHelper).createAssignedAttributes(attributeGroupAttributes);
				will(returnValue(Collections.emptyList()));
			}
		});
		
		List<String> result = productTypeAdapter.createAssignedSkuAttributes(mockDomain);
		
		assertEquals(Collections.<String>emptyList(), result);
	}
	
	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		assertNotNull(productTypeAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		assertNotNull(productTypeAdapter.createDtoObject());
	}

}
