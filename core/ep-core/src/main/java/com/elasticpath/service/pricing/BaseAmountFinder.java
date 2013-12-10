package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;

/**
 * <code>BaseAmountFinder</code> finds the base amounts related to different entities.
 */
public interface BaseAmountFinder {


	/**
	 * Get the list of base amounts related to the given sku in the given price lists. 
	 * @param productSku the sku
	 * @param plGuids list of price list GUIDs
	 * @param baseAmountDataSource the data source to be used to retrieve the base amounts
	 * @return list A list containing the related base amounts
	 */
	Collection<BaseAmount> getBaseAmounts(final ProductSku productSku, final List<String> plGuids, final BaseAmountDataSource baseAmountDataSource);
	
	/**
	 * Filters the given collection of base amounts according to the plGuid and object type. It not change the input collection.
	 * @param baseAmounts set of base amounts to filter
	 * @param plGuid price list descriptor guid.
	 * @param objectType product or sku
	 * @param guid product or sku guid
	 * 
	 * @return filtered collection.
	 */
	List<BaseAmount> filterBaseAmounts(final Collection<BaseAmount> baseAmounts, final String plGuid, final BaseAmountObjectType objectType,
			final String guid);

	
}
