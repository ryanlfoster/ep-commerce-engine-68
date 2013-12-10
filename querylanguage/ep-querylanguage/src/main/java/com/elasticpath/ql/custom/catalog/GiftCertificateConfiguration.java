package com.elasticpath.ql.custom.catalog;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EqQL fields and field descriptors for GiftCertificate.
 */
public class GiftCertificateConfiguration extends AbstractEpQLCustomConfiguration {
	
	@Override
	public void initialize() {
		setQueryPrefix("SELECT g.guid FROM GiftCertificateImpl g ");
		setQueryPostfix(" ORDER BY g.guid ASC ");
	}
	
}
