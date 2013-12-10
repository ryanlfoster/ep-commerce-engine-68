package com.elasticpath.service.search.solr.query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.LuceneRawSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * A query composer for {@link LuceneRawSearchCriteria}.
 */
public class LuceneRawQueryComposerImpl extends AbstractQueryComposerImpl {
	
	private Analyzer analyzer;
	
	private final ThreadLocal<QueryParser> queryParsers = new ThreadLocal<QueryParser>();

	@Override
	protected Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		return composeQueryInternal(searchCriteria, searchConfig);
	}

	@Override
	protected Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final LuceneRawSearchCriteria rawCriteria = (LuceneRawSearchCriteria) searchCriteria;
		
		if (rawCriteria.getIndexType() == null) {
			throw new EpServiceException("IndexType must be specified, it cannot be null.");
		}
		
		if (rawCriteria.getQuery() == null) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}
		
		Query query;
		try {
			query = getParser().parse(rawCriteria.getQuery());
		} catch (ParseException e) {
			throw new EpServiceException("Error while parsing query.", e);
		}
		return query;
	}

	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof LuceneRawSearchCriteria;
	}
	
	private QueryParser getParser() {
		QueryParser parser = queryParsers.get();
		if (parser == null) {
			parser = new QueryParser("", analyzer);
			queryParsers.set(parser);
		}
		return parser;
	}

	/**
	 * Sets the analyzer to be used for the {@link QueryParser}.
	 *
	 * @param analyzer the analyzer to be used for the {@link QueryParser}
	 */
	public void setLuceneAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}
}
