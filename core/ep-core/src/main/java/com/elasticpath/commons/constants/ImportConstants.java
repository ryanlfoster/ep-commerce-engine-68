package com.elasticpath.commons.constants;


/**
 * Contains all constants used by the import manager.
 */
public final class ImportConstants {
	/**
	 * The import directory name.
	 */
	public static final String IMPORT_DIRECTORY_NAME = "import";

	/**
	 * The suffix of the error report file.
	 */
	public static final String IMPORT_ERROR_REPORT_SUFFIX = "_error_report.txt";

	/**
	 * The suffix of the leftover rows file.
	 */
	public static final String IMPORT_LEFT_OVER_SUFFIX = "_leftover_rows.txt";

	/**
	 * The strings to express <code>null</code> in import data files.
	 */
	public static final String[] IMPORT_NULL_VALUES = { GlobalConstants.NULL_VALUE, "" };

	/**
	 * The prefix of the validation error code -- not null.
	 */
	public static final String VALIDATION_ERROR_NONNULL_PREFIX = "validation.error.notnull.";

	/**
	 * Text email template for import report.
	 */
	public static final String EMAIL_IMPORT_REPORT_TXT_TEMPLATE = "import.report.txt";

	/**
	 * The attribute name of the "from" email address.
	 */
	public static final String EMAIL_FROM = "mail.cm.from";

	/**
	 * The attribute name of the email host.
	 */
	public static final String MAIL_HOST = "mail.host";

	/**
	 * Flush magic number.
	 */
	public static final int FLUSH_MAGIC_NUMBER = 27;

	/**
	 * Comma.
	 */
	public static final String COMMA = ",";

	/**
	 * Row separator in error report file.
	 */
	public static final String ROW_SEPERATOR = "========================================================================";

	/**
	 * The import commit unit - the number of input file rows committed to the
	 * database in a single transaction.
	 * 
	 * 100 has been selected as a default value because it exhibits reasonable
	 * performance characteristics whilst keeping and database locking/liveness
	 * issues to a minimum (not too many rows held in a single transaction).
	 * 
	 * Different databases, network setups and store store types, performance
	 * concerns, import types, etc, may affect contribute to the number that 
	 * should be used as the commit unit.
	 */
	public static final int COMMIT_UNIT = 100;

	/**
	 * Seperator for multi values in the short text type.
	 */
	public static final String SHORT_TEXT_MULTI_VALUE_SEPERATOR = ",";

}
