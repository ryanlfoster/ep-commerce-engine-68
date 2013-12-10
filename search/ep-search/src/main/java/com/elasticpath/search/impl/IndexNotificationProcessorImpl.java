package com.elasticpath.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.search.IndexNotificationProcessor;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Default implementation of {@link IndexNotificationProcessor}.
 */
public class IndexNotificationProcessorImpl implements IndexNotificationProcessor {

	private IndexNotificationService indexNotificationService;

	private List<IndexNotification> collapsedNotifications = Collections.emptyList();

	private List<IndexNotification> rawNotifications = Collections.emptyList();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Finds all new notifications to process. Notifications are collapsed so that only the necessary notifications are returned. Use
	 * {@link #getRawNotifications()} to get the list of pre-processed {@link IndexNotification}s.
	 * 
	 * @param indexType the type of notifications to retrieve
	 * @return new notifications to process
	 * @see #getRawNotifications()
	 */
	public List<IndexNotification> findAllNewNotifications(final IndexType indexType) {
		findRawNotifications(indexType);
		return collapsedNotifications;
	}

	/**
	 * Retrieves the list of {@link IndexNotifications}s. Notifications are collapsed so that only the necessary notifications are returned. This
	 * list is a cached version of what is stored.
	 * 
	 * @return the list of {@link IndexNotification}s
	 * @see #findAllNewNotifications()
	 */
	public List<IndexNotification> getNotifications() {
		return collapsedNotifications;
	}

	/**
	 * Retrieves the list of raw {@link IndexNotification}s. These notifications have not been pre-processed in any way. These notifications are a
	 * cached version of what is stored.
	 * 
	 * @return the list of raw {@link IndexNotification}s
	 * @see #findAllNewRawNotifications()
	 */
	public List<IndexNotification> getRawNotifications() {
		return rawNotifications;
	}

	/**
	 * Retrieves the list of raw {@link IndexNotification}s. These notifications have not been pre-processed in any way. This list is freshly
	 * retrieved.
	 * 
	 * @param indexType the type of notifications to retrieve
	 * @return the list of raw {@link IndexNotification}s
	 */
	public List<IndexNotification> findAllNewRawNotifications(final IndexType indexType) {
		findRawNotifications(indexType);
		return rawNotifications;
	}

	/**
	 * Removes all of the stored notifications.
	 */
	public void removeStoredNotifications() {
		lock.writeLock().lock();
		try {
			for (IndexNotification notification : rawNotifications) {
				indexNotificationService.remove(notification);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void findRawNotifications(final IndexType indexType) {
		if (indexType == null) {
			throw new EpSystemException("indexType must not be null");
		}
		lock.writeLock().lock();
		try {
			rawNotifications = indexNotificationService.findByIndexType(indexType);
			collapsedNotifications = new ArrayList<IndexNotification>(rawNotifications);
			collapseNotifications(collapsedNotifications);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void collapseNotifications(final List<IndexNotification> notifications) {
		for (int i = 0; i < notifications.size(); ++i) {
			final IndexNotification notification = notifications.get(i);
			if (notification.getUpdateType() == UpdateType.DELETE_ALL || notification.getUpdateType() == UpdateType.REBUILD) {
				// the update type encompasses all previous ones, no point in processing them
				for (int j = 0; j < i; ++j) {
					notifications.remove(0);
				}
			}
		}
	}

	/**
	 * Sets the {@link IndexNotificationService} instance to use.
	 * 
	 * @param indexNotificationService the {@link IndexNotificationService} instance to use
	 */
	public void setIndexNotificationService(final IndexNotificationService indexNotificationService) {
		this.indexNotificationService = indexNotificationService;
	}
}
