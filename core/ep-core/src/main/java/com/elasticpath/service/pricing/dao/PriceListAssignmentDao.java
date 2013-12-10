package com.elasticpath.service.pricing.dao;

import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;

/**
 * Interface for PriceListAssignment related DAO operations. 
 */
public interface PriceListAssignmentDao {

	/**
	 * Saves or updates given {@link PriceListAssignment}.
	 * 
	 * @param plAssignment PriceListAssignment to persist 
	 * @return persisted PriceListAssignment
	 */
	PriceListAssignment saveOrUpdate(PriceListAssignment plAssignment);
	
	/**
	 * @param includeHidden To include hidden price list assignments or not
	 * @return list with all existing PriceListAssignments
	 */
	List<PriceListAssignment> list(boolean includeHidden);
	
	/**
	 * Lists the PriceListAssignments by currency code and catalog.
	 * 
	 * @param catalogCode the Catalog that has assigned PriceListAssignments
	 * @param currencyCode currency of PriceListDescriptors that 
	 * 		  are assigned to PriceListAssignments 
	 * @param includeHidden To include hidden price list assignments or not
	 * @return list of all matching PriceListDescriptors
	 */
	List<PriceListAssignment> listByCatalogAndCurrencyCode(String catalogCode, String currencyCode,
			boolean includeHidden);

	/**
	 * Lists the PriceListAssignments by partial catalog and price list names.
	 * 
	 * @param catalogName part or whole catalog's name. Optional. 
	 * @param priceListName part or whole catalog's name. Optional.
	 * @param includeHidden To include hidden price list assignments or not 
	 * @return list of all matching PriceListDescriptors
	 */	
	List<PriceListAssignment> listByCatalogAndPriceListNames(
			String catalogName, String priceListName, boolean includeHidden);

	/**
	 * Lists the PriceListAssignments by catalog.
	 * 
	 * @param catalog the Catalog that has assigned PriceListAssignments
	 * @param includeHidden To include hidden price list assignments or not
	 * @return list of all matching PriceListAssignments
	 */
	List<PriceListAssignment> listByCatalog(Catalog catalog, boolean includeHidden);
	
	/**
	 * Lists the PriceListAssignments by catalog.
	 * 
	 * @param catalogCode the Catalog that has assigned PriceListAssignments
	 * @param includeHidden To include hidden price list assignments or not
	 * @return list of all matching PriceListAssignments
	 */
	List<PriceListAssignment> listByCatalog(String catalogCode, boolean includeHidden);
	
	
	/**
	 * Lists the PriceListAssignments by price list guid.
	 * 
	 * @param priceListGuid the price list that has assigned PriceListAssignments
	 * @return list of all matching PriceListAssignments
	 */	
	List<PriceListAssignment> listByPriceList(String priceListGuid);
	

	/**
	 * Remove the {@link PriceListAssignment} instance.
	 *
	 * @param plAssignment to remove
	 */
	void delete(PriceListAssignment plAssignment);
	
	/**
	 * Find {@link PriceListAssignment} by guid.
	 * @param guid given guid
	 * @return instance of {@link PriceListAssignment} if it found, otherwise null.
	 */
	PriceListAssignment findByGuid(String guid);
	
	/**
	 * Find {@link PriceListAssignment} by name.
	 * @param name given name
	 * @return instance of {@link PriceListAssignment} if it found, otherwise null.
	 */
	PriceListAssignment findByName(String name);

	/**
	 * @return list of catalog guids that have PLAs (both hidden and non-hidden) assigned
	 */
	List<String> listAssignedCatalogsGuids();
	
}
