package com.elasticpath.service.cartorder;

import com.elasticpath.domain.cartorder.CartOrder;

/**
 * The Interface CartOrderPopulationStrategy.
 * It is used by <code>CartOrderService</code> to decide how to fill the billing address GUID and payment method GUID
 * when a <code>CartOrder</code> is created.
 */
public interface CartOrderPopulationStrategy {

	/**
	 * Creates the cart order.
	 *
	 * @param cartGuid the cart guid
	 * @return the cart order
	 */
	CartOrder createCartOrder(final String cartGuid);
}
