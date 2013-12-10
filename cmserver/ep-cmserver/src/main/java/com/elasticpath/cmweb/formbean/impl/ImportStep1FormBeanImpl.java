package com.elasticpath.cmweb.formbean.impl;

import com.elasticpath.cmweb.formbean.ImportStep1FormBean;

/**
 * This bean represents import form.
 */
public class ImportStep1FormBeanImpl implements ImportStep1FormBean {

	private int importTaskType;

	private long importJob;

	private boolean editJob;

	/**
	 * Returns the import task type.
	 * 
	 * @return the import task type
	 */
	public int getImportTaskType() {
		return importTaskType;
	}

	/**
	 * Sets the import task type.
	 * 
	 * @param importTaskType the import task type
	 */
	public void setImportTaskType(final int importTaskType) {
		this.importTaskType = importTaskType;
	}

	/**
	 * Retrieves the import job.
	 * 
	 * @return the import job
	 */
	public long getImportJob() {
		return importJob;
	}

	/**
	 * Sets the import job.
	 * 
	 * @param importJob the import job
	 */
	public void setImportJob(final long importJob) {
		this.importJob = importJob;
	}

	/**
	 * Indicates whether to edit job.
	 * 
	 * @return true if editing, false otherwise
	 */
	public boolean isEditJob() {
		return editJob;
	}

	/**
	 * Set to indicate whether to edit job.
	 * 
	 * @param editJob true if editing, false otherwise
	 */
	public void setEditJob(final boolean editJob) {
		this.editJob = editJob;
	}

}
