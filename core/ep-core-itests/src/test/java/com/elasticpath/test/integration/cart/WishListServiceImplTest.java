/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.CustomerScenario;
import com.elasticpath.test.util.Utils;

/**
 * TODO.
 */
public class WishListServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private WishListService wishListService;

	@Autowired
	private ShopperService shopperService;

	private CustomerScenario scenario;

	private static final String GUID = "WLGUID";

	/**
	 *
	 * @throws java.lang.Exception if something bad happens.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(CustomerScenario.class);
	}

	/**
	 * Test method for {@link WishListService#save(WishList)}.
	 */
	@DirtiesDatabase
	@Test
	public void testSave() {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		final Shopper savedShopper = shopperService.save(shopper);

		assertNotNull(savedShopper);
		final WishList wishList = createWishList();
		wishList.setGuid(GUID);
		wishList.setShopper(savedShopper);

		final WishList savedWishList = wishListService.save(wishList);
		assertTrue("Saved wish list should be persistent", savedWishList.isPersisted());
	}

	/**
	 * Test method for {@link WishListService#load(long)}.
	 */
	@DirtiesDatabase
	@Test
	public void testLoad() {
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper = shopperService.save(shopper);

		final WishList wishList = createWishList();
		final String guid = Utils.uniqueCode("WL-");
		wishList.setGuid(guid);
		wishList.setShopper(shopper);

		wishListService.save(wishList);

		final long uidPk = wishList.getUidPk();
		assertTrue("Wishlist should have a valid UIDPK", uidPk > 0);
		final WishList loadedWishList = wishListService.get(uidPk);
		assertNotNull("Wishlist was loaded", loadedWishList);
		assertEquals("Wishlist has the expected guid", guid, loadedWishList.getGuid());
		assertEquals("Shopper guids should be equal.", shopper.getGuid(), wishList.getShopper().getGuid());
	}

	/**
	 * Test method for {@link WishListService#findWishListByCustomerGuid(String)}.
	 */
	@DirtiesDatabase
	@Test
	public void testFindWishListByCustomerGuid() {
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper = shopperService.save(shopper);

		final Customer customer = scenario.getCustomer();
		shopper.setCustomer(customer);

		final WishList wishList = createWishList();
		wishList.setGuid(Utils.uniqueCode("WL-"));
		wishList.setShopper(shopper);
		final WishList savedWishList = wishListService.save(wishList);
		assertTrue("Saved wish list should be persistent", savedWishList.isPersisted());

		final WishList loadedWishList = wishListService.findOrCreateWishListByShopper(shopper);
		assertNotNull("Wishlist was loaded", loadedWishList);
		assertEquals("Wishlist has the expected guid", wishList.getGuid(), loadedWishList.getGuid());
	}

	private WishList createWishList() {
		final WishList wishList = getBeanFactory().getBean(ContextIdNames.WISH_LIST);
		return wishList;
	}

	/**
	 * Test method for {@link com.elasticpath.service.shoppingcart.impl.WishListServiceImpl#findWishListByCustomerSessionGuid(java.lang.String)}.
	 */
	@DirtiesDatabase
	@Test
	public void testFindWishListByCustomerSessionGuid() {
		final Customer customer = scenario.getCustomer();
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.setCustomer(customer);
		shopper = shopperService.save(shopper);

		final CustomerSession custSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		custSession.setGuid(Utils.uniqueCode("CS-"));
		custSession.setCreationDate(new Date());
		custSession.setLastAccessedDate(new Date());
		custSession.setShopper(shopper);
		final CustomerSessionService customerSessionService = getBeanFactory().getBean("customerSessionService");
		customerSessionService.add(custSession);

		final WishList wishList = createWishList();
		wishList.setGuid(Utils.uniqueCode("WL-"));
		wishList.setShopper(shopper);

		final WishList savedWishList = wishListService.save(wishList);
		assertTrue("Saved wish list should be persistent", savedWishList.isPersisted());

		final WishList loadedWishList = wishListService.findOrCreateWishListByShopper(shopper);
		assertNotNull("Wishlist was loaded", loadedWishList);
		assertEquals("Wishlist has the expected guid", wishList.getGuid(), loadedWishList.getGuid());
	}

}
