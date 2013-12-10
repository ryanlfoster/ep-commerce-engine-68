package com.elasticpath.ql.parser.valueresolver;

import java.util.List;

import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Resolves values of fields for search.
 */
public interface EpQLValueResolver {

	/**
	 * Resolves field value provided in EPQL into the list of values to search for.
	 * 
	 * @param epQLTerm EPQL term
	 * @param fieldType type of field value passed in EPQL query
	 * @param epQuery the epQuery
	 * @return list of values corresponding to given value of EpQL field
	 * @throws ParseException if value passed in EPQL term is incorrect
	 */
	List<String> resolve(final EpQLTerm epQLTerm, final EpQLFieldType fieldType, final EpQuery epQuery) throws ParseException;
}
