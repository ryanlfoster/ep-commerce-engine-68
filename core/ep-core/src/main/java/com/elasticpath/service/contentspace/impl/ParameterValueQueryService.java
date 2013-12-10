/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.contentspace.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.impl.AbstractQueryService;
import com.elasticpath.service.query.relations.ParameterValueRelation;

/**
 * Query service for {@link ParameterValue}.
 */
public class ParameterValueQueryService extends AbstractQueryService<ParameterValue> {

	@Override
	protected Map<ResultType, String> initializeSelectFields() {
		Map<ResultType, String> fields = new HashMap<ResultType, String>();
		fields.put(ResultType.ENTITY, getSelfRelation().getAlias());
		fields.put(ResultType.GUID, getSelfRelation().getAlias() + ".guid");
		fields.put(ResultType.UID, getSelfRelation().getAlias() + ".uidPk");
		fields.put(ResultType.CONDITIONAL, "count(" + getSelfRelation().getAlias() + ")");
		return Collections.unmodifiableMap(fields);
	}

	@Override
	protected void processCriteria(final QueryCriteria<ParameterValue> criteria, final JpqlQueryBuilder queryBuilder) {
		// No additional criteria
	}

	@Override
	protected Relation<ParameterValue> getSelfRelation() {
		return new ParameterValueRelation();
	}

	@Override
	protected void configureLoadTuner(final LoadTuner loadTuner) {
		// No load tuners involved
	}

}
