package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for ShippingRegion.
 */
public class ShippingRegionConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT sr.name FROM ShippingRegionImpl sr ");
		setQueryPostfix(" ORDER BY sr.name ASC ");
	}
	
}
