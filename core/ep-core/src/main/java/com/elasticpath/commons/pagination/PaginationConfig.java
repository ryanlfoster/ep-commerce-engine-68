/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * The pagination configuration.
 */
public interface PaginationConfig {

	/**
	 *
	 * @return the objectId
	 */
	String getObjectId();

	/**
	 *
	 * @param objectId the objectId to set
	 */
	void setObjectId(String objectId);

	/**
	 *
	 * @return the pageSize
	 */
	int getPageSize();

	/**
	 *
	 * @param pageSize the pageSize to set
	 */
	void setPageSize(int pageSize);

	/**
	 *
	 * @return the sortingFields
	 */
	DirectedSortingField[] getSortingFields();

	/**
	 *
	 * @param sortingFields the sortingFields to set
	 */
	void setSortingFields(DirectedSortingField... sortingFields);
	
	/**
	 *
	 * @param loadTuner the load tuner to set
	 */
	void setLoadTuner(LoadTuner loadTuner);

	
	/**
	 *
	 * @return the load tuner
	 */
	LoadTuner getLoadTuner();
}
