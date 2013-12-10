/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport;

import java.util.Locale;

import com.elasticpath.domain.cmuser.CmUser;


/**
 * A request containing required data for a import job process to be initiated.
 */
public interface ImportJobRequest {

	/**
	 * Gets the import job.
	 * 
	 * @return the import job
	 */
	ImportJob getImportJob();

	/**
	 * Sets the import job.
	 * 
	 * @param importJob the import job
	 */
	void setImportJob(ImportJob importJob);
	
	/**
	 * Gets the import source which could be a file.
	 * 
	 * @return the import source
	 */
	String getImportSource();
	
	/**
	 * Sets the import source.
	 * 
	 * @param importSource the import source
	 */
	void setImportSource(String importSource);
	
	/**
	 * Gets the initiator.
	 * 
	 * @return the CM user
	 */
	CmUser getInitiator();
	
	/**
	 * Sets the initiator.
	 * 
	 * @param cmUser the CM user
	 */
	void setInitiator(CmUser cmUser);
	
	/**
	 * Gets the reporting locale.
	 * 
	 * @return the reporting locale
	 */
	Locale getReportingLocale();
	
	/**
	 * Sets the reporting locale.
	 * 
	 * @param locale the locale
	 */
	void setReportingLocale(Locale locale);

	/**
	 * Sets the maximum allowed rows before a job is considered failed.
	 * 
	 * @param maxAllowedFailedRows the number of rows
	 */
	void setMaxAllowedFailedRows(int maxAllowedFailedRows);
	
	/**
	 * Gets the maximum allowed rows before a job is considered failed.
	 * 
	 * @return the max allowed failed rows
	 */
	int getMaxAllowedFailedRows();

	/**
	 * Gets this request's ID.
	 * 
	 * @return the request ID
	 */
	String getRequestId();

	/**
	 * Sets the import type to use.
	 * 
	 * @param importType the import type
	 */
	void setImportType(ImportType importType);

	/**
	 * Gets the import type.
	 * 
	 * @return the import type
	 */
	ImportType getImportType();
	
	/**
	 * Sets the column delimiter character.
	 * 
	 * @param colDelimeter the column delimiter
	 */
	void setImportSourceColDelimiter(char colDelimeter);

	/**
	 * Gets the import source column delimiter character.
	 * 
	 * @return the import source column delimiter character
	 */
	char getImportSourceColDelimiter();
	
	/**
	 * Sets the text qualifier character.
	 * 
	 * @param textQualifier the text qualifier
	 */
	void setImportSourceTextQualifier(char textQualifier);
	
	/**
	 * Gets the text qualifier character.
	 * 
	 * @return the text qualifier
	 */
	char getImportSourceTextQualifier();

	/**
	 * Gets the change set GUID.
	 * 
	 * @return the change set GUID
	 */
	String getChangeSetGuid();
	
	/**
	 * Sets the change set GUID.
	 * 
	 * @param changeSetGuid the change set GUID
	 */
	void setChangeSetGuid(String changeSetGuid);
	
	/**
	 * Get parameter.
	 * 
	 * @return the parameter
	 */
	String getParameter();
	
	/**
	 * Set the parameter.
	 * 
	 * @param parameter the parameter
	 */
	void setParameter(String parameter);
}
