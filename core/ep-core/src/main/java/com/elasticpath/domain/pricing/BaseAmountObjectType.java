package com.elasticpath.domain.pricing;
/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Enumeration of base amount object types.
 */
public class BaseAmountObjectType extends AbstractExtensibleEnum<BaseAmountObjectType> {

	/** Ordinal constant for PRODUCT. */
	public static final int PRODUCT_ORDINAL = 1;

	/** Active change set state. */
	public static final BaseAmountObjectType PRODUCT = new BaseAmountObjectType(PRODUCT_ORDINAL, "PRODUCT");

	/** Ordinal constant for SKU. */
	public static final int SKU_ORDINAL = 2;

	/** Closed change set state. */
	public static final BaseAmountObjectType SKU = new BaseAmountObjectType(SKU_ORDINAL, "SKU");

	/** 
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * Constructs a new base amount object type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of the object type
	 */
	protected BaseAmountObjectType(final int ordinal, final String name) {
		super(ordinal, name, BaseAmountObjectType.class);
	}

	/**
	 * Get the enumeration list of base amount object types.
	 * 	 
	 * @return list of {@link Enum} types
	 */
	public static List<BaseAmountObjectType> getEnumList() {
		return new ArrayList<BaseAmountObjectType>(values(BaseAmountObjectType.class));
	}

	/**
	 * Utility method for retrieving a base amount object type code by its name.
	 * 
	 * @param objType the object type code
	 * @return an instance of {@link BaseAmountObjectType} or null if not found
	 */
	public static BaseAmountObjectType getObjectType(final String objType) {
		if (objType == null) {
			return null;
		}
		return valueOf(objType, BaseAmountObjectType.class);
	}
	
	/**
	 * To string.
	 *
	 * @return string representation of this object.
	 */
	@Override
	public String toString() {
		return getName();
	}

	@Override
	protected Class<BaseAmountObjectType> getEnumType() {
		return BaseAmountObjectType.class;
	}
	
	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the base amount object type
	 */
	public BaseAmountObjectType valueOf(final String name) {
		return valueOf(name, BaseAmountObjectType.class);
	}
}
