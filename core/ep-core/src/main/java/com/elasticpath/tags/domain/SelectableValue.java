package com.elasticpath.tags.domain;

import java.io.Serializable;

/**
 * 
 * A pair of selectable value - name.
 * 
 * @param <VALUE> type of value object.
 * 
 */
public interface SelectableValue<VALUE> extends Serializable {

	/** 
	 * @return Value of value-name pair.
	 */
	VALUE getValue();

	/**
	 * @return Name of value-name pair.
	 */
	String getName();

}