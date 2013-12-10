package com.elasticpath.service.pricing;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.subscriptions.PaymentSchedule;

/**
 * Factory and helper for {@link PaymentSchedule}.
 */
public interface PaymentScheduleHelper {

	/**
	 * Create a {@link PaymentSchedule} based on the given sku. Implementations might decide not to create a new instance for
	 * every method call, and return the value from a pool.   
	 * @param productSku the sku to look up the payment schedule information from
	 * @return the payment schedule if the sku has a recurring price, <code>null</code> otherwise
	 */
	PaymentSchedule getPaymentSchedule(final ProductSku productSku);
	
	
	/**
	 * Create a {@link PaymentSchedule} based on the given shopping item recurring price.
	 *
	 * @param shoppingItemRecurringPrice the recurring price
	 * @return the payment schedule
	 */
	PaymentSchedule getPaymentSchedule(final ShoppingItemRecurringPrice shoppingItemRecurringPrice);

	/**
	 * Determines whether a product can have payment schedule.
	 * @param product the product to examine
	 * @return <code>true</code> iff the product is capabale of having payment schedules
	 */
	boolean isPaymentScheduleCapable(final Product product);
	
}
