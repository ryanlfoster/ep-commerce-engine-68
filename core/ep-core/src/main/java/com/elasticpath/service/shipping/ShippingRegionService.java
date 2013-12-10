/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shipping;

import java.util.List;

import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.PropertyService;

/**
 * Provide shipping service level-related business service.
 */
public interface ShippingRegionService extends EpPersistenceService {
	/**
	 * Adds the given shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to add
	 * @return the persisted instance of shippingRegion
	 * @throws ShippingRegionExistException - If a shipping region with the same name already exists.
	 */
	ShippingRegion add(final ShippingRegion shippingRegion) throws ShippingRegionExistException;

	/**
	 * Updates the given shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to update
	 * @return ShippingRegion the updated ShippingRegion
	 * @throws ShippingRegionExistException - If a shipping region with the same name already exists.
	 */
	ShippingRegion update(final ShippingRegion shippingRegion) throws ShippingRegionExistException;

	/**
	 * Delete the shippingRegion.
	 *
	 * @param shippingRegion the shippingRegion to remove
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final ShippingRegion shippingRegion) throws EpServiceException;

	/**
	 * List all shippingRegions stored in the database.
	 *
	 * @return a list of shippingRegions
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	List<ShippingRegion> list() throws EpServiceException;

	/**
	 * Load the shippingRegion with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param shippingRegionUid the shippingRegion UID
	 *
	 * @return the shippingRegion if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingRegion load(final long shippingRegionUid) throws EpServiceException;

	/**
	 * Get the shippingRegion with the given UID.
	 * Return null if no matching record exists.
	 *
	 * @param shippingRegionUid the shippingRegion UID
	 *
	 * @return the shippingRegion if UID exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingRegion get(final long shippingRegionUid) throws EpServiceException;

	/**
	 * Check the given shipping region's name exists or not.
	 *
	 * @param name - the group name to check
	 * @return true if the given shipping region name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean nameExists(final String name) throws EpServiceException;

	/**
	 * Check if a different shipping region with the given shipping region's name exists exists or not.
	 *
	 * @param shippingRegion - the shippingRegion to check
	 * @return true if a different shipping region with the same name exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean nameExists(final ShippingRegion shippingRegion) throws EpServiceException;

	/**
	 * Find the shipping region with the given name.
	 *
	 * @param name - the shipping region name
	 *
	 * @return the shippingRegion with the given name if exists, otherwise null
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	ShippingRegion findByName(final String name) throws EpServiceException;

	/**
	 * Return the List of all possible <code>Region</code> (country/sub country combintaion) based on the country and
	 * subcountry property configuraion.
	 *
	 * @return the list of all possible <code>Region</code>.
	 */
	List<Region> getAllRegions();

	/**
	 * Return the propertyService singleton.
	 *
	 * @return the propertyService singleton.
	 */
	PropertyService getPropertyService();

	/**
	 * Set the propertyService singleton.
	 * @param propertyService - the propertyService singleton.
	 */
	void setPropertyService(final PropertyService propertyService);
}
