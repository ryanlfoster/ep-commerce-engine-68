package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * An integration test for the ShoppingCartServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ShoppingCartServiceImplTest extends BasicSpringContextTest {

	private static final String PRODUCT_SHOULD_NOT_BE_NULL = "Product should not be null";

	private static final String PRODUCT_SKU_SHOULD_NOT_BE_NULL = "Product Sku should not be null";

	private static final String CART_ITEM_SHOULD_NOT_BE_NULL = "Cart Item should not be null";

	private static final String SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL = "Shopping Cart items should not be null";

	/** The main object under test. */
	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private ShopperService shopperService;

	@Autowired
	private CustomerSessionService customerSessionService;

	private SimpleStoreScenario scenario;

	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CartOrderService cartOrderService;
	
	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception when exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		persisterFactory = getTac().getPersistersFactory();
	}

	/**
	 * A new non-persistent instance should be created when load(0) is called.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadWithUidPk0() {
		final ShoppingCart shoppingCart = (ShoppingCart) shoppingCartService.getObject(0);
		assertFalse(shoppingCartService.isPersisted(shoppingCart));
	}

	/**
	 * Should be identical to testLoadWithUidPk0 - implementation uses same code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetObjectWithUidPk0() {
		final ShoppingCartImpl shoppingCart = (ShoppingCartImpl) shoppingCartService.getObject(0);
		assertFalse(shoppingCart.getShoppingCartMemento().isPersisted());
		assertEquals(0, shoppingCart.getShoppingCartMemento().getUidPk());
	}

	/**
	 * Create a new instance, store it and retrieve it.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndLoad() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		final ShoppingCart retrievedShoppingCart = shoppingCartService.findByGuid(shoppingCart.getGuid());
		assertEquals(shoppingCart.getGuid(), retrievedShoppingCart.getGuid());
		assertNotSame("Should be distinct objects", shoppingCart, retrievedShoppingCart);
	}

	/**
	 * Should be exactly the same as testAddAndLoad - the implementation uses the same code.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetObject() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		final ShoppingCart retrievedShoppingCart = shoppingCartService.findByGuid(shoppingCart.getGuid());
		assertEquals(shoppingCart.getGuid(), retrievedShoppingCart.getGuid());
		assertNotSame("Should be distinct objects", retrievedShoppingCart, shoppingCart);
	}

	/**
	 * Create a new instance, store it, modify it, and update it.
	 */
	@DirtiesDatabase
	@Test
	public void testAddItemToExistingCartObject() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, dto);
		// TODO: EH: This next call needs to be in a separate transaction.
		final ShoppingCart updatedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedShoppingCart.getCartItems());
		assertFalse("Shopping Cart items should not be empty", updatedShoppingCart.getCartItems().isEmpty());
		final ShoppingItem cartItem = updatedShoppingCart.getCartItems().get(0);
		assertNotNull(CART_ITEM_SHOULD_NOT_BE_NULL, cartItem);
		assertTrue("Cart Item should have a UidPk greater than 0", cartItem.getUidPk() > 0);
		assertNotNull(PRODUCT_SKU_SHOULD_NOT_BE_NULL, cartItem.getProductSku());
		assertNotNull(PRODUCT_SHOULD_NOT_BE_NULL, cartItem.getProductSku().getProduct());
	}

	/**
	 * Create a new instance, store it, add a few items, and update it. Checks that the cart items were correctly persisted via cascade.
	 */
	@DirtiesDatabase
	@Test
	public void testAddMultipleItemsToExistingCartObject() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final String skuCode = product.getDefaultSku().getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCode, 2));
		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final String skuCode2 = product2.getDefaultSku().getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCode2, 1));

		final ShoppingCart updatedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedShoppingCart.getCartItems());
		assertTrue("There should be 2 items in the cart", updatedShoppingCart.getCartItems().size() == 2);
		for (final ShoppingItem cartItem : updatedShoppingCart.getCartItems()) {
			assertNotNull(CART_ITEM_SHOULD_NOT_BE_NULL, cartItem);
			assertTrue("Cart Item should have a UidPk greater than 0", cartItem.getUidPk() > 0);
			assertNotNull(PRODUCT_SKU_SHOULD_NOT_BE_NULL, cartItem.getProductSku());
			assertNotNull(PRODUCT_SHOULD_NOT_BE_NULL, cartItem.getProductSku().getProduct());
		}
	}

	/**
	 * Test adding items to the cart, with changes being persisted in between additions. This is reasonably similar behaviour to what happens when a
	 * user adds things to their cart in the storefront.
	 */
	@DirtiesDatabase
	@Test
	public void testAddMultipleItemsUpdatingBetweenEach() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);


		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final String skuCode = product.getDefaultSku().getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCode, 2));
		final ShoppingCart updatedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedShoppingCart.getCartItems());
		assertEquals("There should be 1 item in the cart", 1, updatedShoppingCart.getCartItems().size());
		for (final ShoppingItem cartItem : updatedShoppingCart.getAllItems()) {
			assertNotNull(CART_ITEM_SHOULD_NOT_BE_NULL, cartItem);
			assertTrue("Cart Item should have a UidPk greater than 0", cartItem.getUidPk() > 0);
			assertNotNull(PRODUCT_SKU_SHOULD_NOT_BE_NULL, cartItem.getProductSku());
			assertNotNull(PRODUCT_SHOULD_NOT_BE_NULL, cartItem.getProductSku().getProduct());
		}

		final Product product2 = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final String skuCodeProduct2 = product2.getDefaultSku().getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCodeProduct2, 1));
		boolean contains = false;
		for (final ShoppingItem item : updatedShoppingCart.getAllItems()) {
			if (item.getProductSku().getSkuCode().equals(skuCodeProduct2)) {
				contains = true;
			}
		}
		assertTrue("Memento should contain new cart item", contains);
		final ShoppingCart updatedTwiceShoppingCart = shoppingCartService.saveOrUpdate(updatedShoppingCart);


		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedTwiceShoppingCart.getCartItems());
		assertEquals("There should be 2 items in the cart", 2, updatedTwiceShoppingCart.getCartItems().size());
		for (final ShoppingItem cartItem : updatedTwiceShoppingCart.getCartItems()) {
			assertNotNull(CART_ITEM_SHOULD_NOT_BE_NULL, cartItem);

			assertTrue("Cart Item should have a UidPk greater than 0", cartItem.getUidPk() > 0);
			assertNotNull(PRODUCT_SKU_SHOULD_NOT_BE_NULL, cartItem.getProductSku());
			assertNotNull(PRODUCT_SHOULD_NOT_BE_NULL, cartItem.getProductSku().getProduct());
		}
	}

	/**
	 * Test adding an item to the cart, persisting the cart, and then changing the quantity of that item.
	 */
	@DirtiesDatabase
	@Test
	public void testAddItemUpdateThenChangeQuantity() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final String skuCode = product.getDefaultSku().getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCode, 1));

		final ShoppingCart updatedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedShoppingCart.getCartItems());
		assertEquals("There should be 1 item in the cart", 1, updatedShoppingCart.getCartItems().size());
		ShoppingItem addedItem = updatedShoppingCart.getCartItems().get(0);
		assertTrue("Cart Item should be persistent", addedItem.isPersisted());
		assertEquals("The item should have a quantity of 1", 1, updatedShoppingCart.getCartItemById(addedItem.getUidPk()).getQuantity());
		final int newQuantity = 1 + 2;
		addedItem = cartDirector.updateCartItem(updatedShoppingCart, addedItem.getUidPk(), new ShoppingItemDto(skuCode, newQuantity));
		final ShoppingCart updatedTwiceShoppingCart = shoppingCartService.saveOrUpdate(updatedShoppingCart);

		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedTwiceShoppingCart.getCartItems());
		assertEquals("There should still be 1 item in the cart", 1, updatedTwiceShoppingCart.getCartItems().size());
		assertEquals("The item should have a quantity of 3", newQuantity,
				updatedTwiceShoppingCart.getCartItemByGuid(addedItem.getGuid()).getQuantity());

	}

	/**
	 * Create a non-persistent {@ShoppingCart} tied to the default store.
	 *
	 * @return the shopping cart
	 */
	private ShoppingCart createShoppingCart() {
		final Shopper shopper = shopperService.createAndSaveShopper(scenario.getStore().getCode());
		final CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(shopper);
		shoppingCart.setStore(scenario.getStore());
		((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento().setGuid(Utils.uniqueCode("CART-"));
		return shoppingCart;
	}

	/**
	 * Create a non-persistent shopping cart tied to the default store.
	 *
	 * @return the gift certificate.
	 */
	private ShoppingCart createFullShoppingCart() {
		final ShoppingCart shoppingCart = createShoppingCart();
		final Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, dto);
		// note that the cart isn't saved as the callers do this for us.
		return shoppingCart;
	}

	/**
	 * Test add multisku prod.
	 */
	@DirtiesDatabase
	@Test
	public void testAddMultiSkuToShoppingCart() {
		ShoppingCart shoppingCart = createFullShoppingCart();
		shoppingCart.clearItems();

		final Product multiSkuProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		final ProductSku defaultSku = multiSkuProduct.getDefaultSku();

		final String skuCode = defaultSku.getSkuCode();
		cartDirector.addItemToCart(shoppingCart, new ShoppingItemDto(skuCode, 1));
		for (final SkuOption skuOption : multiSkuProduct.getProductType().getSkuOptions()) {
			final SkuOptionValue skuOptionValue = defaultSku.getSkuOptionValue(skuOption);
			assertNotNull("The sku option value should not be null", skuOptionValue);
			final String displayName = skuOptionValue.getDisplayName(Locale.US, false);
			assertNotNull("The display name should not be null", displayName);
			assertTrue("The display name should not be empty", displayName.length() > 0);
		}

		assertEquals("The shopping cart should contain 1 item", 1, shoppingCart.getAllItems().size());
		shoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		final ShoppingCart updatedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);

		assertNotNull(SHOPPING_CART_ITEMS_SHOULD_NOT_BE_NULL, updatedShoppingCart.getCartItems());
		assertFalse("Shopping Cart items should not be empty", updatedShoppingCart.getCartItems().isEmpty());
		final ShoppingItem cartItem = updatedShoppingCart.getCartItems().get(0);
		assertNotNull(CART_ITEM_SHOULD_NOT_BE_NULL, cartItem);
		assertNotNull(PRODUCT_SKU_SHOULD_NOT_BE_NULL, cartItem.getProductSku());
		assertNotNull(PRODUCT_SHOULD_NOT_BE_NULL, cartItem.getProductSku().getProduct());

		assertEquals("There should be one item in the cart", 1, updatedShoppingCart.getAllItems().size());

		for (final ShoppingItem shoppingCartItem : updatedShoppingCart.getAllItems()) {
			final ProductSku sku = shoppingCartItem.getProductSku();

			for (final SkuOption skuOption : sku.getProduct().getProductType().getSkuOptions()) {

				final SkuOptionValue skuOptionValue = sku.getSkuOptionValue(skuOption);
				assertNotNull("The sku option value should not be null", skuOptionValue);
				final String displayName = skuOptionValue.getDisplayName(Locale.US, false);
				assertNotNull("The sku option value should have a display name", displayName);
				assertTrue("The display name should not be empty", displayName.length() > 0);
			}
		}
	}

	/** */
	@DirtiesDatabase
	@Test
	public void testFindByGuidWhenCartHasShoppingItems() {
		ShoppingCart cart = createFullShoppingCart();
		cart = shoppingCartService.saveOrUpdate(cart);
		final int totalNumberOfShoppingItems = 1;

		final ShoppingCart foundCart = shoppingCartService.findByGuid(cart.getGuid());
		Assert.assertEquals(cart.getGuid(), foundCart.getGuid());
		Assert.assertEquals(totalNumberOfShoppingItems, foundCart.getAllItems().size());
	}

	/**
	 * Creates a Customer and two ShoppingCarts and asserts that the cart GUIDs can be retrieved.
	 */
	@DirtiesDatabase
	@Test
	public void testFindShoppingCartGuidsByCustomerAndStore() {
		final Customer savedCustomer = createSavedCustomer();
		final ShoppingCart savedShoppingCart1 = createSavedShoppingCart(savedCustomer);
		final ShoppingCart savedShoppingCart2 = createSavedShoppingCart(savedCustomer);

		List<String> guids = shoppingCartService.findByCustomerAndStore(savedCustomer.getGuid(), scenario.getStore().getCode());
		assertEquals("There should be two ShoppingCart GUIDs.", 2, guids.size());
		assertTrue(String.format("Should have found ShoppingCart GUID [%s]", savedShoppingCart1.getGuid()),
					guids.contains(savedShoppingCart1.getGuid()));
		assertTrue(String.format("Should have found ShoppingCart GUID [%s]", savedShoppingCart2.getGuid()),
					guids.contains(savedShoppingCart2.getGuid()));
	}

	/**
	 * Creates a Customer and two ShoppingCarts and asserts that the cart GUIDs cannot be retrieved
	 * because the Store Code does not exist.
	 */
	@DirtiesDatabase
	@Test
	public void testFindNoShoppingCartGuids() {
		final Customer savedCustomer = createSavedCustomer();
		createSavedShoppingCart(savedCustomer);
		createSavedShoppingCart(savedCustomer);

		// Store doesn't exist.
		List<String> guids = shoppingCartService.findByCustomerAndStore(savedCustomer.getGuid(), "BAD_STORE_CODE");
		assertEquals("There should be zero ShoppingCart GUIDs.", 0, guids.size());
	}


	/**
	 * Tests the {@link ShoppingCartService#getShoppingCartLastModifiedDate(String)}.
	 */
	@DirtiesDatabase
	@Test
	public void testGetLastModifiedDate() {
		ShoppingCart shoppingCart = createShoppingCart();
		shoppingCartService.saveOrUpdate(shoppingCart);

		ShoppingCart retrievedShoppingCart = shoppingCartService.findByGuid(shoppingCart.getGuid());
		Date dateFromCart = retrievedShoppingCart.getLastModifiedDate();

		Date dateFromService = shoppingCartService.getShoppingCartLastModifiedDate(shoppingCart.getGuid());
		assertEquals("The date loaded from the object should be equal to the date retrieved by the service.", dateFromCart, dateFromService);
	}

	/**
	 * Verifies the {@link ShoppingCartService#getShoppingCartLastModifiedDate(String)} returns a <code>null</code> for an invalid cart GUID.
	 */
	@DirtiesDatabase
	@Test
	public void testGetLastModifiedDateInvalidGuid() {
		Date dateFromService = shoppingCartService.getShoppingCartLastModifiedDate("INVALID_GUID");
		assertNull("The method should return null when the GUID is invalid.", dateFromService);
	}
	
	/**
	 * Ensure delete all shopping carts by shopper uids correctly handles associated cart orders.
	 */
	@DirtiesDatabase
	@Test
	public void ensureDeleteAllShoppingCartsByShopperUidsCorrectlyHandlesAssociatedCartOrders() {
		final Customer savedCustomer = createSavedCustomer();
		ShoppingCart cartWithCartOrderToDelete = createSavedShoppingCartWithCartOrder(savedCustomer);
		ShoppingCart cartWithoutCartOrderToKeep = createSavedShoppingCart(savedCustomer);
		ShoppingCart cartWithCartOrderToKeep = createSavedShoppingCartWithCartOrder(savedCustomer);
		
		List<Long> shopperUidsForDeletion = Arrays.asList(cartWithCartOrderToDelete.getShopper().getUidPk());
		shoppingCartService.deleteAllShoppingCartsByShopperUids(shopperUidsForDeletion);

		assertFalse("Shopping cart to delete should be deleted.", shoppingCartService.shoppingCartExists(cartWithCartOrderToDelete.getGuid()));
		assertNull("Cart order on deleted shopping cart should also be deleted.", cartOrderService.findByShoppingCartGuid(cartWithCartOrderToDelete.getGuid()));	
		assertTrue("Shopping cart to keep should be ignored.", shoppingCartService.shoppingCartExists(cartWithCartOrderToKeep.getGuid()));
		assertNotNull("Cart order from kept shopping cart should not be deleted.", cartOrderService.findByShoppingCartGuid(cartWithCartOrderToKeep.getGuid()));
		assertTrue("Shopping cart without cart order should be ignored.", shoppingCartService.shoppingCartExists(cartWithoutCartOrderToKeep.getGuid()));
	}
	
	private ShoppingCart createSavedShoppingCartWithCartOrder(Customer savedCustomer) {
		ShoppingCart cartWithCartOrder = createSavedShoppingCart(savedCustomer);
		boolean cartOrderCreated = cartOrderService.createIfNotExists(cartWithCartOrder.getGuid());
		assertTrue("Cart order should not pre-exist.", cartOrderCreated);
		return cartWithCartOrder;
	}
	

	private Customer createSavedCustomer() {
		final Customer customer = getBeanFactory().getBean(ContextIdNames.CUSTOMER);
		customer.setEmail("a@b.com");
		customer.setStoreCode(scenario.getStore().getCode());
		customer.setAnonymous(false);
		final Customer savedCustomer = customerService.add(customer);
		return savedCustomer;
	}

	private ShoppingCart createSavedShoppingCart(final Customer savedCustomer) {
		final Shopper savedShopper = shopperService.createAndSaveShopper(scenario.getStore().getCode());
		final CustomerSession customerSession = customerSessionService.createWithShopper(savedShopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		savedShopper.setCustomer(savedCustomer);
		final Shopper savedShopperWithCustomer = shopperService.save(savedShopper);

		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(savedShopperWithCustomer);
		shoppingCart.setStore(scenario.getStore());
		((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento().setGuid(Utils.uniqueCode("CART-"));
		final ShoppingCart savedShoppingCart = shoppingCartService.saveOrUpdate(shoppingCart);
		return savedShoppingCart;
	}
}
