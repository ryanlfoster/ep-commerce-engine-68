package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.ItemConfiguration;

/**
 * Validates the item configuration.
 */
public interface ItemConfigurationValidator {

	/**
	 * Validate.
	 *
	 * @param itemConfiguration the item configuration
	 * @return the validation result
	 */
	ItemConfigurationValidationResult validate(ItemConfiguration itemConfiguration);
}
