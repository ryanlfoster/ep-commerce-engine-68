/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.springframework.util.CollectionUtils;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportJobStatusMutator;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The import job status implementation.<br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@Entity
@Table(name = ImportJobStatusImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportJobStatusImpl extends AbstractPersistableImpl implements ImportJobStatus, ImportJobStatusMutator {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The data source table name.
	 */
	public static final String TABLE_NAME = "TIMPORTJOBSTATUS";
	
	private long uidPk;
	private Date endTime;
	private Date startTime;
	private int totalRows;
	private int failedRows;
	private int currentRow;
	private ImportJob importJob;
	private ImportJobState state = ImportJobState.QUEUED_FOR_VALIDATION;
	private CmUser startedBy;
	
	private List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();

	private String processId;

	private Date lastModifiedDate;
	
	@Override
	@OneToMany(targetEntity = ImportBadRowImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "IMPORT_JOB_STATUS_UID", nullable = false)
	@ElementForeignKey
	public List<ImportBadRow> getBadRows() {
		return badRows;
	}
	
	/**
	 * Set bad rows. 
	 * 
	 * @param badRows the list of bad rows
	 */
	protected void setBadRows(final List<ImportBadRow> badRows) {
		this.badRows = badRows;
	}

	@Override
	@Basic
	@Column(name = "CURRENT_ROW")
	public int getCurrentRow() {
		return currentRow;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	public Date getEndTime() {
		return endTime;
	}

	@Override
	@Basic
	@Column(name = "FAILED_ROWS")
	public int getFailedRows() {
		return failedRows;
	}

	/**
	 * Sets the failed rows.
	 * 
	 * @param failedRows the failed rows
	 */
	public void setFailedRows(final int failedRows) {
		this.failedRows = failedRows;
	}
	
	@Override
	@Transient
	public int getLeftRows() {
		return getTotalRows() - getCurrentRow();
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	public Date getStartTime() {
		return startTime;
	}

	
	/**
	 * Sets the start time.
	 * 
	 * @param startTime the start time
	 */
	public void setStartTime(final Date startTime) {
		this.startTime = startTime;
	}

	@Override
	@Transient
	public int getSucceededRows() {
		int curRow = getCurrentRow();
		int failedRows = getFailedRows();
		int succRow = 0;
		if (curRow > failedRows) {
			succRow = curRow - failedRows;
		}
		return succRow;
	}

	@Override
	@Basic
	@Column(name = "TOTAL_ROWS")
	public int getTotalRows() {
		return totalRows;
	}

	@Override
	@Persistent
	@Column(name = "STATE", nullable = false)
	@Factory("valueOf")
	@Externalizer("getName")
	public ImportJobState getState() {
		return state;
	}
	
	@Override
	public void setState(final ImportJobState state) {
		this.state = state;
	}
	
	@Override
	@Transient
	public boolean isCanceled() {
		return ObjectUtils.equals(getState(), ImportJobState.CANCELLED);
	}

	@Override
	@Transient
	public boolean isFinished() {
		return isTerminalState(getState());
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
	public void setImportJob(final ImportJob importJob) {
		this.importJob = importJob;
	}

	@Override
	@ManyToOne(targetEntity = ImportJobImpl.class, optional = false)
	@JoinColumn(name = "IMPORT_JOB_UID", nullable = false)
	@ForeignKey
	public ImportJob getImportJob() {
		return importJob;
	}

	@Override
	public void addBadRow(final ImportBadRow importBadRow) {
		this.getBadRows().add(importBadRow);
	}

	@Override
	public void setEndTime(final Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public void setTotalRows(final int totalRows) {
		this.totalRows = totalRows;
	}

	@Override
	public void setCurrentRow(final int currentRow) {
		this.currentRow = currentRow;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ImportJobStatusImpl)) {
			return false;
		}
		ImportJobStatusImpl other = (ImportJobStatusImpl) obj;
		return new EqualsBuilder().
			append(this.importJob, other.importJob).
			append(this.state, other.state).
			isEquals();
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = prime;
		result = result * ObjectUtils.hashCode(this.importJob) + prime;
		result = result * ObjectUtils.hashCode(this.state) + prime;
		
		return result;
	}

	@Override
	@ManyToOne(targetEntity = CmUserImpl.class, optional = false)
	@JoinColumn(name = "CMUSER_UID", nullable = false)
	@ForeignKey
	public CmUser getStartedBy() {
		return startedBy;
	}

	@Override
	public void setStartedBy(final CmUser cmUser) {
		this.startedBy = cmUser;
	}
	
	/**
	 * Verifies if the state is a terminal state.
	 * 
	 * @param state the state
	 * @return true if this is a terminal state
	 */
	protected boolean isTerminalState(final ImportJobState state) {
		return CollectionUtils.contains(Arrays.asList(
				ImportJobState.FINISHED, 
				ImportJobState.FAILED, 
				ImportJobState.CANCELLED, 
				ImportJobState.VALIDATION_FAILED).iterator(), state);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImportJobStatus[").append(getImportJob()).
		append(", startedBy: ").append(getStartedBy()).
		append(", state: ").append(getState()).
		append("]");
		return builder.toString();
	}

	/**
	 * Return the import job process ID.
	 * 
	 * @return the guid.
	 */
	@Basic(optional = false)
	@Column(name = "PROCESS_ID", nullable = false)
	public String getProcessId() {
		return processId;
	}

	/**
	 * Set the process ID.
	 * 
	 * @param processId the process ID to set.
	 */
	public void setProcessId(final String processId) {
		this.processId = processId;
	}

	/**
	 * Returns the last modified date.
	 * 
	 * @return the last modified date
	 */
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Set the last modified date.
	 * 
	 * @param lastModifiedDate the last modified date
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
