package com.elasticpath.importexport.exporter.delivery.impl;

import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;

/**
 * Abstract delivery method allows to provide initialization of the data common for all delivery methods.
 */
public abstract class AbstractDeliveryMethodImpl implements DeliveryMethod {
	
	/**
	 * Initialize target full path. 
	 * 
	 * @param target destination address: URL, file name, etc.
	 */
	public abstract void initialize(final String target);
}
