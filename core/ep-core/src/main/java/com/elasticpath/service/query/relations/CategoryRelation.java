package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * A category query relationship.
 */
public class CategoryRelation extends AbstractRelation<Category> {

	private static final long serialVersionUID = 1L;

	/**
	 * Start building a category relation.
	 *
	 * @return the category relation
	 */
	public static CategoryRelation having() {
		return new CategoryRelation();
	}
	
	/**
	 * With category uids.
	 *
	 * @param uids the uids
	 * @return the category relation
	 */
	public CategoryRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}

	/**
	 * With category uids.
	 *
	 * @param uids the uids
	 * @return the category relation
	 */
	public CategoryRelation uids(final Long... uids) {
		uids(Arrays.asList(uids));
		return this;
	}
	
	@Override
	public String getBeanName() {
		return ContextIdNames.CATEGORY;
	}

	@Override
	public String getAlias() {
		return "pc";
	}

	@Override
	protected Map<IdentifierType, String> initializeIdentifierMap() {
		Map<IdentifierType, String> identifierMap = new HashMap<IdentifierType, String>();
		identifierMap.put(IdentifierType.UID, "uidPk");
		return Collections.unmodifiableMap(identifierMap);
	}

	@Override
	protected Map<Class<?>, RelationJoin> initializeRelationMap() {
		Map<Class<?>, RelationJoin> relationMap = new HashMap<Class<?>, RelationJoin>();
		relationMap.put(Product.class, new RelationInfo("productCategories", "pc", null, "category", null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<Category> getRelationClass() {
		return Category.class;
	}

}
