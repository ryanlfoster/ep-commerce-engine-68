/**
 * 
 */
package com.elasticpath.tags.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Enumerates the possible logical operator types: AND, OR, NOT.
 */
public enum LogicalOperatorType {
	/** AND. */
	AND("LogicalOperator_AND"),
	/** OR. */
	OR("LogicalOperator_OR");
	
	private String messageKey;
	
	private static List<LogicalOperatorType> list = new LinkedList<LogicalOperatorType>();
	
	static {
		list.add(AND);
		list.add(OR);
	}

	/**
	 * Default constructor.
	 * @param messageKey the localized message key
	 */
	private LogicalOperatorType(final String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * Get the localized message key.
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * @return the list
	 */
	public static List<LogicalOperatorType> getList() {
		return list;
	}
	
}
