/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.catalog;

import com.elasticpath.commons.pagination.SortingField;
import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Product SKU ordering fields.
 */
public class ProductSkuOrderingField extends AbstractExtensibleEnum<ProductSkuOrderingField> implements SortingField {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Ordinal constant for SKU_CODE. */
	public static final int SKU_CODE_ORDINAL = 1;

	/**
	 * Order by SKU code field.
	 */
	public static final SortingField SKU_CODE = new ProductSkuOrderingField(SKU_CODE_ORDINAL, "skuCodeInternal");

	private final String fieldName;
	
	/**
	 * Instantiates a new product sku ordering field.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of the field
	 */
	protected ProductSkuOrderingField(final int ordinal, final String name) {
		super(ordinal, name, ProductSkuOrderingField.class);
		this.fieldName = name;
	}
	
	@Override
	public String getName() {
		return fieldName;
	}

	@Override
	protected Class<ProductSkuOrderingField> getEnumType() {
		return ProductSkuOrderingField.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the ProductSkuOrderingField
	 */
	public static ProductSkuOrderingField valueOf(final String name) {
		return valueOf(name, ProductSkuOrderingField.class);
	}
}
