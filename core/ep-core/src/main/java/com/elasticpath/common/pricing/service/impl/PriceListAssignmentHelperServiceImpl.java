package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.assembler.pricing.PriceListAssignmentsDtoAssembler;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.common.pricing.service.PriceListAssignmentHelperService;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Service that is used as facade to {@link PriceListAssignmentService} to minimize 
 * round trips to server by client. 
 */
public class PriceListAssignmentHelperServiceImpl extends AbstractEpServiceImpl implements PriceListAssignmentHelperService {
	
	private PriceListAssignmentService priceListAssignmentService;
	
	private PriceListAssignmentsDtoAssembler priceListAssignmentsDtoAssembler;

	private CatalogService catalogService;
	
	@Override
	public List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(
			final String catalogName, final String priceListName, final CmUser cmUser) {
		
		//only want the visible price list assignment DTOs
		List<PriceListAssignmentsDTO> priceListAssignments =  priceListAssignmentsDtoAssembler.assembleDto(
				priceListAssignmentService.listByCatalogAndPriceListNames(
						adaptToSearch(catalogName), 
						adaptToSearch(priceListName),
						false)
				);

		if (cmUser == null) {  
				return priceListAssignments;
		}
		
		return filterByCmUser(cmUser, priceListAssignments);
	}

	private List<PriceListAssignmentsDTO> filterByCmUser(final CmUser cmUser, final List<PriceListAssignmentsDTO> priceListAssignments) {
		Collection<String> priceLists = cmUser.getPriceLists();

		List<Catalog> catalogs = new ArrayList<Catalog>(catalogService.findAllCatalogs());
		List<String> catalogGuids = new ArrayList<String>();
		if (!cmUser.isAllCatalogsAccess()) {
			final Set<Catalog> userCatalogs = cmUser.getCatalogs();
			if (userCatalogs == null) {
				catalogs.clear();
			} else {
				catalogs.retainAll(userCatalogs);
			}
			for (Catalog catalog : catalogs) {
					catalogGuids.add(catalog.getGuid());
			}
		}
		List<PriceListAssignmentsDTO> priceListAssignmentsResultingList = new ArrayList<PriceListAssignmentsDTO>();

		for (PriceListAssignmentsDTO priceListAssignmentDTO : priceListAssignments) {
			if (
				(cmUser.isAllCatalogsAccess() 
						|| CollectionUtils.isNotEmpty(catalogs) && catalogGuids.contains(priceListAssignmentDTO.getCatalogGuid()))  
				&&
				(cmUser.isAllPriceListsAccess() 
						|| CollectionUtils.isNotEmpty(priceLists) && priceLists.contains(priceListAssignmentDTO.getPriceListGuid()))
				) { 
				priceListAssignmentsResultingList.add(priceListAssignmentDTO);
			}
		}
		return priceListAssignmentsResultingList;
	}
	
	@Override
	public void deletePriceListAssignments(final String guid) {
		priceListAssignmentService.delete(
			priceListAssignmentService.findByGuid(guid)
		);
	}
	
	@Override
	public List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(final String catalogCode) {
		return priceListAssignmentsDtoAssembler.assembleDto(
				priceListAssignmentService.listByCatalog(catalogCode)
				);
	}
	
	@Override
	public List<PriceListAssignmentsDTO> getPriceListAssignmentsDTOByPriceListGuid(final String priceListGuid) {		
		return priceListAssignmentsDtoAssembler.assembleDto(
				priceListAssignmentService.listByPriceList(priceListGuid));
	}

	
	@Override
	public void deletePriceListAssignmentsByCatalogCode(final String catalogCode) {
		List<PriceListAssignment> plas = priceListAssignmentService.listByCatalog(catalogCode, true);
		for (PriceListAssignment pla : plas) {
			deletePriceListAssignments(pla.getGuid());
		}		
	}
	
	
	
	private String adaptToSearch(final String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return "%" + str + "%"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	

	/**
	 * Set price list assignment service.
	 * @param priceListAssignmentService instance to set
	 */
	public void setPriceListAssignmentService(
			final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	/**
	 * Set DTO assembler.
	 * @param priceListAssignmentsDtoAssembler instance to set
	 */
	public void setPriceListAssignmentsDtoAssembler(
			final PriceListAssignmentsDtoAssembler priceListAssignmentsDtoAssembler) {
		this.priceListAssignmentsDtoAssembler = priceListAssignmentsDtoAssembler;
	}

	/**
	 * Set the catalog service factory to use.
	 * @param catalogService catalog service instance
	 */	
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@Override
	public List<PriceListAssignmentsDTO> getPriceListAssignmentsDTO(final Collection<Catalog> catalogList, final CmUser cmUser) {

		List<PriceListAssignmentsDTO> resultList = new LinkedList<PriceListAssignmentsDTO>();
		List<PriceListAssignmentsDTO> priceListAssignments;
		for (Catalog catalog : catalogList) {
			priceListAssignments = 
				priceListAssignmentsDtoAssembler.assembleDto(
					priceListAssignmentService.listByCatalog(catalog.getCode()));
			resultList.addAll(filterByCmUser(cmUser, priceListAssignments));
		}
		return resultList;
	}
	
	@Override
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}

	
	
}
