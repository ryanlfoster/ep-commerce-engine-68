package com.elasticpath.sfweb.search.impl;

import java.util.Currency;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.sfweb.EpSearchKeyWordNotGivenException;
import com.elasticpath.sfweb.EpSearchKeyWordTooLongException;
import com.elasticpath.sfweb.search.StoreSearchRequestFactory;

/**
 * Creator for populating the SearchRequest for product/category search.
 *
 */
public class StoreSearchRequestFactoryImpl implements StoreSearchRequestFactory {

	private BeanFactory beanFactory;
	/**
	 * Variable for no category.
	 */
	protected static final long NO_CATEGORY = 0;
	
	@Override
	public SearchRequest build(final HttpServletRequest request, final Store store,
			final Locale locale, final Currency currency) {
		
		final String filterIdStr = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_FILTERS, null);
		final String sorterIdStr = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_SORTER,
				SortUtility.constructSortTypeOrderString(StandardSortBy.RELEVANCE, SortOrder.DESCENDING));

		final SearchRequest searchRequest = getBeanFactory().getBean(ContextIdNames.SEARCH_REQUEST);
		final String keyWords = getKeywords(request);
		if (keyWords.length() > CatalogViewConstants.SEARCH_KEYWORDS_MAX_LENGTH) {
			throw new EpSearchKeyWordTooLongException("Search key word is too long.");
		}
		final long categoryId = getCategoryId(request);
		searchRequest.setKeyWords(keyWords);
		searchRequest.setCategoryUid(categoryId);
		searchRequest.setFiltersIdStr(filterIdStr, store);
		searchRequest.parseSorterIdStr(sorterIdStr);
		searchRequest.setCurrency(currency);
		searchRequest.setLocale(locale);
		return searchRequest;
	}
	
	private String getKeywords(final HttpServletRequest request) {
		try {
			return ServletRequestUtils.getRequiredStringParameter(request, WebConstants.REQUEST_KEYWORDS);
		} catch (ServletRequestBindingException e) {
			throw new EpSearchKeyWordNotGivenException("No keywords in search request."); // NOPMD
		}
	}
	
	
	private long getCategoryId(final HttpServletRequest request) {
		final long categoryId = ServletRequestUtils.getLongParameter(request, WebConstants.REQUEST_CATEGORY_ID, NO_CATEGORY);
		
		return categoryId;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the beanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
