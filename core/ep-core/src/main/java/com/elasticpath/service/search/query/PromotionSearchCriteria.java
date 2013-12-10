package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Represents criteria for advanced promotions search.
 */
public class PromotionSearchCriteria extends AbstractSearchCriteriaImpl {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private Boolean enabled;
	private Boolean active;
	private String ruleSetUid;
	
	private String ruleSetName;
	
	private Long catalogUid;
	
	/** GUID. */
	private String promotionName;

	private Set<String> catalogCodes;
	
	private Set<String> storeCodes;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	/**
	 * Returns the rule set uid of the promotion type.
	 * 
	 * @return the rule set uid of the promotion type
	 * @deprecated use {@link PromotionSearchCriteria#getRuleSetName(String)} instead.
	 */
	@Deprecated
	public String getRuleSetUid() {
		return this.ruleSetUid;
	}

	/**
	 * Sets the rule set uid of the promotion type.
	 * 
	 * @param ruleSetUid the rule set uid of the promotion type
	 * @deprecated use {@link PromotionSearchCriteria#setRuleSetName(String)} instead.
	 */
	@Deprecated
	public void setRuleSetUid(final String ruleSetUid) {
		this.ruleSetUid = ruleSetUid;
	}

	/**
	 * 
	 * @return the name of the rule set identifying the promotion type
	 */
	public String getRuleSetName() {
		return ruleSetName;
	}

	/**
	 * Sets the name of the rule set identifying the promotion type.
	 * @param ruleSetName the name of the ruleset
	 */
	public void setRuleSetName(final String ruleSetName) {
		this.ruleSetName = ruleSetName;
	}

	/**
	 * Returns the store code.
	 * 
	 * @return the store code
	 */
	public String getStoreCode() {
		if (storeCodes != null && !storeCodes.isEmpty()) {
			return (String) CollectionUtils.get(storeCodes, 0);
		}
		return null;
	}

	/**
	 * Sets the store code.
	 * 
	 * @param storeCode the store code
	 */
	public void setStoreCode(final String storeCode) {
		storeCodes = new HashSet<String>();
		storeCodes.add(storeCode);
	}
	
	/**
	 * Returns the set of store code.
	 * 
	 * @return the set of store code
	 */
	public Set<String> getStoreCodes() {
		return this.storeCodes;
	}

	/**
	 * Sets the set of store code.
	 * 
	 * @param storeCodes the set of store code
	 */
	public void setStoreCodes(final Set<String> storeCodes) {
		this.storeCodes = storeCodes;
	}
	
	/**
	 * Returns the promotion name.
	 *
	 * @return the promotionName
	 */
	public String getPromotionName() {
		return promotionName;
	}

	/**
	 * Sets the promotion name.
	 *
	 * @param promotionName the promotionName to set
	 */
	public void setPromotionName(final String promotionName) {
		this.promotionName = promotionName;
	}
	
	/**
	 * Gets the catalog UID.
	 * @deprecated use the Catalog Code instead
	 *
	 * @return the catalog UID
	 */
	@Deprecated
	public Long getCatalogUid() {
		return catalogUid;
	}
	
	/**
	 * Sets the catalog UID.
	 * @deprecated use the Catalog Code instead.
	 *
	 * @param catalogUid the catalog UID
	 */
	@Deprecated
	public void setCatalogUid(final Long catalogUid) {
		this.catalogUid = catalogUid;
	}
	
	/**
	 * Gets the catalog Code.
	 *
	 * @return the catalog Code
	 */
	public String getCatalogCode() {
		if (catalogCodes != null && !catalogCodes.isEmpty()) {
			return (String) CollectionUtils.get(catalogCodes, 0);
		}
		return null;
	}
	
	/**
	 * Sets the catalog Code.
	 *
	 * @param catalogCode the catalog Code
	 */
	public void setCatalogCode(final String catalogCode) {
		catalogCodes = new HashSet<String>();
		catalogCodes.add(catalogCode);
	}

	/**
	 * Gets the set of catalog codes.
	 * 
	 * It allows to search in a set of catalog codes
	 * @return the set of catalog codes
	 */
	public Set<String> getCatalogCodes() {
		return catalogCodes;
	}

	/**
	 * Sets the set of catalog codes. 
	 * 
	 * @param catalogCodes the set of the catalog codes
	 */
	public void setCatalogCodes(final Set<String> catalogCodes) {
		this.catalogCodes = catalogCodes;
	}
	
	/**
	 * Returns <code>true</code> if this <code>PromotionSearchCriteria</code> has no search
	 * criteria, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this <code>PromotionSearchCriteria</code> has no search
	 *         criteria, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		boolean empty = true;
		empty &= enabled == null;
		empty &= active == null;
		empty &= !isStringValid(promotionName);
		empty &= !isStringValid(ruleSetUid);
		empty &= !isUidValid(catalogUid);
		
		if (storeCodes != null) {
			for (String storeCode : storeCodes) {
				empty &= !isStringValid(storeCode);
			}
		}
		
		return empty;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (!isStringValid(ruleSetUid)) {
			ruleSetUid = null;
		}
		if (storeCodes != null) {
			Set<String> tempStoreCodes = new HashSet<String>();
			for (String storeCode : storeCodes) {
				if (isStringValid(storeCode)) {
					tempStoreCodes.add(storeCode);
				}
			}
			storeCodes = tempStoreCodes;
		}
		if (!isStringValid(promotionName)) {
			promotionName = null;
		}
	}
	
	/**
	 * Clears this <code>PromotionSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		enabled = null;
		active = null;

		ruleSetUid = null;
		storeCodes = null;
		promotionName = null;
		
		catalogUid = null;
		catalogCodes = null;
		promotionName = null;
	}
	
	/**
	 * Returns the index type this criteria deals with.
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return IndexType.PROMOTION;
	}
}