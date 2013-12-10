package com.elasticpath.ql.parser;

import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Represents EpQL query term which is generally: field{parameter2}[parameter1] operator query_text.
 */
public final class EpQLTerm {

	private final EpQLField epQLField;

	private final String parameter1;

	private final String parameter2;

	private final EpQLOperator operator;

	private final String queryText;

	/**
	 * Constructs EpQL Term.
	 * 
	 * @param epQLField string representation of epQLField
	 * @param parameter1 first EpQL parameter passed in square brackets []
	 * @param parameter2 second EpQL parameter passed in curly brackets {}
	 * @param operator boolean operator: "=","!=","<", "<=", ">", ">="
	 * @param queryText text value EpQL field should be equal to
	 * @throws ParseException if there is no EpQLField with the given name
	 */
	public EpQLTerm(final String epQLField, final String parameter1, final String parameter2, final String operator, final String queryText)
			throws ParseException {
		this.epQLField = EpQLField.getEpQLField(epQLField);

		if (this.epQLField == null) {
			throw new ParseException("Unknown field: " + epQLField);
		}

		this.parameter1 = parameter1;
		this.parameter2 = parameter2;

		this.operator = EpQLOperator.getEpQLOperator(operator);

		this.queryText = queryText;
	}

	/**
	 * Gets EpQLField as a part of this EpQL term.
	 * 
	 * @return EpQLField
	 */
	public EpQLField getEpQLField() {
		return epQLField;
	}

	/**
	 * Gets first parameter of EpQL term.
	 * 
	 * @return parameter1 containing in square brackets [] or null
	 */
	public String getParameter1() {
		return parameter1;
	}

	/**
	 * Gets second parameter of EpQL term.
	 * 
	 * @return parameter2 containing in curly brackets {} or null
	 */
	public String getParameter2() {
		return parameter2;
	}

	/**
	 * Gets boolean operator used in this query term.
	 * 
	 * @return string representation of boolean operator
	 */
	public EpQLOperator getOperator() {
		return operator;
	}

	/**
	 * Gets query text.
	 * 
	 * @return query text
	 */
	public String getQueryText() {
		return queryText;
	}
}
