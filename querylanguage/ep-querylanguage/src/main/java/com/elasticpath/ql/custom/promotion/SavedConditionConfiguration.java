package com.elasticpath.ql.custom.promotion;

import com.elasticpath.ql.parser.AbstractEpQLCustomConfiguration;

/**
 * Holds mapping between EPQL fields and field descriptors for Conditional expression.
 */
public class SavedConditionConfiguration extends AbstractEpQLCustomConfiguration {

	@Override
	public void initialize() {
		setQueryPrefix("SELECT tc.guid FROM ConditionalExpressionImpl tc WHERE tc.named = true");
		setQueryPostfix(" ORDER BY tc.guid ASC ");
	}

}
