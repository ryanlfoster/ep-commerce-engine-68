package com.elasticpath.importexport.importer.importers.impl;

/**
 * Class to hold import status string.
 */
public class ImportStatusHolder {

	private String status;

	/**
	 * Sets the import status.
	 * 
	 * @param status string
	 */
	public void setImportStatus(final String status) {
		this.status = status;
	}

	/**
	 * Gets the import status.
	 * 
	 * @return import status
	 */
	public String getImportStatus() {
		if (status == null) {
			return "";
		}
		return status;
	}

	@Override
	public String toString() {
		return getImportStatus();
	}
}
