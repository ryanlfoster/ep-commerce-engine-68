package com.elasticpath.service.search;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Provides index related services that notifies indexers of certain milestones.
 */
public interface IndexNotificationService extends EpPersistenceService {

	/**
	 * Add the given {@link IndexNotification} to the notification queue.
	 * 
	 * @param notification the {@link IndexNotification} to save or update
	 * @return the saved notification
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any errors
	 */
	IndexNotification add(IndexNotification notification);
	
	/**
	 * Removes the given {@link IndexNotification}.
	 *
	 * @param notification the {@link IndexNotification} to remove
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any errors
	 */
	void remove(IndexNotification notification);
	
	/**
	 * Gets a list of {@link IndexNotification}s which are for the given {@link IndexType}.
	 * 
	 * @param indexType the type of index to
	 * @return a list of {@link IndexNotification}s which are for the given {@link IndexType}
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any errors
	 */
	List<IndexNotification> findByIndexType(IndexType indexType);
	
	/**
	 * Convenience method to send a notification about the given {@link UpdateType updateType} of
	 * the UIDs affected by the given {@link SearchCriteria searchCriteria}. The update type
	 * should generally be either {@link UpdateType#UPDATE UPDATE} or
	 * {@link UpdateType#DELETE DELETE}.
	 * 
	 * @param updateType update type to apply
	 * @param searchCriteria the search criteria used to find the affected UIDs
	 * @param isFuzzy whether the composed query should be fuzzy
	 * @throws com.elasticpath.base.exception.EpServiceException in case of any errors
	 */
	void addViaQuery(final UpdateType updateType, final SearchCriteria searchCriteria, final boolean isFuzzy);

	/**
	 * Convenience method for creating an index notification for the certain entity that requires an index update.
	 *  
	 * @param indexType index type of the entity
	 * @param affectedUid entity's UID
	 */
	void addNotificationForEntityIndexUpdate(IndexType indexType, Long affectedUid);
	
	/**
	 * Finds an index notification by an index type and an update type.
	 * 
	 * @param indexType the index type
	 * @param updateType the update type
	 * @return a collection of index notifications
	 */
	Collection<IndexNotification> findByIndexAndUpdateType(IndexType indexType, UpdateType updateType);
}
