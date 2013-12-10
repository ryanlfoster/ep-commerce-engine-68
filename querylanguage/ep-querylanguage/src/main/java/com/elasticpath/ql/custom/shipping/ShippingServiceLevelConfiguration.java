package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for ShippingServiceLevel.
 */
public class ShippingServiceLevelConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT ssl.code FROM ShippingServiceLevelImpl ssl ");
		setQueryPostfix(" ORDER BY ssl.code ASC ");
	}
	
}
