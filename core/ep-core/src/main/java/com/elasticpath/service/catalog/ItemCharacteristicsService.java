package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ItemCharacteristics;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;

/**
 * Service for retrieving item characteristics.
 */
public interface ItemCharacteristicsService {

	/**
	 * Gets the item characteristics.
	 *
	 * @param itemConfigurationId the item configuration id
	 * @return the item characteristics
	 */
	ItemCharacteristics getItemCharacteristics(ItemConfigurationId itemConfigurationId);
	
}
