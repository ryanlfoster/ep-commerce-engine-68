package com.elasticpath.ql.custom.shipping;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for Warehouse.
 */
public class WarehouseConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT w.code FROM WarehouseImpl w ");
		setQueryPostfix(" ORDER BY w.code ASC ");
	}
	
}
