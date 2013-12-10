package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;


import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.util.Utils;

/**
 * Test cart director integration.
 */
public class CartDirectorImplIntegrationTest extends DbTestCase {

	/** The main object under test. */
	@Autowired
	@Qualifier("shoppingCartService")
	private ShoppingCartService service;

	@Autowired
	private ShopperService shopperService;

	@Autowired
	private CartDirector cartDirector;

	/**
	 * Test cart refresh changes on price.
	 */
	@DirtiesDatabase
	@Test
	public void testCartRefreshChangesPrices() {
		ShoppingCart shoppingCart = createFullShoppingCart();
		shoppingCart = service.saveOrUpdate(shoppingCart);

		final ShoppingItem item = shoppingCart.getAllItems().iterator().next();
		final Product product = item.getProductSku().getProduct();
		persisterFactory.getCatalogTestPersister().addOrUpdateProductBaseAmount(scenario.getCatalog(), product, BigDecimal.ONE, BigDecimal.ONE,
				BigDecimal.ONE, "USD");

		cartDirector.refresh(shoppingCart);

		final ShoppingItem secondItem = shoppingCart.getCartItem(item.getProductSku().getSkuCode());
		assertEquals(BigDecimal.ONE.setScale(2), secondItem.getListUnitPrice().getAmount());
	}

	
	@DirtiesDatabase
	@Test
	public void testAddToCartIdentityRetainer() {
        ShoppingCart shoppingCart = createFullShoppingCart();
        shoppingCart = service.saveOrUpdate(shoppingCart);

        final ShoppingItem item = shoppingCart.getAllItems().iterator().next();
        final String itemGuid = item.getGuid();
        
        assertEquals(2, item.getQuantity());
        
        
        //Add the same sku to cart again
        addSkuToCart(shoppingCart, item.getProductSku().getSkuCode(), 1);
        cartDirector.saveShoppingCart(shoppingCart);

        final ShoppingItem newItem = shoppingCart.getAllItems().iterator().next();
        assertEquals(itemGuid, newItem.getGuid());
        assertEquals(3, newItem.getQuantity());
	}
	
	
	/**
	 * Create a non-persistent shopping cart tied to the default store. Puts a SKU with quantity of two into the cart.
	 * 
	 * @return the shopping cart
	 */
	private ShoppingCart createFullShoppingCart() {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopperService.save(shopper);

		final CustomerSession custSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		custSession.setCurrency(Currency.getInstance("USD"));

		final ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart.setCustomerSession(custSession);
		shopper.setCurrentShoppingCart(shoppingCart);

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		addSkuToCart(shoppingCart, product.getDefaultSku().getSkuCode(), 2);

		// note that the cart isn't saved as the callers do this for us.
		return shoppingCart;
	}

    private void addSkuToCart(final ShoppingCart shoppingCart, final String skuCode, final int quantity) {
        final ShoppingItemDto dto = new ShoppingItemDto(skuCode, quantity);
		cartDirector.addItemToCart(shoppingCart, dto);
    }

	/**
	 * Create a non-persistent shopping cart tied to the default store.
	 * 
	 * @return the shopping cart
	 */
	private ShoppingCart createShoppingCart() {
		final ShoppingCart shoppingCart = getBeanFactory().getBean("shoppingCart");
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(scenario.getStore());
		((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento().setGuid(Utils.uniqueCode("CART-"));
		return shoppingCart;
	}
}
