/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.dataimport.ImportAction;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportNotification;
import com.elasticpath.domain.dataimport.ImportNotificationMetadata;
import com.elasticpath.domain.dataimport.ImportNotificationState;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * A notification object wrapping information for the import job to be executed.
 */
@Entity
@Table(name = ImportNotificationImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportNotificationImpl extends AbstractLegacyPersistenceImpl implements ImportNotification {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The table name.
	 */
	public static final String TABLE_NAME = "TIMPORTNOTIFICATION";

	/**
	 * Metadata key - column delimiter.
	 */
	protected static final String KEY_COLUMN_DELIMITER = "column.delimiter";

	/**
	 * Metadata key - text qualifier.
	 */
	protected static final String KEY_TEXT_QUALIFIER = "text.qualifier";

	/**
	 * Metadata key - import type.
	 */
	protected static final String KEY_IMPORT_TYPE = "import.type";

	/**
	 * Metadata key - import source.
	 */
	protected static final String KEY_IMPORT_SOURCE = "import.source";

	/**
	 * Metadata key - reporting locale.
	 */
	protected static final String KEY_REPORTING_LOCALE = "reporting.locale";

	/**
	 * Metadata key - max allowed failed rows.
	 */
	protected static final String KEY_MAX_ALLOWED_FAILED_ROWS = "max.allowed.failed.rows";

	/**
	 * Metadata key - parameter map.
	 */
	protected static final String KEY_PARAMETER = "parameter";

	private long uidPk;

	private ImportJob importJob;

	private Date dateCreated;

	private ImportAction action;

	private CmUser cmUser;

	private String requestId;

	private ImportNotificationState state = ImportNotificationState.NEW;

	private Map<String, ImportNotificationMetadata> metadata = new HashMap<String, ImportNotificationMetadata>();

	private String changeSetGuid;

	@Override
	@ManyToOne(targetEntity = ImportJobImpl.class, optional = false)
	@JoinColumn(name = "IMPORT_JOB_UID", nullable = false)
	@ForeignKey
	public ImportJob getImportJob() {
		return importJob;
	}

	@Override
	public void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Transient
	public String getImportSource() {
		String importSource = getMetadataValue(KEY_IMPORT_SOURCE);
		if (StringUtils.isEmpty(importSource)) {
			return getImportJob().getCsvFileName();
		}
		return importSource;
	}

	@Override
	public void setImportSource(final String csvImportFile) {
		setMetadataValue(KEY_IMPORT_SOURCE, csvImportFile);
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_CREATED", nullable = false, insertable = false, updatable = false)
	public Date getDateCreated() {
		return dateCreated;
	}

	@Override
	public void setDateCreated(final Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	/**
	 * Gets the import action.
	 *
	 * @return the import action
	 */
	@Persistent(optional = false)
	@Column(name = "ACTION", nullable = false)
	@Externalizer("getName")
	@Factory("valueOf")
	public ImportAction getAction() {
		return action;
	}

	/**
	 * Sets the import action.
	 *
	 * @param action the import action
	 */
	public void setAction(final ImportAction action) {
		this.action = action;
	}

	/**
	 * Gets the CM User.
	 *
	 * @return the CM user
	 */
	@ManyToOne(targetEntity = CmUserImpl.class, optional = false)
	@JoinColumn(name = "CMUSER_UID", nullable = false)
	@ForeignKey
	public CmUser getInitiator() {
		return cmUser;
	}

	/**
	 * Sets the CM user.
	 *
	 * @param cmUser the CM user
	 */
	public void setInitiator(final CmUser cmUser) {
		this.cmUser = cmUser;
	}

	@Override
	@Transient
	public Locale getReportingLocale() {
		String value = getMetadataValue(KEY_REPORTING_LOCALE);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return LocaleUtils.toLocale(value);
	}

	@Override
	public void setReportingLocale(final Locale locale) {
		if (locale == null) {
			throw new IllegalArgumentException("Locale cannot be set to null");
		}
		setMetadataValue(KEY_REPORTING_LOCALE, String.valueOf(locale));
	}

	@Override
	@Transient
	public int getMaxAllowedFailedRows() {
		String value = getMetadataValue(KEY_MAX_ALLOWED_FAILED_ROWS);
		if (StringUtils.isEmpty(value)) {
			return getImportJob().getMaxAllowErrors();
		}
		return Integer.valueOf(value);
	}

	@Override
	public void setMaxAllowedFailedRows(final int maxAllowedFailedRows) {
		setMetadataValue(KEY_MAX_ALLOWED_FAILED_ROWS, maxAllowedFailedRows);
	}

	@Override
	@Basic(optional = false)
	@Column(name = "REQUEST_ID", nullable = false)
	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(final String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Returns the process ID which for this implementation is equal to the request ID.
	 *
	 * @return the request ID
	 */
	@Transient
	public String getProcessId() {
		return getRequestId();
	}

	/**
	 * Sets the process ID. This implementation sets the request ID.
	 *
	 * @param processId the process ID
	 */
	public void setProcessId(final String processId) {
		setRequestId(processId);
	}

	@Override
	@Persistent(optional = false)
	@Externalizer("getName")
	@Factory("valueOf")
	@Column(name = "NOTIFICATION_STATE", nullable = false)
	public ImportNotificationState getState() {
		return state;
	}

	@Override
	public void setState(final ImportNotificationState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return new ToStringBuilder("ImportNotification").
			append("action", getAction()).
			append("state", getState()).
			append("importSource", getImportSource()).
			append("processId", getProcessId()).
			append("importJob", getImportJob()).
			append("initiator", getInitiator()).
			toString();
	}

	@Override
	@Transient
	public char getImportSourceColDelimiter() {
		String value = getMetadataValue(KEY_COLUMN_DELIMITER);
		if (StringUtils.isEmpty(value)) {
			return getImportJob().getCsvFileColDelimeter();
		}
		return Character.valueOf(value.toCharArray()[0]);
	}

	/**
	 * Gets the value of a key.
	 *
	 * @param key the key
	 * @return the value
	 */
	@Transient
	private String getMetadataValue(final String key) {
		ImportNotificationMetadata metadataValue = getMetadata().get(key);
		if (metadataValue != null) {
			return metadataValue.getValue();
		}
		return null;
	}

	/**
	 * Gets the notification metadata.
	 *
	 * @return the metadata map
	 */
	@OneToMany(targetEntity = ImportNotificationMetadataImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKey(name = "key")
	@ElementJoinColumn(name = "IMPORT_NOTIFICATION_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	protected Map<String, ImportNotificationMetadata> getMetadata() {
		return metadata;
	}

	/**
	 * Sets the metadata map.
	 *
	 * @param metadata the metadata to set
	 */
	protected void setMetadata(final Map<String, ImportNotificationMetadata> metadata) {
		this.metadata = metadata;
	}

	@Override
	@Transient
	public char getImportSourceTextQualifier() {
		String value = getMetadataValue(KEY_TEXT_QUALIFIER);
		if (StringUtils.isEmpty(value)) {
			return getImportJob().getCsvFileTextQualifier();
		}
		return Character.valueOf(value.toCharArray()[0]);
	}

	@Override
	@Transient
	public ImportType getImportType() {
		String importTypeId = getMetadataValue(KEY_IMPORT_TYPE);
		if (StringUtils.isEmpty(importTypeId)) {
			return getImportJob().getImportType();
		}
		return AbstractImportTypeImpl.getInstance(Integer.valueOf(importTypeId));
	}

	@Override
	public void setImportSourceColDelimiter(final char colDelimeter) {
		setMetadataValue(KEY_COLUMN_DELIMITER, colDelimeter);
	}

	/**
	 * Sets the value of a key.
	 *
	 * @param key the key
	 * @param value the value
	 */
	private void setMetadataValue(final String key, final Object value) {
		ImportNotificationMetadata metadataEntry = getBean("importNotificationMetadata");
		metadataEntry.setKey(key);
		metadataEntry.setValue(String.valueOf(value));
		getMetadata().put(key, metadataEntry);
	}

	@Override
	public void setImportSourceTextQualifier(final char textQualifier) {
		setMetadataValue(KEY_TEXT_QUALIFIER, textQualifier);
	}

	@Override
	public void setImportType(final ImportType importType) {
		setMetadataValue(KEY_IMPORT_TYPE, importType.getTypeId());
	}

	@Override
	@Basic
	@Column(name = "CHANGESET_GUID")
	public String getChangeSetGuid() {
		return changeSetGuid;
	}

	@Override
	public void setChangeSetGuid(final String changeSetGuid) {
		this.changeSetGuid = changeSetGuid;
	}

	@Override
	@Transient
	public String getParameter() {
		return getMetadataValue(KEY_PARAMETER);
	}

	@Override
	public void setParameter(final String parameter) {
		setMetadataValue(KEY_PARAMETER, parameter);
	}
}
