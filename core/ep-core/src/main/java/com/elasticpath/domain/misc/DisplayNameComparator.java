package com.elasticpath.domain.misc;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;

/**
 * Represents a <code>Comparator</code> on display name.
 * <p>
 * Notice: it must be initialized with locale before use.
 */
public interface DisplayNameComparator extends Comparator<ObjectWithLocaleDependantFields>, Serializable {
	/**
	 * Intialize the comparaor with the given locale.
	 * 
	 * @param locale the locale
	 */
	void initialize(Locale locale);
}
