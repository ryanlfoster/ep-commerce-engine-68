package com.elasticpath.cmweb.jobs.impl;

import java.util.Date;

import com.elasticpath.cmweb.jobs.SearchTermsAggregatorJob;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.TimeService;

/**
 * Default implementation of {@link SearchTermsAggregatorJob}.
 */
public class SearchTermsAggregatorJobImpl implements SearchTermsAggregatorJob {

	private static final int MILLIS_PER_SECOND = 1000;
	private PersistenceEngine persistenceEngine;
	private TimeService timeService;

	@Override
	public void updateSearchTermsActivity() {
		Date startTime = getSafeLastAccessDate(timeService.getCurrentTime());
		Object[] parameters = new Object[]{startTime};
		persistenceEngine.executeNamedQuery("SEARCH_TERMS_AGGREGATE_EXISTING", parameters);
		persistenceEngine.executeNamedQuery("SEARCH_TERMS_AGGREGATE_NEW", parameters);
		persistenceEngine.executeNamedQuery("SEARCH_TERMS_DELETE_OLD_ACTIVITY", parameters);
	}

	/**
	 * Get the latest safe time to use for the aggregation.
	 * Some databases do not store time stamps with enough precision. For example, MySQL (ver < 5.6.7) does not
	 * keep the millisecond fraction of the time stamp. This might cause inconsistencies in the statistics due to race conditions.
	 * 
	 * This method creates a time stamp that is at least one second before the current time, and has a millisecond value of zero.
	 *
	 * @param now the current time
	 * @return the latest safe time
	 */
	protected Date getSafeLastAccessDate(final Date now) {
		long currentTime = now.getTime();
		long safeTime = (currentTime / MILLIS_PER_SECOND - 1) * MILLIS_PER_SECOND;
		return new Date(safeTime);
	}
	
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}
}
