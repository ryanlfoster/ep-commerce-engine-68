package com.elasticpath.service.shoppingcart.impl;

import java.io.Serializable;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.NullPredicate;

import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Predicate that checks if a given object matches a specific shopping item.
 */
public class MatchingShoppingItemPredicate implements Predicate, Serializable {
	
	private static final long serialVersionUID = 641L;

    private final ShoppingItem comparisonItem;
    
    /**
     * Instantiates a new matching shopping item predicate.
     *
     * @param comparisonItem the comparison item
     */
    public MatchingShoppingItemPredicate(final ShoppingItem comparisonItem) {
		this.comparisonItem = comparisonItem;
	}

	/**
	 * Gets the single instance of MatchingShoppingItemPredicate.
	 *
	 * @param comparisonItem the comparison item
	 * @return single instance of MatchingShoppingItemPredicate
	 */
	public static Predicate getInstance(final ShoppingItem comparisonItem) {
    	if (comparisonItem == null) {
    		return NullPredicate.INSTANCE;
    	}
    	return new MatchingShoppingItemPredicate(comparisonItem);
    }
    
	@Override
	public boolean evaluate(final Object object) {
		if (!(object instanceof ShoppingItem)) {
			return false;
		}
		
		ShoppingItem shoppingItem = (ShoppingItem) object;
		
		// Only check the product if neither of the items is configurable
		if (comparisonItem.isConfigurable() || shoppingItem.isConfigurable()) {
			return false;
		}
		
        return comparisonItem.getProductSku().equals(shoppingItem.getProductSku());
	}

}
