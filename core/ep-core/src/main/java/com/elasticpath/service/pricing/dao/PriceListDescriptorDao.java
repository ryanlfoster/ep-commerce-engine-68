package com.elasticpath.service.pricing.dao;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Interface for PriceList related DAO operations.
 */
public interface PriceListDescriptorDao {

	/**
	 * Get the PriceListDescriptor instance with the given GUID. Returns null if not found.
	 *
	 * @param guid key of the PriceListDescriptor
	 * @return PriceListDescriptor if found, or null.
	 */
	PriceListDescriptor findByGuid(final String guid);
	/**
	 * Get the PriceListDescriptor instance with the given name. Returns null if not found.
	 *
	 * @param name name of the PriceListDescriptor to be found
	 * @return PriceListDescriptor if found, or null.
	 */
	PriceListDescriptor findByName(final String name);
	/**
	 * Remove the PriceListDescriptor instance.
	 *
	 * @param priceListDescriptor to remove
	 */
	void delete(final PriceListDescriptor priceListDescriptor);

	/**
	 * Add the new PriceListDescriptor.
	 *
	 * @param priceListDescriptor to save
	 * @return persisted PriceListDescriptor
	 */
	PriceListDescriptor add(final PriceListDescriptor priceListDescriptor);

	/**
	 * Update the PriceListDescriptor.
	 *
	 * @param priceListDescriptor to update
	 * @return persisted PriceListDescriptor
	 */
	PriceListDescriptor update(final PriceListDescriptor priceListDescriptor);
	
	/**
	 * Gets all price list descriptors.
	 *
	 * @param includeHidden whether to show also the hidden price lists descriptors.
	 * @return list of <code>PriceListDescriptor</code>
	 */
	List<PriceListDescriptor> getPriceListDescriptors(final boolean includeHidden);
	
	/**
	 * Gets list of price list descriptors by GUIDS.
	 * 
	 * @param priceListDescriptorsGuids - collection of requested price list descriptors guids 
	 * @return list of requested price list descriptors
	 */
	List<PriceListDescriptor> getPriceListDescriptors(final Collection<String> priceListDescriptorsGuids);
}
