package com.elasticpath.service.changeset;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Change Set member action enum. Objects are edited or deleted as part of Change Sets.
 */
@SuppressWarnings("PMD.UseSingleton")
public class ChangeSetMemberAction extends AbstractExtensibleEnum<ChangeSetMemberAction> {
	
	/** Ordinal constant for ADD. */
	public static final int ADD_ORDINAL = 1;

	/**
	 * Business object add action.
	 */
	public static final ChangeSetMemberAction ADD = new ChangeSetMemberAction(ADD_ORDINAL, "ADD");

	/** Ordinal constant for EDIT. */
	public static final int EDIT_ORDINAL = 2;

	/**
	 * Business object edit action.
	 */
	public static final ChangeSetMemberAction EDIT = new ChangeSetMemberAction(EDIT_ORDINAL, "EDIT");

	/** Ordinal constant for DELETE. */
	public static final int DELETE_ORDINAL = 3;

	/**
	 * Business object delete action.
	 */
	public static final ChangeSetMemberAction DELETE = new ChangeSetMemberAction(DELETE_ORDINAL, "DELETE");

	/** Ordinal constant for UNDEFINED. */
	public static final int UNDEFINED_ORDINAL = 4;

	/**
	 * Business object undefined action.
	 */
	public static final ChangeSetMemberAction UNDEFINED = new ChangeSetMemberAction(UNDEFINED_ORDINAL, "UNDEFINED");

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5000000002L;

	/**
	 * The max length of a action name.
	 */
	public static final int MAX_LENGTH = 64;

	/**
	 * Constructs a new change set action.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of the action
	 */
	protected ChangeSetMemberAction(final int ordinal, final String name) {
		super(ordinal, name, ChangeSetMemberAction.class);
		if (name.length() > MAX_LENGTH) {
			throw new IllegalArgumentException("Max allowed length for an action is " + MAX_LENGTH);
		}
	}

	/**
	 * Get the enumeration list of change set state codes.
	 *
	 * @return list of {@link Enum} types
	 */
	public static List<ChangeSetMemberAction> getEnumList() {
		return new ArrayList<ChangeSetMemberAction>(values(ChangeSetMemberAction.class));
	}

	/**
	 * Utility method for retrieving an action by name.
	 *
	 * @param name the action code
	 * @return an instance of {@link ChangeSetMemberAction} or null if not found
	 */
	public static ChangeSetMemberAction getChangeSetMemberAction(final String name) {
		return valueOf(name, ChangeSetMemberAction.class);
	}

	@Override
	protected Class<ChangeSetMemberAction> getEnumType() {
		return ChangeSetMemberAction.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the ChangeSetMemberAction
	 */
	public static ChangeSetMemberAction valueOf(final String name) {
		return valueOf(name, ChangeSetMemberAction.class);
	}
}
