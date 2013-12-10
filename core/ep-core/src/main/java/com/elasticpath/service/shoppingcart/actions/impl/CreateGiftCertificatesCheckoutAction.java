package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.shoppingcart.GiftCertificateFactory;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * CheckoutAction to create gift certificates that were purchased
 * in an order and store necessary values from the certificates on the
 * order skus.
 */
public class CreateGiftCertificatesCheckoutAction implements ReversibleCheckoutAction {

	private GiftCertificateService giftCertificateService;

	private GiftCertificateFactory giftCertificateFactory;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		final Customer customer = context.getCustomer();
		final ShoppingCart shoppingCart = context.getShoppingCart();
		Map<OrderSku, GiftCertificate> giftCertificateMap;
		giftCertificateMap = createAndPersistGiftCertificates(context.getOrder(), customer, shoppingCart.getStore());
		updateOrderSkus(context.getOrder(), giftCertificateMap);
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		for (final OrderSku orderSku : context.getOrder().getOrderSkus()) {
			if (GiftCertificate.KEY_PRODUCT_TYPE.equals(orderSku.getProductTypeName())) {
				rollbackGiftCertificate(orderSku);
			}
		}
	}
	
	/**
	 * Removes the given GiftCertificate from the DB and also resets the KEY_CODE and KEY_SENDER_EMAIL to null.
	 * @param orderSku The GiftCertificate to rollback.
	 */
	private void rollbackGiftCertificate(final OrderSku orderSku) {
		String gcCode = orderSku.getFieldValue(GiftCertificate.KEY_CODE);
		if (gcCode == null) {
			return;
		}
		GiftCertificate giftCertificate = giftCertificateService.findByGiftCertificateCode(gcCode);
		if (giftCertificate == null) {
			return;
		}
		giftCertificateService.removeGiftCertificate(giftCertificate.getUidPk());
		orderSku.setFieldValue(GiftCertificate.KEY_CODE, null);
		orderSku.setFieldValue(GiftCertificate.KEY_SENDER_EMAIL, null);
	}
	
	/**
	 * Records any data necessary from the gift certificates on the order skus. For example
	 * the key and sender email.
	 * 
	 * @param order order to get the skus from
	 * @param giftCertificateMap map the order sku to the gift certificate created from it
	 */
	protected void updateOrderSkus(final Order order, final Map<OrderSku, GiftCertificate> giftCertificateMap) {
		for (final OrderSku orderSku : order.getOrderSkus()) {
			for (final OrderSku giftCertificateOrderSku : giftCertificateMap.keySet()) {
				if (orderSku.equals(giftCertificateOrderSku)) {
					final GiftCertificate giftCertificate = giftCertificateMap.get(giftCertificateOrderSku);
					orderSku.setFieldValue(GiftCertificate.KEY_CODE, giftCertificate.getGiftCertificateCode());
					orderSku.setFieldValue(GiftCertificate.KEY_SENDER_EMAIL, giftCertificate.getPurchaser().getEmail());
				}
			}
		}
	}

	/**
	 * Creates and persists all gift certificates that were purchased as part of the given {@code Order}.
	 * Calls {{@link #createAndPersistGiftCertificate(ShoppingItem, Customer, Store)}.
	 * @param completedOrder the completed (persisted) order.
	 * @param customer the customer who's purchased the gift certificates
	 * @param store the store in which the gift certificates were purchased
	 * @return a map of {@code OrderSku}s to created {@code GiftCertificate}s
	 */
	protected Map<OrderSku, GiftCertificate> createAndPersistGiftCertificates(final Order completedOrder, 
			final Customer customer, final Store store) {
		final Map<OrderSku, GiftCertificate> giftCertificateMap = new HashMap<OrderSku, GiftCertificate>();
		for (final OrderShipment orderShipment : completedOrder.getAllShipments()) {
			for (final OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
				if (GiftCertificate.KEY_PRODUCT_TYPE.equals(orderSku.getProductTypeName())) {
					giftCertificateMap.put(orderSku, createAndPersistGiftCertificate(orderSku, customer, store, completedOrder));
				}
			}
		}
		return giftCertificateMap;
	}

	/**
	 * Creates a new gift certificate.
	 * @param shoppingItem the cartItem
	 * @param customer the customer (purchaser)
	 * @param store the store
	 * @param order the order
	 * @return the created GiftCertificate
	 */
	GiftCertificate createAndPersistGiftCertificate(final ShoppingItem shoppingItem, final Customer customer, final Store store, final Order order) {
		//Persist the created GC so that when the OrderSku is persisted and cascades the persist to its GC, the foreign
		//key constraint is satisfied. Once GC is removed from the OrderSku this will change.
		final GiftCertificate giftCertificate = giftCertificateFactory.createGiftCertificate(shoppingItem, customer, store);
		giftCertificate.setOrderGuid(order.getGuid()); //TODO: Set this in the GiftCertificateFactory?
		return giftCertificateService.add(giftCertificate);
	}

	/**
	 * Inject giftCertificateFactory.
	 * 
	 * @param giftCertificateFactory the giftCertificateFactory to set
	 */
	public void setGiftCertificateFactory(final GiftCertificateFactory giftCertificateFactory) {
		this.giftCertificateFactory = giftCertificateFactory;
	}

	/**
	 * Inject giftCertificateService.
	 * 
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}
}