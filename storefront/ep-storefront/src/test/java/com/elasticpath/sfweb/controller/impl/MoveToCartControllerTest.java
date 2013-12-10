package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Tests the {@code AddToCartSimpleController}.
 */
public class MoveToCartControllerTest {
	private static final String ERROR_FU = "Error-fu";

	private static final String SES_SHOPPING_CART = "sesShoppingCart";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private MockHttpServletRequest mockRequest;

	private MockHttpServletResponse mockResponse;

	private SfRequestHelper requestHelper;

	private CustomerSession customerSession;

	private ShoppingCart shoppingCart;

	private CartDirector cartDirector;

	private final MoveToCartControllerImpl controller = new MoveToCartControllerImpl();

	/**
	 * Setup objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of exception during setup
	 */
	@Before
	public void setUp() throws Exception {
		// Setup objects required for controller
		mockRequest = new MockHttpServletRequest();
		mockResponse = new MockHttpServletResponse();

		requestHelper = context.mock(SfRequestHelper.class);
		customerSession = context.mock(CustomerSession.class);
		shoppingCart = context.mock(ShoppingCart.class);
		cartDirector = context.mock(CartDirector.class);

		controller.setCartDirector(cartDirector);
		controller.setRequestHelper(requestHelper);

		// Set common expectations
		context.checking(new Expectations() {
			{
				allowing(requestHelper).getCustomerSession(mockRequest);
				will(returnValue(customerSession));

				allowing(customerSession).getShoppingCart();
				will(returnValue(shoppingCart));
			}
		});

	}

	/**
	 * Tests that the controller retrieves the parameters and calls CartDirectorImpl with them.<br>
	 * Uses default quantity of 1.
	 * 
	 * @throws Exception If any failure detected.
	 */
	@Test
	public void testHandleRequestInternal() throws Exception {
		final ShoppingItemDtoFactory dtoFactory = context.mock(ShoppingItemDtoFactory.class);

		final String currentSkuGuid = "SKU_FUN";
		final ShoppingItemDto dto = new ShoppingItemDto("", 1);

		controller.setShoppingItemDtoFactory(dtoFactory);

		mockRequest.getSession().setAttribute(SES_SHOPPING_CART, shoppingCart);
		mockRequest.setParameter("skuCode", currentSkuGuid);
		mockRequest.setParameter("qty", "2");

		context.checking(new Expectations() {
			{
				oneOf(dtoFactory).createDto(currentSkuGuid, 2);
				will(returnValue(dto));

				oneOf(cartDirector).moveItemFromWishListToCart(shoppingCart, dto);
			}
		});

		controller.handleRequestInternal(mockRequest, mockResponse);

	}

	/**
	 * Tests handling no skuCode parameter.
	 * 
	 * @throws Exception If any failure detected.
	 */
	@Test
	public void testRequestNoSku() throws Exception {

		// Note no skuCode parameter set.

		mockRequest.getSession().setAttribute(SES_SHOPPING_CART, shoppingCart);

		controller.handleRequestInternal(mockRequest, mockResponse);

		// expect a log entry if debug enabled but cannot verify

	}

	/**
	 * Tests that the controller retrieves the parameters and calls CartDirectorImpl with them.
	 * 
	 * @throws Exception If any failure detected.
	 */
	@Test
	public void testRequestSkuNotFound() throws Exception {
		final String currentSkuGuid = "SKU_FUN";
		final ShoppingItemDtoFactory dtoFactory = context.mock(ShoppingItemDtoFactory.class);
		final ShoppingItemDto dto = new ShoppingItemDto("", 1);

		controller.setErrorView(ERROR_FU);
		controller.setShoppingItemDtoFactory(dtoFactory);

		mockRequest.getSession().setAttribute(SES_SHOPPING_CART, shoppingCart);
		mockRequest.setParameter("skuCode", currentSkuGuid);

		context.checking(new Expectations() {
			{
				oneOf(dtoFactory).createDto(currentSkuGuid, 1);
				will(returnValue(dto));

				oneOf(cartDirector).moveItemFromWishListToCart(shoppingCart, dto);
				will(throwException(new EpServiceException("")));
			}
		});

		ModelAndView modelAndView = controller.handleRequestInternal(mockRequest, mockResponse);
		assertEquals("Expect error message code", "product.unavailable", modelAndView.getModel().get("error.message"));
		assertEquals("Expect error view set previously", ERROR_FU, modelAndView.getViewName());

	}

	/**
	 * Tests that the a product not purchasable produces the required error handling.
	 * 
	 * @throws Exception If any failure detected.
	 */
	@Test
	public void testRequestProductNotPurchasable() throws Exception {
		final String currentSkuGuid = "SKU_FUN";
		final ShoppingItemDtoFactory dtoFactory = context.mock(ShoppingItemDtoFactory.class);
		final ShoppingItemDto dto = new ShoppingItemDto("", 1);

		controller.setShoppingItemDtoFactory(dtoFactory);
		controller.setErrorView(ERROR_FU);

		mockRequest.getSession().setAttribute(SES_SHOPPING_CART, shoppingCart);
		mockRequest.setParameter("skuCode", currentSkuGuid);

		context.checking(new Expectations() {
			{
				oneOf(dtoFactory).createDto(currentSkuGuid, 1);
				will(returnValue(dto));

				oneOf(cartDirector).moveItemFromWishListToCart(shoppingCart, dto);
				will(throwException(new ProductNotPurchasableException("")));
			}
		});

		ModelAndView modelAndView = controller.handleRequestInternal(mockRequest, mockResponse);
		assertEquals("Expect error message code", "product.unavailable", modelAndView.getModel().get("error.message"));
		assertEquals("Expect error view set previously", ERROR_FU, modelAndView.getViewName());

	}
}
