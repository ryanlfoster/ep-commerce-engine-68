/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.tax.TaxCategory;

/** Test cases for <code>TaxCategoryImpl</code>.*/
public class TaxCategoryImplTest {
	
	private static final String GST_TAX_CATEGORY_NAME = "GST (Canada)";
	private static final String PST_TAX_CATEGORY_NAME = "PST (Canada)";
	private static final String GST_TAX_CATEGORY_NAME_FR = "GST (Le Canada)";
	private static final String PST_TAX_CATEGORY_NAME_FR = "PST (Le Canada)";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ElasticPath elasticPath = context.mock(ElasticPath.class);

	private TaxCategoryImpl taxCategoryImpl;
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		this.taxCategoryImpl = getNewTaxCategory();
	}

	private TaxCategoryImpl getNewTaxCategory() {
		context.checking(new Expectations() {
			{
				allowing(elasticPath).getBean(with(any(String.class)));
				will(returnValue(new LocalizedPropertiesImpl()));
			}
		});
		
		final TaxCategoryImpl taxCategoryImpl = new TaxCategoryImpl() {
			private static final long serialVersionUID = 3269598929064240710L;

			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
		};
		
		return taxCategoryImpl;
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxCategoryImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		this.taxCategoryImpl.setName(GST_TAX_CATEGORY_NAME);
		assertEquals(GST_TAX_CATEGORY_NAME, this.taxCategoryImpl.getName());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.tax.impl.TaxCategoryImpl.getName()'.
	 */
	@Test
	public void testGetSetLocalizedProperties() {
		final LocalizedProperties localizedProperties = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 7344929117085742381L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedProperties.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.FRANCE, GST_TAX_CATEGORY_NAME_FR);
		
		this.taxCategoryImpl.setLocalizedProperties(localizedProperties);
		assertEquals(GST_TAX_CATEGORY_NAME_FR, 
					this.taxCategoryImpl.getLocalizedProperties().getValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.FRANCE));
		
		assertEquals(GST_TAX_CATEGORY_NAME_FR, this.taxCategoryImpl.getDisplayName(Locale.FRANCE));
	}
	
	/**
	 * Test that the equals method works as expected. Different instances of TaxCatagory should be equal as long as their localized property maps are
	 * either both null or contain the same values.  In all other cases they should not be equal.
	 */
	@Test
	public void testEquals() {
		final TaxCategory taxCategoryOne = getNewTaxCategory();
		final TaxCategory taxCategoryTwo = getNewTaxCategory();
		
		assertEquals("These two taxCategories should be equal since they both contain null localized property maps.", taxCategoryOne, taxCategoryTwo);
		
		final LocalizedProperties localizedPropertiesOne = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -4072887475393323316L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedPropertiesOne.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.FRANCE, GST_TAX_CATEGORY_NAME_FR);
		localizedPropertiesOne.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, GST_TAX_CATEGORY_NAME);
		taxCategoryOne.setLocalizedProperties(localizedPropertiesOne);
		
		assertFalse("These two taxCategories should no longer be equal since only one has a null localized property map.", taxCategoryOne
				.equals(taxCategoryTwo));
		
		final LocalizedProperties localizedPropertiesTwo = new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = -2631541471647114542L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl(); // arbitrary implementation
			}
		};
		localizedPropertiesTwo.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.FRANCE, GST_TAX_CATEGORY_NAME_FR);
		localizedPropertiesTwo.setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, GST_TAX_CATEGORY_NAME);
		taxCategoryTwo.setLocalizedProperties(localizedPropertiesTwo);

		assertEquals("These two taxCategories should be equal since they contain identical localized property values.", taxCategoryOne,
				taxCategoryTwo);
		
		taxCategoryTwo.getLocalizedProperties().setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, PST_TAX_CATEGORY_NAME);
		taxCategoryTwo.getLocalizedProperties().setValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.FRANCE, PST_TAX_CATEGORY_NAME_FR);
		
		assertFalse("These two taxCategories should no longer be equal since their localized property map values are different.", taxCategoryOne
				.equals(taxCategoryTwo));
	}

	/**
	 * Tests hash code.
	 */
	@Test
	public void testHashCode() {
		final TaxCategory taxCategoryOne = getNewTaxCategory();
		final TaxCategory taxCategoryTwo = getNewTaxCategory();
		
		assertEquals(taxCategoryOne, taxCategoryTwo);
		assertEquals(taxCategoryOne.hashCode(), taxCategoryTwo.hashCode());
		
		taxCategoryOne.setName("GST");
		taxCategoryTwo.setName("GST");
		assertEquals(taxCategoryOne, taxCategoryTwo);
		assertEquals(taxCategoryOne.hashCode(), taxCategoryTwo.hashCode());
	}
}
