package com.elasticpath.test.util;

import com.elasticpath.domain.rules.Rule;

/**
 * Utility class for recognizing strings used in FIT tests.
 */
public class StringCodeConverter {
	
	/**
	 * Calculate logical operator by its code "all" == AND, "any" == OR.
	 * 
	 * @param operatorCode string representation of logical operator
	 * @return true if operator is recognized successfully
	 */
	public static boolean getLogicalOperator(final String operatorCode) {
		boolean operator;
		if ("ALL".equals(operatorCode)) {
			operator = Rule.AND_OPERATOR;
		} else if ("ANY".equals(operatorCode)) {
			operator = Rule.OR_OPERATOR;
		} else {
			throw new IllegalArgumentException("It possible to set only 'ALL' or 'ANY' as condition type");
		}
		return operator;
	}
}
