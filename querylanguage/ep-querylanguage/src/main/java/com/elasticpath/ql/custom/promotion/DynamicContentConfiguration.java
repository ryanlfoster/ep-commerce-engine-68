package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;
import com.elasticpath.ql.parser.FetchType;

/**
 * Holds mapping between EpQL fields and field descriptors for dynamic content.
 */
public class DynamicContentConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("select dc.guid FROM DynamicContentImpl dc ");
		setQueryPostfix("ORDER BY dc.guid ASC");
		setFetchType(FetchType.GUID);
	}
	
}
