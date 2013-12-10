package com.elasticpath.domain.shoppingcart.impl;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test for shopping cart.
 */
public class ShoppingCartImplIntegrationTest extends DbTestCase {

	private ShoppingCartImpl shoppingCart;

	/**
	 * Tests that the shopping cart can be serialized even when services are 
	 * linked during a get.
	 * 
	 * @throws Exception if a failure is detected 
	 */
	@DirtiesDatabase
	@Test
	public void testSerialization() throws Exception {
		shoppingCart = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.getGiftCertificateService();
		shoppingCart.getRuleService();
		shoppingCart.getCouponUsageService();
		shoppingCart.getCouponService();
		shoppingCart.getTaxCalculationService();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

		objectOutputStream.writeObject(shoppingCart);
	}
}