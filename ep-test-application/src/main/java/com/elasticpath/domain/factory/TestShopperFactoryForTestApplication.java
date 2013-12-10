package com.elasticpath.domain.factory;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * A factory for producing properly constructed {@link CustomerSession}s for use in tests.
 * 
 * The intent is to keep all the {@link CustomerSession} creation code in one place so that changes in it's instantiation can
 * be fixed all at once.
 */
public final class TestShopperFactoryForTestApplication {

	private static final TestShopperFactoryForTestApplication INSTANCE = new TestShopperFactoryForTestApplication();
	
	/**
	 * Default private constructor.
	 */
	private TestShopperFactoryForTestApplication() {
		
	}
	
	/**
	 * Gets an instance of the factory for use.
	 *
	 * @return {@link TestShopperFactoryForTestApplication}.
	 */
	public static TestShopperFactoryForTestApplication getInstance() {
		return INSTANCE;
	}	

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}. Also properly initializes the {@link Shopper} with the
	 * {@link CustomerSession} reference. 
	 *
	 * @param customer the {@link Customer} to attach the {@link Shopper}.
	 * @param customerSession the {@link CustomerSession} to initialize the {@link Shopper} with.
	 * @param shoppingCart the {@link ShoppingCart} to attach the {@link Shopper}.
	 * @return a new {@link Shopper}. 
	 */
	public Shopper createNewShopperWithMementoAndCustomerAndCustomerSessionAndShoppingCart(final Customer customer,
			final CustomerSession customerSession, final ShoppingCart shoppingCart) {
		final Shopper shopper = createNewShopperWithMementoAndCustomerAndShoppingCart(customer, shoppingCart);
		shopper.updateTransientDataWith(customerSession);
		customerSession.setShopper(shopper);
		return shopper;
	}

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}.
	 *
	 * @param customer the {@link Customer} to attach the {@link Shopper}.
	 * @param shoppingCart the {@link ShoppingCart} to attach the {@link Shopper}.
	 * @return a new {@link Shopper}. 
	 */
	public Shopper createNewShopperWithMementoAndCustomerAndShoppingCart(final Customer customer, final ShoppingCart shoppingCart) {
		final Shopper shopper = createNewShopperWithMemento();
		shopper.setCurrentShoppingCart(shoppingCart);
		shopper.setCustomer(customer);
		shoppingCart.setShopper(shopper);
		return shopper;
	}

	/**
	 * Creates a new {@link Shopper} with a {@link ShopperMemento}.
	 *
	 * @param customer the {@link Customer} to attach the {@link Shopper}.
	 * @return a new {@link Shopper}. 
	 */
	public Shopper createNewShopperWithMementoAndCustomer(final Customer customer) {
		final Shopper shopper = createNewShopperWithMemento();
		shopper.setCustomer(customer);
		return shopper;
	}

	/**
	 * Creates a new {@link CustomerSession} with a {@link ShopperMemento}. 
	 *
	 * @return a new {@link CustomerSession}. 
	 */
	public Shopper createNewShopperWithMemento() {
		final ShopperMemento shopperMemento = new ShopperMementoImpl();
		shopperMemento.setGuid(TestGuidUtility.getGuid());

		final Shopper shopper = new ShopperImpl();
		shopper.setShopperMemento(shopperMemento);

		return shopper;
	}

}
 