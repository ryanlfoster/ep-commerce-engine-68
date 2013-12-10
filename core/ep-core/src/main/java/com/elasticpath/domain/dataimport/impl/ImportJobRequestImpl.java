/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import java.util.Locale;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * A request containing required data for a import job process to be initiated.
 */
public class ImportJobRequestImpl extends AbstractEpDomainImpl implements ImportJobRequest {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 500000000000L;

	private ImportJob importJob;

	private Locale locale;

	private CmUser initiator;

	private String importSource;

	private int maxAllowedFailedRows = -1; // by default no limit exists. 0 is reserver for this value if it was overwritten

	private final String requestId;

	private ImportType importType;

	private char textQualifier;

	private char columnDelimiter;

	private String changeSetGuid;

	private String parameter;

	/**
	 * Constructs the import job request.
	 * Assigns a unique request ID.
	 */
	public ImportJobRequestImpl() {
		requestId = getBean(ContextIdNames.RANDOM_GUID).toString();
	}

	/**
	 * Constructor.
	 *
	 * @param requestId the request ID
	 */
	public ImportJobRequestImpl(final String requestId) {
		this.requestId = requestId;
	}

	/**
	 *
	 * @return the importJob
	 */
	public ImportJob getImportJob() {
		return importJob;
	}
	/**
	 *
	 * @param importJob the importJob to set
	 */
	public void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
	}
	/**
	 *
	 * @return the locale
	 */
	public Locale getReportingLocale() {
		return locale;
	}
	/**
	 *
	 * @param locale the locale to set
	 */
	public void setReportingLocale(final Locale locale) {
		this.locale = locale;
	}
	/**
	 *
	 * @return the initiator
	 */
	public CmUser getInitiator() {
		return initiator;
	}
	/**
	 *
	 * @param initiator the initiator to set
	 */
	public void setInitiator(final CmUser initiator) {
		this.initiator = initiator;
	}
	/**
	 *
	 * @return the importSource
	 */
	public String getImportSource() {
		return importSource;
	}
	/**
	 *
	 * @param importSource the importSource to set
	 */
	public void setImportSource(final String importSource) {
		this.importSource = importSource;
	}

	/**
	 * {@inheritDoc}
	 * Falls back to the import job value if it was not specified.
	 */
	public int getMaxAllowedFailedRows() {
		if (maxAllowedFailedRows < 0 && getImportJob() != null) {
			return getImportJob().getMaxAllowErrors();
		}
		return maxAllowedFailedRows;
	}

	/**
	 *
	 * @param maxAllowedFailedRows the maxAllowedFailedRows to set
	 */
	public void setMaxAllowedFailedRows(final int maxAllowedFailedRows) {
		this.maxAllowedFailedRows = maxAllowedFailedRows;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	/**
	 * {@inheritDoc}
	 * Falls back to the import job value if it was not specified.
	 */
	public char getImportSourceColDelimiter() {
		if (columnDelimiter == 0 && getImportJob() != null) {
			return getImportJob().getCsvFileColDelimeter();
		}
		return columnDelimiter;
	}

	/**
	 * {@inheritDoc}
	 * Falls back to the import job value if it was not specified.
	 */
	public char getImportSourceTextQualifier() {
		if (textQualifier == 0 && getImportJob() != null) {
			return getImportJob().getCsvFileTextQualifier();
		}
		return textQualifier;
	}

	/**
	 * {@inheritDoc}
	 * Falls back to the import job value if it was not specified.
	 */
	public ImportType getImportType() {
		if (importType == null && getImportJob() != null) {
			return getImportJob().getImportType();
		}
		return importType;
	}

	@Override
	public void setImportSourceColDelimiter(final char columnDelimiter) {
		this.columnDelimiter = columnDelimiter;
	}

	@Override
	public void setImportSourceTextQualifier(final char textQualifier) {
		this.textQualifier = textQualifier;
	}

	@Override
	public void setImportType(final ImportType importType) {
		this.importType = importType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder("ImportJobRequestImpl").
			append(getImportJob()).
			append("initiator", getInitiator()).
			append("requestId", getRequestId()).
			append("importSource", getImportSource()).
			append("importType", getImportType()).
			append("colDelimiter", getImportSourceColDelimiter()).
			append("textQualifier", getImportSourceTextQualifier()).
			append("maxAllowedFailedRows", getMaxAllowedFailedRows()).
			toString();
	}

	@Override
	public String getChangeSetGuid() {
		return changeSetGuid;
	}

	@Override
	public void setChangeSetGuid(final String changeSetGuid) {
		this.changeSetGuid = changeSetGuid;
	}

	@Override
	public String getParameter() {
		return parameter;
	}

	@Override
	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}




}
