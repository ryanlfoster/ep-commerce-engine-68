package com.elasticpath.search.index.solr.builders.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * An implementation of <code>IndexBuilder</code> to create index for category.
 */
public class CategoryIndexBuilder extends AbstractIndexBuilder {
	
	private CategoryService categoryService;
	
	private IndexingPipeline<Collection<Long>, Category> categoryIndexingPipeline;

	/**
	 * Returns index build service name.
	 * 
	 * @return index build service name
	 */	
	public String getName() {
		return SolrIndexConstants.CATEGORY_SOLR_CORE;
	}

	/**
	 * Retrieve deleted UIDs.
	 * 
	 * @param lastBuildDate the last build date
	 * @return deleted UIDs.
	 */
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return categoryService.findUidsByDeletedDate(lastBuildDate);
	}

	/**
	 * Retrieve added or modified UIDs since last build.
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		final List<Long> directlyModifiedCategoryUids = categoryService.findUidsByModifiedDate(lastBuildDate);
		final List<Long> indirectlyModifiedCategoryUids = categoryService.findDescendantCategoryUids(directlyModifiedCategoryUids);
		final Set<Long> categoryUidsSet = new HashSet<Long>(directlyModifiedCategoryUids.size()
				+ indirectlyModifiedCategoryUids.size());
		categoryUidsSet.addAll(directlyModifiedCategoryUids);
		categoryUidsSet.addAll(indirectlyModifiedCategoryUids);
		return new ArrayList<Long>(categoryUidsSet);
	}

	/**
	 * Retrieve all UIDs.
	 * 
	 * @return all UIDs
	 */
	public List<Long> findAllUids() {
		return categoryService.findAllUids();
	}

	/**
	 * Publishes updates to the Solr server for the specified {@link Category} uids.  
	 * @param uids the {@link Category} uids to publish.
	 */
	public void submit(final Collection<Long> uids) {
		categoryIndexingPipeline.start(uids);
	}

	/**
	 * Sets the category service.
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	/**
	 * Return the index type this class builds.
	 * @return the index type this class builds.
	 */
	public IndexType getIndexType() {
		return IndexType.CATEGORY;
	}
	
	/**
	 * Throws UnsupportedOperationException, since this method has not been implemented
	 * and is not supported.
	 * 
	 * @param notification a DELETE index notification.
	 * @return collection of Order UIDs that need to be removed from the index.
	 * @throws UnsupportedOperationException if the notification is not a DELETE notification. 
	 */
	public Collection<Long> findUidsByNotification(final IndexNotification notification) {
		throw new UnsupportedOperationException("not supported");
	}

	/**
	 * @param categoryIndexingPipeline the categoryIndexingPipeline to set
	 */
	public void setCategoryIndexingPipeline(final IndexingPipeline<Collection<Long>, Category> categoryIndexingPipeline) {
		this.categoryIndexingPipeline = categoryIndexingPipeline;
	}

	/**
	 * @return the categoryIndexingPipeline
	 */
	public IndexingPipeline<Collection<Long>, Category> getCategoryIndexingPipeline() {
		return categoryIndexingPipeline;
	}
}
