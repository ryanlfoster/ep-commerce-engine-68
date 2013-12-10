package com.elasticpath.service.query.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * Query relationship with a store.
 */
public class StoreRelation extends AbstractRelation<Store> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Map<Class<?>, RelationJoin> initializeRelationMap() {
		Map<Class<?>, RelationJoin> relationMap = new HashMap<Class<?>, RelationJoin>();
		relationMap.put(Product.class, new RelationInfo("productCategories", "pc", "pc.category.catalog = s.catalog", null, null));
		return Collections.unmodifiableMap(relationMap);
	}

	/**
	 * Start building a store relation.
	 *
	 * @return the store relation
	 */
	public static StoreRelation having() {
		return new StoreRelation();
	}
	
	/**
	 * With store uids.
	 *
	 * @param uids the uids
	 * @return the store relation
	 */
	public StoreRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}
	
	/**
	 * With store uids.
	 *
	 * @param uids the uids
	 * @return the store relation
	 */
	public StoreRelation uids(final Long... uids) {
		uids(Arrays.asList(uids));
		return this;
	}
	
	/**
	 * With store codes.
	 *
	 * @param codes the codes
	 * @return the store relation
	 */
	public StoreRelation codes(final Collection<String> codes) {
		addValuesForIdentifier(IdentifierType.CODE, codes);
		return this;
	}
	
	/**
	 * With store codes.
	 *
	 * @param codes the codes
	 * @return the store relation
	 */
	public StoreRelation codes(final String... codes) {
		codes(Arrays.asList(codes));
		return this;
	}
	
	@Override
	public String getBeanName() {
		return ContextIdNames.STORE;
	}

	@Override
	public String getAlias() {
		return "s";
	}
	
	@Override
	protected Map<IdentifierType, String> initializeIdentifierMap() {
		Map<IdentifierType, String> identifierMap = new HashMap<IdentifierType, String>();
		identifierMap.put(IdentifierType.UID, "uidPk");
		identifierMap.put(IdentifierType.CODE, "storeCode");
		return Collections.unmodifiableMap(identifierMap);
	}

	@Override
	public Class<Store> getRelationClass() {
		return Store.class;
	}

}
