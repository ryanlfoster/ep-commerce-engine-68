package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.FetchType;

/**
 * Holds mapping between EpQL fields and field descriptors for dynamic content delivery.
 */
public class DynamicContentDeliveryConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("select dcd.guid FROM DynamicContentDeliveryImpl dcd ");
		setQueryPostfix(" ORDER BY dcd.guid ASC");
		setFetchType(FetchType.GUID);
	}
	
}
