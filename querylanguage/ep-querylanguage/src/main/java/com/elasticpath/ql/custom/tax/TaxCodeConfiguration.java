package com.elasticpath.ql.custom.tax;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EqQL fields and field descriptors for TaxCode.
 */
public class TaxCodeConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT tc.code FROM TaxCodeImpl tc ");
		setQueryPostfix(" ORDER BY tc.code ASC ");
	}
	
}
