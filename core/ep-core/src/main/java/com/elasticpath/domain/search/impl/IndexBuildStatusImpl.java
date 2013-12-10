package com.elasticpath.domain.search.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.base.Initializable;
import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.service.search.IndexType;

/**
 * <p>
 * An implementation of the IndexBuildStatus interface for OpenJPA database persistence.
 * </p>
 */
@Entity
@Table(name = IndexBuildStatusImpl.TABLE_NAME)
@DataCache(enabled = false)
public class IndexBuildStatusImpl extends AbstractPersistableImpl implements IndexBuildStatus, Initializable {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TINDEXBUILDSTATUS";

	private long uidPk;

	private String indexType;

	private Date lastBuildDate;

	private IndexStatus indexStatus;

	private int totalRecords;

	private int processedRecords;

	private Date operationStartDate;

	private Date lastModifiedDate;

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public
			long getUidPk() {
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

	@Basic(optional = false)
	@Column(name = "INDEX_TYPE", nullable = false)
	public String getIndexTypeInternal() {
		return indexType;
	}

	public void setIndexTypeInternal(final String type) {
		this.indexType = type;
	}

	@Override
	@Transient
	public IndexType getIndexType() {
		return IndexType.findFromName(indexType);
	}

	@Override
	public void setIndexType(final IndexType type) {
		this.indexType = type.getIndexName();
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_BUILD_DATE")
	public Date getLastBuildDate() {
		return lastBuildDate;
	}

	@Override
	public void setLastBuildDate(final Date date) {
		this.lastBuildDate = date;
	}

	@Override
	@Persistent(optional = false)
	@Column(name = "INDEX_STATUS")
	@Externalizer("getValue")
	@Factory("valueOf")
	public IndexStatus getIndexStatus() {
		return indexStatus;
	}

	@Override
	public void setIndexStatus(final IndexStatus status) {
		this.indexStatus = status;
	}

	@Override
	public void initialize() {

		if (this.getIndexStatus() == null) {
			this.setIndexStatus(IndexStatus.MISSING);
		}
	}

	@Override
	@Basic
	@Column(name = "TOTAL_RECORDS")
	public int getTotalRecords() {
		return totalRecords;
	}

	@Override
	@Basic
	@Column(name = "PROCESSED_RECORDS")
	public int getProcessedRecords() {
		return processedRecords;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPERATION_START_DATE")
	public Date getOperationStartDate() {
		return operationStartDate;
	}

	@Override
	public void setTotalRecords(final int totalRecords) {
		this.totalRecords = totalRecords;
	}

	@Override
	public void setProcessedRecords(final int processedRecords) {
		this.processedRecords = processedRecords;
	}

	@Override
	public void setOperationStartDate(final Date operationStartDate) {
		this.operationStartDate = operationStartDate;
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	@Basic(optional = false)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public String toString() {
		return "indexType=" + getIndexType() + " status=" + getIndexStatus() + " lastBuild=" + getLastBuildDate() + " operationStart="
				+ getOperationStartDate() + " processedRecords=" + getProcessedRecords() + " totalRecords=" + getTotalRecords() 
				+ " indexTypeInternal=" + getIndexTypeInternal();
	}
}
