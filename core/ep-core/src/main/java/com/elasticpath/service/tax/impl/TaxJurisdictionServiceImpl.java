/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.tax.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.tax.TaxJurisdictionExistException;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * The default implementation of <code>TaxJurisdictionService</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class TaxJurisdictionServiceImpl extends AbstractEpPersistenceServiceImpl implements TaxJurisdictionService {

	@Override
	public List<String> getCountryCodesInUse() {
		sanityCheck();

		return getPersistenceEngine().retrieveByNamedQuery("SELECT_COUNTRIES_IN_USE");
	}

	@Override
	public TaxJurisdiction add(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException {
		sanityCheck();

		if (regionCodeExists(taxJurisdiction)) {
			throw new TaxJurisdictionExistException("TaxJurisdiction with the same region code already exists with the same parent");
		}

		getPersistenceEngine().save(taxJurisdiction);
		clearTaxJurisdictionCache(taxJurisdiction);
		return taxJurisdiction;
	}

	/**
	 * Ensure when tax jurisdictions are changed that any cached versions are cleared out.
	 * 
	 * @param taxJurisdiction the tax jurisdiction to clear from the cache.
	 */
	protected void clearTaxJurisdictionCache(final TaxJurisdiction taxJurisdiction) {
		if (getPersistenceEngine().isCacheEnabled()) {
			getPersistenceEngine().evictObjectFromCache(taxJurisdiction);
			for (TaxCategory category : taxJurisdiction.getTaxCategorySet()) {
				getPersistenceEngine().evictObjectFromCache(category);
			}
		}
	}

	@Override
	public TaxJurisdiction update(final TaxJurisdiction taxJurisdiction) throws TaxJurisdictionExistException {
		sanityCheck();

		if (regionCodeExists(taxJurisdiction)) {
			throw new TaxJurisdictionExistException("TaxJurisdiction with the same region code already exists with the same parent");
		}
		TaxJurisdiction updatedTaxJurisdiction = getPersistenceEngine().merge(taxJurisdiction);
		clearTaxJurisdictionCache(updatedTaxJurisdiction);
		return updatedTaxJurisdiction;
	}

	private boolean regionCodeExists(final TaxJurisdiction taxJurisdiction) throws EpServiceException {
		if (taxJurisdiction.getRegionCode() == null) {
			throw new EpServiceException("Region code not set.");
		}

		List<TaxJurisdiction> results = getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_SELECT_BY_COUNTRY_CODE",
				taxJurisdiction.getRegionCode());

		boolean regionExists = false;
		if (results.size() > 1) {
			throw new EpServiceException("Inconsistent date: multiple taxJurisdiction with region \"" + taxJurisdiction.getRegionCode()
					+ "\" exists.");
		} else if (results.size() == 1 && taxJurisdiction.getUidPk() != results.get(0).getUidPk()) {
			regionExists = true;
		}
		return regionExists;
	}

	@Override
	public void remove(final TaxJurisdiction taxJurisdiction) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(taxJurisdiction);
	}

	@Override
	public List<TaxJurisdiction> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_SELECT_ALL");
	}

	@Override
	public TaxJurisdiction load(final long taxJurisdictionUid) throws EpServiceException {
		sanityCheck();
		TaxJurisdiction taxJurisdiction = null;
		if (taxJurisdictionUid <= 0) {
			taxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		} else {
			taxJurisdiction = getPersistentBeanFinder().load(ContextIdNames.TAX_JURISDICTION, taxJurisdictionUid);
		}
		return taxJurisdiction;
	}

	@Override
	public TaxJurisdiction get(final long taxJurisdictionUid) throws EpServiceException {
		sanityCheck();
		TaxJurisdiction taxJurisdiction = null;
		if (taxJurisdictionUid <= 0) {
			taxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		} else {
			taxJurisdiction = getPersistentBeanFinder().get(ContextIdNames.TAX_JURISDICTION, taxJurisdictionUid);
		}

		return taxJurisdiction;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public Collection<Long> getTaxJurisdictionsInUse() throws EpServiceException {
		sanityCheck();
		Set<Long> taxJurisdictionsInUse = new HashSet<Long>();
		List<Long> queryResponse = getPersistenceEngine().retrieveByNamedQuery("TAX_JURISDICTION_UIDS_WITH_STORE");
		taxJurisdictionsInUse.addAll(queryResponse);
		return taxJurisdictionsInUse;
	}

	@Override
	public TaxJurisdiction retrieveEnabledInStoreTaxJurisdiction(final Store store, final Address address) throws EpServiceException {
		sanityCheck();

		if (store == null || address == null) {
			throw new EpServiceException("Tax calculation address or store code are not set.");
		}

		// Make sure the match starts from the right country since the region codes are not unique.
		// For example, how to distinguish "CA" (Canada) from "CA" (California).
		List<TaxJurisdiction> taxJurisdictions = getPersistenceEngine().retrieveByNamedQuery("TAX_JURISDICTIONS_FROM_STORE_BY_COUNTRY_CODE",
				store.getCode(),
				address.getCountry());

		TaxJurisdiction foundTaxJurisdiction = null;
		if (taxJurisdictions.size() > 1) {
			throw new EpServiceException("Invalid taxJurisdiction configuration.");
		} else if (taxJurisdictions.size() == 1) {
			foundTaxJurisdiction = filterTaxJurisdictonByRegion(address, taxJurisdictions.get(0));
		}

		return foundTaxJurisdiction;
	}

	/**
	 * Given a Tax Jurisdiction, return a new instance that only contains
	 * tax categories that apply to the region that the given address belongs to.
	 * 
	 * @param address the address whose region to filter by
	 * @param taxJurisdiction the persisted tax jurisdiction
	 * @return a filtered <code>TaxJurisdiction</code>
	 */
	protected TaxJurisdiction filterTaxJurisdictonByRegion(final Address address, final TaxJurisdiction taxJurisdiction) {

		TaxJurisdiction foundTaxJurisdiction = getBean(ContextIdNames.TAX_JURISDICTION);
		foundTaxJurisdiction.setUidPk(taxJurisdiction.getUidPk());
		foundTaxJurisdiction.setRegionCode(taxJurisdiction.getRegionCode());
		foundTaxJurisdiction.setPriceCalculationMethod(taxJurisdiction.getPriceCalculationMethod());

		Set<TaxCategory> categorySet = taxJurisdiction.getTaxCategorySet();

		if (categorySet.isEmpty()) {
			// FIXME: or return null??
			throw new EpServiceException("Invalid taxJurisdiction configuration.");
		}

		for (TaxCategoryTypeEnum categoryTypeEnum : TaxCategoryTypeEnum.values()) {
			String region = null;

			switch (categoryTypeEnum) {
			case FIELD_MATCH_COUNTRY:
				region = address.getCountry();
				break;
			case FIELD_MATCH_SUBCOUNTRY:
				region = address.getSubCountry();
				break;
			case FIELD_MATCH_CITY:
				region = address.getCity();
				break;
			case FIELD_MATCH_ZIP_POSTAL_CODE:
				region = address.getZipOrPostalCode();
				break;
			default:
				break;
			}

			if (region != null && !"".equals(region)) {
				for (TaxCategory taxCategory : categorySet) {
					if (taxCategory.getFieldMatchType() == categoryTypeEnum) {
						TaxRegion foundTaxRegion = taxCategory.getTaxRegion(region);
						if (foundTaxRegion != null) {
							TaxCategory foundTaxCategory = getBean(ContextIdNames.TAX_CATEGORY);
							foundTaxCategory.setName(taxCategory.getName());
							foundTaxCategory.setLocalizedProperties(taxCategory.getLocalizedProperties());
							foundTaxCategory.setFieldMatchType(taxCategory.getFieldMatchType());

							foundTaxCategory.addTaxRegion(foundTaxRegion);
							foundTaxJurisdiction.getTaxCategorySet().add(foundTaxCategory);
						}
					}
				}
			}
		}

		// Ensure the JPA cache is not confused by the new TaxJurisdiction object
		if (foundTaxJurisdiction != null) {
			this.clearTaxJurisdictionCache(foundTaxJurisdiction);
		}
		return foundTaxJurisdiction;
	}
	
	@Override
    public List<TaxJurisdiction> findByUids(final Collection<Long> taxJurisdictionUids) {
        sanityCheck();
        if (taxJurisdictionUids == null || taxJurisdictionUids.isEmpty()) {
        		return new ArrayList<TaxJurisdiction>();
        }
        return getPersistenceEngine().<TaxJurisdiction, Long>retrieveByNamedQueryWithList("TAX_JURISDICTION_BY_UIDS", "list", taxJurisdictionUids);
    }

	@Override
    public List<TaxJurisdiction> findByGuids(final Collection<String> taxJurisdictionUids) {
        sanityCheck();
        if (taxJurisdictionUids == null || taxJurisdictionUids.isEmpty()) {
        		return new ArrayList<TaxJurisdiction>();
        }
        return getPersistenceEngine().<TaxJurisdiction, String>retrieveByNamedQueryWithList("TAX_JURISDICTION_BY_GUIDS", "list", taxJurisdictionUids);
    }
    
	@Override
	public TaxJurisdiction findByGuid(final String guid) throws EpServiceException {
		final List<TaxJurisdiction> jurisdictions = getPersistenceEngine().retrieveByNamedQuery("TAXJURISDICTION_FIND_BY_GUID", guid);
		final int size = jurisdictions.size();
		if (size > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		if (size == 0) {
			return null;
		}
		return jurisdictions.get(0);
	}

}
