/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * ParameterValueRelation.
 */
public class ParameterValueRelation extends AbstractRelation<ParameterValue> {

	private static final long serialVersionUID = 1L;

	/**
	 * Start building a ContentSpace relation.
	 *
	 * @return the parameter value relation
	 */
	public static ParameterValueRelation having() {
		return new ParameterValueRelation();
	}

	/**
	 * With Uids.
	 *
	 * @param uids the uids
	 * @return the parameter value relation
	 */
	public ParameterValueRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}

	/**
	 * With Uids.
	 *
	 * @param uids the uids
	 * @return the parameter value relation
	 */
	public ParameterValueRelation uids(final Long... uids) {
		addValuesForIdentifier(IdentifierType.UID, Arrays.asList(uids));
		return this;
	}

	/**
	 * With Guids.
	 *
	 * @param guids the guids
	 * @return the parameter value relation
	 */
	public ParameterValueRelation guids(final Collection<String> guids) {
		addValuesForIdentifier(IdentifierType.GUID, guids);
		return this;
	}

	/**
	 * With Guids.
	 *
	 * @param guids the guids
	 * @return the parameter value relation
	 */
	public ParameterValueRelation guids(final String... guids) {
		addValuesForIdentifier(IdentifierType.GUID, Arrays.asList(guids));
		return this;
	}

	/**
	 * With Names.
	 *
	 * @param names the names
	 * @return the parameter value relation
	 */
	public ParameterValueRelation names(final Collection<String> names) {
		addValuesForIdentifier(IdentifierType.NAME, names);
		return this;
	}

	/**
	 * With Names.
	 *
	 * @param names the names
	 * @return the parameter value relation
	 */
	public ParameterValueRelation names(final String... names) {
		addValuesForIdentifier(IdentifierType.NAME, Arrays.asList(names));
		return this;
	}

	@Override
	public String getBeanName() {
		return ContextIdNames.DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE;
	}

	@Override
	public String getAlias() {
		return "pv";
	}

	/**
	 * Initialize identifier map.
	 *
	 * @return the map
	 */
	@Override
	protected Map<IdentifierType, String> initializeIdentifierMap() {
		Map<IdentifierType, String> identifierMap = new HashMap<IdentifierType, String>();
		identifierMap.put(IdentifierType.UID, "uidPk");
		identifierMap.put(IdentifierType.GUID, "guid");
		identifierMap.put(IdentifierType.NAME, "parameterName");
		return Collections.unmodifiableMap(identifierMap);
	}

	/**
	 * Initialize relation map.
	 *
	 * @return the map
	 */
	@Override
	protected Map<Class<?>, RelationJoin> initializeRelationMap() {
		Map<Class<?>, RelationJoin> relationMap = new HashMap<Class<?>, RelationJoin>();
		relationMap.put(ParameterValue.class, new RelationInfo(null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<ParameterValue> getRelationClass() {
		return ParameterValue.class;
	}


}
