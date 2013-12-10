/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.persister;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.CustomerOrderingService;
import com.elasticpath.service.OrderConfigurationService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Persister allows to create and save into database catalog dependent domain objects.
 */
public class OrderTestPersister {

	private final BeanFactory beanFactory;

	private final ShoppingCartService shoppingCartService;

	private final TestDataPersisterFactory persisterFactory;

	public OrderTestPersister(final BeanFactory beanFactory, final TestDataPersisterFactory persisterFactory) {
		this.beanFactory = beanFactory;
		shoppingCartService = beanFactory.getBean(ContextIdNames.SHOPPING_CART_SERVICE);
		this.persisterFactory = persisterFactory;
	}

	/**
	 * Create an empty shopping cart in the DB.
	 *
	 * @param billingAddress the billing address
	 * @param shippingAddress the shipping address
	 * @param customerSession the customer session the cart belongs to
	 * @param shippingServiceLevel the shipping service level
	 * @param store the store the cart is for
	 * @return the persisted shopping cart
	 */
	public ShoppingCart persistEmptyShoppingCart(final Address billingAddress, final Address shippingAddress,
			final CustomerSession customerSession, final ShippingServiceLevel shippingServiceLevel, final Store store) {
		ShoppingCart shoppingCart = beanFactory.getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.initialize();
		shoppingCart.setBillingAddress(billingAddress);
		shoppingCart.setShippingAddress(shippingAddress);
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setShippingServiceLevelList(Arrays.asList(shippingServiceLevel));
		shoppingCart.setSelectedShippingServiceLevelUid(shippingServiceLevel.getUidPk());
		shoppingCart.setCurrency(Currency.getInstance(Locale.US));
		shoppingCart.setStore(store);
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	/**
	 * Create an order payment object.
	 *
	 * @param customer the customer whose payment this is
	 * @param creditCard the credit card to use for payment
	 * @return an order payment object
	 */
	public OrderPayment createOrderPayment(final Customer customer, final CustomerCreditCard creditCard) {
		OrderPayment orderPayment = beanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCardHolderName(creditCard.getCardHolderName());
		orderPayment.setCardType(creditCard.getCardType());
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail(customer.getEmail());
		orderPayment.setExpiryMonth(creditCard.getExpiryMonth());
		orderPayment.setExpiryYear(creditCard.getExpiryYear());
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		orderPayment.setCvv2Code(creditCard.getSecurityCode());
		orderPayment.setUnencryptedCardNumber(creditCard.getUnencryptedCardNumber());
		return orderPayment;
	}

	/**
	 * Create an order in the given store with the given skus.
	 *
	 * @param store the store that the order is for
	 * @param skus the list of skus to include in the order
	 * @return the order
	 */
	public Order createOrderWithSkus(final Store store, final ProductSku... skus) {
		return createOrderWithSkusQuantity(store, 1, skus);
	}

	/**
	 * Creates an Order containing the given quantity of each of the given Skus.
	 * @return the created Order
	 */
	public Order createOrderWithSkusQuantity(final Store store, final int quantity, final ProductSku... skus) {
		final Customer customer = persisterFactory.getStoreTestPersister().createDefaultCustomer(store);
		return createOrderForCustomerWithSkusQuantity(customer, store, quantity, skus);
	}

	/**
	 * Creates the Order containing the given quantity of each of the given Skus.
	 *
	 * @param customer the customer
	 * @param store the store
	 * @param quantity the quantity
	 * @param skus the skus
	 * @return the order
	 */
	public Order createOrderForCustomerWithSkusQuantity(final Customer customer, final Store store, final int quantity, final ProductSku... skus) {
		final CustomerSession customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		final Shopper shopper = customerSession.getShopper();

		final Map<ProductSku, Integer> skuQuantityMap = new HashMap<ProductSku, Integer>();
		for (ProductSku sku : skus) {
			skuQuantityMap.put(sku, quantity);
		}
		ShoppingCart shoppingCart = getOrderConfigurationService().createShoppingCart(store, customer, skuQuantityMap);
		final ShippingServiceLevel shippingServiceLevel = persisterFactory.getStoreTestPersister().persistDefaultShippingServiceLevel(store);
		shoppingCart.getShippingServiceLevelList().add(shippingServiceLevel);
		shoppingCart.setShippingAddress(customer.getAddresses().get(0));

		getOrderConfigurationService().selectCustomerAddressesToShoppingCart(
				shopper, customer.getAddresses().get(0).getStreet1(),customer.getAddresses().get(0).getStreet1());
		shoppingCart = getOrderConfigurationService().selectShippingServiceLevel(shoppingCart, store.getDefaultLocale(), shippingServiceLevel.getDisplayName(store.getDefaultLocale(), true));
		final OrderPayment orderPayment = getOrderConfigurationService().createOrderPayment(customer, customer.getCreditCards().get(0).getCardHolderName());

		getCustomerOrderingService().checkout(shoppingCart, orderPayment);

		// only one order should have been created by the checkout service
		return shoppingCart.getCompletedOrder();
	}

	OrderConfigurationService getOrderConfigurationService() {
		return beanFactory.getBean("orderConfigurationService");
	}

	CustomerOrderingService getCustomerOrderingService() {
		return beanFactory.getBean("customerOrderingService");
	}

}
