package com.elasticpath.importexport.exporter.packager;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.delivery.DeliveryMethod;

/**
 * Packager Factory produces ready to use packagers based on packager configuration. 
 */
public interface PackagerFactory {

	/**
	 * Create packager by package type and initialize it with delivery method.
	 *
	 * @param packagerConfiguration contains type of packager to be created and some additional options
	 * @param deliveryMethod delivery method used for delivering
	 * @throws ConfigurationException if packager couldn't be configured 
	 * @return configured and ready to use packager
	 */
	Packager createPackager(final PackagerConfiguration packagerConfiguration, final DeliveryMethod deliveryMethod) throws ConfigurationException;
}
