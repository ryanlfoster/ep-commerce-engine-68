/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.changeset;

import com.elasticpath.commons.pagination.SortingField;
import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * The change set member sorting fields.
 */
public class ChangeSetMemberSortingField extends AbstractExtensibleEnum<ChangeSetMemberSortingField> implements SortingField {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** Ordinal constant for OBJECT_TYPE. */
	public static final int OBJECT_TYPE_ORDINAL = 1;

	/**
	 * Object type sorting field.
	 */
	public static final SortingField OBJECT_TYPE = new ChangeSetMemberSortingField(OBJECT_TYPE_ORDINAL, "objectType");

	/** Ordinal constant for OBJECT_ID. */
	public static final int OBJECT_ID_ORDINAL = 2;

	/**
	 * Object ID sorting field.
	 */
	public static final SortingField OBJECT_ID = new ChangeSetMemberSortingField(OBJECT_ID_ORDINAL, "objectIdentifier");

	/** Ordinal constant for CHANGE_TYPE. */
	public static final int CHANGE_TYPE_ORDINAL = 3;

	/** Change Type sorting field. */
	public static final SortingField CHANGE_TYPE = new ChangeSetMemberSortingField(CHANGE_TYPE_ORDINAL, "action");

	/** Ordinal constant for OBJECT_NAME. */
	public static final int OBJECT_NAME_ORDINAL = 4;

	/** Object Name sorting field. */
	public static final SortingField OBJECT_NAME = new ChangeSetMemberSortingField(OBJECT_NAME_ORDINAL, "objectName");

	/** Ordinal constant for DATE_ADDED. */
	public static final int DATE_ADDED_ORDINAL = 5;

	/** Date Added sorting field. */
	public static final SortingField DATE_ADDED = new ChangeSetMemberSortingField(DATE_ADDED_ORDINAL, "dateAdded");

	/** Ordinal constant for ADDED_BY. */
	public static final int ADDED_BY_ORDINAL = 6;

	/** Added By sorting field. */
	public static final SortingField ADDED_BY = new ChangeSetMemberSortingField(ADDED_BY_ORDINAL, "addedByUserGuid");

	private final String fieldName;
	
	/**
	 * The name should be consistent with the domain field name or meta data key.
	 * For example, objectType is a domain field name
	 * objectName is a meta data key
	 * The name is used in "ORDER BY" clause in JPQL
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 */
	protected ChangeSetMemberSortingField(final int ordinal, final String name) {
		super(ordinal, name, ChangeSetMemberSortingField.class);
		this.fieldName = name;
	}

	@Override
	public String getName() {
		return fieldName;
	}
	
	@Override
	protected Class<ChangeSetMemberSortingField> getEnumType() {
		return ChangeSetMemberSortingField.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the ChangeSetMemberSortingField
	 */
	public static ChangeSetMemberSortingField valueOf(final String name) {
		return valueOf(name, ChangeSetMemberSortingField.class);
	}
}
