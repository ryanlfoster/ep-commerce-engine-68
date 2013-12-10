package com.elasticpath.test.factory;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * A factory for producing properly constructed {@link CustomerSession}s for use
 * in tests.
 *
 * The intent is to keep all the {@link CustomerSession} creation code in one
 * place so that changes in it's instantiation can be fixed all at once.
 */
public final class TestShopperFactory {

	/**
	 * Default private constructor.
	 */
	private TestShopperFactory() {

	}

	/**
	 * From: http://en.wikipedia.org/wiki/Singleton_pattern#
	 * The_solution_of_Bill_Pugh SingletonHolder is loaded on the first
	 * execution of Singleton.getInstance() or the first access to
	 * SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		public static final TestShopperFactory TEST_SHOPPER_FACTORY = new TestShopperFactory(); // NOPMD
	}

	/**
	 * Gets an instance of the factory for use. (Bill Pugh's singleton.)
	 *
	 * @return {@link TestShopperFactory}.
	 */
	public static TestShopperFactory getInstance() {
		return SingletonHolder.TEST_SHOPPER_FACTORY;
	}

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}.
	 *
	 * @param customer
	 *            the customer
	 * @param shoppingCart
	 *            the shopping cart
	 * @return a new {@link CustomerSession}.
	 */
	public Shopper createNewShopperWithMementoAndCustomerAndShoppingCart(
			final Customer customer, final ShoppingCart shoppingCart) {
		Shopper shopper = createNewShopperWithMemento();
		shopper.setCurrentShoppingCart(shoppingCart);
		shopper.setCustomer(customer);
		return shopper;
	}

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}.
	 *
	 * @param customer
	 *            the customer
	 * @param customerSession
	 *            the customer session
	 * @param shoppingCart
	 *            the shopping cart
	 * @return a new {@link CustomerSession}.
	 */
	public Shopper createNewShopperWithMementoAndCustomerAndCustomerSessionAndShoppingCart(
			final Customer customer, final CustomerSession customerSession,
			final ShoppingCart shoppingCart) {
		Shopper shopper = createNewShopperWithMementoAndCustomerAndShoppingCart(
				customer, shoppingCart);
		shopper.updateTransientDataWith(customerSession);
		customerSession.setShopper(shopper);
		return shopper;
	}

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}.
	 *
	 * @return a new {@link Shopper}.
	 */
	public Shopper createNewShopperWithMemento() {
		final ShopperMemento shopperMemento = new ShopperMementoImpl();
		shopperMemento.setGuid(TestGuidUtility.getGuid());

		final Shopper shopper = new ShopperImpl();
		shopper.setShopperMemento(shopperMemento);

		return shopper;
	}

}
