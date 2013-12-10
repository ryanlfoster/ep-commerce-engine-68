/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport;

import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * Represents an import job runner.
 */
public interface ImportJobRunner extends Runnable {
	/**
	 * Validate the import job.
	 * 
	 * @param locale of the result messages.
	 * @return a list of <code>ImportBadRow</code>, or a empty list if there is no errors.
	 */
	List<ImportBadRow> validate(Locale locale);

	/**
	 * Initialise this runner with required data.
	 * 
	 * @param request the import job request
	 * @param importJobProcessId the import job process ID
	 */
	void init(ImportJobRequest request, String importJobProcessId);

	/**
	 * Returns the total number of rows for the import job being processed.
	 * 
	 * @return the total number of rows
	 */
	int getTotalRows();
}
