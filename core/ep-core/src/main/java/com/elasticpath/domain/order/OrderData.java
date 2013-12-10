package com.elasticpath.domain.order;

import com.elasticpath.persistence.api.Persistable;

/**
 * Holds generic key/value information associated with an {@link Order}.
 */
public interface OrderData extends Persistable {
	/**
	 * @return the key
	 */
	String getKey();

	/**
	 * @return the value
	 */
	String getValue();

	/**
	 * Sets the OrderData value.
	 * @param value the value to set
	 */
	void setValue(String value);
}
