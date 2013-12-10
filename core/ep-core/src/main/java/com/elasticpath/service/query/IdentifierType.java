package com.elasticpath.service.query;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Types of identifiers - e.g. UID, GUID, CODE
 */
public class IdentifierType extends AbstractExtensibleEnum<IdentifierType> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The identifier class. */
	private final Class<?> identifierClass;

	/**
	 * Instantiates a new identifier type.
	 *
	 * @param ordinal the ordinal
	 * @param name the name
	 * @param identifierClass the identifier class
	 */
	protected IdentifierType(final int ordinal, final String name, final Class<?> identifierClass) {
		super(ordinal, name, IdentifierType.class);
		this.identifierClass = identifierClass;
	}

	/** Ordinal constant for UID. */
	public static final int UID_ORDINAL = 1;

	/** UID Identifier. */
	public static final IdentifierType UID = new IdentifierType(UID_ORDINAL, "uidPk", Long.class);

	/** Ordinal constant for GUID. */
	public static final int GUID_ORDINAL = 2;

	/** GUID identifier. */
	public static final IdentifierType GUID = new IdentifierType(GUID_ORDINAL, "guid", String.class);

	/** Ordinal constant for CODE. */
	public static final int CODE_ORDINAL = 3;

	/** CODE identifier. */
	public static final IdentifierType CODE = new IdentifierType(CODE_ORDINAL, "code", String.class);

	/** Ordinal constant for NAME. */
	public static final int NAME_ORDINAL = 4;

	/** NAME identifier. */
	public static final IdentifierType NAME = new IdentifierType(NAME_ORDINAL, "name", String.class);

	@Override
	protected Class<IdentifierType> getEnumType() {
		return IdentifierType.class;
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<IdentifierType> values() {
		return values(IdentifierType.class);
	}

	public Class<?> getIdentifierClass() {
		return identifierClass;
	}
}
