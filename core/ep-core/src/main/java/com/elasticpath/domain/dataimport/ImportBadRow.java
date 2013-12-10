package com.elasticpath.domain.dataimport;

import java.util.List;
import java.util.Locale;

import com.elasticpath.csvimport.CsvRow;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents an bad row in a CSV import.
 */
public interface ImportBadRow extends CsvRow, Persistable {

	/**
	 * Returns a list of <code>ImportFault</code>.
	 *
	 * @return a list of <code>ImportFault</code>
	 */
	List<ImportFault> getImportFaults();

	/**
	 * Adds a list of <code>ImportFault</code>.
	 *
	 * @param importFaults the the list of <code>ImportFault</code> to add
	 */
	void addImportFaults(List<ImportFault> importFaults);

	/**
	 * Add the given <code>ImportFault</code> to the list.
	 *
	 * @param importFault the <code>ImportFault</code>
	 */
	void addImportFault(ImportFault importFault);

	/**
	 * Returns a list of error messages in the server's locale.
	 * @return a list of error messages
	 */
	List<String> getImportErrors();

	/**
	 * Returns a list of error messages.
	 * @param locale the locale to
	 * @return a list of error messages
	 */
	List<String> getImportErrors(Locale locale);
}
