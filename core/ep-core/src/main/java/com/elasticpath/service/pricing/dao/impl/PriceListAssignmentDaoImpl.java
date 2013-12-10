package com.elasticpath.service.pricing.dao.impl;

import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.pricing.dao.PriceListAssignmentDao;

/** @see com.elasticpath.service.pricing.dao.PriceListAssignmentDao */
public class PriceListAssignmentDaoImpl implements PriceListAssignmentDao {

	private PersistenceEngine persistenceEngine;
	
	@Override
	public PriceListAssignment findByGuid(final String guid) {
		List<PriceListAssignment> priceListAssignments =
				getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_ASSIGNMENTS_FIND_BY_GUID", guid);
		final int size = priceListAssignments.size();
		if (size > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		if (size == 0) {
			return null;
		}
		return priceListAssignments.get(0);
	}
	
	@Override
	public PriceListAssignment findByName(final String name) {
		List<PriceListAssignment> priceListAssignments =
				getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_ASSIGNMENTS_FIND_BY_NAME", name);
		final int size = priceListAssignments.size();
		if (size == 0) {
			return null;
		}
		return priceListAssignments.get(0);
	}
	

	@Override
	public List<PriceListAssignment> list(final boolean includeHidden) {
		String queryName;
		
		if (includeHidden) {
			queryName = "PRICE_LIST_ASSIGNMENTS_ALL";
		} else {
			queryName = "PRICE_LIST_ASSIGNMENTS_ALL_VISIBLE_ONLY";
		}
		
		List<PriceListAssignment> priceListAssignments = getPersistenceEngine()
				.retrieveByNamedQuery(queryName);

		if (priceListAssignments.isEmpty()) {
			return Collections.emptyList();
		}

		return priceListAssignments;
	}

	@Override
	public List<PriceListAssignment> listByCatalogAndCurrencyCode(final String catalogCode, 
			final String currencyCode, final boolean includeHidden) {
		String queryName;
		
		if (includeHidden) {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_CURRENCY";
		} else {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_CURRENCY_VISIBLE_ONLY";
		}

		List<PriceListAssignment> priceListAssignments = getPersistenceEngine().retrieveByNamedQuery(queryName, catalogCode, currencyCode);
		if (priceListAssignments.isEmpty()) {
			return Collections.emptyList();
		}

		return priceListAssignments;
	}
	
	
	@Override
	public List<PriceListAssignment> listByCatalogAndPriceListNames(
			final String catalogName, final String priceListName, final boolean includeHidden) {
		String queryName;
		
		if (includeHidden) {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_PRICELIST";
		} else {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_PRICELIST_VISIBLE_ONLY";
		}

		List<PriceListAssignment> priceListAssignments = getPersistenceEngine().retrieveByNamedQuery(queryName, catalogName, priceListName);
		if (priceListAssignments.isEmpty()) {
			return Collections.emptyList();
		}

		return priceListAssignments;

	}
	
	@Override
	public List<PriceListAssignment> listByCatalog(
			final String catalogCode, final boolean includeHidden) {
		String queryName;
		
		if (includeHidden) {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_CODE";
		} else {
			queryName = "PRICE_LIST_ASSIGNMENTS_CATALOG_CODE_VISIBLE_ONLY";
		}

		List<PriceListAssignment> priceListAssignments = getPersistenceEngine().retrieveByNamedQuery(queryName, catalogCode);
		if (priceListAssignments.isEmpty()) {
				return Collections.emptyList();
		}
		return priceListAssignments;
	}
	
	@Override
	public List<PriceListAssignment> listByPriceList(final String priceListGuid) {
		List<PriceListAssignment> priceListAssignments = getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_ASSIGNMENTS_PRICE_LIST_GUID",
				priceListGuid);
		if (priceListAssignments.isEmpty()) {
				return Collections.emptyList();
		}
		return priceListAssignments;		
	}
	

	@Override
	public List<PriceListAssignment> listByCatalog(final Catalog catalog, final boolean includeHidden) {
		return listByCatalog(catalog.getCode(), includeHidden);
	}	
	
	@Override
	public PriceListAssignment saveOrUpdate(final PriceListAssignment plAssignment) {
		try {
			return getPersistenceEngine().saveOrUpdate(plAssignment);
		} catch (PersistenceException e) {
			throw new EpPersistenceException("Persisting " + plAssignment
					+ " failed.", e);
		}
	}
	
	/**
	 * Remove the {@link PriceListAssignment} instance.
	 *
	 * @param plAssignment to remove
	 */
	public void delete(final PriceListAssignment plAssignment) {
		getPersistenceEngine().delete(plAssignment);
	}

	/**
	 * Sets the persistence engine to use.
	 * 
	 * @param persistenceEngine
	 *            The persistence engine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * Gets the persistence engine.
	 * 
	 * @return The persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	public List<String> listAssignedCatalogsGuids() {		
		return getPersistenceEngine().retrieveByNamedQuery("CATALOG_GUIDS_THAT_HAVE_PLAS");
	}
}
