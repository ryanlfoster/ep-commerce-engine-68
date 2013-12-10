package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.service.search.CatalogAwareSearchCriteria;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;


/**
 * A criteria for advanced product search.
 */
public class ProductSearchCriteria extends AbstractProductCategorySearchCriteria implements SpellSuggestionSearchCriteria,
CatalogAwareSearchCriteria {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String brandCode;

	private String productName;

	private String productSku;

	private String productCode;

	private Set<Long> ancestorCategoryUids;

	private Long directCategoryUid;

	private Boolean onlyWithinDirectCategory = true;

	private Boolean featuredProductsOnly = false;

	private Boolean featuredOnlyInCategory = true;

	private Boolean inActiveOnly = false;

	private Long productUid;

	private Set<Locale> catalogSearchableLocales = new HashSet<Locale>();

	private String masterCategoryCode;

	private String masterCategoryCatalogCode;

	private Boolean onlySearchMasterCategory = false;

	/**
	 * Gets the list of potential misspelled strings. Currently product name, and product
	 * attributes could be misspelled.
	 * 
	 * @return the list of potential misspelled strings
	 */
	@Override
	public Set<String> getPotentialMisspelledStrings() {
		final Set<String> result = new HashSet<String>();
		result.add(getProductName());
		return result;
	}

	/**
	 * Gets the product name.
	 * 
	 * @return the product name
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Sets the product name.
	 * 
	 * @param productName the product name
	 */
	public void setProductName(final String productName) {
		this.productName = productName;
	}

	/**
	 * Gets the product sku.
	 * 
	 * @return the product sku
	 */
	public String getProductSku() {
		return productSku;
	}

	/**
	 * Sets the product sku.
	 * 
	 * @param productSku the product sku
	 */
	public void setProductSku(final String productSku) {
		this.productSku = productSku;
	}

	/**
	 * Gets the product code.
	 * 
	 * @return the product code
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * Sets the product code.
	 * 
	 * @param productCode the product code
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	/**
	 * Returns the brand code.
	 * 
	 * @return the brand code
	 */
	public String getBrandCode() {
		return brandCode;
	}

	/**
	 * Sets the brand code.
	 * 
	 * @param brandCode the brand code
	 */
	public void setBrandCode(final String brandCode) {
		this.brandCode = brandCode;
	}

	/**
	 * Returns a set of ancestor category UIDs. An ancestor is defined as any of the category
	 * above the one in which the product lives.
	 * 
	 * @return the the set of ancestor category UIDs
	 */
	public Set<Long> getAncestorCategoryUids() {
		return ancestorCategoryUids;
	}

	/**
	 * Sets the set of ancestor category UIDs. An ancestor is defined as any of the category above
	 * the one in which the product lives.
	 * 
	 * @param ancestorCategoryUids the set of category UIDs
	 */
	public void setAncestorCategoryUids(final Set<Long> ancestorCategoryUids) {
		this.ancestorCategoryUids = ancestorCategoryUids;
	}

	/**
	 * Returns the direct category UID. A direct category is defined as the exact category in
	 * which a product lives.
	 * 
	 * @return the the set of ancestor category UIDs
	 */
	public Long getDirectCategoryUid() {
		return directCategoryUid;
	}

	/**
	 * Sets the direct category UID. A direct category is defined as the exact category in which a
	 * product lives.
	 * 
	 * @param directCategoryUid the set of category UIDs
	 */
	public void setDirectCategoryUid(final Long directCategoryUid) {
		this.directCategoryUid = directCategoryUid;
	}

	/**
	 * Returns <code>true</code> if searching only for inactive products.
	 * 
	 * @return <code>true</code> if searching only for in active products
	 */
	public boolean isInActiveOnly() {
		return inActiveOnly;
	}

	/**
	 * Sets the inactive-only flag to <code>true</code> if only searching for inactive products.
	 * 
	 * @param inActiveOnlyFlag the inactive-only flag
	 */
	public void setInActiveOnly(final boolean inActiveOnlyFlag) {
		inActiveOnly = inActiveOnlyFlag;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public void optimizeInternal() {
		if (!isStringValid(brandCode)) {
			brandCode = null;
		}
		if (!isStringValid(productName)) {
			productName = null;
		}
		if (!isStringValid(productSku)) {
			productSku = null;
		}
		if (!isStringValid(productCode)) {
			productCode = null;
		}
		if (ancestorCategoryUids != null && ancestorCategoryUids.isEmpty()) {
			ancestorCategoryUids = null;
		}
	}

	@Override
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	public SearchCriteria clone() throws CloneNotSupportedException {
		final ProductSearchCriteria searchCriteria = (ProductSearchCriteria) super.clone();

		if (ancestorCategoryUids != null) {
			final Set<Long> categoryUids = new HashSet<Long>();
			categoryUids.addAll(ancestorCategoryUids);
			searchCriteria.setAncestorCategoryUids(categoryUids);
		}

		return searchCriteria;
	}

	/**
	 * {@inheritDoc}
	 * @see #getDirectCategoryUid()
	 */
	public Long getCategoryUid() {
		return getDirectCategoryUid();
	}

	/**
	 * Gets whether to search only within the direct category or to include category ancestors.
	 * Return <code>true</code> to only search within the direct category.
	 * 
	 * @return whether to search only within the direct category
	 */
	public boolean isOnlyWithinDirectCategory() {
		return onlyWithinDirectCategory;
	}

	/**
	 * Sets whether to search only within the direct category or to include category ancestors.
	 * Return <code>true</code> to only search within the direct category.
	 * 
	 * @param onlyWithinDirectCategory whether to search only within the direct category
	 */
	public void setOnlyWithinDirectCategory(final boolean onlyWithinDirectCategory) {
		this.onlyWithinDirectCategory = onlyWithinDirectCategory;
	}

	/**
	 * Gets whether to only search for featured products only.
	 * 
	 * @return whether to search for featured products only
	 */
	public boolean isOnlyFeaturedProducts() {
		return featuredProductsOnly;
	}

	/**
	 * Sets whether to only search for featured products only.
	 * 
	 * @param featuredProductsOnly whether to search for featured products only
	 */
	public void setOnlyFeaturedProducts(final boolean featuredProductsOnly) {
		this.featuredProductsOnly = featuredProductsOnly;
	}

	/**
	 * Gets whether a limit on the featured products such that they are only in the current
	 * category. This option is modified by {@link #isOnlyWithinDirectCategory()} to include
	 * featured products in sub-categories. This option is only valid with
	 * {@link #isOnlyFeaturedProducts()}=<code>true</code>.
	 * 
	 * @return whether to only search for those featured products in the current category
	 * @see #isOnlyWithinDirectCategory()
	 */
	public boolean isFeaturedOnlyInCategory() {
		return featuredOnlyInCategory;
	}

	/**
	 * Sets whether a limit on the featured products such that they are only in the current
	 * category. This option is modified by {@link #isOnlyWithinDirectCategory()} to include
	 * featured products in sub-categories. This option is only valid with
	 * {@link #isOnlyFeaturedProducts()}=<code>true</code>.
	 * 
	 * @param featuredOnlyInCategory whether to only search for those featured products in the
	 *            current category
	 * @see #isOnlyWithinDirectCategory()
	 */
	public void setFeaturedOnlyInCategory(final boolean featuredOnlyInCategory) {
		this.featuredOnlyInCategory = featuredOnlyInCategory;
	}

	/**
	 * Gets the specific UID to search for.
	 *
	 * @return the specific UID to search for
	 */
	public Long getProductUid() {
		return productUid;
	}

	/**
	 * Sets the specific UID to search for.
	 *
	 * @param productUid the specific UID to search for
	 */
	public void setProductUid(final Long productUid) {
		this.productUid = productUid;
	}

	/**
	 * @return locales
	 */
	public Set<Locale> getCatalogSearchableLocales() {
		return catalogSearchableLocales;
	}

	/**
	 * @param locales locales to set
	 */
	public void setCatalogSearchableLocales(final Set<Locale> locales) {
		catalogSearchableLocales = locales;
	}

	/**
	 * Clears this <code>ProductSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		productName = null;
		productCode = null;
		productSku = null;
		brandCode = null;
		directCategoryUid = null;
		setActiveOnly(false);
	}

	/**
	 * The masterCategoryCatalogCode must also be set.
	 * @param masterCategoryCode The uid of the master category to search in.
	 */
	public void setMasterCategoryCode(final String masterCategoryCode) {
		this.masterCategoryCode = masterCategoryCode;
	}

	/**
	 * The masterCategoryCode must also be set.
	 * @param masterCategoryCatalogCode The catalog code for the the category associated with the masterCategoryCode.
	 */
	public void setMasterCategoryCatalogCode(final String masterCategoryCatalogCode) {
		this.masterCategoryCatalogCode = masterCategoryCatalogCode;
	}

	/**
	 * 
	 * @param onlySearchMasterCategory True if only the master category, set by masterCategoryCode
	 * and masterCategoryCatalogCode, should be searched.
	 */
	public void setOnlySearchMasterCategory(final boolean onlySearchMasterCategory) {
		this.onlySearchMasterCategory = onlySearchMasterCategory;
	}

	/**
	 * 
	 * @return The catalog code for the the category associated with the masterCategoryCode.
	 */
	public String getMasterCategoryCatalogCode() {
		return masterCategoryCatalogCode;
	}

	/**
	 * 
	 * @return The uid of the master category to search in.
	 */
	public String getMasterCategoryCode() {
		return masterCategoryCode;
	}

	/**
	 * 
	 * @param onlySearchMasterCategory True if only the master category, set by masterCategoryCode
	 * and masterCategoryCatalogCode, should be searched.
	 */
	public void getOnlySearchMasterCategory(final boolean onlySearchMasterCategory) {
		this.onlySearchMasterCategory = onlySearchMasterCategory;
	}

	/**
	 * 
	 * @return True if only the master category should be searched.
	 */
	public boolean isOnlySearchMasterCategory() {
		return onlySearchMasterCategory;
	}
}
