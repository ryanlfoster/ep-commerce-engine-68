/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination.impl;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * The pagination configuration bean.
 */
public class PaginationConfigImpl implements PaginationConfig {

	private int pageSize;
	private DirectedSortingField[] sortingFields;
	private String objectId;
	private LoadTuner loadTuner;
	
	/**
	 *
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}
	/**
	 *
	 * @param objectId the objectId to set
	 */
	public void setObjectId(final String objectId) {
		this.objectId = objectId;
	}
	/**
	 *
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 *
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 *
	 * @return the sortingFields
	 */
	public DirectedSortingField[] getSortingFields() {
		return (DirectedSortingField[]) ArrayUtils.clone(sortingFields);
	}
	
	/**
	 *
	 * @param sortingFields the sortingFields to set
	 */
	public void setSortingFields(final DirectedSortingField... sortingFields) {
		this.sortingFields = (DirectedSortingField[]) ArrayUtils.clone(sortingFields);
	}
	/**
	 *
	 * @return the load tuner 
	 */
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}
	/**
	 *
	 * @param loadTuner the load tuner to set
	 */
	public void setLoadTuner(final LoadTuner loadTuner) {
		this.loadTuner = loadTuner;
	}
}
