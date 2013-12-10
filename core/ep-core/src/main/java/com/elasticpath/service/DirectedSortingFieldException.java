package com.elasticpath.service;

import com.elasticpath.commons.pagination.DirectedSortingField;

/**
 * Provides exception formatting for exceptions which involved DirectedSortingFields.
 *
 */
public class DirectedSortingFieldException extends IllegalArgumentException {
	private static final long serialVersionUID = -7760916909137750919L;

	private final String message;
	
	/**
	 * Default construction.
	 * @param messageStart The string at the start of the message.
	 * @param codeName The name of the code field.
	 * @param sortingFields The array of sortingFields.
	 * @param code The code.
	 */
	public DirectedSortingFieldException(final String messageStart,
			final String codeName, final DirectedSortingField[] sortingFields,
			final String code) {
		StringBuffer sortingFieldsOutput = new StringBuffer();
		if (sortingFields == null) {
			sortingFieldsOutput.append("null");				
		} else {
			for (int i = 0; i < sortingFields.length; i++) {
				if (i > 0) {
					sortingFieldsOutput.append(',');
				}
				sortingFieldsOutput.append(String.format("%s, %s", sortingFields[i].getSortingField(), sortingFields[i].getSortingDirection()));
			}
		}
		
		message = String.format(messageStart + ": " + codeName + "=%s, sortingField=%s", code, sortingFieldsOutput);
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
