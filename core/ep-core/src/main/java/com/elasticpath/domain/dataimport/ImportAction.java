/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Indicates the type of import notification.
 */
public class ImportAction extends AbstractExtensibleEnum<ImportAction> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal
	 * @param name the action's name
	 */
	protected ImportAction(final int ordinal, final String name) {
		super(ordinal, name, ImportAction.class);
	}

	/** Ordinal constant for LAUNCH_IMPORT. */
	public static final int LAUNCH_IMPORT_ORDINAL = 1;

	/**
	 * Launch import action.
	 */
	public static final ImportAction LAUNCH_IMPORT = new ImportAction(LAUNCH_IMPORT_ORDINAL, "LAUNCH_IMPORT");

	/** Ordinal constant for CANCEL_IMPORT. */
	public static final int CANCEL_IMPORT_ORDINAL = 2;

	/**
	 * Cancel import action.
	 */
	public static final ImportAction CANCEL_IMPORT = new ImportAction(CANCEL_IMPORT_ORDINAL, "CANCEL_IMPORT");

	@Override
	protected Class<ImportAction> getEnumType() {
		return ImportAction.class;
	}
	
	/**
	 * Get the enum value corresponding to the given name.
	 *
	 * @param name the name
	 * @return the import action
	 */
	public static ImportAction valueOf(final String name) {
		return valueOf(name, ImportAction.class);
	}
}
