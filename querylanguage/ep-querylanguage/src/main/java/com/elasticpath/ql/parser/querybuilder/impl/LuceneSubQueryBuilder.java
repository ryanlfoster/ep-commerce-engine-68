package com.elasticpath.ql.parser.querybuilder.impl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.LuceneQuery;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.SubQueryBuilder;

/**
 * Constructs Lucene queries for EpQL terms.
 */
public class LuceneSubQueryBuilder implements SubQueryBuilder {

	private final LuceneRangeSubQueryBuilder rangeQueryBuilder;

	/**
	 * Prepares helper classes for building queries.
	 */
	public LuceneSubQueryBuilder() {
		rangeQueryBuilder = new LuceneRangeSubQueryBuilder(); // the only instance of utility helper.
	}

	/**
	 * Builds lucene query based on prepared information about Solr field, value to search for and operator. 
	 * 
	 * @param resolvedSolrField descriptor containing resolved Solr field and values to search for
	 * @param epQLTerm EPQL Term
	 * @return Lucene search query
	 * @throws ParseException if range query couldn't be built
	 */
	public NativeQuery buildQuery(final NativeResolvedTerm resolvedSolrField, final EpQLTerm epQLTerm) throws ParseException {
		final String resolvedValue = getResolvedValue(resolvedSolrField);
		Query query = null;

		query = rangeQueryBuilder.getRangeQuery(resolvedSolrField, epQLTerm,  resolvedValue);

		if (query == null) {
			query = getFieldQuery(resolvedSolrField.getResolvedField(), resolvedValue);
		}
		return new LuceneQuery(query);
	}

	/**
	 * Retrieves first value from the list of resolved and verifies that there is no any more.
	 * 
	 * @param resolvedSolrField resolved Solr field
	 * @return first resolved value
	 * @throws ParseException if there are several values in resolved field
	 */
	public String getResolvedValue(final NativeResolvedTerm resolvedSolrField) throws ParseException {
		if (resolvedSolrField.getResolvedValues().size() != 1) {
			throw new ParseException("Multiple values are not allowed in QL");
		}
		return resolvedSolrField.getResolvedValues().get(0);
	}

	/**
	 * Generates a term query using field and value.
	 * 
	 * @param field Solr field
	 * @param queryText analyzed query string
	 * @return term query 
	 */
	private Query getFieldQuery(final String field, final String queryText) {
		// TODO: queryText may be interpreted as single phrase or as several terms
		final PhraseQuery fieldQuery = new PhraseQuery();
		fieldQuery.add(new Term(field, queryText));
		return fieldQuery;
	}
}
