/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * The import notification state.
 */
@SuppressWarnings("PMD.UseSingleton")
public class ImportNotificationState extends AbstractExtensibleEnum<ImportNotificationState> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal
	 * @param name the state's name
	 */
	protected ImportNotificationState(final int ordinal, final String name) {
		super(ordinal, name, ImportNotificationState.class);
	}

	/** Ordinal constant for IN_PROCESS. */
	public static final int IN_PROCESS_ORDINAL = 1;

	/**
	 * In process state. The notification is being processed.
	 */
	public static final ImportNotificationState IN_PROCESS = new ImportNotificationState(IN_PROCESS_ORDINAL, "IN_PROCESS");

	/** Ordinal constant for NEW. */
	public static final int NEW_ORDINAL = 2;

	/**
	 * New state. The notification ha been created but not process or in process yet.
	 */
	public static final ImportNotificationState NEW = new ImportNotificationState(NEW_ORDINAL, "NEW");

	/** Ordinal constant for PROCESSED. */
	public static final int PROCESSED_ORDINAL = 3;

	/**
	 * Processed state. The notification has been processed.
	 */
	public static final ImportNotificationState PROCESSED = new ImportNotificationState(PROCESSED_ORDINAL, "PROCESSED");

	@Override
	protected Class<ImportNotificationState> getEnumType() {
		return ImportNotificationState.class;
	}

	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the import notification state
	 */
	public static ImportNotificationState valueOf(final String name) {
		return valueOf(name, ImportNotificationState.class);
	}
	
}
