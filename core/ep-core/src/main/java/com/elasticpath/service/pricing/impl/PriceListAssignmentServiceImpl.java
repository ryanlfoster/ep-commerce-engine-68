package com.elasticpath.service.pricing.impl;

import java.util.List;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.PriceUpdatedNotificationService;
import com.elasticpath.service.pricing.dao.PriceListAssignmentDao;

/** @see com.elasticpath.service.pricing.PriceListAssignmentService */
public class PriceListAssignmentServiceImpl implements
		PriceListAssignmentService {

	private PriceListAssignmentDao dao;
	
	private PriceUpdatedNotificationService priceUpdatedNotificationService;

	@Override
	public List<PriceListAssignment> list() {
		return dao.list(false);
	}
	
	@Override
	public List<PriceListAssignment> list(final boolean includeHidden) {
		return dao.list(includeHidden);
	}
	
	@Override
	public PriceListAssignment findByGuid(final String guid) {
		return dao.findByGuid(guid);
	}
	
	@Override
	public PriceListAssignment findByName(final String name) {
		return dao.findByName(name);
	}
	
	@Override
	public List<PriceListAssignment> listByCatalogAndCurrencyCode(final String catalogCode, 
			final String currencyCode) {
		return dao.listByCatalogAndCurrencyCode(catalogCode, currencyCode, false);
	}
	
	@Override
	public List<PriceListAssignment> listByCatalogAndCurrencyCode(final String catalogCode, 
			final String currencyCode, final boolean includeHidden) {
		return dao.listByCatalogAndCurrencyCode(catalogCode, currencyCode, includeHidden);
	}

	@Override
	public List<PriceListAssignment> listByCatalog(final Catalog catalog) {
		return dao.listByCatalog(catalog, false);	
	}
	
	@Override
	public List<PriceListAssignment> listByCatalog(final Catalog catalog, final boolean includeHidden) {
		return dao.listByCatalog(catalog, includeHidden);	
	}
	
	@Override
	public List<PriceListAssignment> listByCatalog(final String catalogCode) {
		return dao.listByCatalog(catalogCode, false);
	}
	
	@Override
	public List<PriceListAssignment> listByCatalog(final String catalogCode, final boolean includeHidden) {
		return dao.listByCatalog(catalogCode, includeHidden);
	}
	
	@Override
	public List<PriceListAssignment> listByPriceList(final String priceListGuid) {
		return dao.listByPriceList(priceListGuid);		
	}
	
	
	@Override
	public List<PriceListAssignment> listByCatalogAndPriceListNames(final String catalogName, 
			final String priceListName) {
		return dao.listByCatalogAndPriceListNames(catalogName, priceListName, false);
	}
	
	@Override
	public List<PriceListAssignment> listByCatalogAndPriceListNames(final String catalogName, 
			final String priceListName, final boolean includeHidden) {
		return dao.listByCatalogAndPriceListNames(catalogName, priceListName, includeHidden);
	}	
	
	@Override
	public PriceListAssignment saveOrUpdate(final PriceListAssignment plAssignment) {
		PriceListAssignment updatedAssignment = dao.saveOrUpdate(plAssignment);
		
		notifyPriceListAssignmentChanged(updatedAssignment);
		
		return updatedAssignment;
	}
	
	private void notifyPriceListAssignmentChanged(final PriceListAssignment assignment) {
		priceUpdatedNotificationService.notifyPriceUpdated(assignment);		
	}

	@Override
	public void delete(final PriceListAssignment plAssignment) {
		dao.delete(plAssignment);	
		notifyPriceListAssignmentChanged(plAssignment);
	}

	/**
	 * @param plAssignmentDao
	 * 		  <code>PriceListAssignmentDao</code> to use
	 */
	public void setPriceListAssignmentDao(
			final PriceListAssignmentDao plAssignmentDao) {
		this.dao = plAssignmentDao;
	}

	/**
	 *
	 * @param priceNotificationService the priceNotificationService to set
	 */
	public void setPriceUpdatedNotificationService(final PriceUpdatedNotificationService priceNotificationService) {
		this.priceUpdatedNotificationService = priceNotificationService;
	}

	@Override
	public List<String> listAssignedCatalogsCodes() {
		return dao.listAssignedCatalogsGuids();
	}

}
