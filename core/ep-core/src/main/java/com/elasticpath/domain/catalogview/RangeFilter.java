/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This defines the filter which has a range value. When this filter is applied on an object, the
 * object will be rejected if its value not falls into the range.
 * 
 * @param <T> the type of comparable range filter
 * @param <E> the type of comparable object to range on
 */
public interface RangeFilter<T, E extends Comparable<E>> extends Filter<T> {

	/** property key for the lower value. */
	String LOWER_VALUE_PROPERTY = "lowerValue";

	/** property key for the upper value. */
	String UPPER_VALUE_PROPERTY = "upperValue";

	/**
	 * Range Token counts.
	 */
	int RANGE_TOKENS = 3;

	/**
	 * The lower value position.
	 */
	int LOWER_VALUE_POSITION = 1;

	/**
	 * The heigher value position.
	 */
	int UPPER_VALUE_POSITION = 2;

	/**
	 * The range contains all the values.
	 */
	String ALL = "all";

	/**
	 * The range has both upper and lower value set.
	 */
	String AND = "and";

	/**
	 * Get the range type.
	 * The range type will be one of the MORETHAN / LESSTHAN / BETWEEN.
	 * @return the rangeType
	 */
	RangeFilterType getRangeType();

	/**
	 * Get the localized display info map.
	 *
	 * @param locale the given locale.
	 * @return the display info for this locale
	 */
	FilterDisplayInfo getDisplayInfo(final Locale locale);

	/**
	 * Add a localized display info in the localized map.
	 *
	 * @param locale the given locale.
	 * @param displayInfo the displayInfo.
	 */
	void addDisplayInfo(final Locale locale, final FilterDisplayInfo displayInfo);

	/**
	 * Get the localized display map.
	 *
	 * @return the displayMap
	 */
	Map<Locale, FilterDisplayInfo> getLocalizedDisplayMap();

	/**
	 * Set the localized display map.
	 *
	 * @param displayMap the displayMap to set
	 */
	void setLocalizedDisplayMap(final Map<Locale, FilterDisplayInfo> displayMap);


	/**
	 * Set the lower value for the range.
	 *
	 * @param lowerValue the lowerValue to set
	 */
	void setLowerValue(final E lowerValue);

	/**
	 * Set the upper value for the range.
	 *
	 * @param upperValue the upperValue to set
	 */
	void setUpperValue(final E upperValue);

	/**
	 * Returns the lower value of the filter.
	 *
	 * @return the lower value of the filter
	 */
	E getLowerValue();

	/**
	 * Returns the upper value of the filter.
	 *
	 * @return the upper value of the filter
	 */
	E getUpperValue();

	/**
	 * Get the parent filter of this filter. Returns null if this filter is a root node.
	 *
	 * @return the parent filter or null if it is a root node
	 */
	RangeFilter<T, E> getParent();

	/**
	 * Set the parent filter of this filter.
	 *
	 * @param parent the parent filter
	 */
	void setParent(final RangeFilter<T, E> parent);

	/**
	 * Get the children filters.
	 *
	 * @return the children filters as a set
	 */
	Set<T> getChildren();

	/**
	 * Add the given filter as a child.
	 *
	 * @param childFilter the filter to be added as a child
	 */
	void addChild(final T childFilter);

	/**
	 * Returns <code>true</code> if this filter contains the given filter.
	 *
	 * @param filter the given filter
	 * @return <code>true</code> if this filter contains the given filter
	 */
	boolean contains(final RangeFilter<T, E> filter);
}
