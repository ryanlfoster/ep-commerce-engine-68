/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.domain.shoppingcart;

/**
 * The <code>ShoppingCartEvaluator</code> allows evaluations of shopping items.
 *
 * @param <T> the generic type of the evaluation
 */
public interface ShoppingCartEvaluator<T> {

	/**
	 * Evaluate the given ShoppingItem. <br>
	 * The ShoppingItem cannot be null.
	 *
	 * @param item the shopping item
	 * @return the result of evaluating the shopping item
	 */
	T evaluate(ShoppingItem item);

}
