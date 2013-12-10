/**
 * 
 */
package com.elasticpath.common.pricing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.assembler.pricing.PriceListDescriptorDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.BaseAmountUpdateStrategy;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.pricing.dao.PriceListDescriptorDao;
import com.elasticpath.service.pricing.impl.PriceListDescriptorServiceImpl;

/**
 * Tests {@link PriceListServiceImpl}'s behaviour.
 */
public class PriceListServiceImplTest {

	private static final String PRODUCT_SKU_ENRICH_DTO_BY_GUIDS = "PRODUCT_SKU_ENRICH_DTO_BY_GUIDS";

	private static final String PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS = "PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS";

	private static final String BUNDLE_ENRICH_DTO_BY_GUIDS = "BUNDLE_ENRICH_DTO_BY_GUIDS";

	private static final String PRODUCT_ENRICH_DTO_BY_GUIDS = "PRODUCT_ENRICH_DTO_BY_GUIDS";

	private static final String TEST_NAME = "TEST_NAME";

	private static final String BASE_AMOUNT_GUID = "BA_GUID";

	private static final String PRICE_LIST_GUID = "Catalog_USD";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE); // to make Assemblers accessible to mock.
		}
	};
	
	private PriceListServiceImpl priceListService;
	
	private final PriceListDescriptorService priceListDescriptorService = context.mock(PriceListDescriptorService.class);
	private final BaseAmountService baseAmountService = context.mock(BaseAmountService.class);	
	private final BaseAmountDtoAssembler baseAmountDtoAssembler = context.mock(BaseAmountDtoAssembler.class);
	private final PriceListDescriptorDtoAssembler priceListDescriptorDtoAssembler = context.mock(PriceListDescriptorDtoAssembler.class);	
	private final BaseAmountUpdateStrategy baseAmountStrategy = context.mock(BaseAmountUpdateStrategy.class);

	@SuppressWarnings("unchecked")
	private final ChangeSetObjects<BaseAmountDTO> changeSet = context.mock(ChangeSetObjects.class);

	private final BaseAmountFilter baseAmountFilter = context.mock(BaseAmountFilter.class);

	private final BaseAmount baseAmount = context.mock(BaseAmount.class);

	private final PriceListDescriptor priceListDescriptor = context.mock(PriceListDescriptor.class);
	
	private final PriceListDescriptorDao priceListDescriptorDao = context.mock(PriceListDescriptorDao.class);
		
	private final PriceListDescriptorServiceImpl priceListDescriptorServiceImpl = new PriceListDescriptorServiceImpl();
	
	private final ProductService productService = context.mock(ProductService.class);
	
	/**
	 * SetUps the PriceListServiceImpl instance.
	 */
	@Before
	public void setUp() {
		priceListService = new PriceListServiceImpl();
		priceListService.setBaseAmountDtoAssembler(baseAmountDtoAssembler);
		priceListService.setBaseAmountService(baseAmountService);
		priceListService.setBaseAmountUpdateStrategy(baseAmountStrategy);
		priceListService.setPriceListDescriptorDtoAssembler(priceListDescriptorDtoAssembler);
		priceListService.setPriceListDescriptorService(priceListDescriptorService);
		priceListService.setProductService(productService);
		priceListDescriptorServiceImpl.setPriceListDescriptorDao(priceListDescriptorDao);
	}

	/**
	 * Test method for {@link PriceListServiceImpl#modifyBaseAmountChangeSet(DtoChangeSet)}.
	 */
	@Test
	public void testProcessBaseAmountChangeSet() {
		context.checking(new Expectations() { {
			oneOf(baseAmountStrategy).modifyBaseAmounts(changeSet);
		} });
		priceListService.modifyBaseAmountChangeSet(changeSet);
	}

	/**
	 * Test method for {@link PriceListServiceImpl#getBaseAmounts(com.elasticpath.common.pricing.service.BaseAmountFilter)}.
	 */
	@Test
	public void testGetBaseAmounts() {
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final List<BaseAmount> baseAmountList = Arrays.asList(baseAmount);
		
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts(baseAmountFilter); will(returnValue(baseAmountList));
			oneOf(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
		} });
		
		Collection<BaseAmountDTO> baseAmounts = priceListService.getBaseAmounts(baseAmountFilter);
		
		assertEquals(1, baseAmounts.size());
		assertSame(baseAmountDTO, baseAmounts.iterator().next());
	}

	/**
	 * Test method for {@link PriceListServiceImpl#getPriceListDescriptor(String)}.
	 */
	@Test
	public void testGetPriceListDescriptor() {	
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).findByGuid(PRICE_LIST_GUID); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptorDtoAssembler).assembleDto(priceListDescriptor); will(returnValue(priceListDescriptorDTO));
		} });
		
		assertSame(priceListDescriptorDTO, priceListService.getPriceListDescriptor(PRICE_LIST_GUID));
	}

	/**
	 * Test method for {@link PriceListServiceImpl#getBaseAmount(String)}.
	 */
	@Test
	public void testGetBaseAmount() {
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
	
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findByGuid(BASE_AMOUNT_GUID); will(returnValue(baseAmount));
			oneOf(baseAmountDtoAssembler).assembleDto(baseAmount); will(returnValue(baseAmountDTO));
		} });
		
		assertSame(baseAmountDTO, priceListService.getBaseAmount(BASE_AMOUNT_GUID));
	}

	/**
	 * Test method for {@link PriceListServiceImpl#getPriceListDescriptors(boolean)}.
	 */
	@Test
	public void testGetPriceListDescriptors() {
		final List<PriceListDescriptor> priceListDescriptors = Arrays.asList(priceListDescriptor);
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
				
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).getPriceListDescriptors(false); will(returnValue(priceListDescriptors));
			oneOf(priceListDescriptorDtoAssembler).assembleDto(priceListDescriptors); will(returnValue(Arrays.asList(priceListDescriptorDTO)));
		} });
		
		Collection<PriceListDescriptorDTO> descriptors = priceListService.getPriceListDescriptors(false);
		
		assertEquals(1, descriptors.size());		
		assertSame(priceListDescriptorDTO, descriptors.iterator().next());
	}

	/**
	 * Test method for {@link PriceListServiceImpl#saveOrUpdate(com.elasticpath.common.dto.pricing.PriceListDescriptorDTO)}.
	 */
	@Test
	public void testSaveOrUpdate1() {
		priceListService = new PriceListServiceImpl() {
			@Override
			protected boolean isDtoPersistent(final PriceListDescriptorDTO pldDto) {
				return true;
			}
		};
		priceListService.setPriceListDescriptorDtoAssembler(priceListDescriptorDtoAssembler);
		priceListService.setPriceListDescriptorService(priceListDescriptorService);
		
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setCurrencyCode("CAD");
		
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorDtoAssembler).assembleDomain(priceListDescriptorDTO); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptorService).update(priceListDescriptor); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptorDtoAssembler).assembleDto(priceListDescriptor); will(returnValue(priceListDescriptorDTO));
		} });
		
		assertSame(priceListDescriptorDTO, priceListService.saveOrUpdate(priceListDescriptorDTO));
	}
	
	/**
	 * Test method for {@link PriceListServiceImpl#saveOrUpdate(com.elasticpath.common.dto.pricing.PriceListDescriptorDTO)}.
	 */
	@Test
	public void testSaveOrUpdate2() {
		priceListService = new PriceListServiceImpl() {
			@Override
			protected boolean isDtoPersistent(final PriceListDescriptorDTO pldDto) {
				return false;
			}
		};		
		priceListService.setPriceListDescriptorDtoAssembler(priceListDescriptorDtoAssembler);
		priceListService.setPriceListDescriptorService(priceListDescriptorService);
		
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setCurrencyCode("CAD");
		
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorDtoAssembler).assembleDomain(priceListDescriptorDTO); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptorService).add(priceListDescriptor); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptorDtoAssembler).assembleDto(priceListDescriptor); will(returnValue(priceListDescriptorDTO));
		} });
		
		assertSame(priceListDescriptorDTO, priceListService.saveOrUpdate(priceListDescriptorDTO));
	}

	/**
	 * Test method for {@link PriceListServiceImpl#isDtoPersistent(com.elasticpath.common.dto.pricing.PriceListDescriptorDTO)}.
	 */
	@Test
	public void testIsDtoPersistent() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();

		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).findByGuid(with((String) null));
			will(returnValue(null));
		} });
		
		assertFalse(priceListService.isDtoPersistent(priceListDescriptorDTO));

		priceListDescriptorDTO.setGuid(PRICE_LIST_GUID);

		context.checking(new Expectations() { {
			oneOf(priceListDescriptorService).findByGuid(with(PRICE_LIST_GUID));
			will(returnValue(new PriceListDescriptorImpl()));
		} });
		
		assertTrue(priceListService.isDtoPersistent(priceListDescriptorDTO));
	}

	
	/**
	 * Test method for 
	 * {@link PriceListServiceImpl#isPriceListNameUnique(String, String)}.
	 */
	@Test
	public void testCheckNameUnique() {
		
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorDao).findByName(TEST_NAME); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptor).getGuid(); will(returnValue(PRICE_LIST_GUID));
		} });
		
		assertTrue(priceListDescriptorServiceImpl.isPriceListNameUnique(PRICE_LIST_GUID, TEST_NAME));
		
		context.checking(new Expectations() { {
			oneOf(priceListDescriptorDao).findByName(TEST_NAME); will(returnValue(priceListDescriptor));
			oneOf(priceListDescriptor).getGuid(); will(returnValue(PRICE_LIST_GUID + "bla"));
		} });
		
		assertFalse(priceListDescriptorServiceImpl.isPriceListNameUnique(PRICE_LIST_GUID, TEST_NAME));
		
	}
	
	/**
	 * Test method for test Base amount ext for exact match.
	 * There are different query names based on the parameters provided in baseAmountFilterExt.
	 * The queries are generated because Derby does not support Null in sql.
	 * This test is to make sure the correct query name is selected. 
	 */
	@Test
	public void testGetBaseAmountsExtForExactMatch() {
		boolean exactMatch = true;
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final List<BaseAmount> baseAmountList = Arrays.asList(baseAmount);
		
		final BaseAmountFilterExt baseAmountFilterExt = new BaseAmountFilterExtImpl();
		baseAmountFilterExt.setLowestPrice(BigDecimal.ZERO);
		
		final Object [] searchCriteria = new Object [] {
				baseAmountFilterExt.getPriceListDescriptorGuid(),
				baseAmountFilterExt.getObjectType(),
				null,
				baseAmountFilterExt.getLowestPrice(),
				baseAmountFilterExt.getHighestPrice(),
				baseAmountFilterExt.getQuantity()				
		};
		
		
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_LOWVALUE_PROVIDED",
				searchCriteria, baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).getProductSkuGuids(null); will(returnValue(new ArrayList<String>()));
		} });
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(null);
		baseAmountFilterExt.setHighestPrice(BigDecimal.TEN);
		final int lowestPriceIndex = 3;
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		final int highestPriceIndex = 4;
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_UPVALUE_PROVIDED",
				searchCriteria, baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(null);
		baseAmountFilterExt.setHighestPrice(null);
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE",
				searchCriteria, baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(BigDecimal.ONE);
		baseAmountFilterExt.setHighestPrice(BigDecimal.TEN);
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_CASE_SENSITIVE_LOWVALUE_UPVALUE_PROVIDED",
				searchCriteria, baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
	}
	
	/**
	 * Test method for test Base amount ext.
	 * There are different query names based on the parameters provided in baseAmountFilterExt.
	 * The queries are generated because Derby does not support Null in sql.
	 * This test is to make sure the correct query name is selected.
	 */
	@Test
	public void testGetBaseAmountsExt() {
		boolean exactMatch = false;
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final List<BaseAmount> baseAmountList = Arrays.asList(baseAmount);
		
		final BaseAmountFilterExt baseAmountFilterExt = new BaseAmountFilterExtImpl();
		baseAmountFilterExt.setLowestPrice(BigDecimal.ZERO);
		
		final Object [] searchCriteria = new Object [] {
				baseAmountFilterExt.getPriceListDescriptorGuid(),
				baseAmountFilterExt.getObjectType(),
				null,
				baseAmountFilterExt.getLowestPrice(),
				baseAmountFilterExt.getHighestPrice(),
				baseAmountFilterExt.getQuantity()				
		};
		
		
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_LOWVALUE_PROVIDED", searchCriteria,
				baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).getProductSkuGuids(null); will(returnValue(new ArrayList<String>()));
		} });
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(null);
		baseAmountFilterExt.setHighestPrice(BigDecimal.TEN);
		final int lowestPriceIndex = 3;
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		final int highestPriceIndex = 4;
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_UPVALUE_PROVIDED", searchCriteria,
				baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(null);
		baseAmountFilterExt.setHighestPrice(null);
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER", searchCriteria,
				baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
		
		baseAmountFilterExt.setLowestPrice(BigDecimal.ONE);
		baseAmountFilterExt.setHighestPrice(BigDecimal.TEN);
		searchCriteria[lowestPriceIndex] = baseAmountFilterExt.getLowestPrice();
		searchCriteria[highestPriceIndex] = baseAmountFilterExt.getHighestPrice();
		context.checking(new Expectations() { {
			oneOf(baseAmountService).findBaseAmounts("BASE_AMOUNTS_BY_EXT_FILTER_LOWVALUE_UPVALUE_PROVIDED",
				searchCriteria, baseAmountFilterExt.getStartIndex(), baseAmountFilterExt.getLimit(), new ArrayList<String>());
			will(returnValue(baseAmountList));
			allowing(baseAmountDtoAssembler).assembleDto(baseAmountList); will(returnValue(Arrays.asList(baseAmountDTO)));
			allowing(productService).findEnrichingData(PRODUCT_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(BUNDLE_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_OPTION_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
			allowing(productService).findEnrichingData(PRODUCT_SKU_ENRICH_DTO_BY_GUIDS, new ArrayList<String>(), null);
		} });
		
		
		priceListService.getBaseAmountsExt(baseAmountFilterExt , exactMatch);
	}
}
