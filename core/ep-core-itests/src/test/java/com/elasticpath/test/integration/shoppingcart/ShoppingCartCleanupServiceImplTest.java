/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.test.integration.shoppingcart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartCleanupService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.ShoppingItemService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.customer.PersistenceEngineTestEntityListenerConfigurer;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Integration testing when dealing with shopping cart abandonment issues.
 */
public class ShoppingCartCleanupServiceImplTest extends BasicSpringContextTest {

	private static final int EXPECTED_MAX_HISTORY = 60;

	private static final int EXPECTED_MAX_RESULTS = 1000;

	private static final int EXPECTED_TWO_CARTS_REMOVED = 2;

	private static final String SKU_CODE = "0349B004";

	@Autowired private ShoppingCartCleanupService shoppingCartCleanupService;

	@Autowired private ShoppingCartService shoppingCartService;

	@Autowired private ShopperService shopperService;

	@Autowired private CustomerSessionService customerSessionService;

	@Autowired private PersistenceEngineTestEntityListenerConfigurer persistenceEngineTestEntityListenerConfigurer;

	private ShoppingCartSimpleStoreScenario scenario;

	@Autowired private CartDirector cartDirector;

	@Autowired private ShoppingItemService shoppingItemService;

	@Autowired private WishListService wishListService;

	/**
	 * Get a reference to TestApplicationContext for use within the test. <br>
	 * Setup scenarios.
	 * @throws Exception on exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);
		persistenceEngineTestEntityListenerConfigurer.disableLastModifiedListenerOnTypes(ShoppingCartMemento.class);
	}

	/**
	 * Test that the max history applied to a date removes the expected number of shopping carts.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalByDateBoundaries() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY);
		Date beforeRemovalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY + 1);

		// create shopping carts around the max history date boundaries
		Shopper shopperAfterRemovalDate = addShoppingCart(afterRemovalDate);
		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		Shopper shopperBeforeRemovalDate = addShoppingCart(beforeRemovalDate);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, EXPECTED_MAX_RESULTS);
		assertEquals("There should have been two shopping carts removed.", EXPECTED_TWO_CARTS_REMOVED, removed);

		ShoppingCart updatedCartAfterRemovalDate = shoppingCartService.findOrCreateByShopper(shopperAfterRemovalDate);
		assertEquals("The non-candidate shopping cart should still exist in database.", updatedCartAfterRemovalDate.getGuid(),
				shopperAfterRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartEqualsRemovalDate = shoppingCartService.findOrCreateByShopper(shopperEqualsRemovalDate);
		assertFalse("The candidate shopping cart which is equals to the removal date should not exist in database. A new cart was created.",
				updatedCartEqualsRemovalDate.getGuid().equals(shopperEqualsRemovalDate.getCurrentShoppingCart().getGuid()));

		ShoppingCart updatedCartBeforeRemovalDate = shoppingCartService.findOrCreateByShopper(shopperBeforeRemovalDate);
		assertFalse("The candidate shopping cart which is before the removal date should not exist in database. A new cart was created.",
				updatedCartBeforeRemovalDate.getGuid().equals(shopperBeforeRemovalDate.getCurrentShoppingCart().getGuid()));
	}

	/**
	 * Test that the the batch size limits the number of abandoned carts.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalByBatchSizeAlsoNoticeRemovesOldestAbandonedCartsFirst() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY);
		Date beforeRemovalDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY + 1);

		// create shopping carts around the max history date boundaries
		Shopper shopperAfterRemovalDate = addShoppingCart(afterRemovalDate);
		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		Shopper shopperBeforeRemovalDate = addShoppingCart(beforeRemovalDate);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, 1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		ShoppingCart updatedCartAfterRemovalDate = shoppingCartService.findOrCreateByShopper(shopperAfterRemovalDate);
		assertEquals("The non-candidate shopping cart should still exist in database.", updatedCartAfterRemovalDate.getGuid(),
				shopperAfterRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartEqualsRemovalDate = shoppingCartService.findOrCreateByShopper(shopperEqualsRemovalDate);
		assertEquals("The candidate shopping cart which is equals to the removal date should still exist in database.",
				updatedCartEqualsRemovalDate.getGuid(), shopperEqualsRemovalDate.getCurrentShoppingCart().getGuid());

		ShoppingCart updatedCartBeforeRemovalDate = shoppingCartService.findOrCreateByShopper(shopperBeforeRemovalDate);
		assertFalse("The candidate shopping cart which is before the removal date should not exist in database. A new cart was created.",
				updatedCartBeforeRemovalDate.getGuid().equals(shopperBeforeRemovalDate.getCurrentShoppingCart().getGuid()));
	}

	/**
	 * Test that if an abandoned shopping cart is removed, that it cascades to remove dependent shopping cart items as well.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalRemovesShoppingItems() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY);

		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		ShoppingCart shoppingCart = shopperEqualsRemovalDate.getCurrentShoppingCart();

		ShoppingItemDto dto = new ShoppingItemDto(SKU_CODE, 1);

		ShoppingItem shoppingItem = cartDirector.addItemToCart(shoppingCart, dto);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, 1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		ShoppingItem actualShoppingItem = shoppingItemService.findByGuid(shoppingItem.getGuid(), null);
		assertNull("This shopping item should have been cascade deleted when the shopping cart was removed.", actualShoppingItem);
	}

	/**
	 * Test that if an abandoned shopping cart is removed, that wish list items can still be accessed by shopper.
	 */
	@DirtiesDatabase
	@Test
	public void testCandidateRemovalDoesNotAdverselyAffectShopperWishLists() {
		Date afterRemovalDate = new Date();
		Date equalsBoundaryDate = getAdjustedDate(afterRemovalDate, EXPECTED_MAX_HISTORY);

		Shopper shopperEqualsRemovalDate = addShoppingCart(equalsBoundaryDate);
		final Store store = scenario.getStore();
		ShoppingItem shoppingItem = cartDirector.addSkuToWishList(SKU_CODE, shopperEqualsRemovalDate, store);

		int removed = shoppingCartCleanupService.deleteAbandonedShoppingCarts(equalsBoundaryDate, 1);
		assertEquals("There should have been one shopping cart removed.", 1, removed);

		WishList actualWishList = wishListService.findOrCreateWishListByShopper(shopperEqualsRemovalDate);
		assertFalse("This wishlist should not be null.", actualWishList == null);

		assertEquals("This wishlist should have the same amount of items as previously held.", actualWishList.getAllItems().size(), 1);

		assertEquals("This wishlist should contain the same product sku.", actualWishList.getAllItems().get(0).getProductSku().getGuid(),
				shoppingItem.getProductSku().getGuid());
	}

	private Date getAdjustedDate(final Date now, final int adjustment) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_YEAR, - adjustment);
		return calendar.getTime();
	}

	private Shopper addShoppingCart(final Date lastModifiedDate) {
		final Shopper shopper = shopperService.createAndSaveShopper(scenario.getStore().getCode());
		ShoppingCart cart = createShoppingCart(shopper, lastModifiedDate);
		ShoppingCart updatedCart = shoppingCartService.saveOrUpdate(cart);
		shopper.setCurrentShoppingCart(updatedCart);
		return shopper;
	}

	/**
	 * Create a non-persistent {@ShoppingCart} tied to the default store.
	 *
	 * @return the shopping cart
	 */
	private ShoppingCart createShoppingCart(final Shopper shopper, final Date lastModifiedDate) {
		final CustomerSession customerSession = customerSessionService.createWithShopper(shopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		final ShoppingCart shoppingCart = shoppingCartService.findOrCreateByShopper(shopper);
		shoppingCart.setStore(scenario.getStore());

		final ShoppingCartMemento memento = ((ShoppingCartMementoHolder)shoppingCart).getShoppingCartMemento();
		memento.setGuid(Utils.uniqueCode("CART-"));
		memento.setLastModifiedDate(lastModifiedDate);

		shopper.setCurrentShoppingCart(shoppingCart);
		return shoppingCart;
	}
}
