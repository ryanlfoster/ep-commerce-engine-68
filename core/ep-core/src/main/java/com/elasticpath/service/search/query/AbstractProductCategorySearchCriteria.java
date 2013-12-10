package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.StoreAwareSearchCriteria;

/**
 * Abstract class from which {@link ProductCategorySearchCriteria}s should inherit from.
 */
public abstract class AbstractProductCategorySearchCriteria extends AbstractSearchCriteriaImpl implements 
		ProductCategorySearchCriteria, StoreAwareSearchCriteria {
	
	private static final long serialVersionUID = -5625885056742648762L;

	private boolean facetingEnabled;
	
	private boolean displayableOnly;
	
	private boolean activeOnly;
	
	private Long storeUid;
	
	private String storeCode;
	
	private Set<String> catalogCodes;

	private static final String PERMISSION_ENFORCER = "PermissionEnforcer";
	
	@Override
	public IndexType getIndexType() {
		return IndexType.PRODUCT;
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
	 * Gets whether to search for displayable products only. Displayable products are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * <li>in stock (any of the product SKUs have at least 1 inventory or if the product is
	 * displayable when out of stock)</li>
	 * </ul>
	 * 
	 * @return whether to search for displayable products only
	 */
	public boolean isDisplayableOnly() {
		return displayableOnly;
	}
	
	/**
	 * Sets whether to search for displayable products only. Displayable products are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * <li>in stock (any of the product SKUs have at least 1 inventory or if the product is
	 * displayable when out of stock)</li>
	 * </ul>
	 * 
	 * @param displayableOnly whether to search for displayable products only
	 */
	public void setDisplayableOnly(final boolean displayableOnly) {
		this.displayableOnly = displayableOnly;
	}
	
	@Override
	public boolean isActiveOnly() {
		return activeOnly;
	}

	@Override
	public void setActiveOnly(final boolean activeOnlyFlag) {
		this.activeOnly = activeOnlyFlag;
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

	@Override
	public void optimize() {
		optimizeInternal();
		if (catalogCodes != null) {
			Set<String> tempCatalogCodes = new HashSet<String>();
			for (String catalogCode : catalogCodes) { 
				if (isStringValid(catalogCode)) {
					tempCatalogCodes.add(catalogCode);
				}
			}
			catalogCodes = tempCatalogCodes;
		}
		if (!isUidValid(storeUid)) {
			storeUid = null;
		}
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
		if (catalogCodes != null) {
			if (catalogCodes.contains(PERMISSION_ENFORCER)) {
				java.util.Iterator<String> iterator = catalogCodes.iterator();
				while (iterator.hasNext()) {
					String item = iterator.next();
					if (!item.equalsIgnoreCase(PERMISSION_ENFORCER)) {
						return item;
					}
				}
			} else {
				return (String) CollectionUtils.get(catalogCodes, 0);
			}
		}	
		
		return null;
	}	
	@Override
	public void setCatalogCode(final String catalogCode) {
		catalogCodes = new HashSet<String>();
		catalogCodes.add(catalogCode);
	}
	
	
	/**
	 * Optimizes the search criteria.
	 * 
	 * @see {@link #optimize()}.
	 */
	protected abstract void optimizeInternal();
}
