/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.impl.TaxCodeImpl;

/**
 * Test <code>ProductTypeImpl</code>.
 */
public class ProductTypeImplTest {

	private ProductTypeImpl productType;

	@Before
	public void setUp() throws Exception {
		productType = new ProductTypeImpl();
		productType.setProductAttributeGroup(new AttributeGroupImpl());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		assertNull(productType.getName());
		productType.setName("name");
		assertEquals("name", productType.getName());
	}

	/**
	 * Test method for getting and setting the SKU options.
	 */
	@Test
	public void testGetSetSkuOptions() {
		Set<SkuOption> skuOptionSet = new HashSet<SkuOption>();
		productType.setSkuOptions(skuOptionSet);
		assertSame(skuOptionSet, productType.getSkuOptions());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getDescription()'.
	 */
	@Test
	public void testGetSetDescription() {
		assertNull(productType.getDescription());
		productType.setDescription("description");
		assertEquals("description", productType.getDescription());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getProductAttributeGroup()'.
	 */
	@Test
	public void testGetSetProductAttributeGroup() {
		final AttributeGroup productAttributeGroup = new AttributeGroupImpl();
		productType.setProductAttributeGroup(productAttributeGroup);
		assertSame(productAttributeGroup, productType.getProductAttributeGroup());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.addAttribute()'.
	 */
	@Test
	public void testAddAttributeGroupAttributes() {
		final Attribute attribute = new AttributeImpl();
		final AttributeGroupAttribute ptAttribute = new AttributeGroupAttributeImpl();
		ptAttribute.setAttribute(attribute);
		this.productType.getProductAttributeGroup().addAttributeGroupAttribute(ptAttribute);
		Set<AttributeGroupAttribute> productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		productAttributeGroupAttributes.add(ptAttribute);
		this.productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		assertTrue(this.productType.getProductAttributeGroup().getAttributeGroupAttributes().contains(ptAttribute));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.hasMultipleSkus()'.
	 */
	@Test
	public void testHasMultipleSkus() {
		this.productType.setWithMultipleSkus(true);
		assertTrue(this.productType.isWithMultipleSkus());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getSkuAttributeGroup()'.
	 */
	@Test
	public void testGetSetSkuAttributeGroup() {
		final AttributeGroup skuAttributeGroup = new AttributeGroupImpl();
		productType.setSkuAttributeGroup(skuAttributeGroup);
		assertSame(skuAttributeGroup, productType.getSkuAttributeGroup());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getTaxCode()'.
	 */
	@Test
	public void testGetSetTaxCode() {
		final TaxCode taxCode = new TaxCodeImpl();
		productType.setTaxCode(taxCode);
		assertSame(taxCode, productType.getTaxCode());
	}
	
	/**
	 * Asserts that if identical SkuOption objects are added to the SkuOption list
	 * the second one will replace the first one added.
	 */
	@Test
	public void testAddOrUpdateSkuOption() {
		SkuOption option1 = new SkuOptionImpl();
		option1.setOptionKey("key1");

		SkuOption option2 = new SkuOptionImpl();
		option2.setOptionKey("key2");

		SkuOption sameAsOption1 = new SkuOptionImpl();
		sameAsOption1.setOptionKey("key1");

		assertEquals(0, productType.getSkuOptions().size());
		
		productType.addOrUpdateSkuOption(option1);

		assertEquals(1, productType.getSkuOptions().size());

		productType.addOrUpdateSkuOption(option2);
		
		assertEquals(2, productType.getSkuOptions().size());

		productType.addOrUpdateSkuOption(sameAsOption1);
		
		assertEquals(2, productType.getSkuOptions().size());

		productType.getSkuOptions().remove(option2);
		assertTrue(productType.getSkuOptions().contains(sameAsOption1));

		assertSame(sameAsOption1, productType.getSkuOptions().iterator().next());

	}
	
	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.isExcludeFromDiscount()'.
	 */
	@Test
	public void testGetSetIsExcludedFromDiscount() {
		final boolean  excludeFromDiscount = true;
		productType.setExcludedFromDiscount(excludeFromDiscount);
		assertSame(excludeFromDiscount, productType.isExcludedFromDiscount());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductTypeImpl.getSortedSkuOptionListForRecurringItems()'.
	 */
	@Test
	public void testGetSortedSkuOptionListForRecurringItems() {
		final ProductSku sku = new ProductSkuImpl();
		final SkuOption skuOption1 = createSkuOption("Frequency");
		final SkuOption skuOption2 = createSkuOption("PSize");
		final SkuOption skuOption3 = createSkuOption("Color");
		final SkuOption skuOption4 = createSkuOption("PodPouchColor");
		
		final Set<SkuOption> skuOptions = new HashSet<SkuOption>();
		skuOptions.add(skuOption1);
		skuOptions.add(skuOption2);
		skuOptions.add(skuOption3);
		skuOptions.add(skuOption4);
		productType.setSkuOptions(skuOptions);
		
		final Catalog catalog = new CatalogImpl();
		catalog.setDefaultLocale(Locale.ENGLISH);
		productType.setCatalog(catalog);
		
		final List<SkuOption> sortedSkuOptionListForRecurringItems = productType.getSortedSkuOptionListForRecurringItems(sku);

		final SkuOption resultSkuOption1 = sortedSkuOptionListForRecurringItems.get(0);
		final SkuOption resultSkuOption2 = sortedSkuOptionListForRecurringItems.get(1);
		final SkuOption resultSkuOption3 = sortedSkuOptionListForRecurringItems.get(2);
		final SkuOption resultSkuOption4 = sortedSkuOptionListForRecurringItems.get(3);
		
		assertEquals("Expected skuOption with optionKey Color", "Color", resultSkuOption1.getOptionKey());
		assertEquals("Expected skuOption with optionKey PSize", "PSize", resultSkuOption2.getOptionKey());
		assertEquals("Expected skuOption with optionKey PodPouchColor", "PodPouchColor", resultSkuOption3.getOptionKey());
		assertEquals("Expected skuOption with optionKey Frequency", "Frequency", resultSkuOption4.getOptionKey());
	}
	
	private SkuOption createSkuOption(final String optionKey) {
		final SkuOption skuOption = new SkuOptionImpl() {
			private static final long serialVersionUID = 179539767294764294L;
			private String displayName;
			public String getDisplayName(final Locale locale, final boolean fallback) {
				return displayName;
			}
			public void setDisplayName(final String name, final Locale locale) {
				displayName = name;
			}
		};
		skuOption.setDisplayName(optionKey, Locale.ENGLISH);
		skuOption.setOptionKey(optionKey);
		return skuOption;
	}

}
