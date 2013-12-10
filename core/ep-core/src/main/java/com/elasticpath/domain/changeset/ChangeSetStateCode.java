/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.changeset;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Enumeration of change set state codes.
 */
public class ChangeSetStateCode extends AbstractExtensibleEnum<ChangeSetStateCode> {

	/** Ordinal constant for OPEN. */
	public static final int OPEN_ORDINAL = 1;

	/** Active change set state. */
	public static final ChangeSetStateCode OPEN = new ChangeSetStateCode(OPEN_ORDINAL, "OPEN");

	/** Ordinal constant for FINALIZED. */
	public static final int FINALIZED_ORDINAL = 2;

	/** Closed change set state. */
	public static final ChangeSetStateCode FINALIZED = new ChangeSetStateCode(FINALIZED_ORDINAL, "FINALIZED");

	/** Ordinal constant for LOCKED. */
	public static final int LOCKED_ORDINAL = 3;

	/** Locked change set state. */
	public static final ChangeSetStateCode LOCKED = new ChangeSetStateCode(LOCKED_ORDINAL, "LOCKED");

	/** 
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The max length of a state code.
	 */
	public static final int MAX_LENGTH = 64;

	/**
	 * Constructs a new change set state code.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of the state
	 */
	protected ChangeSetStateCode(final int ordinal, final String name) {
		super(ordinal, name, ChangeSetStateCode.class);
		if (name.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("Max allowed length for a change set code is " + MAX_LENGTH);
		}
	}

	/**
	 * Get the enumeration list of change set state codes.
	 * 	 
	 * @return list of {@link Enum} types
	 */
	public static List<ChangeSetStateCode> getEnumList() {
		return new ArrayList<ChangeSetStateCode>(values(ChangeSetStateCode.class));
	}

	/**
	 * Utility method for retrieving a state code by its name (state code).
	 * 
	 * @param stateCode the state code
	 * @return an instance of {@link ChangeSetStateCode} or null if not found
	 */
	public static ChangeSetStateCode getState(final String stateCode) {
		if (stateCode == null) {
			return null;
		}
		return valueOf(stateCode, ChangeSetStateCode.class);
	}

	@Override
	protected Class<ChangeSetStateCode> getEnumType() {
		return ChangeSetStateCode.class;
	}
}
