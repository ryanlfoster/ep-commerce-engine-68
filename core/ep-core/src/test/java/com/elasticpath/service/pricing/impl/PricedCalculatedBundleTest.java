package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceAdjustment;

/**
 * 
 * Test class for calculated bundle pricing.
 * 
 */
public class PricedCalculatedBundleTest {
	

	private static final double EXPECTED_COMPUTED_AMOUNT = 30.00;

	private PricedCalculatedBundle pricedCalculatedBundle;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * 
	 */
	@Before
	public void setUp() {
		ProductBundle bundle = new ProductBundleImpl();
		pricedCalculatedBundle = new PricedCalculatedBundle(bundle, null, null) {
			@Override
			protected Price getConstituentPrice(final BundleConstituent item) {
				return null;
			}
		};
	}
	
	/**
	 * 
	 */
	@Test
	public void testPricedBundle() {
		
		final int quantity = 1;
		
		final Price bundleItemPrice = context.mock(Price.class);
		
		final BundleConstituent constituent = context.mock(BundleConstituent.class);
		
		final PriceAdjustment priceAdjustment = context.mock(PriceAdjustment.class);
		
		final String plGuid = "somePlGuid";
		final PriceTier bundleItemPriceTierForMinQty = context.mock(PriceTier.class);
		final BigDecimal adjAmount = BigDecimal.valueOf(-5.00);
		final BigDecimal lowestItemPriceAmount = BigDecimal.valueOf(20.00);
		final Money lowestItemPrice = context.mock(Money.class);
		final int constituentItemQty  = 2; 
		context.checking(new Expectations() { {
			
		
			allowing(bundleItemPrice).getPriceTierByQty(quantity);		
			will(returnValue(bundleItemPriceTierForMinQty));
			
			allowing(bundleItemPriceTierForMinQty).getPriceListGuid();
			will(returnValue(plGuid));
			
			allowing(constituent).getPriceAdjustmentForPriceList(plGuid);
			will(returnValue(priceAdjustment));
			
			allowing(priceAdjustment).getAdjustmentAmount();
			will(returnValue(adjAmount));
			
			allowing(bundleItemPrice).getLowestPrice(quantity);
			will(returnValue(lowestItemPrice));
			
			allowing(lowestItemPrice).getAmount();
			will(returnValue(lowestItemPriceAmount));
			//
			
			allowing(constituent).getQuantity();
			will(returnValue(constituentItemQty));			
			
			
			
		} });
	
		BigDecimal computedAmount = pricedCalculatedBundle.getConstituentComputedAmount(
				constituent, bundleItemPrice, quantity, true);
		
		BigDecimal expectedAmount = BigDecimal.valueOf(EXPECTED_COMPUTED_AMOUNT);
		Assert.assertTrue("Bundle Price Calculated amount should be be 30.00", expectedAmount.equals(computedAmount));
		
	}
	
}
