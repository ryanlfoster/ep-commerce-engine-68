package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewResultImpl;

/**
 * Test case for {@link AbstractCatalogViewResultImpl}.
 */
public class AbstractCatalogViewResultImplTest {
	
	private AbstractCatalogViewResultImpl catalogViewResult;
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		catalogViewResult = new AbstractCatalogViewResultImpl() {
			private static final long serialVersionUID = -3180037047744150218L;
		};
	}
	
	/**
	 * Test method for {@link AbstractCatalogViewResultImpl#getCategoryFilterOptions()} and
	 * {@link AbstractCatalogViewResultImpl#setCategoryFilterOptions(List)}.
	 */
	@Test
	public void testCategoryFilterOptions() {
		assertNotNull(catalogViewResult.getCategoryFilterOptions());
		final List<FilterOption<CategoryFilter>> categoryFilterOptions = new ArrayList<FilterOption<CategoryFilter>>();
		catalogViewResult.setCategoryFilterOptions(categoryFilterOptions);
		assertEquals(categoryFilterOptions, catalogViewResult.getCategoryFilterOptions());
	}

	/**
	 * Test method for {@link AbstractCatalogViewResultImpl#getBrandFilterOptions()} and
	 * {@link AbstractCatalogViewResultImpl#setBrandFilterOptions(List)}.
	 */
	@Test
	public void testBrandFilterOptions() {
		assertNotNull(catalogViewResult.getBrandFilterOptions());
		final List<FilterOption<BrandFilter>> brandFilterOptions = new ArrayList<FilterOption<BrandFilter>>();
		catalogViewResult.setBrandFilterOptions(brandFilterOptions);
		assertEquals(brandFilterOptions, catalogViewResult.getBrandFilterOptions());
	}

	/**
	 * Test method for {@link AbstractCatalogViewResultImpl#getPriceFilterOptions()} and
	 * {@link AbstractCatalogViewResultImpl#setPriceFilterOptions(List)}.
	 */
	@Test
	public void testPriceFilterOptions() {
		assertNotNull(catalogViewResult.getPriceFilterOptions());
		final List<FilterOption<PriceFilter>> priceFilterOptions = new ArrayList<FilterOption<PriceFilter>>();
		catalogViewResult.setPriceFilterOptions(priceFilterOptions);
		assertEquals(priceFilterOptions, catalogViewResult.getPriceFilterOptions());
	}
	
	/**
	 * Test method for {@link AbstractCatalogViewResultImpl#getAttributeValueFilterOptions()} and
	 * {@link AbstractCatalogViewResultImpl#setAttributeValueFilterOptions(Map)}.
	 */
	@Test
	public void testAttributeValueFilterOptions() {
		assertNotNull(catalogViewResult.getAttributeValueFilterOptions());
		final Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeFilterOptions =
			new HashMap<Attribute, List<FilterOption<AttributeValueFilter>>>();
		catalogViewResult.setAttributeValueFilterOptions(attributeFilterOptions);
		assertEquals(attributeFilterOptions, catalogViewResult.getAttributeValueFilterOptions());
	}
	
	/**
	 * Test method for {@link AbstractCatalogViewResultImpl#getAttributeRangeFilterOptions()} and
	 * {@link AbstractCatalogViewResultImpl#setAttributeRangeFilterOptions(Map)}.
	 */
	@Test
	public void testAttributeRangeFilterOptions() {
		assertNotNull(catalogViewResult.getAttributeRangeFilterOptions());
		final Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeFilterOptions =
			new HashMap<Attribute, List<FilterOption<AttributeRangeFilter>>>();
		catalogViewResult.setAttributeRangeFilterOptions(attributeFilterOptions);
		assertEquals(attributeFilterOptions, catalogViewResult.getAttributeRangeFilterOptions());
	}
}
