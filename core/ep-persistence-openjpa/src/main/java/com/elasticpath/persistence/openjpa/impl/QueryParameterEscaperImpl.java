package com.elasticpath.persistence.openjpa.impl;

import com.elasticpath.persistence.openjpa.QueryParameterEscaper;

/**
 * Implementation for {@link QueryParameterEscaper}. 
 */
public class QueryParameterEscaperImpl implements QueryParameterEscaper {

	@Override
	public String escapeStringParameter(final String param) {
		//here you can chain any other escape methods for strings that you need
		return escapeQuotes(param);	
	}
	
	/**
	 * Escapes single quotes from String.
	 * 
	 * @param param parameter to escape
	 * @return escaped String with escaped single quotes
	 */
	protected String escapeQuotes(final String param) {
		return param.replace("'", "''");	
	}
	
}
