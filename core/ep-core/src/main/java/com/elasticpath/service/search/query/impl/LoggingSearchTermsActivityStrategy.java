package com.elasticpath.service.search.query.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.query.SearchTermsActivity;
import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.persistence.dao.SearchTermsActivityDao;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.query.SearchTermsActivityStrategy;

/**
 * A logging implementation of {@link SearchTermsActivityStrategy}.
 */
public class LoggingSearchTermsActivityStrategy implements SearchTermsActivityStrategy {

	private SearchTermsActivityDao searchTermsActivityDao;
	private BeanFactory beanFactory;
	private TimeService timeService;

	@Override
	public void logSearchTerm(final SearchTermsMemento memento) {
		SearchTermsActivity activity = beanFactory.getBean(ContextIdNames.SEARCH_TERMS_ACTIVITY);
		activity.setSearchTerms(memento);
		activity.setLastAccessDate(timeService.getCurrentTime());
		searchTermsActivityDao.save(activity);
	}

	public void setSearchTermsActivityDao(final SearchTermsActivityDao searchTermsActivityDao) {
		this.searchTermsActivityDao = searchTermsActivityDao;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}
}
