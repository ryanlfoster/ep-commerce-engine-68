/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search.query;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.CatalogAwareSearchCriteria;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;

/**
 * A criteria for sku search.
 */
public class SkuSearchCriteria extends AbstractSearchCriteriaImpl implements SpellSuggestionSearchCriteria,
	CatalogAwareSearchCriteria {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String brandCode;

	private String productName;

	private String productCode;

	private String skuCode;

	private Set<Locale> catalogSearchableLocales = new HashSet<Locale>();

	private boolean facetingEnabled;

	private String storeCode;

	private Set<String> catalogCodes;
	
	private Set<SkuOptionAndValues> skuOptionAndValuesSet;
	
	private boolean activeOnly;

	/**
	 * Clears this <code>SkuSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		this.productName = null;
		this.productCode = null;
		this.skuCode = null;
		this.brandCode = null;
		this.storeCode = null;
		if (catalogSearchableLocales != null) {
			this.catalogSearchableLocales.clear();
		}
		if (catalogCodes != null) {
			this.catalogCodes.clear();
		}
		clearSkuOptionAndValues();
		setActiveOnly(false);
	}

	/**
	 * Clear sku option and values.
	 */
	public void clearSkuOptionAndValues() {
		if (skuOptionAndValuesSet != null) {
			this.skuOptionAndValuesSet.clear();
		}
	}

	/**
	 * This criteria is for the SKU index.
	 *
	 * @return the SKU index type
	 */
	public IndexType getIndexType() {
		return IndexType.SKU;
	}

	@Override
	public void optimize() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * is active only.
	 * 
	 * @return true if it is active only
	 */
	public boolean isActiveOnly() {
		return activeOnly;
	}

	/**
	 * set active only.
	 * 
	 * @param activeOnly the value of active only
	 */
	public void setActiveOnly(final boolean activeOnly) {
		this.activeOnly = activeOnly;
	}
	
	
	/**
	 * get sku option and values set. 
	 * 
	 * @return the sku option and values set
	 */
	public Set<SkuOptionAndValues> getSkuOptionAndValuesSet() {
		return skuOptionAndValuesSet;
	}

	/**
	 * set sku option and values set.
	 * 
	 * @param skuOptionAndValuesSet the sku option and values set
	 */
	public void setSkuOptionAndValuesSet(
			final Set<SkuOptionAndValues> skuOptionAndValuesSet) {
		this.skuOptionAndValuesSet = skuOptionAndValuesSet;
	}

	@Override
	public void setCatalogCodes(final Set<String> catalogCodes) {
		this.catalogCodes = catalogCodes;
	}

	@Override
	public Set<String> getCatalogCodes() {
		return catalogCodes;
	}

	@Override
	public String getCatalogCode() {
		if (CollectionUtils.isNotEmpty(catalogCodes)) {
			return (String) CollectionUtils.get(catalogCodes, 0);
		}

		return null;
	}

	@Override
	public void setCatalogCode(final String catalogCode) {
		catalogCodes = new HashSet<String>();
		catalogCodes.add(catalogCode);
	}

	/**
	 * @return the store code
	 */
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * Set the store code. This implementation does not set the store UID.
	 * @param storeCode the store code
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
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
	 * Gets whether faceting is enabled for the search criteria.
	 *
	 * @return whether faceting is enabled for the search criteria
	 */
	public boolean isFacetingEnabled() {
		return facetingEnabled;
	}

	/**
	 * Sets whether faceting is enabled for the search criteria.
	 *
	 * @param facetingEnabled whether faceting is enabled for the search criteria
	 */
	public void setFacetingEnabled(final boolean facetingEnabled) {
		this.facetingEnabled = facetingEnabled;
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
		this.catalogSearchableLocales = locales;
	}

	/**
	 * Set the sku code.
	 *
	 * @param skuCode the sku code
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Get the SKU code.
	 *
	 * @return the sku code
	 */
	public String getSkuCode() {
		return skuCode;
	}
	
	/**
	 * Add sku option and values for searching.
	 * 
	 * @param skuOptionKey the sku option key
	 * @param skuOptionValues the sku option values
	 */
	public void addSkuOptionAndValues(final String skuOptionKey, final Set<String> skuOptionValues) {
		if (skuOptionAndValuesSet == null) {
			skuOptionAndValuesSet = new HashSet<SkuOptionAndValues>(); 
		}
		skuOptionAndValuesSet.add(new SkuOptionAndValues(skuOptionKey, skuOptionValues));
	}
	
	/**
	 * The class for searching sku option and values.
	 */
	public class SkuOptionAndValues implements Serializable {
		/** Serial version id. */
		private static final long serialVersionUID = 5000000001L;
	
		private String skuOptionKey;
		private Set<String> skuOptionValues;
		
		/**
		 * The constructor.
		 *  
		 * @param skuOptionKey the sku option key
		 * @param skuOptionValues the sku option values
		 */
		public SkuOptionAndValues(final String skuOptionKey, final Set<String> skuOptionValues) {
			this.skuOptionKey = skuOptionKey;
			this.skuOptionValues = skuOptionValues;
		}
		
		/**
		 * Get sku option key.
		 * 
		 * @return the sku option key
		 */
		public String getSkuOptionKey() {
			return skuOptionKey;
		}
		
		/**
		 * Set the sku option key.
		 * 
		 * @param skuOptionKey the sku option key
		 */
		public void setSkuOptionKey(final String skuOptionKey) {
			this.skuOptionKey = skuOptionKey;
		}
		
		/**
		 * get sku option values.
		 * 
		 * @return the set of sku option values
		 */
		public Set<String> getSkuOptionValues() {
			return skuOptionValues;
		}
		
		/**
		 * Set sku option values. 
		 * 
		 * @param skuOptionValues the set of sku option values
		 */
		public void setSkuOptionValues(final Set<String> skuOptionValues) {
			this.skuOptionValues = skuOptionValues;
		}
		
		
	}

}
