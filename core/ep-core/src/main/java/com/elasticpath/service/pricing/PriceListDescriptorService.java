package com.elasticpath.service.pricing;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Service for managing PriceListDescriptors.
 * Descriptors of price lists are managed separately from {@link com.elasticpath.domain.pricing.BaseAmount BaseAmounts}
 */
public interface PriceListDescriptorService {

	/**
	 * Add a new price list descriptor.
	 *
	 * @param priceListDescriptor the PriceListDescriptor be saved
	 * @return merged PriceListDescriptor from DB
	 */
	PriceListDescriptor add(final PriceListDescriptor priceListDescriptor);

	/**
	 * Update the given price list descriptor.
	 *
	 * @param priceListDescriptor the PriceListDescriptor to update
	 * @return merged PriceListDescriptor from DB
	 */
	PriceListDescriptor update(final PriceListDescriptor priceListDescriptor);
	
	/**
	 * Delete the price list descriptor.
	 *
	 * @param priceListDescriptor to delete
	 */
	void delete(final PriceListDescriptor priceListDescriptor);

	/**
	 * Get a price list descriptor.
	 *
	 * @param priceListDescriptorGuid GUID of the PriceListDescriptor
	 * @return the retrieved PriceListDescriptor
	 */
	PriceListDescriptor findByGuid(String priceListDescriptorGuid);

	/**
	 * Gets all the visible-only price list descriptors.
	 * To get all the price lists (including hidden ones), use getPriceListDescriptors(true).
	 *
	 * @return list of <code>PriceListDescriptor</code>
	 */
	List<PriceListDescriptor> getPriceListDescriptors();
	
	/**
	 * Gets all price list descriptors.
	 *
	 * @param includeHidden whether or not to return hidden price list descriptors as well in the results.
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

	/**
	 * Get the PriceListDescriptor instance with the given name. Returns null if not found.
	 *
	 * @param name name of the PriceListDescriptor to be found
	 * @return PriceListDescriptor if found, or null.
	 */
	PriceListDescriptor findByName(String name);
	
	/**
	 * Checks if Price List descriptor name is unique.
	 *
	 * @param guid - guid of the price list object
	 * @param name - name of the price list object  
	 * @return true if name is unique, false otherwise
	 */
	boolean isPriceListNameUnique(final String guid, final String name);
}
