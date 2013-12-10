/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.tax.TaxCodeExistException;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * The default implementation of the <code>TaxCodeService</code>.
 */
public class TaxCodeServiceImpl extends AbstractEpPersistenceServiceImpl implements TaxCodeService {
	
	/**
	 * Adds the given taxCode.
	 * 
	 * @param taxCode the taxCode to add
	 * @return the persisted instance of taxCode
	 * @throws TaxCodeExistException - if the specified tax code already exists.
	 */
	public TaxCode add(final TaxCode taxCode) throws TaxCodeExistException {
		sanityCheck();

		if (this.taxCodeExists(taxCode.getCode())) {
			throw new TaxCodeExistException("Tax code \"" + taxCode.getCode() + "\" already exists.");
		}
		getPersistenceEngine().save(taxCode);
		return taxCode;
	}

	/**
	 * Updates the given taxCode.
	 * 
	 * @param taxCode the taxCode to update
	 * @throws EpServiceException - in case of any errors
	 * 
	 * @return TaxCode the updated instance
	 */
	public TaxCode update(final TaxCode taxCode) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(taxCode);
	}

	/**
	 * Delete the taxCode.
	 * 
	 * @param taxCode the taxCode to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final TaxCode taxCode) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(taxCode);
	}

	/**
	 * List all taxCodes stored in the database.
	 * 
	 * @return a list of taxCodes
	 * @throws EpServiceException - in case of any errors
	 */
	public List<TaxCode> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("TAXCODE_SELECT_ALL");
	}

	/**
	 * Load the taxCode with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * 
	 * @param taxCodeUid the taxCode UID
	 * @return the taxCode if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public TaxCode load(final long taxCodeUid) throws EpServiceException {
		sanityCheck();
		TaxCode taxCode = null;
		if (taxCodeUid <= 0) {
			taxCode = getBean(ContextIdNames.TAX_CODE);
		} else {
			taxCode = getPersistentBeanFinder().load(ContextIdNames.TAX_CODE, taxCodeUid);
		}
		return taxCode;
	}

	/**
	 * Get the taxCode with the given UID. Return null if no matching record exists.
	 * 
	 * @param taxCodeUid the taxCode UID
	 * @return the taxCode if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public TaxCode get(final long taxCodeUid) throws EpServiceException {
		sanityCheck();
		TaxCode taxCode = null;
		if (taxCodeUid <= 0) {
			taxCode = getBean(ContextIdNames.TAX_CODE);
		} else {
			taxCode = getPersistentBeanFinder().get(ContextIdNames.TAX_CODE, taxCodeUid);
		}
		return taxCode;
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
	 * <p>Get the list of tax codes (GUIDS) in use. A {@code TaxCode} is deemed to be in use if:
	 * <ul><li>a ProductType uses it</li>
	 * <li>a TaxValue uses it</li>
	 * <li>a Store uses it</li>
	 * <li>a Product uses it</li></ul>
	 * </p>
	 * 
	 * @return the list of uids of <code>TaxCode</code>s in use.
	 * @throws EpServiceException - in case of any errors
	 */
	public List<String> getTaxCodesInUse() throws EpServiceException {
		sanityCheck();
		List<String> taxCodesInUse = new ArrayList<String>();
		List<String> queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_CODES_WITH_PRODUCT_TYPE");
		taxCodesInUse.addAll(queryResponse);
		queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_CODES_WITH_VALUE");
		taxCodesInUse.addAll(queryResponse);
		queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_CODES_WITH_STORE");
		taxCodesInUse.addAll(queryResponse);
		queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_CODES_WITH_PRODUCT");
		taxCodesInUse.addAll(queryResponse);
		
		Map<String, String> distinctTaxCodesInUseMap = new HashMap<String, String>();
		for (String taxCode : taxCodesInUse) {
			if (distinctTaxCodesInUseMap.get(taxCode) == null) {
				distinctTaxCodesInUseMap.put(taxCode, taxCode);
			}
		}
		return Arrays.asList(distinctTaxCodesInUseMap.keySet().toArray(new String[0]));
	}
	
	/**
	 * Checks the given taxCode exists or not.
	 * 
	 * @param code the taxCode
	 * @return true if the given taxCode exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean taxCodeExists(final String code) throws EpServiceException {
		sanityCheck();
		final List<TaxCode> taxCodes = getPersistenceEngine().retrieveByNamedQuery("TAXCODE_FIND_BY_CODE", code);
		boolean taxCodeExists = false;
		if (!taxCodes.isEmpty()) {
			taxCodeExists = true;
		}
		return taxCodeExists;
	}

	/**
	 * Checks the given taxCode exists or not.
	 * 
	 * @param code the taxCode
	 * @return true if the given taxCode exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean taxCodeExists(final TaxCode code) throws EpServiceException {
		if (code.getCode() == null) {
			return false;
		}
		final TaxCode existingCode = this.findByCode(code.getCode());
		boolean codeExists = false;
		if (existingCode != null && existingCode.getUidPk() != code.getUidPk()) {
			codeExists = true;
		}
		return codeExists;

	}

	/**
	 * Find the code with the given code.
	 * 
	 * @param code the tax code.
	 * @return the tax code that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public TaxCode findByCode(final String code) throws EpServiceException {
		sanityCheck();
		if (code == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}

		final List<TaxCode> results = getPersistenceEngine().retrieveByNamedQuery("TAXCODE_FIND_BY_CODE", code);
		TaxCode taxCode = null;
		if (results.size() == 1) {
			taxCode = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate tax code exist -- " + code);
		}
		return taxCode;
	}

	/**
	 * Find the code with the given guid.
	 * 
	 * @param guid the guid
	 * @return the tax code that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public TaxCode findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve null guid.");
		}

		final List<TaxCode> results = getPersistenceEngine().retrieveByNamedQuery("TAXCODE_FIND_BY_GUID", guid);
		TaxCode taxCode = null;
		if (results.size() == 1) {
			taxCode = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate tax guid exist -- " + guid);
		}
		return taxCode;
	}

}
