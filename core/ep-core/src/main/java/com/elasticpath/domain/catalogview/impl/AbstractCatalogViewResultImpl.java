package com.elasticpath.domain.catalogview.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * This is an abstract implementation of <code>CatalogViewResult</code>. It can be extended to create a concrete catalog view result for a search
 * request or a catalog browsing request.
 */
public abstract class AbstractCatalogViewResultImpl extends AbstractEpDomainImpl implements CatalogViewResult {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private List<StoreProduct> products;

	private List<StoreProduct> featuredProducts;

	private CatalogViewRequest catalogViewRequest;

	private List<FilterOption<PriceFilter>> priceFilterOptions;

	private List<FilterOption<BrandFilter>> brandFilterOptions;

	private List<FilterOption<CategoryFilter>> categoryFilterOptions;

	private Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeValueFilterOptions;

	private Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions;

	private Category category;

	private boolean categoryMatch = false;

	private int resultsCount;

	/**
	 * Returns a list of products as the search result.
	 *
	 * @return a list of products as the search result.
	 */
	public List<StoreProduct> getProducts() {
		return products;
	}

	/**
	 * Sets a list of products as the search result.
	 *
	 * @param products a list of products as the search result.
	 */
	public void setProducts(final List<StoreProduct> products) {
		if (this.products == null) {
			this.products = products;
		} else {
			throw new EpDomainException("Products can only be set once.");
		}
	}

	/**
	 * Returns a list of filter options on price range.
	 *
	 * @return a list of filter options on price range.
	 */
	public List<FilterOption<PriceFilter>> getPriceFilterOptions() {
		if (priceFilterOptions == null) {
			return Collections.emptyList();
		}
		return priceFilterOptions;
	}

	/**
	 * Sets the list of filter options on price range.
	 *
	 * @param priceFilterOptions the list of filter options on price range
	 */
	public void setPriceFilterOptions(final List<FilterOption<PriceFilter>> priceFilterOptions) {
		this.priceFilterOptions = priceFilterOptions;
	}

	/**
	 * Collapses the list of price filter options such that options that are a subset of another
	 * filter option will be removed (counts added to the option that contains that set).
	 */
	public void collapsePriceFilterOptions() {
		collapseRangeFilter(priceFilterOptions);
	}

	/**
	 * Gets the map of attribute value filter options.
	 *
	 * @return a map of filter value options
	 */
	public Map<Attribute, List<FilterOption<AttributeValueFilter>>> getAttributeValueFilterOptions() {
		if (attributeValueFilterOptions == null) {
			return Collections.emptyMap();
		}
		return attributeValueFilterOptions;
	}

	/**
	 * Sets the map of attribute value filter options.
	 *
	 * @param attributeValueFilterOptions map of attribute value filter options
	 */
	public void setAttributeValueFilterOptions(
			final Map<Attribute, List<FilterOption<AttributeValueFilter>>> attributeValueFilterOptions) {
		this.attributeValueFilterOptions = attributeValueFilterOptions;
	}

	/**
	 * Gets the map of attribute range filter options.
	 *
	 * @return the map of attribute range filter options
	 */
	public Map<Attribute, List<FilterOption<AttributeRangeFilter>>> getAttributeRangeFilterOptions() {
		if (attributeRangeFilterOptions == null) {
			return Collections.emptyMap();
		}
		return attributeRangeFilterOptions;
	}

	/**
	 * Sets the map of attribute value filter options.
	 *
	 * @param attributeRangeFilterOptions map of attribute value filter options
	 */
	public void setAttributeRangeFilterOptions(final Map<Attribute, List<FilterOption<AttributeRangeFilter>>> attributeRangeFilterOptions) {
		this.attributeRangeFilterOptions = attributeRangeFilterOptions;
	}

	/**
	 * Collapses the list of attribute filter options. This is only value for attribute range
	 * filter options whereby the the range is contained entirely within another range.
	 */
	public void collapseAttributeRangeFilterOptions() {
		if (attributeRangeFilterOptions != null) {
			for (List<FilterOption<AttributeRangeFilter>> rangeFilterList : attributeRangeFilterOptions.values()) {
				collapseRangeFilter(rangeFilterList);
			}
		}
	}

	/**
	 * Returns a list of filter options on brand.
	 *
	 * @return a list of filter options on brand
	 */
	public List<FilterOption<BrandFilter>> getBrandFilterOptions() {
		if (brandFilterOptions == null) {
			return Collections.emptyList();
		}
		return brandFilterOptions;
	}

	/**
	 * Gets a list of filter options on brand.
	 *
	 * @param brandFilterOptions a list of filter options on brand
	 */
	public void setBrandFilterOptions(final List<FilterOption<BrandFilter>> brandFilterOptions) {
		this.brandFilterOptions = brandFilterOptions;
	}

	/**
	 * Returns the catalog view request which generated this search result.
	 *
	 * @return the catalog view request
	 */
	public CatalogViewRequest getCatalogViewRequest() {
		return catalogViewRequest;
	}

	/**
	 * Sets the catalog view request which generated this search result.
	 *
	 * @param catalogViewRequest the catalog view request
	 */
	public void setCatalogViewRequest(final CatalogViewRequest catalogViewRequest) {
		this.catalogViewRequest = catalogViewRequest;
	}

	/**
	 * Replicate the data to this <code>CatalogViewResult</code> from the given <code>CatalogViewResult</code>.
	 *
	 * @param catalogViewResult the <code>CatalogViewResult</code> to be replicated
	 */
	public void replicateData(final CatalogViewResult catalogViewResult) {
		setProducts(catalogViewResult.getProducts());
		setFeaturedProducts(catalogViewResult.getFeaturedProducts());
	}

	/**
	 * Returns a list of featured products, this list is by default sorted by featured product order.
	 *
	 * @return a list of featured products
	 */
	public List<StoreProduct> getFeaturedProducts() {
		if (featuredProducts == null) {
			return Collections.emptyList();
		}
		return featuredProducts;
	}

	/**
	 * Sets a list of featured products.
	 *
	 * @param featuredProducts a list of featured products
	 */
	public void setFeaturedProducts(final List<StoreProduct> featuredProducts) {
		this.featuredProducts = featuredProducts;
	}

	/**
	 * Sets the category.
	 *
	 * @param category the category to set
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * Returns the category.
	 *
	 * @return the category.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Sets whether a category match was made and should navigate to the category specified by
	 * <code>getCategory()</code>.
	 *
	 * @param categoryMatch whether a category match was made
	 */
	public void setCategoryMatch(final boolean categoryMatch) {
		this.categoryMatch = categoryMatch;
	}

	/**
	 * Gets whether a category match was made and should navigate to the category specified by
	 * <code>getCategory()</code>.
	 *
	 * @return whether a category match was made
	 */
	public boolean isCategoryMatch() {
		return categoryMatch;
	}

	/**
	 * Returns a list of filter options on category.
	 *
	 * @return a list of filter options on category
	 */
	public List<FilterOption<CategoryFilter>> getCategoryFilterOptions() {
		if (categoryFilterOptions == null) {
			return Collections.emptyList();
		}
		return categoryFilterOptions;
	}

	/**
	 * Sets the list of filter options on category.
	 *
	 * @param categoryFilterOptions a list of filter options on category
	 */
	public void setCategoryFilterOptions(final List<FilterOption<CategoryFilter>> categoryFilterOptions) {
		this.categoryFilterOptions = categoryFilterOptions;
	}

	/**
	 * Collapses the list of category filter options such that options that are a subset of
	 * another filter option will be removed (counts added to the option that contains that set).
	 * Optionally remove filter options for this category as well. This category is given by
	 * {@link #getCategory()}.
	 *
	 * @param removeThisCategory whether to remove options from this category
	 */
	public void collapseCategoryFilterOptions(final boolean removeThisCategory) {
		// do this first so that counts aren't all added to this category, and then removed
		for (Iterator<FilterOption<CategoryFilter>> catIter = categoryFilterOptions.iterator(); catIter.hasNext();) {
			CategoryFilter catFilter = catIter.next().getFilter();
			if (catFilter.getCategory().equals(getCategory())) {
				catIter.remove();
			}
		}
		for (int i = 0; i < categoryFilterOptions.size(); ++i) {
			CategoryFilter outerFilter = categoryFilterOptions.get(i).getFilter();
			for (int j = 0; j < categoryFilterOptions.size(); ++j) {
				if (i == j) {
					continue;
				}
				CategoryFilter innerFilter = categoryFilterOptions.get(j).getFilter();
				if (isInCurrentCategory(innerFilter.getCategory(), outerFilter.getCategory())) {
					// need to add counts because we SOLR is faceting on the field and not on a
					// query
					int outerOptionHitsCount = categoryFilterOptions.get(i).getHitsNumber();
					int innerOptionHitsCount = categoryFilterOptions.get(j).getHitsNumber();

					categoryFilterOptions.get(i).setHitsNumber(outerOptionHitsCount + innerOptionHitsCount);
					// need to offset our j indexer as well
					categoryFilterOptions.remove(j--);
					if (j < i) {
						--i;
					}
				}
			}
		}
	}

	/**
	 * Returns the number of hits for this result. The number of hits is the actual number of
	 * products returned from the result which may or may not be the same as the number of items
	 * in.
	 *
	 * @return the number of hits for this result
	 */
	public int getResultsCount() {
		return resultsCount;
	}

	/**
	 * Sets the number of hits for this result. The number of hits is the actual number of
	 * products returned from the result which may or may not be the same as the number of items
	 * in.
	 *
	 * @param resultsCount the number of hits for this result
	 */
	public void setResultsCount(final int resultsCount) {
		this.resultsCount = resultsCount;
	}

	private boolean isInCurrentCategory(final Category category, final Category currentCategory) {
		if (currentCategory == null) { //If category is null, means all category is included.
			return true;
		}
		Category cursor = category;
		while (cursor != null) {
			if (currentCategory.equals(cursor)) {
				return true;
			} else if (cursor.getLevel() <= currentCategory.getLevel()) {
				return false;
			}
			cursor = cursor.getParent();
		}

		return false;
	}

	private <T extends RangeFilter<T, E>, E extends Comparable<E>> void collapseRangeFilter(final List<FilterOption<T>> filterOptions) {
		for (int i = 0; i < filterOptions.size(); ++i) {
			RangeFilter<T, E> outerFilter = filterOptions.get(i).getFilter();
			for (int j = 0; j < filterOptions.size(); ++j) {
				if (i == j) {
					continue;
				}
				RangeFilter<T, E> innerFilter = filterOptions.get(j).getFilter();
				if (outerFilter.contains(innerFilter)) {
					// need to offset our j indexer as well
					filterOptions.remove(j--);
					if (j < i) {
						--i;
					}
				}
			}
		}
	}
}
