package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.tags.TagSet;

/**
 * Test for CartSubtotalTagger.
 */
public class CartSubtotalTaggerTest {
	
	private static final long C_35_L = 35L;
	private static final long C_0_L = 0L;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
	private final SfRequestHelper requestHelper = context.mock(SfRequestHelper.class);
	
	private TagSet tagSet;
	private Shopper shopper;
	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;
	private CustomerSession customerSession;
	private CartSubtotalTagger listener;
	
	private static final String CART_SUBTOTAL = "CART_SUBTOTAL";
	
	/**
	 * Setting up instances.
	 */
	@Before
	public void setUp() {
		tagSet = new TagSet();
		request = new MockHttpServletRequest();
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(requestHelper, null, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(request);
		customerSession = context.mock(CustomerSession.class);
		listener = new CartSubtotalTagger();
		shopper = context.mock(Shopper.class);
	}
	
	/**
	 * Test that {@link CartSubtotalTagger} add a value to tag set.
	 * Testing a not-empty shopping cart.
	 */
	@Test
	public void testCartSubtotalListenerAddSubtotalToTagSet() {
		context.checking(new Expectations() { {
			allowing(customerSession).getCustomerTagSet(); will(returnValue(tagSet));
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
			allowing(customerSession).getShopper(); will(returnValue(shopper)); 
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			allowing(shoppingCart).getSubtotal(); will(returnValue(BigDecimal.valueOf(C_35_L)));
		} });

		listener.execute(customerSession, requestFacade);
		
		assertNotNull("Subtotal tag was null", tagSet.getTagValue(CART_SUBTOTAL));
		assertEquals("Failed to get the correct subtotal value from the tagset", 
				BigDecimal.valueOf(C_35_L), tagSet.getTagValue(CART_SUBTOTAL).getValue());
	}

	/**
	 * Test that {@link CartSubtotalTagger} adds a null value to tag set.
	 * This occurs if you've some items in the cart, but subtotal for the cart is 0.  
	 */
	@Test
	public void testCartSubtotalListenerAddSubtotalToTagSet2() {
		ShoppingItem item = context.mock(ShoppingItem.class);
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>();
		cartItems.add(item);
		context.checking(new Expectations() { {
			allowing(customerSession).getCustomerTagSet(); will(returnValue(tagSet));
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));
			allowing(customerSession).getShopper(); will(returnValue(shopper)); 
			allowing(shopper).getCurrentShoppingCart(); will(returnValue(shoppingCart));
			allowing(customerSession).getShopper(); 
			allowing(shoppingCart).getSubtotal(); will(returnValue(BigDecimal.valueOf(C_0_L)));
			allowing(shoppingCart).getCartItems(); will(returnValue(cartItems));
		} });

		listener.execute(customerSession, requestFacade);
		
		assertNotNull("Subtotal tag was null", tagSet.getTagValue(CART_SUBTOTAL));
		assertNull("Subtotal tag value should be null, but it wasn't", 
				tagSet.getTagValue(CART_SUBTOTAL).getValue());
	}

	
}
