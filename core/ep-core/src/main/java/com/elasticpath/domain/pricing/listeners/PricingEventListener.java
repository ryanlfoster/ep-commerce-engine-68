package com.elasticpath.domain.pricing.listeners;

import com.elasticpath.persistence.api.Entity;

/**
 * A listener interface for pricing event.
 * @param <T> The type of input this listener will require for execution
 */
public interface PricingEventListener<T extends Entity> {

	/**
	 * Executes the action for this listener.
	 * @param param The input to the execute method
	 */
	void execute(T param);
}
