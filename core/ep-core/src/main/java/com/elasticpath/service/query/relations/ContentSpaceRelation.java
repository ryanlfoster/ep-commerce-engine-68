package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * The Class ContentSpaceRelation.
 */
public class ContentSpaceRelation extends AbstractRelation<ContentSpace> {

	private static final long serialVersionUID = 1L;

	/**
	 * Start building a ContentSpace relation.
	 *
	 * @return the content space relation
	 */
	public static ContentSpaceRelation having() {
		return new ContentSpaceRelation();
	}

	/**
	 * With Uids.
	 *
	 * @param uids the uids
	 * @return the content space relation
	 */
	public ContentSpaceRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}

	/**
	 * With Uids.
	 *
	 * @param uids the uids
	 * @return the content space relation
	 */
	public ContentSpaceRelation uids(final Long... uids) {
		addValuesForIdentifier(IdentifierType.UID, Arrays.asList(uids));
		return this;
	}

	/**
	 * With Guids.
	 *
	 * @param guids the guids
	 * @return the content space relation
	 */
	public ContentSpaceRelation guids(final Collection<String> guids) {
		addValuesForIdentifier(IdentifierType.GUID, guids);
		return this;
	}

	/**
	 * With Guids.
	 *
	 * @param guids the guids
	 * @return the content space relation
	 */
	public ContentSpaceRelation guids(final String... guids) {
		addValuesForIdentifier(IdentifierType.GUID, Arrays.asList(guids));
		return this;
	}

	/**
	 * With Names.
	 *
	 * @param names the names
	 * @return the content space relation
	 */
	public ContentSpaceRelation names(final Collection<String> names) {
		addValuesForIdentifier(IdentifierType.NAME, names);
		return this;
	}

	/**
	 * With Names.
	 *
	 * @param names the names
	 * @return the content space relation
	 */
	public ContentSpaceRelation names(final String... names) {
		addValuesForIdentifier(IdentifierType.NAME, Arrays.asList(names));
		return this;
	}

	/**
	 * With Name like.
	 *
	 * @param name the name
	 * @return the content space relation
	 */
	public ContentSpaceRelation nameLike(final String name) {
		addLikeValueForIdentifier(IdentifierType.NAME, name);
		return this;
	}

	@Override
	public String getBeanName() {
		return ContextIdNames.CONTENTSPACE;
	}

	@Override
	public String getAlias() {
		return "con";
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
		identifierMap.put(IdentifierType.NAME, "targetId");
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
		relationMap.put(ContentSpace.class, new RelationInfo(null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<ContentSpace> getRelationClass() {
		return ContentSpace.class;
	}

}
