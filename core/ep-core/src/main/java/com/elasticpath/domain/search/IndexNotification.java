package com.elasticpath.domain.search;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.IndexType;

/**
 * Represents a notification of a type of index that needs to be updated.
 */
public interface IndexNotification extends Persistable {

	/**
	 * Gets the {@link IndexType} that is affected for this index update.
	 *
	 * @return the {@link IndexType} that is affected for this index update
	 */
	IndexType getIndexType();
	
	/**
	 * Sets the {@link IndexType} that is affected for this index update.
	 *
	 * @param indexType the {@link IndexType} that is affected for this index update
	 */
	void setIndexType(IndexType indexType);
	
	/**
	 * Gets the affected object UID.
	 * 
	 * @return the affected object UID
	 */
	Long getAffectedUid();
	
	/**
	 * Sets the affected object UID.
	 * 
	 * @param affectedUid the affected object UID
	 */
	void setAffectedUid(Long affectedUid);
	
	/**
	 * Gets the string representation of the affected index entity.
	 *
	 * @return the string representation of the affected index entity
	 */
	String getAffectedEntityType();
	
	/**
	 * Sets the string representation of the affected index entity.
	 *
	 * @param affectedEntityType the string representation of the affected index entity
	 */
	void setAffectedEntityType(String affectedEntityType);
	
	/**
	 * Gets the type of notification.
	 *
	 * @return the type of notification
	 */
	UpdateType getUpdateType();
	
	/**
	 * Sets the type of notification.
	 *
	 * @param updateType the type of notification
	 */
	void setUpdateType(UpdateType updateType);
	
	/**
	 * Gets the query string used in the update process. The query string is used against the
	 * index to find the affected UIDs.
	 * 
	 * @return the query string used in the update process
	 */
	String getQueryString();
	
	/**
	 * Sets the query string used in the update process. The query string is used against the
	 * index to find the affected UIDs.
	 * 
	 * @param queryString the query string used in the update process
	 */
	void setQueryString(String queryString);
	
	/**
	 * Holds known types of affected entities.
	 */
	class AffectedEntityType {
		
		/**
		 * Notification of all items within the store to be updated.
		 */
		public static final String STORE = "store";
		
		/**
		 * Notification of all items within the category to be updated.
		 */
		public static final String CATEGORY = "category";
		
		/**
		 * Notification of a single item to be updated. This actual type is dependent on the index
		 * this is being applied to.
		 */
		public static final String SINGLE_UNIT = "singleUnit";
	}
}
