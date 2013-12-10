package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.impl.ShoppingCartMergerImpl;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.CouponTestPersister;
import com.elasticpath.test.persister.GiftCertificateTestPersister;
import com.elasticpath.test.persister.PromotionTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.ShoppingCartSimpleStoreScenario;

/**
 * Tests to ensure the <code>ShoppingCartMergerImpl</code> will correctly merge to <code>ShoppingCart</code>s.
 */
@SuppressWarnings({ "PMD.TooManyFields" })
public class ShoppingCartMergerImplTest extends BasicSpringContextTest {

	private static final String NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART = "Number of items in the shopping cart";
	private static final String CURRENT_CART_NAME = "current";
	private static final String PREVIOUS_CART_NAME = "previous";

	private Product camera;
	private Product bag;
	private Product memoryCard;
	private Product giftCard;
	private ProductBundle cameraBagCard;

	private ProductSku cameraSku;
	private ProductSku bagSku;
	private ProductSku memoryCardSku;
	private ProductSku giftCardSku;

	private ShoppingItem siCameraQty1;
	private ShoppingItem siBagQty1;
	private ShoppingItem siMemoryCardQty1;
	private ShoppingItem siCfgGiftCard1;
	private ShoppingItem siCfgGiftCard2;
	private ShoppingItem bundleCameraBagCard;

	private ShoppingCartMergerImpl merger;
	private CartDirector cartDirector;
	private ShoppingCartSimpleStoreScenario scenario;
	private Shopper currentShopper;
	private Shopper previousShopper;
	private ShoppingItemFactory shoppingItemFactory;
	private CatalogTestPersister catalogPersister;

	@Autowired
	private ProductService productService;

	/**
	 * Setup the tests.
	 */
	@Before
	public void setUp() {

		// Create the merger we will actually be testing
		// - we mock out the price lookup facade to keep the test as simple as possible, as
		//   price isn't important in the merging case.
		merger = getBeanFactory().getBean("shoppingCartMerger");
		cartDirector = getBeanFactory().getBean("cartDirector");
		merger.setCartDirector(cartDirector);

		scenario = getTac().useScenario(ShoppingCartSimpleStoreScenario.class);

		currentShopper = createShopper();
		previousShopper = createShopper();

		final StoreTestPersister storePersister = getTac().getPersistersFactory().getStoreTestPersister();
		final TaxCodeService taxCodeService = getBeanFactory().getBean(ContextIdNames.TAX_CODE_SERVICE);
		final TaxCode goodTaxCode = taxCodeService.findByCode("GOODS");
		storePersister.updateStoreTaxCodes(scenario.getStore(), new HashSet<TaxCode>(Arrays.asList(goodTaxCode)));

		catalogPersister = getTac().getPersistersFactory().getCatalogTestPersister();

		// Create the products to add to carts.
		camera = catalogPersister.createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		cameraSku = camera.getDefaultSku();

		bag = catalogPersister.createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		bagSku = bag.getDefaultSku();

		giftCard = catalogPersister.createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		giftCard.getProductType().setName(GiftCertificate.KEY_PRODUCT_TYPE); // Make the ShoppingItems 'configurable'
		giftCard = productService.saveOrUpdate(giftCard);
		giftCardSku = giftCard.getDefaultSku();

		memoryCard = catalogPersister.createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		memoryCardSku = memoryCard.getDefaultSku();

		// Create shopping items that we'll be putting into the cart.
		shoppingItemFactory = getBeanFactory().getBean("shoppingItemFactory");

		Currency currency = TestDataPersisterFactory.DEFAULT_CURRENCY;
		PriceImpl price = new PriceImpl();
		price.setCurrency(currency);
		price.setListPrice(MoneyFactory.createMoney(BigDecimal.ONE, currency));

		siCameraQty1 = shoppingItemFactory.createShoppingItem(cameraSku, price, 1, 1, null);
		siBagQty1 = shoppingItemFactory.createShoppingItem(bagSku, price, 1, 1, null);
		siMemoryCardQty1 = shoppingItemFactory.createShoppingItem(memoryCardSku, price, 1, 1, null);
		siCfgGiftCard1 = shoppingItemFactory.createShoppingItem(giftCardSku, price, 1, 1, null);
		siCfgGiftCard2 = shoppingItemFactory.createShoppingItem(giftCardSku, price, 1, 1, null);
	}


	private Shopper createShopper() {
		final ShopperService shopperService = getBeanFactory().getBean(ContextIdNames.SHOPPER_SERVICE);

		final Shopper shopper = shopperService.createAndSaveShopper(scenario.getStore().getCode());
		final CustomerSession customerSession = new CustomerSessionImpl();
		CustomerSessionMemento cMemento = new CustomerSessionMementoImpl();
		cMemento.setCurrency(TestDataPersisterFactory.DEFAULT_CURRENCY);
		customerSession.setCustomerSessionMemento(cMemento);
		shopper.updateTransientDataWith(customerSession);
		return shopper;
	}

	private ShoppingCart buildShoppingCart(final String cartName, final ShoppingItem... items) {
		@SuppressWarnings("serial")
		final ShoppingCart shoppingCart = new ShoppingCartImpl() {
			@Override
			public void fireRules() {
				// Do nothing, we don't want drools involved.
			}
		};

		for (final ShoppingItem item : items) {
			shoppingCart.addShoppingCartItem(item);
		}
		shoppingCart.setStore(scenario.getStore());

		if (cartName == CURRENT_CART_NAME) {
            currentShopper.setCurrentShoppingCart(shoppingCart);
            shoppingCart.setShopper(currentShopper);
        } else {
            previousShopper.setCurrentShoppingCart(shoppingCart);
            shoppingCart.setShopper(previousShopper);
        }

		return shoppingCart;
	}

	/**
	 * The merge should only keep one copy of the same product.
	 * Should this bump the quantity?
	 */
	@DirtiesDatabase
	@Test
	public void testMergeSameProduct() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siCameraQty1);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 1, mergedList.size());

		assertItemAndQuantity(mergedList, 0, cameraSku, 1);
	}

	/**
	 * The merge should keep both Products if they are different.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeDifferentProduct() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siBagQty1);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 2, mergedList.size());

		assertItemAndQuantity(mergedList, 0, cameraSku, 1);
		assertItemAndQuantity(mergedList, 1, bagSku, 1);
	}

	/**
	 * The merge should keep the current quantity when merging the same product.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeSameProductKeepsCorrectQuantity() {
		final ShoppingItem siCameraQty2 = shoppingItemFactory.createShoppingItem(cameraSku, null, 2, 1, null);

		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty2);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siCameraQty1);

		final ShoppingCart mergedCart = merger.merge(previousCart, currentCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 1, mergedList.size());

		assertItemAndQuantity(mergedList, 0, cameraSku, 2);
	}

	/**
	 * The merge should keep both configurable products.
	 */
	@DirtiesDatabase
	@Test
	public void testMergeKeepsBothConfigurableProducts() {
		final String nameField = "name";
		siCfgGiftCard1.setFieldValue(nameField, "bob");
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCfgGiftCard1);

		siCfgGiftCard2.setFieldValue(nameField, "jill");
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siCfgGiftCard2);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 2, mergedList.size());

		assertItemAndQuantity(mergedList, 0, giftCardSku, 1);
		assertSame("bob", mergedList.get(0).getFieldValue(nameField));

		assertItemAndQuantity(mergedList, 1, giftCardSku, 1);
		assertSame("jill", mergedList.get(1).getFieldValue(nameField));
	}


	/**
	 * When merging a bundle, the bundle itself is treated as an independent item
	 * and the bundle contents will merge and not be affected by any
	 * other conflicting items or quantities that may already exist outside of the bundle.
	 */
	@DirtiesDatabase
	@Test
	public void testBundleMergesAsIndependantItemAndQuantity() {

		setUpBundles();

		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siMemoryCardQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, bundleCameraBagCard);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 2, mergedList.size());

		assertItemAndQuantity(mergedList, 0, memoryCardSku, 1);

		assertSame(cameraBagCard.getDefaultSku().getSkuCode(), mergedList.get(1).getProductSku().getSkuCode());
		Assert.assertTrue(mergedList.get(1).isBundle());
	}

	private void setUpBundles() {
		cameraBagCard = catalogPersister.createSimpleProductBundle(
				"Bundle", "Bundle1", scenario.getCatalog(), scenario.getCategory(), scenario.getStore().getTaxCodes().iterator().next());

		cameraBagCard.addConstituent(catalogPersister.createSimpleBundleConstituent(camera, 1));
		cameraBagCard.addConstituent(catalogPersister.createSimpleBundleConstituent(bag, 1));
		cameraBagCard.addConstituent(catalogPersister.createSimpleBundleConstituent(memoryCard, 1));

		cameraBagCard.setCalculated(true);
		final ProductSku sku = new ProductSkuImpl();
		sku.setStartDate(new Date());
		sku.setSkuCode("bundleskucode");
		sku.initialize();
		cameraBagCard.addOrUpdateSku(sku);

		final ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		productService.saveOrUpdate(cameraBagCard);

		bundleCameraBagCard = shoppingItemFactory.createShoppingItem(cameraBagCard.getDefaultSku(), null, 1, 1, null);

		// Derive a ShoppingItemDTO, select all the constituents and convert back to a ShoppingItem.
		final ShoppingItemAssembler shoppingItemAssembler = getBeanFactory().getBean("shoppingItemAssembler");
		final ShoppingItemDto dto = shoppingItemAssembler.assembleShoppingItemDtoFrom(bundleCameraBagCard);
		dto.setSelected(true);
		for (final ShoppingItemDto child : dto.getConstituents()) {
			child.setSelected(true);
		}
		bundleCameraBagCard = shoppingItemAssembler.createShoppingItem(dto);
	}

	/**
	 * .
	 */
	@DirtiesDatabase
	@Test
	public void testOneProductInCurrentCartTwoInPreviousNoConflict() {
		final int three = 3;

		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siBagQty1, siMemoryCardQty1);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();

		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, three, mergedList.size());

		assertItemAndQuantity(mergedList, 0, cameraSku, 1);
		assertItemAndQuantity(mergedList, 1, bagSku, 1);
		assertItemAndQuantity(mergedList, 2, memoryCardSku, 1);
	}

	/**
	 * .
	 */
	@DirtiesDatabase
	@Test
	public void testTwoProductInCurrentCartOneInPreviousNoConflict() {
		final int three = 3;
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1, siBagQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siMemoryCardQty1);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, three, mergedList.size());

		assertItemAndQuantity(mergedList, 0, cameraSku, 1);
		assertItemAndQuantity(mergedList, 1, bagSku, 1);
		assertItemAndQuantity(mergedList, 2, memoryCardSku, 1);
	}


	/**
	 * .
	 */
	@DirtiesDatabase
	@Test
	public void testMergePopulatedCartIntoEmptyCart() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siMemoryCardQty1);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();

		assertItemAndQuantity(mergedList, 0, memoryCardSku, 1);
	}

	/**
	 * .
	 */
	@DirtiesDatabase
	@Test
	public void testMergeEmptyCartIntoPopulatedCart() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siMemoryCardQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		final List<ShoppingItem> mergedList = mergedCart.getCartItems();
		assertEquals(NUMBER_OF_ITEMS_IN_THE_SHOPPING_CART, 1, mergedList.size());
		assertSame(memoryCardSku.getSkuCode(), mergedList.get(0).getProductSku().getSkuCode());
	}

	/**
	 * Test merge preserves coupons.
	 */
	@DirtiesDatabase
	@Test
	public void testMergePreservesCoupons() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siBagQty1);

		PromotionTestPersister promotionTestPersister = getTac().getPersistersFactory().getPromotionTestPersister();
		Rule promotion = promotionTestPersister.createAndPersistSimpleShoppingCartPromotion("Test Promo", scenario.getStore().getCode(),
				"promo1", true);

		CouponTestPersister couponTestPersister = getTac().getPersistersFactory().getCouponTestPersister();
		CouponConfig couponConfig = couponTestPersister.createAndPersistCouponConfig(promotion.getCode(), 1, CouponUsageType.LIMIT_PER_COUPON);
		couponTestPersister.createAndPersistCoupon(couponConfig, "COUPON");

		previousCart.applyPromotionCode("COUPON");

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		assertEquals("There should be a coupon code in the cart", 1, mergedCart.getPromotionCodes().size());
		assertEquals("The coupon code should be code applied to the previous cart", "COUPON", mergedCart.getPromotionCodes().iterator().next());
	}

	/**
	 * Test merge preserves gift certificates.
	 */
	@DirtiesDatabase
	@Test
	public void testMergePreservesGiftCertificates() {
		final ShoppingCart currentCart = buildShoppingCart(CURRENT_CART_NAME, siCameraQty1);
		final ShoppingCart previousCart = buildShoppingCart(PREVIOUS_CART_NAME, siBagQty1);

		GiftCertificateTestPersister giftCertificateTestPersister = getTac().getPersistersFactory().getGiftCertificateTestPersister();
		GiftCertificate giftCertificate = giftCertificateTestPersister.persistGiftCertificate(scenario.getStore(), "gcGuid", "gc100",
				scenario.getStore().getDefaultCurrency().getCurrencyCode(), BigDecimal.TEN, "me", "you", "theme", previousCart.getShopper().getCustomer());

		previousCart.applyGiftCertificate(giftCertificate);

		final ShoppingCart mergedCart = merger.merge(currentCart, previousCart);

		assertEquals("There should be a gift certificate in the cart", 1, mergedCart.getAppliedGiftCertificates().size());
		assertEquals("The gift certificate should be the one from the previous cart", giftCertificate,
				mergedCart.getAppliedGiftCertificates().iterator().next());
	}

	private void assertItemAndQuantity(final List<ShoppingItem> mergedList, final int position, final ProductSku sku, final int expectedQuantity) {
		assertSame("Expected skucodes to match", sku.getSkuCode(), mergedList.get(position).getProductSku().getSkuCode());
		assertEquals("Quantity not as expected", expectedQuantity, mergedList.get(position).getQuantity());
	}

}
