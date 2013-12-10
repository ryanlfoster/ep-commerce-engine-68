package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Represents criteria for <code>ShippingServiceLevel</code> search.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class ShippingServiceLevelSearchCriteria extends AbstractSearchCriteriaImpl { 

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String serviceLevelCode;
	
	private Boolean activeFlag;

	private String carrier;

	private String region;

	private String serviceLevelName;
	
	private String store;
	
	private String serviceLevelNameExact;
	
	private String regionExact;
	
	private String carrierExact;
	
	private Set<String> storeExactNames;

	/**
	 * @return the index type this criteria deals with
	 */
	public IndexType getIndexType() {
		return IndexType.SHIPPING_SERVICE_LEVEL;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() { //NOPMD
		if (!isStringValid(serviceLevelCode)) {
			serviceLevelCode = null;
		}
		if (!isStringValid(carrier)) {
			carrier = null;
		}
		if (!isStringValid(region)) {
			region = null;
		}
		if (!isStringValid(serviceLevelName)) {
			serviceLevelName = null;
		}
		if (!isStringValid(store)) {
			store = null;
		}		
		if (!isStringValid(serviceLevelNameExact)) {
			serviceLevelNameExact = null;
		}		
		if (!isStringValid(regionExact)) {
			regionExact = null;
		}
		if (!isStringValid(carrierExact)) {
			carrierExact = null;
		}
		if (storeExactNames != null) {
			Set<String> tempStoreExactNames = new HashSet<String>();
			for (String storeExact : storeExactNames) {
				if (isStringValid(storeExact)) {
					tempStoreExactNames.add(storeExact);
				}
			}
			storeExactNames = tempStoreExactNames;
		}
	}

	/**
	 * Clears this <code>ShippingServiceLevelSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		serviceLevelCode = null;
		activeFlag = null;
		carrier = null;
		region = null;
		serviceLevelName = null;
		store = null;
		storeExactNames = null;
		serviceLevelNameExact = null;
		regionExact = null;
		carrierExact = null;
	}
	
	/**
	 * @return the serviceLevelNameExact
	 */
	public String getServiceLevelNameExact() {
		return serviceLevelNameExact;
	}

	/**
	 * @param serviceLevelNameExact the serviceLevelNameExact to set
	 */
	public void setServiceLevelNameExact(final String serviceLevelNameExact) {
		this.serviceLevelNameExact = serviceLevelNameExact;
	}

	/**
	 * @return the regionExact
	 */
	public String getRegionExact() {
		return regionExact;
	}

	/**
	 * @param regionExact the regionExact to set
	 */
	public void setRegionExact(final String regionExact) {
		this.regionExact = regionExact;
	}

	/**
	 * @return the carrierExact
	 */
	public String getCarrierExact() {
		return carrierExact;
	}

	/**
	 * @param carrierExact the carrierExact to set
	 */
	public void setCarrierExact(final String carrierExact) {
		this.carrierExact = carrierExact;
	}

	/**
	 * @return service level code
	 */
	public String getServiceLevelCode() {
		return serviceLevelCode;
	}

	/**
	 * @param serviceLevelCode service level code
	 */
	public void setServiceLevelCode(final String serviceLevelCode) {
		this.serviceLevelCode = serviceLevelCode;
	}

	/**
	 * @return true if shipping service level is active
	 */
	public Boolean getActiveFlag() {
		return activeFlag;
	}

	/**
	 * @param activeFlag whether shipping service level should be active or not
	 */
	public void setActiveFlag(final Boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	/**
	 * @return carrier
	 */
	public String getCarrier() {
		return carrier;
	}

	/**
	 * @param carrier carrier
	 */
	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	/**
	 * @return region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region region
	 */
	public void setRegion(final String region) {
		this.region = region;
	}

	/**
	 * @return shipping service level name
	 */
	public String getServiceLevelName() {
		return serviceLevelName;
	}

	/**
	 * @param serviceLevelName shipping service level name
	 */
	public void setServiceLevelName(final String serviceLevelName) {
		this.serviceLevelName = serviceLevelName;
	}

	/**
	 * @return store name
	 */
	public String getStore() {
		return store;
	}

	/**
	 * @param store store name
	 */
	public void setStore(final String store) {
		this.store = store;
	}
	
	/**
	 * @return the storeExact
	 */
	public String getStoreExact() {
		if (CollectionUtils.isNotEmpty(storeExactNames)) {
			return (String) CollectionUtils.get(storeExactNames, 0);
		}
		return null;
	}

	/**
	 * @param storeExact the storeExact to set
	 */
	public void setStoreExact(final String storeExact) {
		storeExactNames = new HashSet<String>();
		storeExactNames.add(storeExact);
	}

	/**
	 * Gets the set of exact store names.
	 * 
	 * It allows to search in a set of store exact names.
	 * 
	 * @return the set of store names 
	 */
	public Set<String> getStoreExactNames() {
		return storeExactNames;
	}

	/**
	 * Sets the set of exact store names.
	 * 
	 * @param storeNames the set of store names
	 */
	public void setStoreExactNames(final Set<String> storeNames) {
		this.storeExactNames = storeNames;
	}
	
	
}
