package com.elasticpath.sfweb.controller.impl;


import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;
import com.elasticpath.sfweb.formbean.impl.ShoppingItemFormBeanImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Tests {@link DefaultProductConfigControllerImpl}.
 */
public class DefaultProductConfigControllerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**.*/
	@Test
	public void testGetExistingShoppingItem() {
		DefaultProductConfigControllerImpl controller = new DefaultProductConfigControllerImpl();
		final Long cartItemId = 123L;
		final ShoppingItemImpl expectedShoppingItem = new ShoppingItemImpl();
		
		final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
		final MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		customerSession.setShoppingCart(shoppingCart);
		controller.setRequestHelper(requestHelper);
		
		context.checking(new Expectations() { {
			ignoring(requestHelper).getCustomerSession(mockRequest); will(returnValue(customerSession));
			ignoring(requestHelper).getLongParameter(mockRequest, WebConstants.REQUEST_CART_ITEM_ID, 0L); will(returnValue(cartItemId));
			
			oneOf(shoppingCart).getCartItemById(cartItemId); will(returnValue(expectedShoppingItem));
		} });

		mockRequest.setParameter(WebConstants.REQUEST_CART_ITEM_ID, String.valueOf(cartItemId));
		ShoppingItem actualShoppingItem = controller.getExistingShoppingItem(mockRequest);
		assertEquals(expectedShoppingItem, actualShoppingItem);
		
	}
	
	/**
	 * Test that the map size changes when given a product bundle form bean.
	 */
	@Test
	public void testPopulateSelectionRuleMap() {
		DefaultProductConfigControllerImpl controller = new DefaultProductConfigControllerImpl();
		Map<String, Integer> map = new HashMap <String, Integer>();
		ShoppingItemFormBean rootBean = new ShoppingItemFormBeanImpl();
		rootBean.setProduct(new StoreProductImpl(new ProductBundleImpl()));
		controller.addPath(map, rootBean);
		
		assertEquals(1, map.values().size());
	}

}
