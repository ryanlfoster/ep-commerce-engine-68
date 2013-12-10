package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sfweb.util.impl.RequestHelperImpl;

/**
 * Tests the {@code AddToCartSimpleController}.
 */
public class AddToCartSimpleControllerTest {

	private static final String ERROR_FU = "Error-fu";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Tests handling no skuCode parameter.
	 * 
	 * @throws Exception If any failure detected.
	 */
	@Test
	public void testRequestNoSku() throws Exception {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		customerSession.getShopper().setCurrentShoppingCart(shoppingCart);
		
		final CartDirector cartDirector = context.mock(CartDirector.class);

		AddToCartSimpleControllerImpl controller = createAddToCartSimpleController(cartDirector);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// Note no skuCode parameter set.

		request.getSession().setAttribute(WebConstants.CUSTOMER_SESSION, customerSession);

		controller.handleRequestInternal(request, response);

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
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		customerSession.getShopper().setCurrentShoppingCart(shoppingCart);
		final CartDirector cartDirector = context.mock(CartDirector.class);
		final ShoppingItemDtoFactory dtoFactory = context.mock(ShoppingItemDtoFactory.class);
		final ShoppingItemDto dto = new ShoppingItemDto("", 1);

		AddToCartSimpleControllerImpl controller = createAddToCartSimpleController(cartDirector);
		controller.setShoppingItemDtoFactory(dtoFactory);
		controller.setErrorView(ERROR_FU);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.getSession().setAttribute(WebConstants.CUSTOMER_SESSION, customerSession);
		request.setParameter("skuCode", currentSkuGuid);

		context.checking(new Expectations() {
			{
				oneOf(dtoFactory).createDto(currentSkuGuid, 1); will(returnValue(dto));
				
				oneOf(cartDirector).addItemToCart(shoppingCart, dto); 
				will(throwException(new EpServiceException("")));
			}
		});

		ModelAndView modelAndView = controller.handleRequestInternal(request, response);
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
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSession();
		customerSession.getShopper().setCurrentShoppingCart(shoppingCart);
		final CartDirector cartDirector = context.mock(CartDirector.class);
		final ShoppingItemDtoFactory dtoFactory = context.mock(ShoppingItemDtoFactory.class);
		final ShoppingItemDto dto = new ShoppingItemDto("", 1);

		AddToCartSimpleControllerImpl controller = createAddToCartSimpleController(cartDirector);
		controller.setShoppingItemDtoFactory(dtoFactory);
		controller.setErrorView(ERROR_FU);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		request.getSession().setAttribute(WebConstants.CUSTOMER_SESSION, customerSession);
		request.setParameter("skuCode", currentSkuGuid);

		context.checking(new Expectations() {
			{
				oneOf(dtoFactory).createDto(currentSkuGuid, 1); will(returnValue(dto));
				oneOf(cartDirector).addItemToCart(shoppingCart, dto); 
				will(throwException(new ProductNotPurchasableException("")));
			}
		});

		ModelAndView modelAndView = controller.handleRequestInternal(request, response);
		assertEquals("Expect error message code", "product.unavailable", modelAndView.getModel().get("error.message"));
		assertEquals("Expect error view set previously", ERROR_FU, modelAndView.getViewName());

	}

	private AddToCartSimpleControllerImpl createAddToCartSimpleController(final CartDirector cartDirector) {
		
		RequestHelperImpl requestHelperImpl = new RequestHelperImpl();
		
		AddToCartSimpleControllerImpl controller = new AddToCartSimpleControllerImpl();
		controller.setCartDirector(cartDirector);
		controller.setRequestHelper(requestHelperImpl);
		return controller;
	}
}
