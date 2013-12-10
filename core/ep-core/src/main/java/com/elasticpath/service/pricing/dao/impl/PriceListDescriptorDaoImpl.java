package com.elasticpath.service.pricing.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.PersistenceException;

import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.pricing.dao.PriceListDescriptorDao;

/**
 * Default implementation of PriceListDao.
 */
public class PriceListDescriptorDaoImpl implements PriceListDescriptorDao {

	private PersistenceEngine persistenceEngine;

	@Override
	public PriceListDescriptor findByGuid(final String guid) {
		List<PriceListDescriptor> descriptors =
				getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_BY_GUID", guid);
		if (!descriptors.isEmpty()) {
			return descriptors.get(0);
		}
		return null;
	}

	@Override
	public void delete(final PriceListDescriptor priceListDescriptor) {
		getPersistenceEngine().delete(priceListDescriptor);
	}

	@Override
	public PriceListDescriptor add(final PriceListDescriptor priceList) {
		try {
			return getPersistenceEngine().saveOrUpdate(priceList);
		} catch (PersistenceException e) {
			throw new EpPersistenceException("Save failed.", e);
		}
	}
	
	@Override
	public PriceListDescriptor update(final PriceListDescriptor priceList) {
		try {
			return getPersistenceEngine().saveOrUpdate(priceList);
		} catch (PersistenceException e) {
			throw new EpPersistenceException("Update failed.", e);
		}
	}

	@Override
	public List<PriceListDescriptor> getPriceListDescriptors(final boolean includeHidden) {
		
		List<PriceListDescriptor> priceListDescriptors;
		
		if (includeHidden) {
			priceListDescriptors = getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_ALL");
		} else {
			priceListDescriptors = getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_ALL_NON_HIDDEN");
		}

		if (priceListDescriptors.isEmpty()) {
			return Collections.emptyList();
		}

		return priceListDescriptors;
	}

	@Override
	public List<PriceListDescriptor> getPriceListDescriptors(final Collection<String> priceListDescriptorsGuids) {
		List<PriceListDescriptor> priceListDescriptors = getPersistenceEngine().
				retrieveByNamedQueryWithList("PRICE_LIST_DESCRIPTORS_BY_GUIDS",  "list", priceListDescriptorsGuids);

		if (priceListDescriptors.isEmpty()) {
			return Collections.emptyList();
		}

		return priceListDescriptors;
	}
	
	/**
	 * Sets the persistence engine to use.
	 *
	 * @param persistenceEngine The persistence engine.
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
	public PriceListDescriptor findByName(final String name) {
		List<PriceListDescriptor> descriptors =
			getPersistenceEngine().retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_BY_NAME", new Object[] { name }, 0, 1);
		if (!descriptors.isEmpty()) {
			return descriptors.get(0);
		}
		return null;
	}

}
