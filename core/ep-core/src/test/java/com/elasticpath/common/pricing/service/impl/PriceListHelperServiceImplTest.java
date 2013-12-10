package com.elasticpath.common.pricing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.MockInterface;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.tags.TagSet;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests {@link PriceListServiceImpl}'s behaver.
 */
public class PriceListHelperServiceImplTest { // NOPMD (it is a big test)

	private static final String TYPE_SKU = "SKU";

	private static final Currency CURRENCY_USD = Currency.getInstance("USD");

	private static final String TYPE_PRODUCT = "PRODUCT";

	private static final String PRODUCT_CODE = "productCode";

	private static final String PRICE_LIST_GUID = "Catalog_USD";

	private static final String PRICE_LIST_GUID2 = "Catalog_GBP";
	
	private static final String PRICE_LIST_GUID3 = "Catalog_UAH";

	private static final String PRODUCT_SKU_CODE = "productSkuCode";


	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PriceListLookupService priceListLookupService = context.mock(PriceListLookupService.class);
	
	private final PriceListService priceListService = context.mock(PriceListService.class);

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	private final ChangeSetObjects<BaseAmountDTO> changeSet = new ChangeSetObjectsImpl<BaseAmountDTO>();

	private final BaseAmountFilter baseAmountFilter = context.mock(BaseAmountFilter.class);

	private final Product product = context.mock(Product.class);

	private final ProductSku productSku = context.mock(ProductSku.class);

	private final Catalog catalog = context.mock(Catalog.class);

	private PriceListHelperServiceImpl priceListHelperService;

	/**
	 * SetUps the PriceListServiceImpl instance.
	 */
	@Before
	public void setUp() {
		priceListHelperService = new PriceListHelperServiceImpl();
		priceListHelperService.setPriceListService(priceListService);
		priceListHelperService.setPriceListLookupService(priceListLookupService);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(Product)}.
	 */
	@Test
	public void testGetPriceListMapProduct() {
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			public Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceListMap(final Product product, final boolean masterOnly) {
				return mock.method(product, masterOnly);				
			}
		};
		
		context.checking(new Expectations() { { 
			oneOf(mock).method(product, false); will(returnValue(Collections.emptyMap()));
		} });
		
		assertSame(Collections.emptyMap(), priceListHelperService.getPriceListMap(product));
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(Product, boolean)}.
	 */
	@Test
	public void testGetPriceListMapProductBooleanFalse() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		final MockInterface mock = context.mock(MockInterface.class);
		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getPriceInfoInternal(final String code, final String objectType, 
					final List<PriceListDescriptorDTO> priceListDescriptors) {
				return mock.method(code, objectType, priceListDescriptors);
			}
			@Override
			List<PriceListDescriptorDTO> prepareDescriptorsList(final Product product, final boolean masterOnly) {
				return mock.method(product, masterOnly);
			}
		};
		
		final List<PriceListDescriptorDTO> priceListDescriptorDTOList = Arrays.asList(priceListDescriptorDTO);
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> resultMap = new HashMap<PriceListDescriptorDTO, List<BaseAmountDTO>>();
		
		context.checking(new Expectations() { {
			oneOf(product).getCode(); will(returnValue(PRODUCT_CODE));
			oneOf(mock).method(product, false); will(returnValue(priceListDescriptorDTOList));
			oneOf(mock).method(PRODUCT_CODE, TYPE_PRODUCT, priceListDescriptorDTOList); will(returnValue(resultMap));
		} });
		
		assertSame(resultMap, priceListHelperService.getPriceListMap(product, false));
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#prepareDescriptorsList(Product, boolean)}.
	 */
	@Test
	public void testPrepareDescriptorsListFalse() {
		final MockInterface mock = context.mock(MockInterface.class);
		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			public List<PriceListDescriptorDTO> findAllDescriptors(final Product product) {
				return mock.method(product);
			}
		};
		
		context.checking(new Expectations() { {
			oneOf(mock).method(product); will(returnValue(Collections.emptyList()));
		} });
		
		assertSame(Collections.emptyList(), priceListHelperService.prepareDescriptorsList(product, false));
	}
	

	/**
	 * Test method for {@link PriceListHelperServiceImpl#prepareDescriptorsList(Product, boolean)}.
	 */
	@Test
	public void testPrepareDescriptorsListTrue() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		final MockInterface mock = context.mock(MockInterface.class);
		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			public List <PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog, final Currency currency) {
				return mock.method(catalog, currency);
			}
			
			@Override
			public Set<Currency> getAllCurrenciesFor(final Catalog catalog) {
				return Collections.singleton(CURRENCY_USD);
			}
			
		};
		
		context.checking(new Expectations() { {			
			oneOf(product).getMasterCatalog(); will(returnValue(catalog));
			oneOf(mock).method(catalog, CURRENCY_USD); will(returnValue(Collections.singletonList(priceListDescriptorDTO)));
		} });
		
		List<PriceListDescriptorDTO> list = priceListHelperService.prepareDescriptorsList(product, true);
		
		assertEquals(1, list.size());
		assertSame(priceListDescriptorDTO, list.get(0));
	}
	

	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(BaseAmountFilter)}.
	 */
	@Test
	public void testGetPriceListMapByFilterSinglePriceList() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setGuid(PRICE_LIST_GUID);

		final Collection<String> baseAmountGuids = new ArrayList<String>();

		final BaseAmountDTO baseAmountDTO1 = new BaseAmountDTO();
		baseAmountDTO1.setPriceListDescriptorGuid(PRICE_LIST_GUID);
		baseAmountGuids.add(PRICE_LIST_GUID);

		final BaseAmountDTO baseAmountDTO2 = new BaseAmountDTO();
		baseAmountDTO2.setPriceListDescriptorGuid(PRICE_LIST_GUID);
		baseAmountGuids.add(PRICE_LIST_GUID);

		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final BaseAmountFilter baseAmountFilter) {
				return Arrays.asList(baseAmountDTO1, baseAmountDTO2);
			}
		};
		priceListHelperService.setPriceListService(priceListService);
		
		context.checking(new Expectations() { {
			oneOf(priceListService).getPriceListDescriptors(baseAmountGuids); will(returnValue(Arrays.asList(priceListDescriptorDTO)));
			
			allowing(baseAmountFilter).getObjectType(); will(returnValue(TYPE_SKU));
			allowing(baseAmountFilter).getObjectGuid(); will(returnValue(PRODUCT_SKU_CODE));
		} });
		
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = priceListHelperService.getPriceListMap(baseAmountFilter);
		
		assertSame(baseAmountDTO1, map.get(priceListDescriptorDTO).get(0));
		assertSame(baseAmountDTO2, map.get(priceListDescriptorDTO).get(1));
		
	}
	
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(BaseAmountFilter)}.
	 */
	@Test
	public void testGetPriceListMapByFilterMultiplePriceLists() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setGuid(PRICE_LIST_GUID);
		
		final PriceListDescriptorDTO priceListDescriptorDTO2 = new PriceListDescriptorDTO();
		priceListDescriptorDTO2.setGuid(PRICE_LIST_GUID2);

		final Collection<String> baseAmountGuids = new ArrayList<String>();

		final BaseAmountDTO baseAmountDTO1 = new BaseAmountDTO();
		baseAmountDTO1.setPriceListDescriptorGuid(PRICE_LIST_GUID);
		baseAmountGuids.add(PRICE_LIST_GUID);

		final BaseAmountDTO baseAmountDTO2 = new BaseAmountDTO();
		baseAmountDTO2.setPriceListDescriptorGuid(PRICE_LIST_GUID2);
		baseAmountGuids.add(PRICE_LIST_GUID2);

		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final BaseAmountFilter baseAmountFilter) {
				return Arrays.asList(baseAmountDTO1, baseAmountDTO2);
			}
		};
		priceListHelperService.setPriceListService(priceListService);
		
		context.checking(new Expectations() { {
			allowing(priceListService).getPriceListDescriptors(baseAmountGuids);
			will(returnValue(Arrays.asList(priceListDescriptorDTO, priceListDescriptorDTO2)));
			
			allowing(baseAmountFilter).getObjectType(); will(returnValue(TYPE_SKU));
			allowing(baseAmountFilter).getObjectGuid(); will(returnValue(PRODUCT_SKU_CODE));
		} });
		
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = priceListHelperService.getPriceListMap(baseAmountFilter);
		
		assertSame(baseAmountDTO1, map.get(priceListDescriptorDTO).get(0));
		assertSame(baseAmountDTO2, map.get(priceListDescriptorDTO2).get(0));
		
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map2 = 
			priceListHelperService.getPriceListMap(baseAmountFilter, new String[]{null});
		assertSame(baseAmountDTO1, map2.get(priceListDescriptorDTO).get(0));
		assertSame(baseAmountDTO2, map2.get(priceListDescriptorDTO2).get(0));
	}		
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(BaseAmountFilter)}.
	 */
	@Test
	public void testGetPriceListMapByFilterMultiplePriceListsFilterCurrency() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setGuid(PRICE_LIST_GUID);
		priceListDescriptorDTO.setCurrencyCode("USD");
		
		final PriceListDescriptorDTO priceListDescriptorDTO2 = new PriceListDescriptorDTO();
		priceListDescriptorDTO2.setGuid(PRICE_LIST_GUID2);
		priceListDescriptorDTO2.setCurrencyCode("GBP");
		
		final PriceListDescriptorDTO priceListDescriptorDTO3 = new PriceListDescriptorDTO();
		priceListDescriptorDTO3.setGuid(PRICE_LIST_GUID3);
		priceListDescriptorDTO3.setCurrencyCode("UAH");

		final Collection<String> baseAmountGuids = new ArrayList<String>();

		final BaseAmountDTO baseAmountDTO1 = new BaseAmountDTO();
		baseAmountDTO1.setPriceListDescriptorGuid(PRICE_LIST_GUID);
		baseAmountGuids.add(PRICE_LIST_GUID);

		final BaseAmountDTO baseAmountDTO2 = new BaseAmountDTO();
		baseAmountDTO2.setPriceListDescriptorGuid(PRICE_LIST_GUID2);
		baseAmountGuids.add(PRICE_LIST_GUID2);

		final BaseAmountDTO baseAmountDTO3 = new BaseAmountDTO();
		baseAmountDTO3.setPriceListDescriptorGuid(PRICE_LIST_GUID3);
		baseAmountGuids.add(PRICE_LIST_GUID3);

		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final BaseAmountFilter baseAmountFilter) {
				return Arrays.asList(baseAmountDTO1, baseAmountDTO2, baseAmountDTO3);
			}
		};
		priceListHelperService.setPriceListService(priceListService);
		
		context.checking(new Expectations() { {
			oneOf(priceListService).getPriceListDescriptors(baseAmountGuids);
			will(returnValue(Arrays.asList(priceListDescriptorDTO, priceListDescriptorDTO2, priceListDescriptorDTO3)));
			
			allowing(baseAmountFilter).getObjectType(); will(returnValue(TYPE_SKU));
			allowing(baseAmountFilter).getObjectGuid(); will(returnValue(PRODUCT_SKU_CODE));
		} });
		
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = priceListHelperService.getPriceListMap(baseAmountFilter, "GBP", "UAH");
		
		assertNull(map.get(priceListDescriptorDTO));
		assertSame(baseAmountDTO2, map.get(priceListDescriptorDTO2).get(0));
		assertSame(baseAmountDTO3, map.get(priceListDescriptorDTO3).get(0));
		
	}		
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceListMap(ProductSku)}.
	 */
	@Test
	public void testGetPriceListMapProductSku() {
		final PriceListDescriptorDTO priceListDescriptorDTO = new PriceListDescriptorDTO();
		priceListDescriptorDTO.setGuid(PRICE_LIST_GUID);
		
		final BaseAmountDTO baseAmountDTO1 = new BaseAmountDTO();
		final BaseAmountDTO baseAmountDTO2 = new BaseAmountDTO();
		
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
				return mock.method(priceListGuid, productGuid, objectType);
			}
			@Override
			public List<PriceListDescriptorDTO> findAllDescriptors(final Product product) {
				return mock.method(product);
			};
		};
		
		context.checking(new Expectations() { {
			oneOf(productSku).getSkuCode(); will(returnValue(PRODUCT_SKU_CODE));
			oneOf(productSku).getProduct(); will(returnValue(product));
			oneOf(product).getCode(); will(returnValue(PRODUCT_CODE));
			oneOf(mock).method(product); will(returnValue(Arrays.asList(priceListDescriptorDTO)));
			oneOf(mock).method(PRICE_LIST_GUID, PRODUCT_CODE, TYPE_PRODUCT);
			will(returnValue(new ArrayList<BaseAmountDTO>(Arrays.asList(baseAmountDTO1))));
			oneOf(mock).method(PRICE_LIST_GUID, PRODUCT_SKU_CODE, TYPE_SKU);
			will(returnValue(Arrays.asList(baseAmountDTO2)));
		} });		
						
		final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = priceListHelperService.getPriceListMap(productSku);
		final List<BaseAmountDTO> baseAmountList = map.get(priceListDescriptorDTO);
		
		assertEquals(1, map.keySet().size());
		assertEquals(2, baseAmountList.size());
		assertSame(baseAmountDTO1, baseAmountList.get(0));
		assertSame(baseAmountDTO2, baseAmountList.get(1));
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#getPriceInfoInternal(String, String, List)}.
	 */
	@Test
	public void testGetPriceInfoInternal() {
		final MockInterface mock = context.mock(MockInterface.class);
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final PriceListDescriptorDTO priceListDescriptorDTO1 = new PriceListDescriptorDTO();
		final PriceListDescriptorDTO priceListDescriptorDTO2 = new PriceListDescriptorDTO();
		
		priceListDescriptorDTO1.setGuid("pld-DTO1");
		priceListDescriptorDTO2.setGuid("pld-DTO2");
		
		List<PriceListDescriptorDTO> pldList = Arrays.asList(priceListDescriptorDTO1, priceListDescriptorDTO2);
		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
				return mock.method(priceListGuid, productGuid, objectType);
			}
		};
		
		final Sequence seq = context.sequence("getPriceInfoInternal_Sequence");
		
		context.checking(new Expectations() { {
			oneOf(mock).method("pld-DTO1", PRODUCT_CODE, TYPE_PRODUCT); will(returnValue(Collections.emptyList())); inSequence(seq);
			oneOf(mock).method("pld-DTO2", PRODUCT_CODE, TYPE_PRODUCT); will(returnValue(Arrays.asList(baseAmountDTO))); inSequence(seq);
		} });		
		
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> map = priceListHelperService.getPriceInfoInternal(PRODUCT_CODE, TYPE_PRODUCT, pldList);
		
		assertEquals(2, map.keySet().size());
		assertEquals(Collections.emptyList(), map.get(priceListDescriptorDTO1));
		
		assertSame(1, map.get(priceListDescriptorDTO2).size());
		assertSame(baseAmountDTO, map.get(priceListDescriptorDTO2).get(0));		
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#findBaseAmounts(String, String, String)}.
	 */
	@Test
	public void testFindBaseAmounts() {
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();

		context.checking(new Expectations() { {
			oneOf(beanFactory).getBean(ContextIdNames.BASE_AMOUNT_FILTER); will(returnValue(baseAmountFilter));
			oneOf(baseAmountFilter).setPriceListDescriptorGuid(PRICE_LIST_GUID);
			oneOf(baseAmountFilter).setObjectType(TYPE_PRODUCT);
			oneOf(baseAmountFilter).setObjectGuid(PRODUCT_CODE);
			oneOf(priceListService).getBaseAmounts(baseAmountFilter); will(returnValue(Arrays.asList(baseAmountDTO)));
		} });	
		
		List<BaseAmountDTO> amounts = priceListHelperService.findBaseAmounts(PRICE_LIST_GUID, PRODUCT_CODE, TYPE_PRODUCT);
		assertEquals(1, amounts.size());
		assertSame(baseAmountDTO, amounts.get(0));
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#processBaseAmountChangeSets }.
	 */
	@Test
	public void testProcessBaseAmountChangeSets() {
		context.checking(new Expectations() { {
			oneOf(priceListService).modifyBaseAmountChangeSet(changeSet);
		} });
		
		priceListHelperService.processBaseAmountChangeSets(Collections.<ChangeSetObjects<BaseAmountDTO>>singletonList(changeSet));
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#findAllDescriptors(Product)}.
	 */
	@Test
	public void testFindAllDescriptors() {
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			public Set<Currency> getAllCurrenciesFor(final Catalog catalog) {
				return Collections.singleton(CURRENCY_USD);
			}
			
		};
		priceListHelperService.setPriceListLookupService(priceListLookupService);
		
		
		context.checking(new Expectations() { {
			oneOf(product).getCatalogs(); will(returnValue(new HashSet<Catalog>(Arrays.asList(catalog))));
			oneOf(catalog).getCode(); will(returnValue(CURRENCY_USD.getCurrencyCode()));
			oneOf(priceListLookupService).getPriceListStack(with(any(String.class)),
					with(any(Currency.class)), with((TagSet) null)); will(returnValue(getStack()));
		} });
		
		List<PriceListDescriptorDTO> list = priceListHelperService.findAllDescriptors(product);
		
		assertEquals(1, list.size());
		assertEquals(getStack().getPriceListStack().get(0), PRICE_LIST_GUID);
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#removePricesForProduct(Product)}.
	 */
	@Test
	public void testRemovePricesForProduct() {
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			ChangeSetObjects<BaseAmountDTO> prepareChangeSetForProduct(final Product product) {
				return mock.method(product);
			}
		};
		priceListHelperService.setPriceListService(priceListService);
		
		context.checking(new Expectations() { {
			oneOf(mock).method(product); will(returnValue(changeSet));
			oneOf(priceListService).modifyBaseAmountChangeSet(changeSet);
		} });	
		
		priceListHelperService.removePricesForProduct(product);
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#removePricesForProductSkus}.
	 * 
	 * Test is complex. {@link PriceListHelperServiceImpl#removePricesForProductSkus} creates BaseAmountChangeSet and use it.
	 * So we re implemented the {@link PriceListService#modifyBaseAmountChangeSet(DtoChangeSet)} as it checks what in changeSet parameter,
	 * also it calls dummy {@link MockInterface#method("run:processBaseAmountChangeSet")} to ensure that method was called by 
	 * {@link PriceListHelperServiceImpl#removePricesForProductSkus}.
	 */
	@Test
	public void testRemovePricesForProductSkus() {
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final MockInterface mock = context.mock(MockInterface.class);
		
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
				return mock.method(priceListGuid, productGuid, objectType);
			}
		};
		priceListHelperService.setPriceListService(new PriceListServiceImpl() {
			@Override
			public void modifyBaseAmountChangeSet(final ChangeSetObjects<BaseAmountDTO> changeSet) throws EpServiceException {
				assertEquals(1, changeSet.getRemovalList().size());
				assertSame(baseAmountDTO, changeSet.getRemovalList().get(0));
				assertTrue(changeSet.getAdditionList().isEmpty());
				assertTrue(changeSet.getUpdateList().isEmpty());
				
				mock.method("run:processBaseAmountChangeSet"); // ensure that processBaseAmountChangeSet is called
			}
		});
		
		context.checking(new Expectations() { {
			oneOf(productSku).getSkuCode(); will(returnValue(PRODUCT_SKU_CODE));
			oneOf(mock).method(null, PRODUCT_SKU_CODE, TYPE_SKU); will(returnValue(Arrays.asList(baseAmountDTO)));
			oneOf(mock).method("run:processBaseAmountChangeSet"); // ensure that processBaseAmountChangeSet is called
		} });	
		
		priceListHelperService.removePricesForProductSkus(Arrays.asList(productSku));
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#prepareChangeSetForProduct(Product)} (if it has multiple skus).
	 */
	@Test
	public void testPrepareChangeSetListForProductHasMultipleSkus() {
		final BaseAmountDTO baseAmountDTO1 = new BaseAmountDTO();
		final BaseAmountDTO baseAmountDTO2 = new BaseAmountDTO();
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
				return mock.method(priceListGuid, productGuid, objectType);
			}
		};
		
		final Map<String, ProductSku> skuMap = new HashMap<String, ProductSku>();
		skuMap.put(PRODUCT_SKU_CODE, productSku);
		
		final Sequence seq = context.sequence("getPriceInfoInternal_Sequence");
				
		context.checking(new Expectations() { {
			oneOf(product).getCode(); will(returnValue(PRODUCT_CODE)); inSequence(seq);
			oneOf(mock).method(null, PRODUCT_CODE, TYPE_PRODUCT); will(returnValue(Arrays.asList(baseAmountDTO1))); inSequence(seq);
			oneOf(product).hasMultipleSkus(); will(returnValue(true)); inSequence(seq);
			oneOf(product).getProductSkus(); will(returnValue(skuMap)); inSequence(seq);
			oneOf(productSku).getSkuCode(); will(returnValue(PRODUCT_SKU_CODE)); inSequence(seq);
			oneOf(mock).method(null, PRODUCT_SKU_CODE, TYPE_SKU); will(returnValue(Arrays.asList(baseAmountDTO2))); inSequence(seq);
		} });
		
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = priceListHelperService.prepareChangeSetForProduct(product);
		
		assertEquals(2, baseAmountChangeSet.getRemovalList().size());
		assertSame(baseAmountDTO1, baseAmountChangeSet.getRemovalList().get(0));
		assertSame(baseAmountDTO2, baseAmountChangeSet.getRemovalList().get(1));
		assertTrue(baseAmountChangeSet.getAdditionList().isEmpty());
		assertTrue(baseAmountChangeSet.getUpdateList().isEmpty());		
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#prepareChangeSetForProduct(Product)}.
	 */
	@Test
	public void testPrepareChangeSetListForProduct() {
		final BaseAmountDTO baseAmountDTO = new BaseAmountDTO();
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			List<BaseAmountDTO> findBaseAmounts(final String priceListGuid, final String productGuid, final String objectType) {
				return mock.method(priceListGuid, productGuid, objectType);
			}
		};
		
		context.checking(new Expectations() { {
			oneOf(product).getCode(); will(returnValue(PRODUCT_CODE));
			oneOf(product).hasMultipleSkus(); will(returnValue(false));
			oneOf(mock).method(null, PRODUCT_CODE, TYPE_PRODUCT); will(returnValue(Arrays.asList(baseAmountDTO)));
		} });
		
		ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = priceListHelperService.prepareChangeSetForProduct(product);
		
		assertEquals(1, baseAmountChangeSet.getRemovalList().size());
		assertSame(baseAmountDTO, baseAmountChangeSet.getRemovalList().get(0));
		assertTrue(baseAmountChangeSet.getAdditionList().isEmpty());
		assertTrue(baseAmountChangeSet.getUpdateList().isEmpty());		
	}
	
	/**
	 * Test method for {@link PriceListHelperServiceImpl#findDescriptor(Catalog, Currency))}.
	 */
	@Test
	public void testFindDescriptor() {
		final PriceListStack stack = getStack();
		
		final String catalogCode = "Catalog";
		context.checking(new Expectations() { {
			oneOf(catalog).getCode(); will(returnValue(catalogCode));
			oneOf(priceListLookupService).getPriceListStack(catalogCode, CURRENCY_USD, null); will(returnValue(stack));
		} });
		
		final List<PriceListDescriptorDTO> descriptors = priceListHelperService.findAllDescriptors(catalog, CURRENCY_USD);
		
		PriceListDescriptorDTO descriptor = descriptors.get(0);
		
		assertEquals(PRICE_LIST_GUID, descriptor.getGuid());
		assertEquals(CURRENCY_USD.getCurrencyCode(), descriptor.getCurrencyCode());
	}

	private PriceListStack getStack() {
		final PriceListStack stack = new PriceListStackImpl();
		List<String> pldGuids = new ArrayList<String>();
		pldGuids.add(PRICE_LIST_GUID);
		stack.setStack(pldGuids);
		return stack;
	}

	/**
	 * Test method for {@link PriceListHelperServiceImpl#findDescriptor(Catalog, Currency))}.
	 */
	@Test
	public void testFindDescriptorsByGuids() {
		final PriceListDescriptorDTO priceListDescriptorDTO1 = new PriceListDescriptorDTO();
		String guid1 = "GUID1";
		priceListDescriptorDTO1.setGuid(guid1);
		final PriceListDescriptorDTO priceListDescriptorDTO2 = new PriceListDescriptorDTO();
		String guid2 = "GUID2";
		priceListDescriptorDTO2.setGuid(guid2);
		final List<PriceListDescriptorDTO> priceListDescriptors = new ArrayList<PriceListDescriptorDTO>();		
		priceListDescriptors.add(priceListDescriptorDTO1);
		priceListDescriptors.add(priceListDescriptorDTO2);
		final List<String> guids = new ArrayList<String>();
		guids.add(guid1);
		guids.add(guid2);
		
		final MockInterface mock = context.mock(MockInterface.class);
		priceListHelperService = new PriceListHelperServiceImpl() {
			@Override
			public List<PriceListDescriptorDTO> findAllDescriptors(final Catalog catalog, final Currency currency) {
				return mock.method(catalog, currency);
			}
			@Override
			public Set<Currency> getAllCurrenciesFor(final Catalog catalog) {
				return Collections.singleton(CURRENCY_USD);
			}

		};
				
		context.checking(new Expectations() { {
			oneOf(priceListService).getPriceListDescriptors(guids); will(returnValue(priceListDescriptors));
		} });		
		
		List<PriceListDescriptorDTO> list = priceListService.getPriceListDescriptors(guids);
		
		assertEquals(2, list.size());
		assertSame(priceListDescriptorDTO1, list.get(0));
		assertSame(priceListDescriptorDTO2, list.get(1));
	}

}
