package com.elasticpath.ql.parser.fieldresolver;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Resolves EP QL term to SolrDescririptor which holds assembled Solr field.
 */
public interface EpQLFieldResolver {

	/**
	 * Resolves Ep QL field to SolrDescririptor which holds assembled Solr field.
	 * 
	 * @param epQuery epQuery
	 * @param epQLTerm EP QL Term
	 * @param solrTemplateDescriptor Solr template descriptor holding Solr field type and other information.
	 * @return ResolvedSolrField holding assembled Solr field.
	 * @throws ParseException if some parameter is missing or resolution can't be provided.
	 */
	NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm,
			final EpQLFieldDescriptor solrTemplateDescriptor) throws ParseException;
}
