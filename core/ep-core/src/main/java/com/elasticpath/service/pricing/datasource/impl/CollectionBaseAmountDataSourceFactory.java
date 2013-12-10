package com.elasticpath.service.pricing.datasource.impl;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * A {@link BaseAmountDataSourceFactory} that creates {@link CollectionBaseAmountDataSource}, based on 
 * a collection of {@link com.elasticpath.domain.pricing.PriceListDescriptor} GUIDs and a collection of 
 * object (i.e. product, sku, etc.) GUIDs. 
 */
public class CollectionBaseAmountDataSourceFactory implements BaseAmountDataSourceFactory {
	

	private BaseAmountDataSource preparedDataSource = null;

	private final List<String> plGuids; 
	private final List<String> objectGuids; 
	
	/**
	 * Constructs an object with the given lists of GUIDs. Upon the first call to {@link #createDataSource(BaseAmountDataSource)}, 
	 * the factory will create a {@link BaseAmountDataSource} that contains all the base amounts with the given GUIDs.
	 * @param plGuids list of price list descriptor GUIDs
	 * @param objectGuids list of product/SKU GUIDs
	 */
	public CollectionBaseAmountDataSourceFactory(final List<String> plGuids, final List<String> objectGuids) {
		this.plGuids = plGuids;
		this.objectGuids = objectGuids;
	}
	
	/**
	 * @return the list of PriceListDescriptor GUIDs for which it will fetch base amounts. 
	 */
	public List<String> getPlGuids() {
		return plGuids;
	}
	
	/**
	 * @return the list of object (products, SKUs, etc.) GUIDs for which it will fetch base amounts. 
	 */
	public List<String> getObjectGuids() {
		return objectGuids;
	}
	
	@Override
	public BaseAmountDataSource createDataSource(final BaseAmountDataSource backingDataSource) {
		if (preparedDataSource == null) {
			Collection<BaseAmount> baseAmounts = backingDataSource.getBaseAmounts(getPlGuids(), getObjectGuids());
			preparedDataSource = new CollectionBaseAmountDataSource(baseAmounts);
		}
		return preparedDataSource;
	}


}
