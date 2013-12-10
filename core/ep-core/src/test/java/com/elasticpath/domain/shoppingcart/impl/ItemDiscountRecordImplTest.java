package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.CartAnySkuPercentDiscountActionImpl;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;

/**
 * Unit tests for {@code ItemDiscountRecordImpl}.
 */
public class ItemDiscountRecordImplTest {
	
	/**
	 * Tests that, when the coupon uses are fractional, that the use count rounds up.
	 */
	@Test
	public void testGetCouponUsesRequiredFractionalUse() {
		
		final int freeSkuQuantity = 2;
		final ItemDiscountRecordImpl itemDiscountRecordImpl = new ItemDiscountRecordImpl(null, null, freeSkuQuantity);
		
		final int usageLimit = 20;
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);
		couponConfig.setUsageLimit(usageLimit);
		
		final Coupon appliedCoupon = new CouponImpl();
		appliedCoupon.setCouponCode("xyz");
		appliedCoupon.setCouponConfig(couponConfig);
		
		RuleParameter skuParam = new RuleParameterImpl();
		skuParam.setKey(RuleParameter.SKU_CODE_KEY);
		skuParam.setValue("FREESKU");
		
		RuleParameter numSkusParam = new RuleParameterImpl();
		numSkusParam.setKey(RuleParameter.NUM_ITEMS_KEY);
		numSkusParam.setValue("3");
		
		final RuleAction action = new CartAnySkuPercentDiscountActionImpl();
		action.addParameter(skuParam);
		action.addParameter(numSkusParam);		
		
		int useCount = itemDiscountRecordImpl.getCouponUsesRequired(action, null);
			
		assertEquals("The use count should round up to the nearest whole", 1, useCount);
	}
	
	/**
	 * Tests that, when the number of items is multiple but the per coupon limit
	 * is one, that the result is the number of items.
	 */
	@Test
	public void testGetCouponUsesRequiredMultipleItems() {
		
		final int freeSkuQuantity = 2;
		final ItemDiscountRecordImpl itemDiscountRecordImpl = new ItemDiscountRecordImpl(null, null, freeSkuQuantity);
		
		final int usageLimit = 20;
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);
		couponConfig.setUsageLimit(usageLimit);
		
		final Coupon appliedCoupon = new CouponImpl();
		appliedCoupon.setCouponCode("xyz");
		appliedCoupon.setCouponConfig(couponConfig);
		
		RuleParameter skuParam = new RuleParameterImpl();
		skuParam.setKey(RuleParameter.SKU_CODE_KEY);
		skuParam.setValue("FREESKU");
		
		RuleParameter numSkusParam = new RuleParameterImpl();
		numSkusParam.setKey(RuleParameter.NUM_ITEMS_KEY);
		numSkusParam.setValue("1");
		
		final RuleAction action = new CartAnySkuPercentDiscountActionImpl();
		action.addParameter(skuParam);
		action.addParameter(numSkusParam);		
		
		int useCount = itemDiscountRecordImpl.getCouponUsesRequired(action, null);
			
		assertEquals("The use count should the number of items", 2, useCount);
	}
	
	/**
	 * Tests that, when the number of items is multiple but the per coupon limit
	 * is multiple, that the result is the number of items.
	 */
	@Test
	public void testGetCouponUsesRequiredMultipleItemsMultiplePerCoupon() {
		
		final int freeSkuQuantity = 6;
		final ItemDiscountRecordImpl itemDiscountRecordImpl = new ItemDiscountRecordImpl(null, null, freeSkuQuantity);
		
		final int usageLimit = 20;
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_ANY_USER);
		couponConfig.setUsageLimit(usageLimit);
		
		final Coupon appliedCoupon = new CouponImpl();
		appliedCoupon.setCouponCode("xyz");
		appliedCoupon.setCouponConfig(couponConfig);
		
		RuleParameter skuParam = new RuleParameterImpl();
		skuParam.setKey(RuleParameter.SKU_CODE_KEY);
		skuParam.setValue("FREESKU");
		
		RuleParameter numSkusParam = new RuleParameterImpl();
		numSkusParam.setKey(RuleParameter.NUM_ITEMS_KEY);
		numSkusParam.setValue("3");
		
		final RuleAction action = new CartAnySkuPercentDiscountActionImpl();
		action.addParameter(skuParam);
		action.addParameter(numSkusParam);		
		
		int useCount = itemDiscountRecordImpl.getCouponUsesRequired(action, null);
			
		assertEquals("The use count should the limit", 2, useCount);
	}
}
