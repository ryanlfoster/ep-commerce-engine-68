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
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.RelationJoin;

/**
 * ProductSku query relation.
 */
public class ProductSkuRelation extends AbstractRelation<ProductSku> {

	private static final long serialVersionUID = 1L;

	/**
	 * Start building a product sku relation.
	 *
	 * @return the store relation
	 */
	public static ProductSkuRelation having() {
		return new ProductSkuRelation();
	}

	/**
	 * With productsku uids.
	 *
	 * @param uids the uids
	 * @return the product sku relation
	 */
	public ProductSkuRelation uids(final Collection<Long> uids) {
		addValuesForIdentifier(IdentifierType.UID, uids);
		return this;
	}

	/**
	 * With productsku uids.
	 *
	 * @param uids the uids
	 * @return the product sku relation
	 */
	public ProductSkuRelation uids(final Long... uids) {
		uids(Arrays.asList(uids));
		return this;
	}

	/**
	 * With sku codes.
	 *
	 * @param codes the codes
	 * @return the sku relation
	 */
	public ProductSkuRelation codes(final Collection<String> codes) {
		addValuesForIdentifier(IdentifierType.CODE, codes);
		return this;
	}

	/**
	 * With sku codes.
	 *
	 * @param codes the codes
	 * @return the sku relation
	 */
	public ProductSkuRelation codes(final String... codes) {
		codes(Arrays.asList(codes));
		return this;
	}

	/**
	 * With sku guids.
	 *
	 * @param guids the guids
	 * @return the sku relation
	 */
	public ProductSkuRelation guids(final Collection<String> guids) {
		addValuesForIdentifier(IdentifierType.GUID, guids);
		return this;
	}

	/**
	 * With sku guids.
	 *
	 * @param guids the guids
	 * @return the sku relation
	 */
	public ProductSkuRelation guids(final String... guids) {
		guids(Arrays.asList(guids));
		return this;
	}

	@Override
	public String getBeanName() {
		return ContextIdNames.PRODUCT_SKU;
	}

	@Override
	public String getAlias() {
		return "ps";
	}

	@Override
	protected Map<IdentifierType, String> initializeIdentifierMap() {
		Map<IdentifierType, String> identifierMap = new HashMap<IdentifierType, String>();
		identifierMap.put(IdentifierType.UID, "uidPk");
		identifierMap.put(IdentifierType.CODE, "skuCodeInternal");
		identifierMap.put(IdentifierType.GUID, "guid");
		return Collections.unmodifiableMap(identifierMap);
	}

	@Override
	protected Map<Class<?>, RelationJoin> initializeRelationMap() {
		Map<Class<?>, RelationJoin> relationMap = new HashMap<Class<?>, RelationJoin>();
		relationMap.put(Product.class, new RelationInfo("productSkusInternal", "ps", null, null, null));
		return Collections.unmodifiableMap(relationMap);
	}

	@Override
	public Class<ProductSku> getRelationClass() {
		return ProductSku.class;
	}

}
