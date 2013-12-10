package com.elasticpath.domain.factory;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.Store;

/**
 * A factory for producing properly constructed {@link ShoppingCarts}s for use in tests.
 * 
 * The intent is to keep all the {@link ShoppingCart} creation code in one place so that changes in its instantiation can
 * be fixed all at once.
 */
public final class TestShoppingCartFactoryForTestApplication {

	private static final TestShoppingCartFactoryForTestApplication INSTANCE = new TestShoppingCartFactoryForTestApplication();
	
	/**
	 * Default private constructor.
	 */
	private TestShoppingCartFactoryForTestApplication() {
		
	}
	
	/**
	 * Gets an instance of the factory for use.
	 *
	 * @return {@link TestShoppingCartFactoryForTestApplication}.
	 */
	public static TestShoppingCartFactoryForTestApplication getInstance() {
		return INSTANCE;
	}	

	/**
	 * Creates a new {@link ShoppingCart} with a {@link ShoppingCartMemento}. 
	 *
	 * @param shopper the {@link Shopper} this cart belongs to.
	 * @param store the {@link Store} this cart belongs to.
	 * @return a new {@link ShoppingCart}. 
	 */
	public ShoppingCartImpl createNewCartWithMemento(final Shopper shopper, final Store store) {
		final ShoppingCartMemento shoppingCartMemento = new ShoppingCartMementoImpl();
		shoppingCartMemento.setGuid(TestGuidUtility.getGuid());
		
		final ShoppingCartImpl shoppingCart = new ShoppingCartImpl();
		shoppingCart.setShoppingCartMemento(shoppingCartMemento);
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(store);
		
		shopper.setCurrentShoppingCart(shoppingCart);

		return shoppingCart;
	}

}
 