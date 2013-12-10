package com.elasticpath.persistence.api;

import java.util.Collection;


/**
 * Load tuner that applies a particular fetch group to the fetch plan.
 */
public interface FetchGroupLoadTuner extends LoadTuner, Iterable<String> {

	/**
	 * Adds a list of fetch groups to added to the fetch plan.
	 * 
	 * @param fetchGroups the list of fetch groups to be added to the fetch plan
	 */
	void addFetchGroup(String... fetchGroups);

	/**
	 * Removes a list of fetch groups that were previously added. Ignores fetch groups that were
	 * not apart of this load tuner.
	 * 
	 * @param fetchGroups the list of fetch groups that were previously added
	 */
	void removeFetchGroup(String... fetchGroups);
	
	
	/**
	 * Gets the collection of fetch groups to use.
	 *
	 * @return fetchGroups the collection of fetch groups to use
	 */
	Collection<String> getFetchGroups();
}
