package com.elasticpath.ql.parser.query;

import org.apache.lucene.search.BooleanClause;


/**
 * This class represents lucene boolean clause. 
 */
public class LuceneBooleanClause extends BooleanClause implements NativeBooleanClause {
	private static final long serialVersionUID = 5469401600179891639L;

	/**
	 * Constructs lucene boolean clause.
	 * 
	 * @param lucenQuery the lucene query
	 * @param occur Specifies how clauses are to occur in matching documents
	 */
	public LuceneBooleanClause(final LuceneQuery lucenQuery, final Occur occur) {
		super(lucenQuery.getQuery(), occur);
	}
}
