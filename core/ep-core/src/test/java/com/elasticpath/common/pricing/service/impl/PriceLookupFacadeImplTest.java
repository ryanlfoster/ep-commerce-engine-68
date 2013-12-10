package com.elasticpath.common.pricing.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.DisplayPriceDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * PriceLookupFacadeImpl test.
 */
public class PriceLookupFacadeImplTest {

	private static final String INVALID_PRICELIST_GUID = "INVALID_PL_GUID";

	private static final BigDecimal D_9_99 = new BigDecimal("9.99");

	private static final BigDecimal D_11_99 = new BigDecimal("11.99");

	private PriceLookupFacadeImpl facade;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final PriceLookupService priceLookupService = context.mock(PriceLookupService.class);
	
	private BeanFactory beanFactory;
	
	private BeanFactoryExpectationsFactory expectationsFactory;
	private Currency currency;

	/**
	 * Sets up the test.
	 */
	@Before
	public void setUp() {
		facade = new PriceLookupFacadeImpl();
		facade.setPriceLookupService(priceLookupService);
	    beanFactory = context.mock(BeanFactory.class);
		
	    expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		currency = Currency.getInstance("USD");
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test that all tiers from price are added to dtos as DTO one tier per one dto.
	 */
	@Test
	public void testAddEachTierAsDisplayPriceDTO() {
		
		final List<DisplayPriceDTO> dtos = new ArrayList<DisplayPriceDTO>();
		
		final PriceListDescriptor pld = context.mock(PriceListDescriptor.class, "pld");
		final ProductSku productSku = context.mock(ProductSku.class, "productSku");
		final Price price = context.mock(Price.class, "price");
		
		final SortedMap<Integer, PriceTier> sortedTiers = new TreeMap<Integer, PriceTier>();
		final PriceTier tier1 = context.mock(PriceTier.class, "tier1");
		final PriceTier tier2 = context.mock(PriceTier.class, "tier2");
		sortedTiers.put(1, tier1);
		sortedTiers.put(2, tier2);
		
		context.checking(new Expectations() { {
			oneOf(price).getPriceTiers(); will(returnValue(sortedTiers));
			allowing(pld).getName(); will(returnValue("price list"));
			allowing(pld).getGuid(); will(returnValue("price list guid"));
			allowing(productSku).getGuid(); will(returnValue("sku guid"));
			oneOf(tier1).getListPrice(); will(returnValue(D_11_99));
			allowing(tier1).getMinQty(); will(returnValue(1));
			oneOf(tier1).getSalePrice(); will(returnValue(null));
			oneOf(price).getLowestPrice(1); will(returnValue(MoneyFactory.createMoney(BigDecimal.ZERO, currency)));
			oneOf(tier2).getListPrice(); will(returnValue(D_9_99));
			allowing(tier2).getMinQty(); will(returnValue(2));
			oneOf(tier2).getSalePrice(); will(returnValue(null));
			oneOf(price).getLowestPrice(2); will(returnValue(MoneyFactory.createMoney(BigDecimal.ZERO, currency)));
		} });
		
		facade.addEachTierAsDisplayPriceDTO(dtos, pld, productSku, price);
		assertNotNull(dtos);
		assertEquals(2, dtos.size());
		assertEquals(D_11_99, dtos.get(0).getListPrice());
		assertEquals(D_9_99, dtos.get(1).getListPrice());
		
	}
	
	/**
	 * Test that only those tiers added to dtos that have tier quantity less or equal to 
	 * the tierQuantity provided.
	 */
	@Test
	public void testAddTiersThatComplyWithQuantityAsDisplayPriceDTO() {
		
		final List<DisplayPriceDTO> dtos = new ArrayList<DisplayPriceDTO>();
		
		final PriceListDescriptor pld = context.mock(PriceListDescriptor.class, "pld");
		final ProductSku productSku = context.mock(ProductSku.class, "productSku");
		final Price price = context.mock(Price.class, "price");
		
		final SortedMap<Integer, PriceTier> sortedTiers = new TreeMap<Integer, PriceTier>();
		final PriceTier tier1 = context.mock(PriceTier.class, "tier1");
		final PriceTier tier2 = context.mock(PriceTier.class, "tier2");
		sortedTiers.put(1, tier1);
		sortedTiers.put(2, tier2);
		
		context.checking(new Expectations() { {
			oneOf(price).getPriceTiers(); will(returnValue(sortedTiers));
			allowing(pld).getName(); will(returnValue("price list"));
			allowing(pld).getGuid(); will(returnValue("price list guid"));
			allowing(productSku).getGuid(); will(returnValue("sku guid"));
			oneOf(tier1).getListPrice(); will(returnValue(D_11_99));
			allowing(tier1).getMinQty(); will(returnValue(1));
			oneOf(tier1).getSalePrice(); will(returnValue(null));
			oneOf(tier2).getMinQty(); will(returnValue(2));
			oneOf(price).getLowestPrice(1); will(returnValue(MoneyFactory.createMoney(BigDecimal.ZERO, currency)));
		} });
		
		facade.addTiersThatComplyWithQuantityAsDisplayPriceDTO(dtos, pld, productSku, price, 1);
		assertNotNull(dtos);
		assertEquals(1, dtos.size());
		assertEquals(D_11_99, dtos.get(0).getListPrice());
		
	}
	
	/**
	 * Test that when getting price adjustments and none are found, an emtpy map is returned.
	 */
	@Test
	public void testGetPriceAdjustmentsForBundle() {
		
		final ProductBundle productBundle = new ProductBundleImpl();
		final ProductSku bundleDefaultSku = new ProductSkuImpl(); 
		productBundle.setDefaultSku(bundleDefaultSku);
		
		final Shopper shopper = context.mock(Shopper.class);
		final PriceListStack plStack = context.mock(PriceListStack.class);
		
		context.checking(new Expectations() {
			{
				allowing(shopper).isPriceListStackValid();
				will(returnValue(true));
				
				allowing(plStack).getPriceListStack();
				will(returnValue(Arrays.asList(INVALID_PRICELIST_GUID)));
				
				allowing(shopper).getPriceListStack();
				will(returnValue(plStack));

				allowing(priceLookupService).findPriceListWithPriceForProductSku(bundleDefaultSku, plStack);
				will(returnValue(null));
			}
		});
		
		assertEquals("Should return an empty map when no price adjustments were found.",
				Collections.emptyMap(),
				facade.getPriceAdjustmentsForBundle(productBundle, "WEYLANDYUTANI", shopper));
	}

	/**
	 * Test that if a ordering on shoppingitem doesn't match the productbundle constituent's ordering,
	 * We throw an exception. This can happen if a bundle structure has been changed.
	 */
	@Test (expected = ProductNotPurchasableException.class)
	public void testIterateChildrenWrongOrdering() {
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);

		ProductBundle bundle = new ProductBundleImpl();
		
		Product product = new ProductImpl();
		BundleConstituent constituent1 = createConstituent(product, 1);
		bundle.addConstituent(constituent1);
		ProductBundle bundle2 = new ProductBundleImpl();
		BundleConstituent constituent2 = createConstituent(bundle2, 2);
		Product product21 = new ProductImpl();
		BundleConstituent constituent21 = createConstituent(product21, 1);
		bundle2.addConstituent(constituent21);
		bundle.addConstituent(constituent2);
		
		ShoppingItem bundleItem = new ShoppingItemImpl();
		
		ShoppingItem productItem = new ShoppingItemImpl();
		productItem.setOrdering(2 + 2 + 2);
		ShoppingItem bundle2Item = new ShoppingItemImpl();
		ShoppingItem product21Item = new ShoppingItemImpl();
		product21Item.setOrdering(0);
		bundle2Item.setOrdering(1);
		
		bundle2Item.addChild(product21Item);
		bundleItem.addChild(productItem);
		bundleItem.addChild(bundle2Item);
		
		PriceLookupFacadeImpl facade = new PriceLookupFacadeImpl();
		AssignedBundleShoppingItemPriceBuilder bundlePriceBuilder = new AssignedBundleShoppingItemPriceBuilder(facade);
		Map<String, String> arrayList = new HashMap<String, String>();
		bundlePriceBuilder.populateMatchingBundleConstituentGuids(bundleItem.getChildren(), bundle.getConstituents(), arrayList);
	}

	private BundleConstituent createConstituent(final Product product, final int ordering) {
		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(product);
		constituent.setOrdering(ordering);
		constituent.setGuid(String.valueOf(System.currentTimeMillis()));
		return constituent;
	}

}
