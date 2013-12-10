package com.elasticpath.ql.parser.valueresolver.impl;

import java.util.Collections;
import java.util.List;

import com.elasticpath.ql.parser.EpQLFieldType;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.gen.ParseException;
import com.elasticpath.ql.parser.valueresolver.EpQLValueResolver;

/**
 * Represents sql value resolver. 
 */
public class SQLValueResolver implements EpQLValueResolver {
	
	/** ?. */
	public static final String VALUE = "?";

	@Override
	public List<String> resolve(final EpQLTerm epQLTerm, final EpQLFieldType fieldType, final EpQuery epQuery) throws ParseException {		
		epQuery.addParam(extractValue(epQLTerm));
		return Collections.singletonList(VALUE);
	}
	
	/**
	 * Checks that epQLTerm contains string literal as query text and retrieves its value from edging single quotes.
	 * 
	 * @param epQLTerm EPQL term to extract value from
	 * @return string value extracted from enclosing quotes
	 * @throws ParseException is epQLTerm contains query text of wong type (not string literal)
	 */
	public String extractValue(final EpQLTerm epQLTerm) throws ParseException {
		if (!epQLTerm.getQueryText().endsWith("'") || !epQLTerm.getQueryText().startsWith("'")) {
			throw new ParseException("Value must be enclosed with single quotes for field "
					+ epQLTerm.getEpQLField().getFieldName());
		}
		return epQLTerm.getQueryText().substring(1, epQLTerm.getQueryText().length() - 1);
	}

}
