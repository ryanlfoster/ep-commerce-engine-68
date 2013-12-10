/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.index.solr.service;

import com.elasticpath.service.search.IndexType;

/**
 * The context of an {@link IndexBuildPolicy} operation.
 */
public class IndexBuildPolicyContext {
	
	private IndexType indexType;
	
	private int operationsCount = -1;
	
	private int documentsAdded = -1;

	/**
	 * The index type.
	 * 
	 * @return the index type
	 */
	public IndexType getIndexType() {
		return indexType;
	}

	/**
	 * Sets the index type.
	 * 
	 * @param indexType the index type
	 */
	public void setIndexType(final IndexType indexType) {
		this.indexType = indexType;
	}

	/**
	 * Gets the total operations done for an index build so far.
	 * 
	 * @return the operations count or -1 if there is no data
	 */
	public int getOperationsCount() {
		return operationsCount;
	}

	/**
	 *
	 * @param operationsCount the operationsCount to set
	 */
	public void setOperationsCount(final int operationsCount) {
		this.operationsCount = operationsCount;
	}

	/**
	 * Gets the count of documents added so far.
	 * 
	 * @return the count of added documents or -1 if there is no data
	 */
	public int getDocumentsAdded() {
		return documentsAdded;
	}

	/**
	 *
	 * @param documentsAdded the documents added count
	 */
	public void setDocumentsAdded(final int documentsAdded) {
		this.documentsAdded = documentsAdded;
	}
}
