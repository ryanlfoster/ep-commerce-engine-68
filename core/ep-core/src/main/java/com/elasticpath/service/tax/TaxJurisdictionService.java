/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide TaxJurisdiction-related business service.
 */
public interface TaxJurisdictionService extends EpPersistenceService {

	/**
	 * Get the list of country codes of existing jurisdictions.
	 * 
	 * @return the list of country codes.
	 */
	List<String> getCountryCodesInUse();

	/**
	 * Adds the given taxJurisdiction.
	 * 
	 * @param taxJurisdiction the taxJurisdiction to add
	 * @return the persisted instance of taxJurisdiction
	 * @throws TaxJurisdictionExistException - if a taxJurisdiction associated with the given region already exists.
	 */
	TaxJurisdiction add(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException;

	/**
	 * Updates the given taxJurisdiction.
	 * 
	 * @param taxJurisdiction the taxJurisdiction to update
	 * @throws TaxJurisdictionExistException - if a taxJurisdiction associated with the given region already exists.
	 * 
	 * @return TaxJurisdiction the updated instance
	 */
	TaxJurisdiction update(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException;

	/**
	 * Delete the current taxJurisdiction and all its direct/indirect children.
	 * 
	 * @param taxJurisdiction the taxJurisdiction to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final TaxJurisdiction taxJurisdiction) throws EpServiceException;

	/**
	 * List all taxJurisdictions stored in the database.
	 * 
	 * @return a list of taxJurisdictions
	 * @throws EpServiceException - in case of any errors
	 */
	List<TaxJurisdiction> list() throws EpServiceException;

	/**
	 * Load the taxJurisdiction with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param taxJurisdictionUid the taxJurisdiction UID
	 * @return the taxJurisdiction if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	TaxJurisdiction load(final long taxJurisdictionUid) throws EpServiceException;

	/**
	 * Get the taxJurisdiction with the given UID. Return null if no matching record exists.
	 * 
	 * @param taxJurisdictionUid the taxJurisdiction UID
	 * @return the taxJurisdiction if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	TaxJurisdiction get(final long taxJurisdictionUid) throws EpServiceException;

	/**
	 * Create the copy of <code>TaxJurisdiction</code> for which rules apply:<br>
	 * 1) <code>TaxJurisdiction</code> should be enabled in store.
	 * 2) <code>TaxGategory.fieldMatchType</code> matches to lowest available field of shipping address.<br>
	 * 3) That <code>TaxCategory</code>'s <code>TaxValue.regionCode</code> matches to shipping address's field.<br>
	 * <br>
	 * Resulting <code>TaxJurisdiction</code> will contain:<br>
	 * 1) One and only one <code>TaxCategory</code> which matches to lowest available field of shipping address.<br>
	 * 2) That <code>TaxCategory</code> will contain one and only one <code>TaxValue</code> which matches to shipping address.<br>
	 * If these conditions can't be met - <code>EpServiceException</code> is thrown.
	 * 
 	 * @param store store that will be used to retrieve tax jurisdictions.
	 * @param shippingAddress - the shippingAddress used to retrieve the matching jurisdiction.
	 * @return the matching taxJurisdiction.
	 * @throws EpServiceException - in case of any errors
	 */
	TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address shippingAddress) throws EpServiceException;
	
	/**
	 * Get the list of tax jurisdictions (uidPk) in use.
	 * 
	 * @return the list of uids of <code>TaxJurisdiction</code>s in use.
	 * @throws EpServiceException - in case of any errors
	 */
	Collection<Long> getTaxJurisdictionsInUse() throws EpServiceException;
	
	/**
	 * Returns a list of <code>TaxJurisdiction</code> based on the given uids.
	 * 
	 * @param taxJurisdictionUids a collection of tax jurisdiction uids
	 * @return a list of <code>TaxJurisdiction</code>s
	 */
    List<TaxJurisdiction> findByUids(final Collection<Long> taxJurisdictionUids);

	/**
	 * Returns a list of <code>TaxJurisdiction</code> based on the given guids.
	 * 
	 * @param taxJurisdictionGuids a collection of tax jurisdiction guids
	 * @return a list of <code>TaxJurisdiction</code>s
	 */
    List<TaxJurisdiction> findByGuids(final Collection<String> taxJurisdictionGuids);
    
    /**
     * Retrieve the <code>TaxJurisdiction</code> based on a guid.
     * 
     * @param guid the guid you're looking for
     * @return <b>null</b> if no guid is found, otherwise a <code>TaxJurisdiction</code>
     * @throws EpServiceException if a system error occurs, or if for some reason more than one jurisdiction is returned.
     */
    TaxJurisdiction findByGuid(final String guid) throws EpServiceException;
    
}
