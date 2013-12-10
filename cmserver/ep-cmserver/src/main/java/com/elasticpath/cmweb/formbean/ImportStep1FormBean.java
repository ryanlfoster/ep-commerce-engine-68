/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;

/**
 * <code>ImportStep1FormBean</code> represents the command object for import form.
 */
public interface ImportStep1FormBean {

	/**
	 * Returns the import task type.
	 * 
	 * @return the import task type
	 */
	int getImportTaskType();

	/**
	 * Sets the import task type.
	 * 
	 * @param importTaskType the import task type
	 */
	void setImportTaskType(final int importTaskType);

	/**
	 * Retrieves the import job.
	 * 
	 * @return the import job
	 */
	long getImportJob();

	/**
	 * Sets the import job.
	 * 
	 * @param importJob the import job
	 */
	void setImportJob(final long importJob);

	/**
	 * Indicates whether to edit job.
	 * 
	 * @return true if editing, false otherwise
	 */
	boolean isEditJob();

	/**
	 * Set to indicate whether to edit job.
	 * 
	 * @param editJob true if editing, false otherwise
	 */
	void setEditJob(boolean editJob);
}
