package com.elasticpath.persistence.openjpa;

/**
 * Utility class for SQL/JPAQL parameter escaping. 
 */
public interface QueryParameterEscaper {

	/**
	 * Escapes string parameter.
	 * 
	 * @param param parameter to escape
	 * @return escaped paramater
	 */
	String escapeStringParameter(String param);

}