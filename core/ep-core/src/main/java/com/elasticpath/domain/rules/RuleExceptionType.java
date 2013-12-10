/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.rules;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * This enumerator lists all types of rule exceptions. The property key of each <code>RuleExceptionType</code> must match the corresponding
 * <code>RuleExceptionType</code>'s discriminator-value and the spring context bean id.
 */
public class RuleExceptionType extends AbstractExtensibleEnum<RuleExceptionType> {

	private static final long serialVersionUID = -8957636723945905622L;

	/** Category exception ordinal. */
	public static final int CATEGORY_EXCEPTION_ORDINAL = 1;
	
	/**
	 * Category exception.
	 */
	public static final RuleExceptionType CATEGORY_EXCEPTION = new RuleExceptionType(CATEGORY_EXCEPTION_ORDINAL, "categoryException"); 
	
	/** Product exception ordinal. */
	public static final int PRODUCT_EXCEPTION_ORDINAL = 2;
	
	/**
	 * Product exception.
	 */
	public static final RuleExceptionType PRODUCT_EXCEPTION = new RuleExceptionType(PRODUCT_EXCEPTION_ORDINAL, "productException");
	
	/** SKU exception ordinal. */
	public static final int SKU_EXCEPTION_ORDINAL = 3;

	/**
	 * SKU exception.
	 */
	public static final RuleExceptionType SKU_EXCEPTION = new RuleExceptionType(SKU_EXCEPTION_ORDINAL, "skuException");

	private final String propertyKey;

	/**
	 * Instantiates a new rule exception type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public RuleExceptionType(final int ordinal, final String name) {
		super(ordinal, name, RuleExceptionType.class);
		this.propertyKey = name;
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}

	@Override
	protected Class<RuleExceptionType> getEnumType() {
		return RuleExceptionType.class;
	}
	
}
