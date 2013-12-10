/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.PropertyService;
import com.elasticpath.service.shipping.ShippingRegionExistException;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Provide shipping region-related business service.
 */
public class ShippingRegionServiceImpl extends AbstractEpPersistenceServiceImpl implements ShippingRegionService {

	private PropertyService propertyService;

	/**
	 * Adds the given shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to add
	 * @return the persisted instance of shippingRegion
	 * @throws ShippingRegionExistException - If a shipping region with the same name already exists.
	 */
	public ShippingRegion add(final ShippingRegion shippingRegion) throws ShippingRegionExistException {
		sanityCheck();
		if (nameExists(shippingRegion.getName())) {
			throw new ShippingRegionExistException("Shipping region with the given name already exists");
		}
		getPersistenceEngine().save(shippingRegion);
		return shippingRegion;
	}

	/**
	 * Updates the given shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to update
	 * @return ShippingRegion the updated ShippingRegion
	 * @throws ShippingRegionExistException - If a shipping region with the same name already exists.
	 */
	public ShippingRegion update(final ShippingRegion shippingRegion) throws ShippingRegionExistException {
		sanityCheck();

		if (nameExists(shippingRegion)) {
			throw new ShippingRegionExistException("Shipping region with the given name already exists");
		}
		return getPersistenceEngine().merge(shippingRegion);
	}

	/**
	 * Delete the shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final ShippingRegion shippingRegion) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(shippingRegion);
	}

	/**
	 * List all shippingRegions stored in the database, sorted by Name.
	 *
	 * @return a sorted list of shippingRegions
	 * @throws EpServiceException - in case of any errors
	 */
	public List<ShippingRegion> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_SELECT_ALL");
	}

	/**
	 * Load the shippingRegion with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param shippingRegionUid the shippingRegion UID
	 * @return the shippingRegion if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ShippingRegion load(final long shippingRegionUid) throws EpServiceException {
		sanityCheck();
		ShippingRegion shippingRegion = null;
		if (shippingRegionUid <= 0) {
			shippingRegion = getBean(ContextIdNames.SHIPPING_REGION);
		} else {
			shippingRegion = getPersistentBeanFinder().load(ContextIdNames.SHIPPING_REGION, shippingRegionUid);
		}
		return shippingRegion;
	}

	/**
	 * Get the shippingRegion with the given UID. Return null if no matching record exists.
	 *
	 * @param shippingRegionUid the shippingRegion UID
	 * @return the shippingRegion if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ShippingRegion get(final long shippingRegionUid) throws EpServiceException {
		sanityCheck();
		ShippingRegion shippingRegion = null;
		if (shippingRegionUid <= 0) {
			shippingRegion = getBean(ContextIdNames.SHIPPING_REGION);
		} else {
			shippingRegion = getPersistentBeanFinder().get(ContextIdNames.SHIPPING_REGION, shippingRegionUid);
		}
		return shippingRegion;
	}

	/**
	 * Generic load method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Check the given shipping region's name exists or not.
	 *
	 * @param name - the shipping region's name to check
	 * @return true if the given shipping region name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean nameExists(final String name) throws EpServiceException {
		if (name == null) {
			return false;
		}
		final ShippingRegion shippingRegion = this.findByName(name);
		boolean nameExists = false;
		if (shippingRegion != null) {
			nameExists = true;
		}
		return nameExists;
	}

	/**
	 * Check if a different shipping region with the given shipping region's name exists exists or not.
	 *
	 * @param shippingRegion - the shippingRegion to check
	 * @return true if a different shipping region with the same name exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean nameExists(final ShippingRegion shippingRegion) throws EpServiceException {
		if (shippingRegion.getName() == null) {
			return false;
		}
		final ShippingRegion existingSR = this.findByName(shippingRegion.getName());
		boolean shippingRegionExists = false;
		if (existingSR != null && existingSR.getUidPk() != shippingRegion.getUidPk()) {
			shippingRegionExists = true;
		}
		return shippingRegionExists;
	}

	/**
	 * Find the shipping region with the given name.
	 *
	 * @param name - the shipping region name
	 * @return the shippingRegion with the given name if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ShippingRegion findByName(final String name) throws EpServiceException {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve ShippingRegion with null name.");
		}

		final List<ShippingRegion> results = getPersistenceEngine().retrieveByNamedQuery("SHIPPINGREGION_FIND_BY_NAME", name);
		ShippingRegion shippingRegion = null;
		if (results.size() == 1) {
			shippingRegion = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate shipping region name exist -- " + name);
		}
		return shippingRegion;
	}

	/**
	 * Return the List of all possible <code>Region</code> (country/sub country combintaion) based on the country and subcountry property
	 * configuraion.
	 *
	 * @return the list of all possible <code>Region</code>.
	 */
	public List<Region> getAllRegions() {
		List<Region> regionList = new ArrayList<Region>();
		Geography countries = getBean(ContextIdNames.GEOGRAPHY);
		for (String countryCode : countries.getCountryCodes()) {
			if (countryCode.trim().length() > 0) {
				Region region = getBean(ContextIdNames.REGION);
				region.setCountryCode(countryCode);

				region.setSubCountryCodeList(new ArrayList<String>(countries.getSubCountryCodes(countryCode)));
				regionList.add(region);
			}
		}
		return regionList;
	}

	/**
	 * Return the propertyService singleton.
	 *
	 * @return the propertyService singleton.
	 */
	public PropertyService getPropertyService() {
		return this.propertyService;
	}

	/**
	 * Set the propertyService singleton.
	 *
	 * @param propertyService - the propertyService singleton.
	 */
	public void setPropertyService(final PropertyService propertyService) {
		this.propertyService = propertyService;
	}
}
