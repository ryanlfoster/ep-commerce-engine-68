/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax;

import java.util.List;

import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide TaxCode-related business service.
 */
public interface TaxCodeService extends EpPersistenceService {
	/**
	 * Adds the given taxCode.
	 * 
	 * @param taxCode the taxCode to add
	 * @return the persisted instance of taxCode
	 * @throws TaxCodeExistException - if the specified tax code already exists.
	 */
	TaxCode add(final TaxCode taxCode) throws TaxCodeExistException;

	/**
	 * Updates the given taxCode.
	 * 
	 * @param taxCode the taxCode to update
	 * @throws TaxCodeExistException - if the specified tax code already exists.
	 * @return TaxCode the updated instance
	 */
	TaxCode update(final TaxCode taxCode) throws TaxCodeExistException;

	/**
	 * Delete the current taxCode and all its direct/indirect children.
	 * 
	 * @param taxCode the taxCode to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final TaxCode taxCode) throws EpServiceException;

	/**
	 * List all taxCodes stored in the database.
	 * 
	 * @return a list of taxCodes
	 * @throws EpServiceException - in case of any errors
	 */
	List<TaxCode> list() throws EpServiceException;

	/**
	 * Load the taxCode with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param taxCodeUid the taxCode UID
	 * @return the taxCode if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	TaxCode load(final long taxCodeUid) throws EpServiceException;

	/**
	 * Get the taxCode with the given UID. Return null if no matching record exists.
	 * 
	 * @param taxCodeUid the taxCode UID
	 * @return the taxCode if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	TaxCode get(final long taxCodeUid) throws EpServiceException;

	/**
	 * <p>
	 * Get the list of tax codes (GUIDS) in use. A {@code TaxCode} is deemed to be in use if:
	 * <ul>
	 * <li>a ProductType uses it</li>
	 * <li>a TaxValue uses it</li>
	 * <li>a Store uses it</li>
	 * <li>a Product uses it</li>
	 * </ul>
	 * </p>
	 * 
	 * @return the list of uids of <code>TaxCode</code>s in use.
	 * @throws EpServiceException - in case of any errors
	 */
	List<String> getTaxCodesInUse() throws EpServiceException;

	/**
	 * Checks the given taxCode exists or not.
	 * 
	 * @param code the taxCode
	 * @return true if the given taxCode exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean taxCodeExists(final String code) throws EpServiceException;

	/**
	 * Checks the given taxCode exists or not.
	 * 
	 * @param code the taxCode
	 * @return true if the given taxCode exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean taxCodeExists(final TaxCode code) throws EpServiceException;

	/**
	 * Find the code with the given code.
	 * 
	 * @param code the tax code.
	 * @return the tax code that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	TaxCode findByCode(final String code) throws EpServiceException;

	/**
	 * Find the tax code with the given guid.
	 * 
	 * @param guid guid
	 * @return an existing TaxCode, otherwise null
	 * @throws EpServiceException on error, or multiple results returned.
	 */
	TaxCode findByGuid(final String guid) throws EpServiceException;

}
