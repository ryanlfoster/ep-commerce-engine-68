/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import com.elasticpath.domain.customer.Address;


/**
 * <code>OrderAddress</code> represents a North American address.
 *
 */
public interface OrderAddress extends Address {
 	    
	/**
	 * Initialize this order address with the information
	 * from the specified customer address.
	 * @param customerAddress the address with information
	 * to load into this order addresss
	 */
	void init(Address customerAddress);
	
}
