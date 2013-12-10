package com.elasticpath.test.util;

import com.elasticpath.commons.exception.EpIntBindException;
import com.elasticpath.domain.catalog.ProductAssociation;

/**
 * Utility class for converting an association type string to code and back.
 */
public final class AssociationTypeConverter {

	private AssociationTypeConverter() {

	}

	/**
	 * Converts an association type from string to integer.
	 * 
	 * @param associationType association type as string
	 * @return association type as integer or -1 if association type isn't found
	 * @throws EpIntBindException in case of any errors
	 */
	public static int convert2Int(final String associationType) throws EpIntBindException {
		if (associationType == null || associationType.length() < 1) {
			throw new EpIntBindException("An association type string is invalid");
		}
		for (int i = 0; i < ProductAssociation.ALL_ASSOCIATION_TYPE_NAMES.length; i++) {
			if (ProductAssociation.ALL_ASSOCIATION_TYPE_NAMES[i].equals(associationType)) {
				return i + 1;
			}
		}
		return -1;
	}

	/**
	 * Converts an association type as integer to string.
	 * 
	 * @param associationType association type
	 * @return association type as string
	 * @throws EpIntBindException in case of any errors
	 */
	public static String convert2String(final int associationType) throws EpIntBindException {
		if (associationType < 1 || associationType > ProductAssociation.ALL_ASSOCIATION_TYPE_NAMES.length) {
			throw new EpIntBindException("Invalid association type index: " + associationType);
		}
		return ProductAssociation.ALL_ASSOCIATION_TYPE_NAMES[associationType - 1];
	}
}
