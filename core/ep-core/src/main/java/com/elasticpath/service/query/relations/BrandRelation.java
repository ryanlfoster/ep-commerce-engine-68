package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * A brand query relationship. 
 */
public class BrandRelation extends AbstractRelation<Brand> {

	private static final long serialVersionUID = 1L;

	/**
	 * start building a brand relation.
	 *
	 * @return the brand relation
	 */
	public static BrandRelation having() {
		return new BrandRelation();
	}
	
	/**
	 * With brand uids.
	 *
	 * @param uids the uids
	 * @return the brand relation
	 */
	public BrandRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}
	
	/**
	 * With brand uids.
	 *
	 * @param uids the uids
	 * @return the brand relation
	 */
	public BrandRelation uids(final Long... uids) {
		uids(Arrays.asList(uids));
		return this;
	}
	
	/**
	 * With brand codes.
	 *
	 * @param codes the codes
	 * @return the brand relation
	 */
	public BrandRelation codes(final Collection<String> codes) {
		addValuesForIdentifier(IdentifierType.CODE, codes);
		return this;
	}
	
	/**
	 * With brand codes.
	 *
	 * @param codes the codes
	 * @return the brand relation
	 */
	public BrandRelation codes(final String... codes) {
		codes(Arrays.asList(codes));
		return this;
	}
	
	@Override
	public String getBeanName() {
		return ContextIdNames.BRAND;
	}

	@Override
	public String getAlias() {
		return "b";
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
		relationMap.put(Product.class, new RelationInfo(null, null, null, "brand", null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<Brand> getRelationClass() {
		return Brand.class;
	}

}
