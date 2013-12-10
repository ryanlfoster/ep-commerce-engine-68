/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc;

import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;

/**
 * Helper for constructing email properties.
 */
public interface OrderEmailPropertyHelper {

	/**
	 * Gets the {@link com.elasticpath.domain.misc.EmailProperties} instance with set properties.
	 * 
	 * @param order the order
	 * @return {@link com.elasticpath.domain.misc.EmailProperties}
	 */
	EmailProperties getOrderConfirmationEmailProperties(final Order order);

	/**
	 * Gets the {@link EmailProperties} with set props.
	 * 
	 * @param order the order
	 * @param orderShipment the order shipment
	 * @return {@link EmailProperties}
	 */
	EmailProperties getShipmentConfirmationEmailProperties(final Order order, final OrderShipment orderShipment);

	/**
	 * Constructs properties for failed shipment.
	 * 
	 * @param shipment OrderShipment
	 * @param errorMessage the error message describing the cause of failure
	 * @return EmailProperties
	 */
	EmailProperties getFailedShipmentPaymentEmailProperties(OrderShipment shipment, String errorMessage);
	
	/**
	 * Get email properties for order return email.
	 * @param orderReturn - the order return
	 * @return the email properties for order return email
	 */
	EmailProperties getOrderReturnEmailProperties(final OrderReturn orderReturn);

}