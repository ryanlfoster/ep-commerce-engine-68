package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Abstract controller for working with shopping cart items.
 */
public class AbstractCartControllerImpl extends SimplePageControllerImpl {
	/**	 */
	protected static final Logger LOG = Logger.getLogger(AbstractCartControllerImpl.class);

	private String successView;

	private String errorView;

	private CartDirector cartDirector;

	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	/**
	 * Add a sku to cart.
	 * 
	 * @param request http request
	 * @param shoppingCart cart instance
	 * @param skuCode sku to add
	 * @param qty quantity of sku to add
	 * @return ModelAndView
	 */
	protected ModelAndView addSkuToCart(final HttpServletRequest request, final ShoppingCart shoppingCart, final String skuCode, final int qty) {
		if (skuCode == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No valid skuCode passed to " + getClass());
			}
		} else {
			try {
				ShoppingItemDto dto = shoppingItemDtoFactory.createDto(skuCode, qty);
				cartDirector.addItemToCart(shoppingCart, dto);
				cartDirector.saveShoppingCart(shoppingCart);
			} catch (ProductNotPurchasableException exc) {
				LOG.warn("Product SKU[" + skuCode + "] is not available for purchase.", exc);
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			} catch (EpServiceException exc) {
				LOG.warn("Cannot add SKU to shopping cart: " + skuCode, exc);
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("error.message", "product.unavailable");
				return new ModelAndView(getErrorView(), model);
			}
		}

		return new ModelAndView(this.getSuccessView());
	}
	
	/**
	 * Sets the static view name.
	 * 
	 * @param successView name of the success view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the success view name.
	 * 
	 * @return name of the success view
	 */
	public String getSuccessView() {
		return this.successView;
	}

	/**
	 * Gets the error view.
	 * 
	 * @return the error view
	 */
	protected String getErrorView() {
		return this.errorView;
	}

	/**
	 * Sets the error view.
	 * 
	 * @param errorView the error view
	 */
	public void setErrorView(final String errorView) {
		this.errorView = errorView;
	}
	
	
	/**
	 * Get cart director.
	 * 
	 * @return the cart director
	 */
	public CartDirector getCartDirector() {
		return cartDirector;
	}

	/**
	 * @param cartDirector The cart director to set.
	 */
	public void setCartDirector(final CartDirector cartDirector) {
		this.cartDirector = cartDirector;
	}

	/**
	 * @param dtoFactory the dtoFactory to set
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory dtoFactory) {
		this.shoppingItemDtoFactory = dtoFactory;
	}

	/**
	 * Get shopping item dto factory.
	 * 
	 * @return the shopping item dto factory
	 */
	public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}
}
