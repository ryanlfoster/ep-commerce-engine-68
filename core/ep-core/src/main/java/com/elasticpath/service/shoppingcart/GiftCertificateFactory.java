/**
 * 
 */
package com.elasticpath.service.shoppingcart;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 * Creates a GiftCertificate.
 */
public interface GiftCertificateFactory {

	/**
	 * Creates a new {@code GiftCertificate} bean, sets its purchaser to the given {@code Customer} and generates a code for it
	 * that is guaranteed to be unique within the store with the given UID.
	 * @param customer the customer
	 * @param shoppingItem the LineItem containing GC data
	 * @param store the store
	 * @return the new GiftCertificate
	 */
	GiftCertificate createGiftCertificate(ShoppingItem shoppingItem, Customer customer, Store store);
}
