package com.elasticpath.common.pricing.service;

import java.util.Collection;
import java.util.List;

import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Interface of Service that is used as facade to 
 * {@link com.elasticpath.service.pricing.PriceListAssignmentService} to minimize 
 * round trips to server by client. 
 */
public interface PriceListAssignmentHelperService {
	
	/**
	 * Returns the price list assignment service.
	 * 
	 * @return the price list assignment service
	 * */
	PriceListAssignmentService getPriceListAssignmentService();
	
	/**
	 * 
	 * Get the list of {@link PriceListAssignmentsDTO} by given catalog and price list names.
	 * Will not include the hidden price list assignments in the results.
	 * 
	 * @param catalogName Can be the whole or part of catalog name for search. Optional. 
	 * @param priceListName Can be the whole or part of price list name for search. Optional.
	 * @param cmUser - user by whose assigned privileges resulting list will be filtered.
	 * @return list of founded PriceListAssignmentsDTO.
	 */
	List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(String catalogName, String priceListName, CmUser cmUser);

	/**
	 * Lists the PriceListAssignments by catalog. Will exclude the hidden price list assignments.
	 * 
	 * @param catalogCode the Catalog that has assigned PriceListAssignments
	 * @return list of all matching PriceListAssignments
	 */	
	List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(String catalogCode);

	/**
	 * Lists the PriceListAssignments by catalog. Will exclude the hidden price list assignments.
	 * 
	 * @param catalogList the list of catalog for retrieving PLA DTO
	 * @param cmUser user by whose assigned privileges resulting list will be filtered
	 * @return list of all matching PriceListAssignments
	 */	
	List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(Collection<Catalog> catalogList, CmUser cmUser);

	/**
	 * Lists the PriceListAssignments by price list guid.
	 * 
	 * @param priceListGuid the price list guid, that has assigned PriceListAssignments
	 * @return list of all matching PriceListAssignments
	 */	
	List<PriceListAssignmentsDTO> getPriceListAssignmentsDTOByPriceListGuid(String priceListGuid);
	
	
	/**
	 * Delete price list assignments by given catalog code(guid).
	 * @param catalogCode given catalog code
	 */
	void deletePriceListAssignmentsByCatalogCode(String catalogCode);

	
	/**
	 * Delete price list assignment by given guid.
	 * @param guid given price list assignment guid
	 */
	void deletePriceListAssignments(String guid);


}
