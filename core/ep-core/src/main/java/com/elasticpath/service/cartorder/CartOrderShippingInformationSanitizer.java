package com.elasticpath.service.cartorder;

import com.elasticpath.domain.cartorder.CartOrder;

/**
 * Clears invalid shipping information on a {@link CartOrder}.
 */
public interface CartOrderShippingInformationSanitizer {
	
	/**
	 * Clears invalid shipping address and shipping service level GUIDs on a {@link CartOrder}.
	 *
	 * @param storeCode the store code
	 * @param cartOrder the cart order
	 * @return the cart order
	 */
	boolean sanitize(String storeCode, CartOrder cartOrder);

}
