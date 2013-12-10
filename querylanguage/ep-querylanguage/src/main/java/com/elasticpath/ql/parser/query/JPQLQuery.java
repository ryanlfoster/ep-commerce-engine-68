package com.elasticpath.ql.parser.query;


/**
 * Represent jpql query.
 */
public class JPQLQuery implements NativeQuery {
	
	private String field;
	
	private String value;
	
	private String conj;
	
	/**
	 * Constructs empty object.
	 */
	protected JPQLQuery() {
		//do nothing
	}

	/**
	 * Constructs jpql query.
	 * 
	 * @param field the field 
	 * @param value the value
	 * @param conj the operator
	 */
	public JPQLQuery(final String field, final String value, final String conj) {
		this.field = field;
		this.value = value;
		this.conj = conj;
	}

	@Override
	public String getNativeQuery() {
		return field + conj + value;
	}
}
