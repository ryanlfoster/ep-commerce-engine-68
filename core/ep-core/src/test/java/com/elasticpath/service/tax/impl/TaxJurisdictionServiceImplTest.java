/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;
import com.elasticpath.domain.tax.impl.TaxJurisdictionImpl;
import com.elasticpath.domain.tax.impl.TaxRegionImpl;
import com.elasticpath.domain.tax.impl.TaxValueImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>TaxJurisdictionServiceImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class TaxJurisdictionServiceImplTest {
	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String REGION_CODE_CA = "CA";

	private static final String REGION_CODE_BC = "BC";

	private static final String REGION_CODE_US = "US";

	private static final String REGION_CODE_ALBERTA = "Alberta";

	private static final String REGION_CODE_VANCOUVER = "Vancouver";

	private static final String REGION_CODE_TORONTO = "Toronto";

	private static final String COUNTRY_CATEGORY = "CountryCategory";

	private static final String SUB_COUNTRY_CATEGORY1 = "SubCountryCategory1";

	private static final String SUB_COUNTRY_CATEGORY2 = "SubCountryCategory2";

	private static final String CITY_CATEGORY = "CityCategory1";

	private static final String TAX_CODE = "TC1";

	private static final long TAX_JUJRISDICTION_UID = 1000L;

	/** Default taxCategory uid. */
	private static final long TAX_CATEGORY_UID = 1000;

	private TaxJurisdictionServiceImpl taxJurisdictionServiceImpl;

	private PersistenceEngine mockPersistenceEngine;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		taxJurisdictionServiceImpl = new TaxJurisdictionServiceImpl();

		mockPersistenceEngine = context.mock(PersistenceEngine.class);

		context.checking(new Expectations() { {
			allowing(mockPersistenceEngine).isCacheEnabled(); will(returnValue(false));
		} });

		taxJurisdictionServiceImpl.setPersistenceEngine(mockPersistenceEngine);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		taxJurisdictionServiceImpl.setPersistenceEngine(null);
		try {
			taxJurisdictionServiceImpl.add(new TaxJurisdictionImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(taxJurisdictionServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.add(TaxJurisdiction)'.
	 */
	@Test
	public void testAdd() {
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setRegionCode(REGION_CODE_CA);

		context.checking(new Expectations() { {
			oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object [] {REGION_CODE_CA}));
			will(returnValue(new ArrayList<TaxJurisdiction>()));

			oneOf(mockPersistenceEngine).save(with(same(taxJurisdiction)));
		} });
		this.taxJurisdictionServiceImpl.add(taxJurisdiction);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.update(TaxJurisdiction)'.
	 */
	@Test
	public void testUpdate() {
		final TaxJurisdiction taxJurisdiction = getTaxJurisdiction(REGION_CODE_CA);
		final TaxJurisdiction updatedTaxJurisdiction = getTaxJurisdiction(REGION_CODE_US);
		final long uidPk = 123456;
		taxJurisdiction.setRegionCode(REGION_CODE_US);
		taxJurisdiction.setUidPk(uidPk);

		context.checking(new Expectations() { {
			oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(any(String.class)), with(new Object [] {REGION_CODE_US}));
			will(returnValue(new ArrayList<TaxJurisdiction>()));

			oneOf(mockPersistenceEngine).merge(with(same(taxJurisdiction)));
			will(returnValue(updatedTaxJurisdiction));
		} });
		final TaxJurisdiction returnedJurisdiction = taxJurisdictionServiceImpl.update(taxJurisdiction);
		assertSame(returnedJurisdiction, updatedTaxJurisdiction);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.delete(TaxJurisdiction)'.
	 */
	@Test
	public void testDelete() {
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setGuid(REGION_CODE_CA);
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);
		taxJurisdiction.setUidPk(TAX_JUJRISDICTION_UID);

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(COUNTRY_CATEGORY);

		TaxRegion taxRegion = new TaxRegionImpl();
		final int value = 111;
		taxRegion.setTaxValuesMap(getTaxValueMap(value));
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		context.checking(new Expectations() { {
			oneOf(mockPersistenceEngine).delete(with(same(taxJurisdiction)));
		} });
		taxJurisdictionServiceImpl.remove(taxJurisdiction);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final TaxJurisdiction taxJurisdiction1 = new TaxJurisdictionImpl();
		taxJurisdiction1.setRegionCode(REGION_CODE_CA);
		final TaxJurisdiction taxJurisdiction2 = new TaxJurisdictionImpl();
		taxJurisdiction2.setRegionCode(REGION_CODE_BC);
		final List<TaxJurisdiction> tjList = new ArrayList<TaxJurisdiction>();
		tjList.add(taxJurisdiction1);
		tjList.add(taxJurisdiction2);

		context.checking(new Expectations() { {
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("TAXJURISDICTION_SELECT_ALL")),
						with(any(Object[].class)));
			will(returnValue(tjList));
		} });
		final List<TaxJurisdiction> retrievedSRList = taxJurisdictionServiceImpl.list();
		assertEquals(tjList, retrievedSRList);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setRegionCode(REGION_CODE_CA);
		taxJurisdiction.setUidPk(uid);

		context.checking(new Expectations() { {
			oneOf(beanFactory).getBeanImplClass(with(ContextIdNames.TAX_JURISDICTION));
			will(returnValue(TaxJurisdictionImpl.class));

			oneOf(mockPersistenceEngine).load(TaxJurisdictionImpl.class, uid);
			will(returnValue(taxJurisdiction));
		} });

		final TaxJurisdiction loadedTaxJurisdiction = taxJurisdictionServiceImpl.load(uid);
		assertSame(taxJurisdiction, loadedTaxJurisdiction);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setRegionCode(REGION_CODE_CA);
		taxJurisdiction.setUidPk(uid);

		context.checking(new Expectations() { {
			oneOf(beanFactory).getBeanImplClass(with(ContextIdNames.TAX_JURISDICTION));
			will(returnValue(TaxJurisdictionImpl.class));

			oneOf(mockPersistenceEngine).get(TaxJurisdictionImpl.class, uid);
			will(returnValue(taxJurisdiction));
		} });
		final TaxJurisdiction loadedTaxJurisdiction = taxJurisdictionServiceImpl.get(uid);
		assertSame(taxJurisdiction, loadedTaxJurisdiction);
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.getCountryCodesInUse()'.
	 */
	@Test
	public void testGetCountryCodesInUse() {
		context.checking(new Expectations() { {
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("SELECT_COUNTRIES_IN_USE")),
						with(any(Object[].class)));
			will(returnValue(new ArrayList<String>()));
		} });
		taxJurisdictionServiceImpl.getCountryCodesInUse();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.getTaxJurisdictionsInUse()'.
	 */
	@Test
	public void testTaxJurisdictionsInUse() {
		context.checking(new Expectations() { {
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with(containsString("TAX_JURISDICTION_UIDS_WITH_STORE")),
						with(any(Object[].class)));
			will(returnValue(new ArrayList<Long>()));
		} });
		taxJurisdictionServiceImpl.getTaxJurisdictionsInUse();
	}

	/**
	 * Test method for 'com.elasticpath.service.impl.TaxJurisdictionServiceImpl.retrieveTaxJurisdiction(Address)'.
	 */
	@Test
	public void testRetrieveTaxJurisdiction() {
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.TAX_JURISDICTION, TaxJurisdictionImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.TAX_CATEGORY, TaxCategoryImpl.class);

		final Address shippingAddress = new CustomerAddressImpl();
		shippingAddress.setCountry(REGION_CODE_CA);
		shippingAddress.setSubCountry(REGION_CODE_ALBERTA);
		shippingAddress.setCity(REGION_CODE_VANCOUVER);

		final TaxJurisdiction taxJurisdiction1 = new TaxJurisdictionImpl();
		taxJurisdiction1.setRegionCode(REGION_CODE_CA);

		final TaxJurisdiction taxJurisdiction2 = new TaxJurisdictionImpl();
		taxJurisdiction2.setRegionCode(REGION_CODE_US);

		final TaxJurisdiction taxJurisdiction3 = new TaxJurisdictionImpl();
		taxJurisdiction3.setRegionCode(REGION_CODE_CA);

		final List<TaxJurisdiction> tjList1 = new ArrayList<TaxJurisdiction>();
		tjList1.add(getFourCategoriesTaxJurisdiction());

		final List<TaxJurisdiction> tjList2 = new ArrayList<TaxJurisdiction>();
		tjList2.add(taxJurisdiction1);
		tjList2.add(taxJurisdiction3);

		context.checking(new Expectations() { {
			oneOf(mockPersistenceEngine).retrieveByNamedQuery(
					with(containsString("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE")),
					with(equal(new Object[] { null, shippingAddress.getCountry() })));
			will(returnValue(tjList1));
		} });

		TaxJurisdiction found = taxJurisdictionServiceImpl.retrieveEnabledInStoreTaxJurisdiction(new StoreImpl(), shippingAddress);
		final int expected = 3;
		assertEquals(expected, found.getTaxCategorySet().size());

		for (TaxCategory taxCategory : found.getTaxCategorySet()) {
			assertNotNull(taxCategory.getTaxRegionSet());
			TaxRegion taxRegion = null;
			if (COUNTRY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_CA, taxCategory.getTaxRegion(REGION_CODE_CA).getRegionName());

				taxRegion = taxCategory.getTaxRegion(REGION_CODE_CA);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int expectedTaxCountry = 111;
				assertEquals(expectedTaxCountry, taxRegion.getTaxRate(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(expectedTaxCountry, taxRegion.getTaxRate(TAX_CODE).intValue());

			} else if (SUB_COUNTRY_CATEGORY2.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_ALBERTA, taxCategory.getTaxRegion(REGION_CODE_ALBERTA).getRegionName());

				taxRegion = taxCategory.getTaxRegion(REGION_CODE_ALBERTA);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int expectedTaxSubCountry = 333;
				assertEquals(expectedTaxSubCountry, taxRegion.getTaxRate(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(expectedTaxSubCountry, taxRegion.getTaxRate(TAX_CODE).intValue());

			} else if (CITY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_VANCOUVER, taxCategory.getTaxRegion(REGION_CODE_VANCOUVER).getRegionName());

				taxRegion = taxCategory.getTaxRegion(REGION_CODE_VANCOUVER);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int expectedTaxCity = 444;
				assertEquals(expectedTaxCity, taxRegion.getTaxRate(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(expectedTaxCity, taxRegion.getTaxRate(TAX_CODE).intValue());
			} else {
				fail("Unexpected category found: " + taxCategory.getName());
			}
		}

	}

	private TaxJurisdiction getFourCategoriesTaxJurisdiction() {

		TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setGuid(REGION_CODE_CA);
		taxJurisdiction.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		taxJurisdiction.setRegionCode(REGION_CODE_CA);

		// 1) category
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxCategory.setName(COUNTRY_CATEGORY);

		TaxRegion taxRegion = new TaxRegionImpl();
		final int expectedTaxCountry = 111;
		taxRegion.setTaxValuesMap(getTaxValueMap(expectedTaxCountry));
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(SUB_COUNTRY_CATEGORY1);

		taxRegion = new TaxRegionImpl();
		final int expectedSubTaxCountry1 = 222;
		taxRegion.setTaxValuesMap(getTaxValueMap(expectedSubTaxCountry1));
		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 3) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(SUB_COUNTRY_CATEGORY2);
		taxCategory.setGuid(SUB_COUNTRY_CATEGORY2);

		taxRegion = new TaxRegionImpl();
		final int expectedSubTaxCountry2 = 333;
		taxRegion.setTaxValuesMap(getTaxValueMap(expectedSubTaxCountry2));
		taxRegion.setRegionName(REGION_CODE_ALBERTA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 4) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_CITY);
		taxCategory.setName(CITY_CATEGORY);
		taxCategory.setGuid(CITY_CATEGORY);

		taxRegion = new TaxRegionImpl();
		final int expectedSubTaxCity1 = 444;
		taxRegion.setTaxValuesMap(getTaxValueMap(expectedSubTaxCity1));
		taxRegion.setRegionName(REGION_CODE_VANCOUVER);

		taxCategory.addTaxRegion(taxRegion);

		taxRegion = new TaxRegionImpl();
		final int expectedSubTaxCity2 = 555;
		taxRegion.setTaxValuesMap(getTaxValueMap(expectedSubTaxCity2));
		taxRegion.setRegionName(REGION_CODE_TORONTO);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		return taxJurisdiction;
	}

	private Map<String, TaxValue> getTaxValueMap(final int value) {

		Map<String, TaxValue> taxValueMap = new HashMap<String, TaxValue>();

		TaxValue taxValue = new TaxValueImpl();
		final TaxCode shippingTaxCode = new TaxCodeImpl();
		shippingTaxCode.setCode(TaxCode.TAX_CODE_SHIPPING);
		shippingTaxCode.setGuid(TaxCode.TAX_CODE_SHIPPING);
		taxValue.setTaxCode(shippingTaxCode);
		taxValue.setTaxValue(new BigDecimal(value));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		taxValue = new TaxValueImpl();
		final TaxCode taxCode = new TaxCodeImpl();
		taxCode.setCode(TAX_CODE);
		taxCode.setGuid(TAX_CODE);
		taxValue.setTaxCode(taxCode);
		taxValue.setTaxValue(new BigDecimal(value));
		taxValueMap.put(taxValue.getTaxCode().getCode(), taxValue);

		return taxValueMap;
	}

	/**
	 * Returns a newly created taxJurisdiction with a default taxCategory contains a shipping tax configuration of value 3.5.
	 *
	 * @param regionCode - region code for the taxJurisdiction to be created.
	 * @return a newly created taxJurisdiction
	 */
	private TaxJurisdiction getTaxJurisdiction(final String regionCode) {
		TaxJurisdiction taxJurisdiction = new TaxJurisdictionImpl();
		taxJurisdiction.setRegionCode(regionCode);
		taxJurisdiction.initialize();
		taxJurisdiction.setUidPk(1L);

		TaxCategory taxCategory1 = getTaxCategory("TC1", TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY);
		taxJurisdiction.addTaxCategory(taxCategory1);

		return taxJurisdiction;
	}

	/**
	 * Returns a newly created taxCategory.
	 *
	 * @param name - the taxCategory name.
	 * @param taxCategoryType - category type.
	 * @return a newly created taxCategory
	 */
	private TaxCategory getTaxCategory(final String name, final TaxCategoryTypeEnum taxCategoryType) {
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setUidPk(TAX_CATEGORY_UID);
		taxCategory.setName(name);
		taxCategory.setFieldMatchType(taxCategoryType);

		return taxCategory;
	}

}
