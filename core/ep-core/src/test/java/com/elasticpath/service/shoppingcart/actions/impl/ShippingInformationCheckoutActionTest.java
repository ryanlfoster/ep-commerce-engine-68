package com.elasticpath.service.shoppingcart.actions.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * Tests the {@link ShippingInformationCheckoutAction}.
 */
public class ShippingInformationCheckoutActionTest {
	
	private static final String SHIPPING_SERVICE_LEVEL_GUID = "shippingServiceLevelGuid";
	private static final String CART_GUID = "cartGuid";

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private ShippingInformationCheckoutAction shippingInformationCheckoutAction;
	private ShoppingCart shoppingCart;
	private CheckoutActionContext checkoutActionContext; 
	private ShippingServiceLevelService shippingServiceLevelService;
	private ShippingServiceLevel shippingServiceLevel;
	private Address shippingAddress;
	
	/**
	 * Initialize mock objects.
	 */
	@Before
	public void initializeMockObjects() {
		shippingServiceLevelService = mockery.mock(ShippingServiceLevelService.class);
		shippingInformationCheckoutAction = new ShippingInformationCheckoutAction();
		shippingInformationCheckoutAction.setShippingServiceLevelService(shippingServiceLevelService);
		shoppingCart = mockery.mock(ShoppingCart.class);
		shippingServiceLevel = mockery.mock(ShippingServiceLevel.class);
		shippingAddress = mockery.mock(Address.class);
		checkoutActionContext = new CheckoutActionContextImpl(shoppingCart, null, false, false, null);
	}
	
	/**
	 * Validate cart with no shippable items.
	 */
	@Test
	public void validateCartWithNoShippableItems() {
		mockery.checking(new Expectations() { 
			{
				oneOf(shoppingCart).requiresShipping();
				will(returnValue(false));
			}
		});
		shippingInformationCheckoutAction.execute(checkoutActionContext);
	}
	
	/**
	 * Validate cart with missing address.
	 */
	@Test(expected = MissingShippingAddressException.class)
	public void validateCartWithMissingAddress() {
		mockery.checking(new Expectations() { 
			{
				oneOf(shoppingCart).requiresShipping();
				will(returnValue(true));
				
				oneOf(shoppingCart).getShippingAddress();
				will(returnValue(null));
				
				oneOf(shoppingCart).getGuid();
				will(returnValue(CART_GUID));
			}
			
		});
		
		shippingInformationCheckoutAction.execute(checkoutActionContext);
	}
	
	/**
	 * Validate cart with missing shipping service level.
	 */
	@Test(expected = MissingShippingServiceLevelException.class)
	public void validateCartWithMissingShippingServiceLevel() {
		
		final List<ShippingServiceLevel> validLevels = new ArrayList<ShippingServiceLevel>();
		validLevels.add(shippingServiceLevel);
		
		mockery.checking(new Expectations() { 
			{
				oneOf(shoppingCart).requiresShipping();
				will(returnValue(true));
				
				oneOf(shoppingCart).getShippingAddress();
				will(returnValue(shippingAddress));
				
				oneOf(shoppingCart).getSelectedShippingServiceLevel();
				will(returnValue(null));
				
				oneOf(shoppingCart).getGuid();
				will(returnValue(CART_GUID));
			}
			
		});
		
		shippingInformationCheckoutAction.execute(checkoutActionContext);
	}
	
	/**
	 * Validate cart with invalid shipping information.
	 */
	@Test(expected = InvalidShippingServiceLevelException.class)
	public void validateCartWithInvalidShippingServiceLevel() {
		
		final List<ShippingServiceLevel> validLevels = new ArrayList<ShippingServiceLevel>();
		validLevels.add(shippingServiceLevel);
		
		mockery.checking(new Expectations() { 
			{
				oneOf(shoppingCart).requiresShipping();
				will(returnValue(true));
				
				oneOf(shoppingCart).getShippingAddress();
				will(returnValue(shippingAddress));
				
				exactly(2).of(shoppingCart).getSelectedShippingServiceLevel();
				will(returnValue(shippingServiceLevel));
				
				oneOf(shippingServiceLevelService).retrieveShippingServiceLevel(shoppingCart);
				will(returnValue(Collections.emptyList()));
				
				oneOf(shippingServiceLevel).getGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));
				
				oneOf(shoppingCart).getGuid();
				will(returnValue(CART_GUID));
			}
			
		});
		
		shippingInformationCheckoutAction.execute(checkoutActionContext);
	}

	/**
	 * Validate cart with valid shipping information.
	 */
	@Test()
	public void validateCartWithValidShippingInformation() {
		final List<ShippingServiceLevel> validLevels = new ArrayList<ShippingServiceLevel>();
		validLevels.add(shippingServiceLevel);
		
		mockery.checking(new Expectations() { 
			{
				oneOf(shoppingCart).requiresShipping();
				will(returnValue(true));
				
				oneOf(shoppingCart).getShippingAddress();
				will(returnValue(shippingAddress));
				
				exactly(2).of(shoppingCart).getSelectedShippingServiceLevel();
				will(returnValue(shippingServiceLevel));
				
				oneOf(shippingServiceLevelService).retrieveShippingServiceLevel(shoppingCart);
				will(returnValue(validLevels));
			}
			
		});
		
		shippingInformationCheckoutAction.execute(checkoutActionContext);
	}

	
}
 