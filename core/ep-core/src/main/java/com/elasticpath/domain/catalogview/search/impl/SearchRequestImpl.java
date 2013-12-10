package com.elasticpath.domain.catalogview.search.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Represents a default implementation of <code>SearchRequest</code>.
 */
public class SearchRequestImpl extends AbstractSearchRequestImpl implements SearchRequest {
	private static final String UTF_8 = "UTF-8";

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String keyWords;

	private boolean fuzzySearchDisabled;

	/**
	 * Returns the keywords specified in the search request.
	 *
	 * @return the keywords
	 */
	public String getKeyWords() {
		return this.keyWords;
	}

	/**
	 * Sets the keywords.
	 *
	 * @param keyWords the keywords to set
	 * @throws EpCatalogViewRequestBindException in case the given keywords is invalid
	 */
	public void setKeyWords(final String keyWords) throws EpCatalogViewRequestBindException {
		this.keyWords = keyWords;
	}

	/**
	 * Returns the url-encoded key words.
	 *
	 * @return the url-encoded key words.
	 */
	public String getEncodedKeyWords() {
		return encode(keyWords);
	}

	@Override
	public String getQueryString() {
		sanityCheck();
		final StringBuffer sbf = new StringBuffer();

		if (0 != getCategoryUid()) {
			sbf.append(WebConstants.REQUEST_CATEGORY_ID).append(WebConstants.SYMBOL_EQUAL).append(getCategoryUid());
			sbf.append(WebConstants.SYMBOL_AND);
		}

		sbf.append(WebConstants.REQUEST_KEYWORDS).append('=').append(getEncodedKeyWords());

		if (getFilters().size() > 0) {
			sbf.append(WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL);
			for (Filter< ? > filter : getFilters()) {
				sbf.append(filter.getId()).append(WebConstants.SYMBOL_PLUS);
			}
			sbf.deleteCharAt(sbf.length() - 1); // remove the redundant '+'
		}
		
		// keep the old values so that we don't modify anything
		SortOrder tmpOrder = getSortOrder();
		SortBy tmpType = getSortType();

		// make the featured product sorter the default one
		if (getSortType() == null) {
			setSortOrder(SortOrder.DESCENDING);
			setSortType(StandardSortBy.FEATURED_CATEGORY);
		}
		sbf.append(WebConstants.SYMBOL_AND).append(WebConstants.REQUEST_SORTER).append(WebConstants.SYMBOL_EQUAL).append(
				getSortTypeOrderString());
		setSortOrder(tmpOrder);
		setSortType(tmpType);

		return sbf.toString();
	}

	/**
	 * Compares this search request with the given search request.
	 *
	 * @param searchRequest the search request to compare
	 * @return 0 if this search request and the given search request has same key words and filters.
	 *         <p>
	 *         1 if this search request and the given search request has same key words, but has more filters.
	 *         <p>
	 *         -1 if this search request and the given search request has same key words, but has unmatching filters.
	 *         <p>
	 * @throws CatalogViewRequestUnmatchException when this search requet and the given search request have different key words
	 */
	public int compare(final CatalogViewRequest searchRequest) throws CatalogViewRequestUnmatchException { //NOPMD
		if (searchRequest == null) {
			throw new EpDomainException("The search requst to compare cannot be null.");
		}
		
		if (!(searchRequest instanceof SearchRequest)) {
			throw new EpDomainException("The request must be a search request to compare :" + searchRequest.getClass());
		}

		if (!this.keyWords.equals(((SearchRequest) searchRequest).getKeyWords())) {
			throw new CatalogViewRequestUnmatchException("Key words are different.");
		}

		return super.compare(searchRequest);
	}

	private String encode(final String string) {
		try {
			return URLEncoder.encode(string, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new EpSystemException("Unsupported encoding.", e);
		}
	}

	/**
	 * Do the sanity check.
	 */
	@Override
	protected void sanityCheck() {
		if (keyWords == null) {
			throw new EpDomainException("Not initialized!");
		}
	}

	/**
	 * Returns whether or not a fuzzy search should be performed for this query.
	 *
	 * @return true if fuzzy search is disabled, false otherwise
	 */
	public boolean isFuzzySearchDisabled() {
		return this.fuzzySearchDisabled;
	}

	/**
	 * Sets whether or not a fuzzy search should be performed for this query.
	 *
	 * @param fuzzySearchDisabled whether or not fuzzy search should be disabled
	 */
	public void setFuzzySearchDisabled(final boolean fuzzySearchDisabled) {
		this.fuzzySearchDisabled = fuzzySearchDisabled;
	}

}
