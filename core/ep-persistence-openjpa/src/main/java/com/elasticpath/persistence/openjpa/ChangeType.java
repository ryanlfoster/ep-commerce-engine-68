package com.elasticpath.persistence.openjpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Type-safe enumeration of audited change types.
 */
public class ChangeType extends AbstractExtensibleEnum<ChangeType> implements Serializable {

	/** 
	 * Serial version UID. 
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/** Ordinal constant for UPDATE. */
	public static final int UPDATE_ORDINAL = 1;

	/**
	 * Change type that represents an update.
	 */
	public static final ChangeType UPDATE = new ChangeType(UPDATE_ORDINAL, "Update");
	
	/** Ordinal constant for DELETE. */
	public static final int DELETE_ORDINAL = 2;

	/**
	 * Change type that represents a deletion. 
	 */
	public static final ChangeType DELETE = new ChangeType(DELETE_ORDINAL, "Delete");
	
	/** Ordinal constant for CREATE. */
	public static final int CREATE_ORDINAL = 3;

	/**
	 * Change type that represents a new object. 
	 */
	public static final ChangeType CREATE = new ChangeType(CREATE_ORDINAL, "Create");

	private final String changeName;
	
	/**
	 * Construct a new ChangeType from the given name.
	 *
	 * @param ordinal the ordinal
	 * @param name the name of a change type
	 */
	protected ChangeType(final int ordinal, final String name) {
		super(ordinal, name, ChangeType.class);
		changeName = name;
	}
	
	/**
	 * Return the list of change types.
	 * 
	 * @return a list of change types.
	 */
	public static List<ChangeType> getEnumList() {
		return new ArrayList<ChangeType>(values(ChangeType.class));
	}
	
	/**
	 * Get a change type by name.
	 * 
	 * @param name the name of the change type
	 * @return the change type
	 */
	public static ChangeType getEnum(final String name) {
		return valueOf(name, ChangeType.class);
	}
	
	/**
	 * Utility method for retrieving a change type by its name.
	 * 
	 * @param changeTypeName the state code
	 * @return an instance of {@link ChangeType} or null if not found
	 */
	public static ChangeType getChangeType(final String changeTypeName) {
		return valueOf(changeTypeName, ChangeType.class);
	}

	@Override
	protected Class<ChangeType> getEnumType() {
		return ChangeType.class;
	}
	
	@Override
	public String getName() {
		return changeName;
	}

}
