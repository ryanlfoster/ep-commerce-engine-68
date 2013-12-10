/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.rules;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Defines the possible states of a rule.
 */
public class RuleState extends AbstractExtensibleEnum<RuleState> {
	private static final long serialVersionUID = 1L;

	/** ACTIVE ordinal. */
	public static final int ACTIVE_ORDINAL = 0;

	/**
	 * State of a rule that the rules engine will attempt to apply.
	 */
	public static final RuleState ACTIVE = new RuleState(ACTIVE_ORDINAL, "ACTIVE");

	/** DISABLED ordinal. */
	public static final int DISABLED_ORDINAL = 1;

	/**
	 * State of a rule that should not be applied by the rules engine.
	 */
	public static final RuleState DISABLED = new RuleState(DISABLED_ORDINAL, "DISABLED");

	/** EXPIRED ordinal. */
	public static final int EXPIRED_ORDINAL = 2;

	/**
	 * State of a rule that has exceeded its expiry date. This should not be applied by the rules engine.
	 */
	public static final RuleState EXPIRED = new RuleState(EXPIRED_ORDINAL, "EXPIRED");

	private final int index;

	/**
	 * Instantiates a new rule state.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	public RuleState(final int ordinal, final String name) {
		super(ordinal, name, RuleState.class);
		this.index = ordinal;
	}

	/**
	 * Returns the index of the state.
	 *
	 * @return the numeric index of the state
	 */
	public int getIndex() {
		return this.index;
	}

	@Override
	protected Class<RuleState> getEnumType() {
		return RuleState.class;
	}

	/**
	 * Return the values.
	 *
	 * @return the collection of values
	 */
	public static Collection<RuleState> values() {
		return values(RuleState.class);
	}

    /**
     * Find the enum value with the specified name.
     * @param name the name
     * @return the enum value
     */
    public static RuleState valueOf(final String name) {
    	return valueOf(name, RuleState.class);
    }

}
