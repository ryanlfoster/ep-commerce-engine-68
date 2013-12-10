/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.domain.tax.TaxValue;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/** Test cases for <code>TaxJurisdictionImpl</code>. */
public class TaxJurisdictionImplTest {
	private static final String REGION_CODE_CA = "CA";

	private static final String REGION_CODE_BC = "BC";

	private static final String REGION_CODE_ALBERTA = "Alberta";

	private static final String REGION_CODE_VANCOUVER = "Vancouver";

	private static final String COUNTRY_CATEGORY = "CountryCategory"; //$NON-NLS-1$

	private static final String SUB_COUNTRY_CATEGORY1 = "SubCountryCategory1"; //$NON-NLS-1$//

	private static final String SUB_COUNTRY_CATEGORY2 = "SubCountryCategory2"; //$NON-NLS-1$

	private static final String CITY_CATEGORY = "CityCategory1"; //$NON-NLS-1$

	private static final String TAX_CODE = "TC1"; //$NON-NLS-1$

	private TaxJurisdiction taxJurisdictionImpl;

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
		expectationsFactory.allowingBeanFactoryGetBean("localizedProperties", LocalizedPropertiesImpl.class);

		this.taxJurisdictionImpl = getFourCategoriesTaxJurisdiction();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxJurisdictionImpl.getRegionCode()'.
	 */
	@Test
	public void testGetSetRegionCode() {
		this.taxJurisdictionImpl.setRegionCode(REGION_CODE_CA);
		assertEquals(REGION_CODE_CA, this.taxJurisdictionImpl.getRegionCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxCategoryImpl.getPriceCalculationMethod()'.
	 */
	@Test
	public void testGetSetPriceCalculationMethod() {
		this.taxJurisdictionImpl.setPriceCalculationMethod(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE);
		assertEquals(TaxJurisdiction.PRICE_CALCULATION_EXCLUSIVE, this.taxJurisdictionImpl.getPriceCalculationMethod());
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxJurisdictionImpl.getTaxCategoryMap()'. Tests TaxRegion number in each category.
	 */
	@Test
	public void testRetrieveRegions() {

		assertNotNull(this.taxJurisdictionImpl.getTaxCategorySet());
		/** Four categories. */
		final int categoryFour = 4;
		assertEquals(categoryFour, this.taxJurisdictionImpl.getTaxCategorySet().size());

		for (TaxCategory taxCategory : this.taxJurisdictionImpl.getTaxCategorySet()) {
			assertNotNull(taxCategory.getTaxRegionSet());
			if (COUNTRY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_CA, taxCategory.getTaxRegion(REGION_CODE_CA).getRegionName());
			}

			if (SUB_COUNTRY_CATEGORY1.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_BC, taxCategory.getTaxRegion(REGION_CODE_BC).getRegionName());
			}

			if (SUB_COUNTRY_CATEGORY2.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_ALBERTA, taxCategory.getTaxRegion(REGION_CODE_ALBERTA).getRegionName());
			}

			if (CITY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(1, taxCategory.getTaxRegionSet().size());
				assertEquals(REGION_CODE_VANCOUVER, taxCategory.getTaxRegion(REGION_CODE_VANCOUVER).getRegionName());
			}
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxJurisdictionImpl.getTaxCategoryMap()'. Tests TaxRegion values
	 */
	@Test
	public void testTaxValues() {

		assertNotNull(this.taxJurisdictionImpl.getTaxCategorySet());
		/** Four categories. */
		final int categoryFour = 4;
		assertEquals(categoryFour, this.taxJurisdictionImpl.getTaxCategorySet().size());

		for (TaxCategory taxCategory : this.taxJurisdictionImpl.getTaxCategorySet()) {
			assertNotNull(taxCategory.getTaxRegionSet());
			TaxRegion taxRegion = null;
			if (COUNTRY_CATEGORY.equals(taxCategory.getName())) {
				taxRegion = taxCategory.getTaxRegion(REGION_CODE_CA);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int taxValueCountry = 111;
				assertEquals(taxValueCountry, taxRegion.getValue(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(taxValueCountry, taxRegion.getValue(TAX_CODE).intValue());
			}

			if (SUB_COUNTRY_CATEGORY1.equals(taxCategory.getName())) {
				taxRegion = taxCategory.getTaxRegion(REGION_CODE_BC);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int taxValueSubCountry1 = 222;
				assertEquals(taxValueSubCountry1, taxRegion.getValue(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(taxValueSubCountry1, taxRegion.getValue(TAX_CODE).intValue());
			}

			if (SUB_COUNTRY_CATEGORY2.equals(taxCategory.getName())) {
				taxRegion = taxCategory.getTaxRegion(REGION_CODE_ALBERTA);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int taxValueSubCountry2 = 333;
				assertEquals(taxValueSubCountry2, taxRegion.getValue(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(taxValueSubCountry2, taxRegion.getValue(TAX_CODE).intValue());
			}

			if (CITY_CATEGORY.equals(taxCategory.getName())) {
				taxRegion = taxCategory.getTaxRegion(REGION_CODE_VANCOUVER);
				assertNotNull(taxRegion.getTaxValuesMap());
				final int taxValueCity = 444;
				assertEquals(taxValueCity, taxRegion.getValue(TaxCode.TAX_CODE_SHIPPING).intValue());
				assertEquals(taxValueCity, taxRegion.getValue(TAX_CODE).intValue());
			}
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxJurisdictionImpl.getFieldMatchType()'.
	 */
	@Test
	public void testGetSetFieldMatchType() {
		assertNotNull(this.taxJurisdictionImpl.getTaxCategorySet());
		/** Four categories. */
		final int categoryFour = 4;
		assertEquals(categoryFour, this.taxJurisdictionImpl.getTaxCategorySet().size());

		for (TaxCategory taxCategory : this.taxJurisdictionImpl.getTaxCategorySet()) {
			assertNotNull(taxCategory.getTaxRegionSet());
			if (COUNTRY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(TaxCategoryTypeEnum.FIELD_MATCH_COUNTRY, taxCategory.getFieldMatchType());
			}

			if (SUB_COUNTRY_CATEGORY1.equals(taxCategory.getName())) {
				assertEquals(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY, taxCategory.getFieldMatchType());
			}

			if (SUB_COUNTRY_CATEGORY2.equals(taxCategory.getName())) {
				assertEquals(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY, taxCategory.getFieldMatchType());
			}

			if (CITY_CATEGORY.equals(taxCategory.getName())) {
				assertEquals(TaxCategoryTypeEnum.FIELD_MATCH_CITY, taxCategory.getFieldMatchType());
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
		final int taxValueCountry = 111;
		taxRegion.setTaxValuesMap(getTaxValueMap(taxValueCountry));
		taxRegion.setRegionName(REGION_CODE_CA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 2) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(SUB_COUNTRY_CATEGORY1);

		taxRegion = new TaxRegionImpl();
		final int taxValueSubCountry1 = 222;
		taxRegion.setTaxValuesMap(getTaxValueMap(taxValueSubCountry1));
		taxRegion.setRegionName(REGION_CODE_BC);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 3) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_SUBCOUNTRY);
		taxCategory.setName(SUB_COUNTRY_CATEGORY2);
		taxCategory.setGuid(SUB_COUNTRY_CATEGORY2);

		taxRegion = new TaxRegionImpl();
		final int taxValueSubCountry2 = 333;
		taxRegion.setTaxValuesMap(getTaxValueMap(taxValueSubCountry2));
		taxRegion.setRegionName(REGION_CODE_ALBERTA);

		taxCategory.addTaxRegion(taxRegion);

		taxJurisdiction.addTaxCategory(taxCategory);

		// 4) category
		taxCategory = new TaxCategoryImpl();
		taxCategory.setFieldMatchType(TaxCategoryTypeEnum.FIELD_MATCH_CITY);
		taxCategory.setName(CITY_CATEGORY);
		taxCategory.setGuid(CITY_CATEGORY);

		taxRegion = new TaxRegionImpl();
		final int taxValueCity = 444;
		taxRegion.setTaxValuesMap(getTaxValueMap(taxValueCity));
		taxRegion.setRegionName(REGION_CODE_VANCOUVER);

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
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxJurisdictionImpl.equals(Object)'.
	 */
	@Test
	public void testEquals() {
		final String gUid = "GUID";
		this.taxJurisdictionImpl.setGuid(gUid);
		TaxJurisdiction jurisdictionToCompare = new TaxJurisdictionImpl();
		jurisdictionToCompare.setGuid(gUid);
		assertEquals(taxJurisdictionImpl, jurisdictionToCompare);

		String anotherGuid = "Another_GUID";
		jurisdictionToCompare.setGuid(anotherGuid);
		assertFalse(taxJurisdictionImpl.equals(jurisdictionToCompare));
	}
}
