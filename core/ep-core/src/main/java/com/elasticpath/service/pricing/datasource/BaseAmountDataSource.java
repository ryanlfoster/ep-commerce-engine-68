package com.elasticpath.service.pricing.datasource;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.pricing.BaseAmount;

/**
 * A source for base amount objects. 
 */
public interface BaseAmountDataSource {

	/**
	 * Gets all the base amounts for all objects (products, SKUs, etc) with
	 * a GUID in the objectGuids, and a price list GUID in the plGuids.
	 * 
	 * @param plGuids price list guids
	 * @param objectGuids product and his sku guids.
	 * @return the collections of BaseAmounts. 
	 */
	Collection<BaseAmount> getBaseAmounts(List<String> plGuids, List<String> objectGuids);	
}
