package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Client facing service API for managing Price Lists (PriceListDescriptor & BaseAmount).
 * This service provides a facade for the core services for PriceListDescriptor and BaseAmount. 
 * Works with DTOs instead of domain objects, which can be common across multiple clients.
 */
public interface PriceListService {

	/**
	 * Apply the changes (update/add/delete) to BaseAmounts in a BaseAmountChangeSet.
	 * 
	 * @param changeSet the container for a set of BaseAmount changes
	 * @throws EpServiceException on error
	 */
	void modifyBaseAmountChangeSet(ChangeSetObjects <BaseAmountDTO> changeSet) throws EpServiceException;

	/**
	 * Get the full list of price list descriptors in the system.
	 * 
	 * @param includeHidden Whether to get the hidden price lists as well.
	 * @return Collection of PriceListDescriptors found, or empty Collection if none
	 */
	Collection <PriceListDescriptorDTO> getPriceListDescriptors(boolean includeHidden);
	
	/**
	 * Get a PriceListDescriptorDTO object for a PriceListDescriptor by GUID.
	 * 
	 * @param pldGuid GUID identifier of a PriceListDescriptor 
	 * @return PriceListDescriptorDTO for the PriceListDescriptor with the giving GUID
	 */
	PriceListDescriptorDTO getPriceListDescriptor(String pldGuid);

	/**
	 * Get a PriceListDescriptorDTO object for a PriceListDescriptor by name.
	 * 
	 * @param priceListDescriptorName a name of a PriceListDescriptor 
	 * @return PriceListDescriptorDTO for the PriceListDescriptor with the giving name
	 */
	PriceListDescriptorDTO getPriceListDescriptorByName(String priceListDescriptorName);

	/**
	 * Get a collection of BaseAmountDTOs matching the BaseAmountFilter criteria.
	 * 
	 * @param filter criteria for searching BaseAmounts.
	 * @return Collection of BaseAmountDTOs
	 */
	Collection <BaseAmountDTO> getBaseAmounts(BaseAmountFilter filter);
	
	/**
	 * Get a collection of BaseAmountDTOs matching the BaseAmountFilterExt criteria.
	 * 
	 * @param baseAmountFilterExt criteria for searching BaseAmounts.
	 * @return Collection of BaseAmountDTOs
	 */
	Collection<BaseAmountDTO> getBaseAmountsExt(BaseAmountFilterExt baseAmountFilterExt);

	/**
	 * Get a collection of BaseAmountDTOs matching the BaseAmountFilterExt criteria.
	 * If a product object type is passed in filters it is used to add base ammount of all the sku's
	 * associated with this product. (the service uses product guid for an exact match to find skus).
	 * 
	 * @param baseAmountFilterExt criteria for searching BaseAmounts.
	 * @return Collection of BaseAmountDTOs
	 */
	Collection<BaseAmountDTO> getBaseAmountsExtWithSkus(BaseAmountFilterExt baseAmountFilterExt);
	
	
	/**
	 * Get a collection of BaseAmountDTOs with extended information 
	 * for given price list and locale.
	 * 
	 * @param priceListGuid criteria for searching BaseAmounts.
	 * @param locale locale
	 * @return Collection of BaseAmountDTOs
	 */
	Collection <BaseAmountDTO> getBaseAmounts(String priceListGuid, Locale locale);
	
	/**
	 * Get a BaseAmountDTO by unique GUID.
	 * 
	 * @param baGuid the GUID of the BaseAmount
	 * @return BaseAmountDTO corresponding to the stored BaseAmount
	 */
	BaseAmountDTO getBaseAmount(String baGuid);
	
	/**
	 * Save or update a PriceListDescritor based on data in the PriceListDescriptorDTO.
	 * 
	 * @param pldDTO the DTO containing PriceListDescriptor data.
	 * @return the saved PriceListDescriptorDTO object.
	 */
	PriceListDescriptorDTO saveOrUpdate(PriceListDescriptorDTO pldDTO);
	
	/**
	 * Delete given PriceListDescritor and related base amounts, 
	 * if price list has no assignments. 
	 * @param pldDTO the DTO containing PriceListDescriptor data.
	 */
	void delete(PriceListDescriptorDTO pldDTO);
	
	/**
	 * Lists all PriceListDescriptors that are connected to the specified Catalog through PriceListAssignments.
	 * Does not look through hidden price list assignments for the catalog specified
	 * 
	 * Use listByCatalog(Catalog, boolean) to specify whether or not to include price list descriptors that are
	 * within hidden price list assignments.
	 * 
	 * @param catalog catalog that is searched for price list descriptors
	 * @return list of the found price list descriptor DTO objects
	 */
	List<PriceListDescriptorDTO> listByCatalog(Catalog catalog);
	
	/**
	 * Lists all PriceListDescriptors that are connected to the specified Catalog through PriceListAssignments.
	 * @param catalog catalog that is searched for price list descriptors
	 * @param includeHidden whether to look through hidden price list assignments for the price list descriptors
	 * @return list of the found price list descriptor DTO objects
	 */
	List<PriceListDescriptorDTO> listByCatalog(final Catalog catalog, final boolean includeHidden);

	/**
	 * Gets list of price list descriptors by GUIDS.
	 * 
	 * @param priceListDescriptorsGuids - collection of requested price list descriptors guids 
	 * @return list of requested price list descriptors as DTO objects
	 */
	List<PriceListDescriptorDTO> getPriceListDescriptors(final Collection<String> priceListDescriptorsGuids);

	/**
	 * Checks if Price List descriptor name is unique.
	 *
	 * @param guid - guid of the price list object
	 * @param name - name of the price list object  
	 * @return true if name is unique, false otherwise
	 */
	boolean isPriceListNameUnique(final String guid, final String name);

	/**
	 * Get a collection of BaseAmountDTOs matching the BaseAmountFilterExt criteria.
	 * 
	 * @param baseAmountFilterExt criteria for searching BaseAmounts.
	 * @param exactMatch true, if the object guid should be matched exactly
	 * @return Collection of BaseAmountDTOs
	 */
	Collection<BaseAmountDTO> getBaseAmountsExt(BaseAmountFilterExt baseAmountFilterExt, boolean exactMatch);
	
	/**
	 * Returns the Price List Descriptor Service.
	 * @return the price list descriptor service
	 * */
	PriceListDescriptorService getPriceListDescriptorService();

	/**
	 * Returns the PLA Service.
	 * @return the pla service
	 * */
	PriceListAssignmentService getPriceListAssignmentService();

	/**
	 * @param priceListAssignmentService <code>PriceListAssignmentService</code> to use
	 */
	void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService);
	
}
