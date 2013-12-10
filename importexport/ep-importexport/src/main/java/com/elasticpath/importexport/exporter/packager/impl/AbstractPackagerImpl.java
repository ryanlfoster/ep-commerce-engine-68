package com.elasticpath.importexport.exporter.packager.impl;

import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;
import com.elasticpath.importexport.exporter.packager.Packager;

/**
 * Abstract packager allows to provide initialization of the data common for all the packagers.
 */
public abstract class AbstractPackagerImpl implements Packager {
	
	/**
	 * Initialize packager with delivery method and the package file name.
	 *
	 * @param deliveryMethod delivery method to be used
	 * @param packageName produced package's file name
	 */
	public abstract void initialize(final DeliveryMethod deliveryMethod, final String packageName);
}
