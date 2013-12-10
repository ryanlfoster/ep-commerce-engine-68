package com.elasticpath.common.pricing.service;

import com.elasticpath.common.dto.pricing.PriceAdjustmentDto;

/**
 * Price adjustment dto service.
 */
public interface PriceAdjustmentDtoService {
	/**
	 * Finds price adjustment by price list guid and constituent guid.
	 * 
	 * @param plGuid price list guid.
	 * @param constituentGuid constituent guid.
	 * @return {@link PriceAdjustmentDto}.
	 */
	PriceAdjustmentDto findPriceAdjustment(String plGuid, String constituentGuid);

	/**
	 * Adds or updates price adjustment based on the {@link PriceAdjustmentDto}.
	 * 
	 * @param priceAdjustmentDto {@link PriceAdjustmentDto}.
	 */
	void addOrUpdatePriceAdjustment(PriceAdjustmentDto priceAdjustmentDto);

	/**
	 * Delete price adjustment based on {@link PriceAdjustmentDto}.
	 * 
	 * @param priceAdjustmentDto {@link PriceAdjustmentDto}.
	 */
	void deletePriceAdjustment(PriceAdjustmentDto priceAdjustmentDto);
}
