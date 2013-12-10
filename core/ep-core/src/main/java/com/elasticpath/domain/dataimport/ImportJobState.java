package com.elasticpath.domain.dataimport;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * The import job state.
 */
@SuppressWarnings("PMD.UseSingleton")
public class ImportJobState extends AbstractExtensibleEnum<ImportJobState> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 *
	 * @param ordinal the ordinal
	 * @param name the enum name
	 */
	protected ImportJobState(final int ordinal, final String name) {
		super(ordinal, name, ImportJobState.class);
	}

	/** Ordinal constant for FINISHED. */
	public static final int FINISHED_ORDINAL = 1;

	/**
	 * Import job finished state.
	 */
	public static final ImportJobState FINISHED = new ImportJobState(FINISHED_ORDINAL, "FINISHED");

	/** Ordinal constant for CANCELLED. */
	public static final int CANCELLED_ORDINAL = 2;

	/**
	 * Import job cancelled state.
	 */
	public static final ImportJobState CANCELLED = new ImportJobState(CANCELLED_ORDINAL, "CANCELLED");

	/** Ordinal constant for RUNNING. */
	public static final int RUNNING_ORDINAL = 3;

	/**
	 * Import job running state.
	 */
	public static final ImportJobState RUNNING = new ImportJobState(RUNNING_ORDINAL, "RUNNING");

	/** Ordinal constant for VALIDATING. */
	public static final int VALIDATING_ORDINAL = 4;

	/**
	 * Import job validating state.
	 */
	public static final ImportJobState VALIDATING = new ImportJobState(VALIDATING_ORDINAL, "VALIDATING");

	/** Ordinal constant for QUEUED_FOR_VALIDATION. */
	public static final int QUEUED_FOR_VALIDATION_ORDINAL = 5;

	/**
	 * Import job queued for validation state.
	 */
	public static final ImportJobState QUEUED_FOR_VALIDATION = new ImportJobState(QUEUED_FOR_VALIDATION_ORDINAL, "QUEUED_FOR_VALIDATION");

	/** Ordinal constant for QUEUED_FOR_IMPORT. */
	public static final int QUEUED_FOR_IMPORT_ORDINAL = 6;

	/**
	 * Import job queued for import state.
	 */
	public static final ImportJobState QUEUED_FOR_IMPORT = new ImportJobState(QUEUED_FOR_IMPORT_ORDINAL, "QUEUED_FOR_IMPORT");

	/** Ordinal constant for VALIDATION_FAILED. */
	public static final int VALIDATION_FAILED_ORDINAL = 7;

	/**
	 * Import job validation failed state.
	 */
	public static final ImportJobState VALIDATION_FAILED = new ImportJobState(VALIDATION_FAILED_ORDINAL, "VALIDATION_FAILED");

	/** Ordinal constant for FAILED. */
	public static final int FAILED_ORDINAL = 8;

	/**
	 * Import job failed state.
	 */
	public static final ImportJobState FAILED = new ImportJobState(FAILED_ORDINAL, "FAILED");

	@Override
	public Class<ImportJobState> getEnumType() {
		return ImportJobState.class;
	}
	
	/**
	 * Get the enum value corresponding to the given name.
	 *
	 * @param name the name
	 * @return the import job state
	 */
	public static ImportJobState valueOf(final String name) {
		return valueOf(name, ImportJobState.class);
	}
	
}