package com.elasticpath.service.pricing.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.pricing.PriceAdjustment;

/**
 * DAO for persisting PriceAdjustments.
 */
public interface PriceAdjustmentDao {
	
	/**
	 * Delete the PriceAdjustment from data stores.
	 *  
	 * @param priceAdjustment to delete.
	 */
	void delete(PriceAdjustment priceAdjustment);
	
	/**
	 * Find a PriceAdjustment by price list.
	 *
	 * @param plGuid pricelist guid
	 * @return list of PriceAdjustments found, empty list if none found
	 */
	List<PriceAdjustment> findByPriceList(final String plGuid);
	
	/**
	 * Find all Price Adjustments within a Price List for given list of bundle constituent guids. 
	 *
	 * @param plGuid Price List guid
	 * @param bcList bundle constituent guids
	 * @return Set of PriceAdjustments
	 * 
	 * @deprecated Use {@link #findByPriceListAndBundleConstituentsAsMap(String, Collection)} instead.
	 */
	@Deprecated
	Collection <PriceAdjustment> findByPriceListBundleConstituents(String plGuid, Collection <String> bcList);

	/**
	 * Finds price adjustments for the list of bundle constituent GUIDs in the given
	 * price list and returns them as a map, keyed by bundle constituent GUID.
	 *
	 * @param plGuid the price list GUID
	 * @param bcList the list of bundle constituent GUIDs
	 * @return map of price adjustments, keyed by constituent GUID, or an empty map if none found
	 */
	Map<String, PriceAdjustment> findByPriceListAndBundleConstituentsAsMap(final String plGuid, final Collection<String> bcList);  
}
