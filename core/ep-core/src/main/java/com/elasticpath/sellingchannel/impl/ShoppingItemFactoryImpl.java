package com.elasticpath.sellingchannel.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.sellingchannel.ShoppingItemFactory;

/**
 * {@see ShoppingItemFactory}.
 */
public class ShoppingItemFactoryImpl implements ShoppingItemFactory {
	
	private BeanFactory beanFactory;
	
	@Override
	public ShoppingItem createShoppingItem(final ProductSku sku, final Price price,
			final int quantity, final int ordering, final Map<String, String> itemFields) {	
		sanityCheck(sku, price);
		
		ShoppingItem shoppingItem = getShoppingItemBean();
		
		shoppingItem.setProductSku(sku);
				
        shoppingItem.setPrice(quantity, price);
        
        shoppingItem.setOrdering(ordering);
        
        shoppingItem.mergeFieldValues(itemFields);
		
		return shoppingItem;
	}

	/**
	 * @return a shopping item form bean instance
	 */
	protected ShoppingItem getShoppingItemBean() {
		return beanFactory.getBean(ContextIdNames.SHOPPING_ITEM);
	}

	/**
	 * Gets minimum quantity from product sku.
	 * 
	 * @param sku Product Sku.
	 * @return the quantity
	 */
	protected int getMinQuantity(final ProductSku sku) {
		return sku.getProduct().getMinOrderQty();
	}

	/**
	 * Do sanity check.
	 * @param sku product sku.
	 * @param price price.
	 */
	protected void sanityCheck(final ProductSku sku, final Price price) {
		if (sku.getProduct() == null) {
			throw new ProductUnavailableException("Product is not available on sku object.");
		}
		
		if (StringUtils.isEmpty(sku.getProduct().getCode())) {
			throw new ProductUnavailableException("Product guid is not given.");
		}
    }
	
	/**
	 * Sets the bean factory for creating beans.
	 * 
	 * @param beanFactory The bean factory.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
}
