package com.elasticpath.sfweb.util.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sfweb.util.PriceFinderForCart;

/**
 * Test for Price Finder for cart.
 *
 */
@SuppressWarnings("PMD")
public class PriceFinderForCartImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PriceFinderForCart priceFinderForCart;

	/**
	 * Setup.
	 */
	@Before
	public void setUp() {
		priceFinderForCart = new PriceFinderForCartImpl();

	}

	/**
	 * Test to make sure the statement coverage for method findPrices().
	 */
	@Test
	public void testFindPrices() {
		StoreProduct storeProduct = new StoreProductImpl(new ProductImpl());

		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = createCustomerSession();
		final Shopper shopper = customerSession.getShopper();
		shopper.setCurrentShoppingCart(shoppingCart);
		final Set<Long> appliedRulesSet = new HashSet<Long>();

		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		priceFinderForCart.setPriceLookupFacade(priceLookupFacade);

		final Store store = new StoreImpl();

		final Map<String, Price> priceMap = new HashMap<String, Price>();

		final List<Product> products = Collections.singletonList((Product) storeProduct);
		final List<StoreProduct> storeProducts = new ArrayList<StoreProduct>();
		storeProducts.add(storeProduct);

		context.checking(new Expectations() {
			{ // NOPMD
				allowing(shoppingCart).getStore();
				will(returnValue(store));
				allowing(shoppingCart).getAppliedRules();
				will(returnValue(appliedRulesSet));

				allowing(priceLookupFacade).getPromotedPricesForProducts(products,
						store, shopper, appliedRulesSet);
				will(returnValue(priceMap));
			}
		});

		priceFinderForCart.findPrices(storeProducts, shopper);
		priceFinderForCart.findPrices(null, shopper);
	}

	/**
	 * Creates a customer Session.
	 * 
	 * This should be done via a factory or the test should be moved to where you can find a factory!
	 *
	 * @return a customerSession.
	 * @deprecated Should be using an exposed factory!!
	 */
	@Deprecated
	private CustomerSession createCustomerSession() {
		final CustomerSession customerSession = new CustomerSessionImpl();
		final CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();
		final Shopper shopper = new ShopperImpl();
		final ShopperMemento shopperMemento = new ShopperMementoImpl();
		shopper.setShopperMemento(shopperMemento);
		customerSession.setCustomerSessionMemento(customerSessionMemento);
		customerSession.setShopper(shopper);
		shopper.updateTransientDataWith(customerSession);
		return customerSession;
	}

	/**
	 * Test to make sure the statement coverage for method findAdjustments().
	 */
	@Test
	public void testFindAdjustments() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final StoreProduct storeProduct = context.mock(StoreProduct.class);
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		final Store store = context.mock(Store.class);

		final Catalog catalog = new CatalogImpl();
		final CustomerSession customerSession = createCustomerSession();
		final Shopper shopper = customerSession.getShopper();
		shopper.setCurrentShoppingCart(shoppingCart);

		catalog.setCode("catalogCode");

		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		priceFinderForCart.setPriceLookupFacade(priceLookupFacade);


		final List<StoreProduct> storeProducts = new ArrayList<StoreProduct>();
		storeProducts.add(storeProduct);

		final Map<String, PriceAdjustment> adjustmentMap = new HashMap<String, PriceAdjustment>();
		final String catalogCode = "catalogCode";

		adjustmentMap.put("test", new PriceAdjustmentImpl());

		context.checking(new Expectations() {
			{ // NOPMD
				allowing(storeProduct).getWrappedProduct();
				will(returnValue(productBundle));

				allowing(productBundle).getCode();
				will(returnValue("testProductCode"));

				allowing(shoppingCart).getStore();
				will(returnValue(store));

				allowing(store).getCatalog();
				will(returnValue(catalog));

				allowing(priceLookupFacade).getPriceAdjustmentsForBundle(productBundle, catalogCode, shopper);
				will(returnValue(adjustmentMap));
			}
		});

		assertEquals(Collections.emptyMap(), priceFinderForCart.findAdjustments(null, shopper));

		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
		resultMap.put("testProductCode", true);

		assertEquals(resultMap, priceFinderForCart.findAdjustments(storeProducts, shopper));
	}

}
