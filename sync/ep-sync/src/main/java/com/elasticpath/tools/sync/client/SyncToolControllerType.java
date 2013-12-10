/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * A type of a controller enumeration.
 */
public class SyncToolControllerType extends AbstractExtensibleEnum<SyncToolControllerType> {

	private static final long serialVersionUID = 1109315095126483988L;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal
	 * @param name the enumeration name
	 */
	protected SyncToolControllerType(final int ordinal, final String name) {
		super(ordinal, name, SyncToolControllerType.class);
	}

	/** Ordinal Constant for LOAD_CONTROLLER. */
	public static final int LOAD_CONTROLLER_ORDINAL = 1;
	
	/**
	 * A type for the load controller.
	 */
	public static final SyncToolControllerType LOAD_CONTROLLER = new SyncToolControllerType(LOAD_CONTROLLER_ORDINAL, "LOAD");
	
	/** Ordinal Constant for FULL_CONTROLLER. */
	public static final int FULL_CONTROLLER_ORDINAL = 2;
	
	/**
	 * A type for the full synchronization controller. 
	 */
	public static final SyncToolControllerType FULL_CONTROLLER = new SyncToolControllerType(FULL_CONTROLLER_ORDINAL, "FULL");
	
	/** Ordinal Constant for FULL_AND_SAVE_CONTROLLER. */
	public static final int FULL_AND_SAVE_CONTROLLER_ORDINAL = 3;
	
	/**
	 * A type for the full synchronization controller with save function. 
	 */
	public static final SyncToolControllerType FULL_AND_SAVE_CONTROLLER = new SyncToolControllerType(FULL_AND_SAVE_CONTROLLER_ORDINAL, 
			"FULL_AND_SAVE");

	/** Ordinal Constant for EXPORT_CONTROLLER. */
	public static final int EXPORT_CONTROLLER_ORDINAL = 4;

	/**
	 * A type for the the export only controller. 
	 */
	public static final SyncToolControllerType EXPORT_CONTROLLER = new SyncToolControllerType(EXPORT_CONTROLLER_ORDINAL, "EXPORT");

	@Override
	protected Class<SyncToolControllerType> getEnumType() {
		return SyncToolControllerType.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the SyncToolControllerType
	 */
	public static SyncToolControllerType valueOf(final String name) {
		return valueOf(name, SyncToolControllerType.class);
	}
}
