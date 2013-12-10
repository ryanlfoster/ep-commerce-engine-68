package com.elasticpath.ql.parser;

/**
 * Represents Ep QL operator as opposed to an operator of a native language which may vary.
 */
public enum EpQLOperator {

	/** = . */
	EQUAL("="),

	/** != . */
	NOT_EQUAL("!="),

	/** > . */
	MORE(">"),

	/** < . */
	LESS("<"),

	/** <= . */
	LESS_OR_EQUAL("<="),

	/** >= . */
	MORE_OR_EQUAL(">=");

	private String stringRepresentation;
	
	/**
	 * Constructs this operator by string representation of the operator.
	 * @param stringRepresentation string representation of the operator
	 */
	EpQLOperator(final String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}

	/**
	 * @return the stringRepresentation
	 */
	public String asString() {
		return stringRepresentation;
	}
	
	/**
	 * Returns EpQLField enum by string representation of the operator.
	 * 
	 * @param epQLOperator string representation of the operator
	 * @return EpQLOperator enum object.
	 */
	public static EpQLOperator getEpQLOperator(final String epQLOperator) {
		for (EpQLOperator op : EpQLOperator.values()) {
			if (op.asString().equals(epQLOperator)) {
				return op;
			}
		}
		return null;
	}
}
