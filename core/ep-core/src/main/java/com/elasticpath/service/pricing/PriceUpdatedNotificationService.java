package com.elasticpath.service.pricing;

import com.elasticpath.domain.pricing.PriceListAssignment;

/**
 * A service that notifies changed price or price list assignment.
 */
public interface PriceUpdatedNotificationService {

	/**
	 * Notifies price update based on a changed {@link PriceListAssignment}.
	 * 
	 * @param assignment {@link PriceListAssignment}.
	 */
	void notifyPriceUpdated(PriceListAssignment assignment);

	/**
	 * Notifies price update based on a changed {@link BaseAmount}.
	 * 
	 * @param priceListDescriptorGuid  - the guid of the pricelist descriptor
	 * @param objectType - the type of the object associated with the base amount
	 * @param objectGuid - the guid of the object associated with this baseAmount
	 */
	void notifyPriceUpdated(final String priceListDescriptorGuid, final String objectType, final String  objectGuid);
}
