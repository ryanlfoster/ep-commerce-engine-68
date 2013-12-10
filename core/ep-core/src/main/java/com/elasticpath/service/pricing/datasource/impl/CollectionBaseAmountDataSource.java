package com.elasticpath.service.pricing.datasource.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;


/**
 * A {@link BaseAmountDataSource} that can be initialized with a collection of {@link BaseAmount}s. It will
 * return results based on the contents of the given collection. 
 */
public class CollectionBaseAmountDataSource implements BaseAmountDataSource {
	
	private final Collection<BaseAmount> baseAmounts;

	/**
	 * c'tor.
	 *
	 * @param baseAmounts the collection of base amounts. It will be used to answer the future queries. 
	 */
	public CollectionBaseAmountDataSource(final Collection<BaseAmount> baseAmounts) {
		this.baseAmounts = baseAmounts;
	}
	
	@Override
	public List<BaseAmount> getBaseAmounts(final List<String> plGuids, final List<String> objectGuids) {
		List<BaseAmount> result = new LinkedList<BaseAmount>();
		for (BaseAmount baseAmount : baseAmounts) {
			if (plGuids.contains(baseAmount.getPriceListDescriptorGuid()) && objectGuids.contains(baseAmount.getObjectGuid())) {
				result.add(baseAmount);
			}
		}
		return result;
	}

}
