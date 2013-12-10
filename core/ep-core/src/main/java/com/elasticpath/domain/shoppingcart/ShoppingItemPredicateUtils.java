package com.elasticpath.domain.shoppingcart;

import org.apache.commons.collections.Predicate;

import com.elasticpath.service.shoppingcart.impl.MatchingShoppingItemPredicate;


/**
 * Provides reference implementations and utilities relating to ShoppingItems
 * for the Commons Collection Predicate functor interface. The supplied predicates are:
 * <ul>
 *   <li>MatchingShoppingItem - true if the object matches a specific shopping item.
 * </ul>
 * 
 * All the supplied predicates are Serializable.
 */
public final class ShoppingItemPredicateUtils {
	
	private ShoppingItemPredicateUtils() {
		// This class should not be instantiated
	}

	/**
	 * Matching shopping item predicate.
	 *
	 * @param shoppingItem the shopping item
	 * @return the predicate
	 */
	public static Predicate matchingShoppingItemPredicate(final ShoppingItem shoppingItem) {
		return MatchingShoppingItemPredicate.getInstance(shoppingItem);
	}
 
}
