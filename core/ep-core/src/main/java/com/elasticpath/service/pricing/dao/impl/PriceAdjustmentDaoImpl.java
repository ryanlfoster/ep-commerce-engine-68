package com.elasticpath.service.pricing.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.pricing.dao.PriceAdjustmentDao;

/**
 * DAO operations for PriceAdjustment.
 */
public class PriceAdjustmentDaoImpl implements PriceAdjustmentDao {

	private static final String PLACEHOLDER_FOR_PRICELIST_GUID = "plGuid";
	private static final String PLACEHOLDER_FOR_LIST = "list";
	private PersistenceEngine persistenceEngine;

	@Override
	public void delete(final PriceAdjustment priceAdjustment) {
		getPersistenceEngine().delete(priceAdjustment);
		getPersistenceEngine().flush();
	}

	/**
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	@Deprecated
	public Collection<PriceAdjustment> findByPriceListBundleConstituents(final String plGuid, final Collection<String> bcList) {
		List<BundleConstituent> bundleConstituents = getPersistenceEngine().retrieveByNamedQueryWithList("PRICE_ADJUSTMENT_BY_PL_BCGUID", 
				PLACEHOLDER_FOR_LIST, bcList);
		if (bundleConstituents == null) {
			return Collections.emptyList();
		}
		List<PriceAdjustment> priceAdjusments = new ArrayList<PriceAdjustment>();
		for (BundleConstituent constituent : bundleConstituents) {
			for (PriceAdjustment adjustment : constituent.getPriceAdjustments()) {
				if (plGuid.equals(adjustment.getPriceListGuid())) {
					priceAdjusments.add(adjustment);
				}
			}
		}
		return priceAdjusments;
	}
	
	@Override
	public Map<String, PriceAdjustment> findByPriceListAndBundleConstituentsAsMap(final String plGuid, final Collection<String> bcList) {
		
		// The query will return an Object array with the following structure:
		//   objArray[0] == constituent GUID (String)
		//   objArray[1] == PriceAdjustment
		final List<Object[]> constituentGuidsAndAdjustments = getPersistenceEngine().retrieveByNamedQuery("PRICE_ADJUSTMENT_AND_BCGUID_BY_PL_BCGUID",
				createParameterMapForAdjustmentMapQuery(plGuid, bcList));
		
		if (constituentGuidsAndAdjustments == null) {
			return Collections.emptyMap();
		}
		
		return mapBundleConstituentGuidsToPriceAdjustments(constituentGuidsAndAdjustments);
	}

	/**
	 * Creates the map of bundle constituent GUIDS to price adjustments.
	 *
	 * @param constituentGuidsAndAdjustments the list of constituent GUID and adjustment pairs
	 * @return map of price adjustments, keyed by constituent GUID
	 */
	private Map<String, PriceAdjustment> mapBundleConstituentGuidsToPriceAdjustments(final List<Object[]> constituentGuidsAndAdjustments) {
		Map<String, PriceAdjustment> adjustmentMap = new HashMap<String, PriceAdjustment>();

		for (Object[] guidAndAdjustment : constituentGuidsAndAdjustments) {
			adjustmentMap.put((String) guidAndAdjustment[0], (PriceAdjustment) guidAndAdjustment[1]);
		}
		
		return adjustmentMap;
	}

	private Map<String, Object> createParameterMapForAdjustmentMapQuery(final String plGuid, final Collection<String> bcList) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put(PLACEHOLDER_FOR_PRICELIST_GUID, plGuid);
		parameterMap.put(PLACEHOLDER_FOR_LIST, bcList);
		return parameterMap;
	}


	@Override
	public List<PriceAdjustment> findByPriceList(final String plGuid) {
		return persistenceEngine.<PriceAdjustment>retrieveByNamedQuery("PRICE_ADJUSTMENT_BY_PL_GUID", plGuid);
	}
}
