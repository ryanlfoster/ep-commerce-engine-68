package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ItemConfigurationMemento;
import com.elasticpath.service.EpService;

/**
 * Provides CRUD services on {@link ItemConfigurationMemento}.
 */
public interface ItemConfigurationMementoService extends EpService {

	/**
	 * Save.
	 *
	 * @param memento the memento to be saved
	 */
	void saveItemConfigurationMemento(ItemConfigurationMemento memento);

	/**
	 * Item configuration memento exists.
	 *
	 * @param guid the guid
	 * @return true, if successful
	 */
	boolean itemConfigurationMementoExistsByGuid(String guid);

	/**
	 * Find the item configuration memento by GUID.
	 *
	 * @param guid the GUID
	 * @return the item configuration memento
	 */
	ItemConfigurationMemento findByGuid(String guid);
}
