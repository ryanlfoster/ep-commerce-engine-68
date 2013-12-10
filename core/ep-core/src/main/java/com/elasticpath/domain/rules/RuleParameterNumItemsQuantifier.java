/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.rules;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Number of item quantifier specifies how a number of items rule condition should be applied.
 */
public class RuleParameterNumItemsQuantifier extends AbstractExtensibleEnum<RuleParameterNumItemsQuantifier> {

	/** At Least ordinal. */
	public static final int AT_LEAST_ORDINAL = 1;

	/**
	 * At least quantifier is used when the number of items value is a minimum quantity.
	 */
    public static final RuleParameterNumItemsQuantifier AT_LEAST = new RuleParameterNumItemsQuantifier(AT_LEAST_ORDINAL, "AT_LEAST");

    /** Exact ordinal. */
    public static final int EXACTLY_ORDINAL = 2;

	/**
	 * Exact quantifier is used when the number of items value is an exact quantity.
	 */
	public static final RuleParameterNumItemsQuantifier EXACTLY = new RuleParameterNumItemsQuantifier(EXACTLY_ORDINAL, "EXACTLY");
	private static final long serialVersionUID = 2203927335989092980L;

	/**
	 * Instantiates a new rule parameter num items quantifier.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public RuleParameterNumItemsQuantifier(final int ordinal, final String name) {
		super(ordinal, name, RuleParameterNumItemsQuantifier.class);
	}

	@Override
	protected Class<RuleParameterNumItemsQuantifier> getEnumType() {
		return RuleParameterNumItemsQuantifier.class;
	}
	
}
