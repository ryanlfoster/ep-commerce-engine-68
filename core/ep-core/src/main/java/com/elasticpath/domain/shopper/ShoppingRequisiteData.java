package com.elasticpath.domain.shopper;

/**
 * Single point of contact for requisites which are needed for {@link com.elasticpath.domain.shoppingcart.ShoppingCart} operations.
 */
public interface ShoppingRequisiteData extends PriceListStackCache, TagSource,
		LocaleProvider, CurrencyProvider, StoreCodeProvider {


}
