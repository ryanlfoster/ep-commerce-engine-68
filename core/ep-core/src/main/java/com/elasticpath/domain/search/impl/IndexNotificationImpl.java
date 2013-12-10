package com.elasticpath.domain.search.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Default implementation of {@link IndexNotification}.
 */
@Entity
@Table(name = IndexNotificationImpl.TABLE_NAME)
@DataCache(enabled = false)
public class IndexNotificationImpl extends AbstractPersistableImpl implements IndexNotification {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TINDEXNOTIFY";
	
	private static final int INDEX_TYPE_LEN = 100;
	
	private long uidPk;
	
	private String indexType;
	
	private Long affectedUid;
	
	private String affectedEntityType;
	
	private UpdateType updateType;
	
	private String queryString;
	
	/**
	 * Gets the index type that is affected for this index update.
	 *
	 * @return the index type that is affected for this index update
	 */
	@Basic(optional = false)
	@Column(name = "INDEX_TYPE", nullable = false, length = INDEX_TYPE_LEN)
	protected String getIndexTypeInternal() {
		return indexType;
	}
	
	/**
	 * Sets the index type that is affected for this index update.
	 *
	 * @param indexType the index type that is affected for this index update
	 */
	protected void setIndexTypeInternal(final String indexType) {
		this.indexType = indexType;
	}

	/**
	 * Gets the {@link IndexType} that is affected for this index update.
	 *
	 * @return the {@link IndexType} that is affected for this index update
	 */
	@Transient
	public IndexType getIndexType() {
		return IndexType.findFromName(indexType);
	}
	
	/**
	 * Sets the {@link IndexType} that is affected for this index update.
	 *
	 * @param indexType the {@link IndexType} that is affected for this index update
	 */
	public void setIndexType(final IndexType indexType) {
		this.indexType = indexType.getIndexName();
	}

	/**
	 * Gets the affected object UID.
	 * 
	 * @return the affected object UID
	 */
	@Column(name = "AFFECTED_UID")
	public Long getAffectedUid() {
		return affectedUid;
	}

	/**
	 * Sets the affected object UID.
	 * 
	 * @param affectedUid the affected object UID
	 */
	public void setAffectedUid(final Long affectedUid) {
		this.affectedUid = affectedUid;
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
	
	/**
	 * Gets the string representation of the affected index entity.
	 *
	 * @return the string representation of the affected index entity
	 */
	@Column(name = "ENTITY_TYPE", length = AbstractEntityImpl.GUID_LENGTH)
	public String getAffectedEntityType() {
		return affectedEntityType;
	}
	
	/**
	 * Sets the string representation of the affected index entity.
	 *
	 * @param affectedEntityType the string representation of the affected index entity
	 */
	public void setAffectedEntityType(final String affectedEntityType) {
		this.affectedEntityType = affectedEntityType;
	}
	
	/**
	 * Gets the type of notification.
	 *
	 * @return the type of notification
	 */
	@Basic(optional = false)
	@Column(name = "UPDATE_TYPE", nullable = false, length = AbstractEntityImpl.GUID_LENGTH)
	@Enumerated(EnumType.STRING)
	public UpdateType getUpdateType() {
		return updateType;
	}
	
	/**
	 * Sets the type of notification.
	 *
	 * @param updateType the type of notification
	 */
	public void setUpdateType(final UpdateType updateType) {
		this.updateType = updateType;
	}
	
	/**
	 * Gets the query string used in the update process. The query string is used against the
	 * index to find the affected UIDs.
	 * 
	 * @return the query string used in the update process
	 */
	@Lob
	@Column(name = "QUERY_STRING", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getQueryString() {
		return queryString;
	}
	
	/**
	 * Sets the query string used in the update process. The query string is used against the
	 * index to find the affected UIDs.
	 * 
	 * @param queryString the query string used in the update process
	 */
	public void setQueryString(final String queryString) {
		this.queryString = queryString;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof IndexNotificationImpl)) {
			return false;
		}
		IndexNotificationImpl other = (IndexNotificationImpl) obj;
		
		if (!ObjectUtils.equals(affectedEntityType, other.affectedEntityType)) {
			return false;
		}
		if (!ObjectUtils.equals(affectedUid, other.affectedUid)) {
			return false;
		}
		if (updateType != other.updateType) {
			return false;
		}
		if (!ObjectUtils.equals(indexType, other.indexType)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(affectedEntityType);
		result = prime * result + ObjectUtils.hashCode(affectedUid);
		result = prime * result + ObjectUtils.hashCode(updateType);
		result = prime * result + ObjectUtils.hashCode(indexType);
		return result;
	}
	
}
