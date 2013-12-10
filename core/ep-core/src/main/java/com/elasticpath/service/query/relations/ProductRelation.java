/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * A product query relationship.
 */
public class ProductRelation extends AbstractRelation<Product> {

	private static final long serialVersionUID = 1L;

	/**
	 * Start building a product relation.
	 *
	 * @return the product relation
	 */
	public static ProductRelation having() {
		return new ProductRelation();
	}

	/**
	 * Having the given uids.
	 *
	 * @param uids the uids
	 * @return the product relation
	 */
	public ProductRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}

	/**
	 * Having the given uids.
	 *
	 * @param uids the uids
	 * @return the product relation
	 */
	public ProductRelation uids(final Long... uids) {
		uids(Arrays.asList(uids));
		return this;
	}

	/**
	 * Having the given codes.
	 *
	 * @param codes the codes
	 * @return the product relation
	 */
	public ProductRelation codes(final Collection<String> codes) {
		addValuesForIdentifier(IdentifierType.CODE, codes);
		return this;
	}

	/**
	 * Having the given codes.
	 *
	 * @param codes the codes
	 * @return the product relation
	 */
	public ProductRelation codes(final String... codes) {
		codes(Arrays.asList(codes));
		return this;
	}

	@Override
	public String getBeanName() {
		return "product";
	}

	@Override
	public String getAlias() {
		return "p";
	}

	@Override
	protected Map<IdentifierType, String> initializeIdentifierMap() {
		Map<IdentifierType, String> identifierMap = new HashMap<IdentifierType, String>();
		identifierMap.put(IdentifierType.UID, "uidPk");
		identifierMap.put(IdentifierType.CODE, "code");
		return Collections.unmodifiableMap(identifierMap);
	}

	@Override
	protected Map<Class<?>, RelationJoin> initializeRelationMap() {
		Map<Class<?>, RelationJoin> relationMap = new HashMap<Class<?>, RelationJoin>();
		relationMap.put(Product.class, new RelationInfo(null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<Product> getRelationClass() {
		return Product.class;
	}

}
