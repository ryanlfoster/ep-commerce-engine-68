/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.index.solr.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.elasticpath.search.index.solr.service.IndexBuildPolicy;
import com.elasticpath.search.index.solr.service.IndexBuildPolicyContext;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.settings.SettingsReader;

/**
 * The default implementation of an {@link IndexBuildPolicy}.
 */
public class IndexBuildPolicyImpl implements IndexBuildPolicy {

	/**
	 * The setting value for the optimization interval.
	 */
	static final String SETTING_OPTIMIZATION_INTERVAL = "COMMERCE/SEARCH/indexOptimizationInterval";

	/**
	 * Specifies the maximum number of docs to add to a collection before adding to the SOLR server.
	 */
	private int maxDocsBeforeAdd;

	/**
	 * Specifies the maximum number of operations SOLR should perform before commit.
	 */
	private int maxOperationsBeforeCommit;

	private TimeService timeService;
	
	private SettingsReader settingsReader;

	private final Map<IndexType, Date> lastOptimizationTimeMap = new HashMap<IndexType, Date>();
	
	
	/**
	 * Checks the current time and the last optimization time.
	 * 
	 * @param context the index policy context
	 * @return true if the current time is after the time of 
	 * 		the last optimization request plus the defined {@link #secondsUntilNextOptimization}
	 */
	public boolean isOptimizationRequired(final IndexBuildPolicyContext context) {
		final String indexName = context.getIndexType().getIndexName();
		final Date nextOptimizationTime = DateUtils.addMinutes(getLastOptimizationRequestTime(context.getIndexType()), 
				getMinutesUntilNextOptimization(indexName));
		
		final boolean optimizationRequired = getCurrentTime().compareTo(nextOptimizationTime) > 0;
		
		if (optimizationRequired) {
			setLastOptimizationTime(context.getIndexType(), getCurrentTime());
		}
		return optimizationRequired;
	}

	private void setLastOptimizationTime(final IndexType indexType, final Date currentTime) {
		this.lastOptimizationTimeMap.put(indexType, currentTime);
	}

	private Date getLastOptimizationRequestTime(final IndexType indexType) {
		if (!lastOptimizationTimeMap.containsKey(indexType)) {
			lastOptimizationTimeMap.put(indexType, getCurrentTime());
		}
		return lastOptimizationTimeMap.get(indexType);
	}

	private Date getCurrentTime() {
		return getTimeService().getCurrentTime();
	}

	/**
	 * Gets the time service.
	 * 
	 * @return the time service
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	/**
	 * Sets the time service.
	 * 
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
	
	/**
	 * Gets the seconds until the next required optimization.
	 * 
	 * @param context the context the setting is retrieved for (it is the same as the index name)
	 * @return number of seconds
	 */
	protected int getMinutesUntilNextOptimization(final String context) {
		return Integer.valueOf(settingsReader.getSettingValue(SETTING_OPTIMIZATION_INTERVAL, context).getValue());
	}

	/**
	 *
	 * @return the settingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 *
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Checks the limit of documents to be added.
	 * 
	 * @param context the index policy context
	 * @return true if the max documents before add has been reached
	 */
	public boolean isAddDocumentsRequired(final IndexBuildPolicyContext context) {
		return context.getDocumentsAdded() % getMaxDocsBeforeAdd() == 0;
	}

	/**
	 * Checks the limit of operations done so far.
	 * 
	 * @param context the index policy context
	 * @return true if the max operations before commit has been reached
	 */
	public boolean isCommitRequired(final IndexBuildPolicyContext context) {
		return context.getOperationsCount() % getMaxOperationsBeforeCommit() == 0;
	}

	/**
	 *
	 * @return the maxDocsBeforeAdd
	 */
	protected int getMaxDocsBeforeAdd() {
		return maxDocsBeforeAdd;
	}

	/**
	 *
	 * @param maxDocsBeforeAdd the maxDocsBeforeAdd to set
	 */
	public void setMaxDocsBeforeAdd(final int maxDocsBeforeAdd) {
		this.maxDocsBeforeAdd = maxDocsBeforeAdd;
	}

	/**
	 *
	 * @return the maxOperationsBeforeCommit
	 */
	protected int getMaxOperationsBeforeCommit() {
		return maxOperationsBeforeCommit;
	}

	/**
	 *
	 * @param maxOperationsBeforeCommit the maxOperationsBeforeCommit to set
	 */
	public void setMaxOperationsBeforeCommit(final int maxOperationsBeforeCommit) {
		this.maxOperationsBeforeCommit = maxOperationsBeforeCommit;
	}

	/**
	 *
	 * @return the lastOptimizationTime
	 */
	protected Map<IndexType, Date> getLastOptimizationTimeMap() {
		return lastOptimizationTimeMap;
	}

	
}

