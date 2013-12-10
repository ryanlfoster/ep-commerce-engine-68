package com.elasticpath.service.catalog.impl;

import java.util.List;

import com.elasticpath.domain.catalog.ItemConfigurationMemento;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.ItemConfigurationMementoService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Provides CRUD operations for the {@link ItemConfigurationMemento}.
 */
public class ItemConfigurationMementoServiceImpl extends AbstractEpServiceImpl implements ItemConfigurationMementoService {
	private PersistenceEngine persistenceEngine;

	@Override
	public void saveItemConfigurationMemento(final ItemConfigurationMemento memento) {
		getPersistenceEngine().save(memento);
	}

	@Override
	public ItemConfigurationMemento findByGuid(final String guid) {
		List<Object> list = getPersistenceEngine().retrieveByNamedQuery("FIND_ITEM_CONFIGURATION_BY_GUID", guid);
		if (list.isEmpty()) {
			return null;
		}
		return (ItemConfigurationMemento) list.get(0);
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	@Override
	public boolean itemConfigurationMementoExistsByGuid(final String guid) {
		List<Object> list = getPersistenceEngine().retrieveByNamedQuery("FIND_ITEM_CONFIGURATION_UID_BY_GUID", guid);
		return !list.isEmpty();
	}

}
