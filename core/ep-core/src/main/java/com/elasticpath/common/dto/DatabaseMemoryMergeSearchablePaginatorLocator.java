package com.elasticpath.common.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocator;

/**
 *
 * @param <T>
 */
public class DatabaseMemoryMergeSearchablePaginatorLocator<T extends UniquelyIdentifiable> implements SearchablePaginatorLocator<T> {
		
	private SearchablePaginatorLocator<T> databasePaginatorLocator;
	
	// The set of items which have been updated.
	private final Map<Long, T> updatedMap = new HashMap<Long, T>();
	
	private final List<T> addedList = new ArrayList<T>();
	
	/**
	 * 
	 * @param databasePaginator The paginator to use to access the database with.
	 */
	public void setDatabasePaginatorLocator(final SearchablePaginatorLocator<T> databasePaginator) {
		this.databasePaginatorLocator = databasePaginator;		
	}


	@Override
	public List<T> findItems(final Page<T> unpopulatedPage, final String objectId, final List<SearchCriterion> searchCriteria) {
		List<T> databaseList = databasePaginatorLocator.findItems(unpopulatedPage, objectId, searchCriteria);
		
		List<T> returnList = new ArrayList<T>(databaseList.size());
		
		for (T databaseItem : databaseList) {
			T updatedItem = updatedMap.get(databaseItem.getUidPk());
			if (updatedItem == null) {
				returnList.add(databaseItem);
			} else {
				returnList.add(updatedItem);
			}
		}
		
		long databaseItemCount = databasePaginatorLocator.getTotalItems(searchCriteria, objectId);
		
		if (unpopulatedPage.getPageStartIndex() + unpopulatedPage.getPageSize() - 1 > databaseItemCount) {
			// Setup for switchover page
			int addedListIndex = 0;
			long indexInPage = databaseItemCount % unpopulatedPage.getPageSize();
			
			// Setup for pages after the switchover page.
			if (unpopulatedPage.getPageStartIndex() > databaseItemCount) {
				indexInPage = 0;
				// Note that the page is 1-based but the list is 0-based.
				addedListIndex = unpopulatedPage.getPageStartIndex() - (int) databaseItemCount - 1;
			}
			
			while (indexInPage < unpopulatedPage.getPageSize() && addedListIndex < addedList.size()) {
				T addedItem = addedList.get(addedListIndex++);
				returnList.add(addedItem);
				indexInPage++;
			}
		}
		
		return returnList;
	}

	@Override
	public long getTotalItems(final List<SearchCriterion> searchCriteria,
			final String objectId) {
		return databasePaginatorLocator.getTotalItems(searchCriteria, objectId) + addedList.size();
	}


	/**
	 * Adds updatedItem to the updated list. Items on this list will override items with the same
	 * uidPk in the return from the databasePaginatorLocator.
	 * @param updatedItem The item to update.
	 */
	public void update(final T updatedItem) {
		updatedMap.put(updatedItem.getUidPk(), updatedItem);
	}

	/**
	 * 
	 * @return The collection of items on which update has been called.
	 */
	public Collection<T> getUpdatedItems() {
		Collection<T> updatedItems = new ArrayList<T>();
		updatedItems.addAll(updatedMap.values());
		return Collections.unmodifiableCollection(updatedItems);
	}

	/**
	 * Returns the items that have been added.
	 * @return The added items.
	 */
	public Collection<T> getAddedItems() {
		return Collections.unmodifiableCollection(addedList);
	}

	/**
	 * Adds {@code addedItem} to the collection.
	 * @param addedItem The item to add.
	 */
	public void add(final T addedItem) {
		addedList.add(addedItem);		
	}
}
