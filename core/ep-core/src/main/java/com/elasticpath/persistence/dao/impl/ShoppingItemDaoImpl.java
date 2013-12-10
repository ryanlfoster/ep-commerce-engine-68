/**
 * 
 */
package com.elasticpath.persistence.dao.impl;

import java.util.List;

import javax.persistence.PersistenceException;

import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.ShoppingItemDao;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * Provides JPA persistence implementation of {@code ShoppingItemDao}.
 */
public class ShoppingItemDaoImpl implements ShoppingItemDao {
	private PersistenceEngine persistenceEngine;

	private FetchPlanHelper fetchPlanHelper;

	private ShoppingItemLoadTuner shoppingItemLoadTunerDefault;

	@Override
	public ShoppingItem findByGuid(final String guid, final LoadTuner loadTuner) throws EpPersistenceException {
		if (loadTuner == null) {
			fetchPlanHelper.configureLoadTuner(shoppingItemLoadTunerDefault);
		} else {
			fetchPlanHelper.configureLoadTuner(loadTuner);
		}

		List<ShoppingItem> items = getPersistenceEngine().retrieveByNamedQuery("SHOPPING_ITEM_BY_GUID", guid);

		fetchPlanHelper.clearFetchPlan();

		if (!items.isEmpty()) {
			return items.get(0);
		}

		return null;
	}

	@Override
	public ShoppingItem saveOrUpdate(final ShoppingItem shoppingItem) throws EpPersistenceException {
		try {
			return getPersistenceEngine().saveOrUpdate(shoppingItem);
		} catch (PersistenceException e) {
			throw new EpPersistenceException("Save failed.", e);
		}
	}

	/**
	 * Setter for persistence engine.
	 * 
	 * @param persistenceEngine the persistenceEngine to set
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	/**
	 * @return the persistenceEngine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Setter for {@link FetchPlanHelper}.
	 * 
	 * @param fetchPlanHelper {@link FetchPlanHelper}
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}
	
	/**
	 * Setter for {@link ShoppingItemLoadTuner}.
	 * 
	 * @param shoppingItemLoadTunerDefault {@link ShoppingItemLoadTuner}
	 */
	public void setShoppingItemLoadTunerDefault(final ShoppingItemLoadTuner shoppingItemLoadTunerDefault) {
		this.shoppingItemLoadTunerDefault = shoppingItemLoadTunerDefault;
	}
}
