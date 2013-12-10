package com.elasticpath.sellingchannel.director.impl;

import java.util.Set;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.tree.Functor;
import com.elasticpath.commons.tree.impl.ProductPriceMemento;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 * Sets the looked up price to the tree of {@link ShoppingItem}s.
 */
public class PricingFunctor implements Functor<ShoppingItem, ProductPriceMemento> {

	private final PriceLookupFacade facade;
	private final Store store;
	private final Shopper shopper;
	private final Set<Long> ruleTracker;

	/**
	 * @param facade {@link PriceLookupFacade}
	 * @param store {@link Store}
	 * @param shopper {@link Shopper}
	 * @param ruleTracker applied rules
	 */
	public PricingFunctor(final PriceLookupFacade facade, final Store store,
			final Shopper shopper, final Set <Long> ruleTracker) {
		this.facade = facade;
		this.store = store;
		this.shopper = shopper;
		this.ruleTracker = ruleTracker;
	}

	@Override
	public ProductPriceMemento processNode(
			final ShoppingItem sourceNode,
			final ShoppingItem parentNode,
			final ProductPriceMemento traversalMemento,
			final int level) {
		ProductPriceMemento memento = traversalMemento;
		if (memento == null) {
			memento = new ProductPriceMemento();
		}

		// don't price nested bundles
		if (sourceNode.isBundle() && parentNode != null) {
			return memento;
		}

		final Price price = facade.getShoppingItemPrice(sourceNode, store, shopper, ruleTracker);
		sourceNode.setPrice(sourceNode.getQuantity(), price);

		memento.add(sourceNode.getProductSku(), price);
		return memento;
	}


}
