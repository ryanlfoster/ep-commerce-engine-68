package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tools.sync.merge.configuration.EntityFilter;

/**
 * Filters out {@link ConditionalExpression}s which are named from the merging process.
 */
public class ConditionalExpressionFilter implements EntityFilter {

	@Override
	public boolean isFiltered(final Persistable value) {
		ConditionalExpression conditionalExpression = (ConditionalExpression) value;
		return conditionalExpression.isNamed();
	}
}
