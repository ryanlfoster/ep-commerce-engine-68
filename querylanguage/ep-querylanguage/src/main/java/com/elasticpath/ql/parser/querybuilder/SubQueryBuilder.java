package com.elasticpath.ql.parser.querybuilder;

import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.query.NativeQuery;

/**
 * Constructs sub queries for EpQL terms.
 */
public interface SubQueryBuilder {
	/**
	 * Builds sub query based on prepared information about resolved field, value to search for and operator. 
	 * 
	 * @param nativeResolvedTerm descriptor containing resolved field and values to search for
	 * @param epQLTerm EPQL Term
	 * @return search query
	 * @throws ParseException if range query couldn't be built
	 */
	NativeQuery buildQuery(final NativeResolvedTerm nativeResolvedTerm, final EpQLTerm epQLTerm) throws ParseException;
}
