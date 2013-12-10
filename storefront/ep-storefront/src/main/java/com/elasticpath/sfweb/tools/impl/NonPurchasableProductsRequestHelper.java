package com.elasticpath.sfweb.tools.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.ProductSkuService;
 
/**
 * Checks if any items were removed from the shopping cart due to un-availability and populates the session
 * with this information to be displayed in the storefront.  
 */
public class NonPurchasableProductsRequestHelper {
	
	private final ProductSkuService productSkuService;
	
	/**
	 * Constructor.
	 * @param productSkuService product sku service
	 */
	public NonPurchasableProductsRequestHelper(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}
	
	/**
	 * Gets non-purchasabel products from the cart, populates the request with this info and cleans 
	 * up the cart - we need to display this information only once.
	 *
	 * @param request request
	 * @param shoppingCart shopping cart
	 */
	public void setNonPurchasableProductsInfoToRequest(final HttpServletRequest request, 
			final ShoppingCart shoppingCart) {
		Collection<String> notPurchasableCartItemSkus = shoppingCart.getNotPurchasableCartItemSkus();
		
		if (notPurchasableCartItemSkus.isEmpty()) {
			return;
		}
		
		Set<String> unPurchasableProductNames = getUnPurchasableProductNames(notPurchasableCartItemSkus, shoppingCart.getLocale());

		setRequestAttributes(request, unPurchasableProductNames);
		cleanUpAfterRequestAttributesAreSet(notPurchasableCartItemSkus);
	}

	private void setRequestAttributes(final HttpServletRequest request, final Set<String> unPurchasableProductNames) {
		request.setAttribute("nonPurchasableItemsWereRemoved", true);
		request.setAttribute("nonPurchasableItemsProductNames", unPurchasableProductNames);
	}

	private void cleanUpAfterRequestAttributesAreSet(final Collection<String> notPurchasableCartItemSkus) {
		notPurchasableCartItemSkus.clear();
	}
	/**
	 * Gets product names corresponding to sku codes of un-purchasable items. 
	 *
	 * @param unPurchasableCartItems un-purchasable cart items to be removed
	 * @param locale locale for product display name
	 * @return set or product names for given sku codes
	 */
	protected Set<String> getUnPurchasableProductNames(final Collection<String> unPurchasableCartItems, final Locale locale) {
		Collection<ProductSku> unPurchasableSkus = productSkuService.findBySkuCodes(unPurchasableCartItems);
		
		Set<String> unPurchasableSkuNames = new HashSet<String>();
		
		for (ProductSku sku : unPurchasableSkus) {
			unPurchasableSkuNames.add(getProductDisplayName(locale, sku));
		}
		return unPurchasableSkuNames;
	}

	/**
	 * Returns the display name for a given sku's product.
	 *
	 * @param locale locale to use
	 * @param sku sku
	 * @return display name for the provided locale
	 */
	protected String getProductDisplayName(final Locale locale, final ProductSku sku) {
		return sku.getProduct().getDisplayName(locale);
	}
	
}