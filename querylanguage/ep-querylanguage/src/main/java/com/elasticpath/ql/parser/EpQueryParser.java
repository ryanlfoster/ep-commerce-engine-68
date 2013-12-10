package com.elasticpath.ql.parser;

/**
 * Interface <code>EpQueryParser</code> provides search capabilities.
 */
public interface EpQueryParser {

	/** NONE conjunction. */
	int CONJ_NONE = 0;
	
	/** AND conjunction. */
	int CONJ_AND = 1;
	
	/** OR conjunction. */
	int CONJ_OR = 2;

	/** negation. */
	int CONJ_NOT = 3;

	/**
	 * Verifies EP Query string and provides syntactic analysis.
	 * 
	 * @param query an EPQueryLanguage (EPQL) string
	 * @throws EpQLParseException if the given query string is not well-formed.
	 * @return pre Solr query. Actual query to be submitted may be different.
	 */
	String verify(final String query) throws EpQLParseException;
	
	/**
	 * Parses EP Query string and populates Lucene query if no errors detected.
	 * 
	 * @param query an EPQueryLanguage (EPQL) string
	 * @return EpQuery query object
	 * @throws EpQLParseException if the given query string has any parse errors
	 */
	EpQuery parse(final String query) throws EpQLParseException;

	/**
	 * Sets the EpQueryComposer - EP query assembler. Provides semantic analyzys and field/values resolution.
	 * 
	 * @param epQueryAssembler the ep query assembler
	 */
	void setEpQueryAssembler(final EpQueryAssembler epQueryAssembler);
}
