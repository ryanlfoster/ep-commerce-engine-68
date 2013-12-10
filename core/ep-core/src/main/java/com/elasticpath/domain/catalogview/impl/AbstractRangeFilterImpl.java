/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalogview.FilterDisplayInfo;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.RangeFilterType;


/**
 * The abstract range filter implement.
 * 
 * @param <T> the type of range filter
 * @param <E> the type of value to store
 */
public abstract class AbstractRangeFilterImpl<T extends RangeFilter<T, E>, E extends Comparable<E>> extends
		AbstractFilterImpl<T> implements RangeFilter<T, E> {
	private static final long serialVersionUID = -7242879783385746423L;

	private E lowerValue;

	private E upperValue;

	private RangeFilter<T, E> parent;

	private final Set<T> children = new HashSet<T>();
	
	private Map<Locale, FilterDisplayInfo> localizedDisplayMap;

	private String alias;

	/**
	 * This method is not used.
	 *
	 * @param locale the locale
	 * @return the SEO url of the filter with the given locale.
	 */
	public String getSeoName(final Locale locale) {
		//Get from the range defination.
		FilterDisplayInfo displayInfo = this.getDisplayInfo(locale);
		if (displayInfo != null) {
			return displayInfo.getSeoName();
		}
		//return String.valueOf(this.getLowerValue()) + " - " + String.valueOf(this.getUpperValue());
		return null;
	}

	/**
	 *
	 * @param locale the given locale
	 * @return the display name
	 */
	public String getDisplayName(final Locale locale) {

		//Get from the range defination.
		FilterDisplayInfo displayInfo = this.getDisplayInfo(locale);
		if (displayInfo != null) {
			return displayInfo.getDisplayName();
		}
		return null;
	}

	/**
	 * Get the range type.
	 * @return the rangeType
	 */
	public RangeFilterType getRangeType() {
		if (lowerValue == null && upperValue == null) {
			return RangeFilterType.ALL;
		} else if (lowerValue == null) {
			return RangeFilterType.LESS_THAN;
		} else if (upperValue == null) {
			return RangeFilterType.MORE_THAN;
		}
		return RangeFilterType.BETWEEN;
	}

	/**
	 * Set the lower value for the range.
	 *
	 * @param lowerValue the lowerValue to set
	 */
	public void setLowerValue(final E lowerValue) {
		this.lowerValue = lowerValue;
	}

	/**
	 * Set the upper value for the range.
	 *
	 * @param upperValue the upperValue to set
	 */
	public void setUpperValue(final E upperValue) {
		this.upperValue = upperValue;
	}

	/**
	 * Returns the lower value of the filter.
	 *
	 * @return the lower value of the filter
	 */
	public E getLowerValue() {
		return lowerValue;
	}

	/**
	 * Returns the upper value of the filter.
	 *
	 * @return the upper value of the filter
	 */
	public E getUpperValue() {
		return upperValue;
	}

	/**
	 * Get the parent filter of this filter. Returns null if this filter is a root node.
	 *
	 * @return the parent filter or null if it is a root node
	 */
	public RangeFilter<T, E> getParent() {
		return parent;
	}

	/**
	 * Set the parent filter of this filter.
	 *
	 * @param parent the parent filter
	 */
	public void setParent(final RangeFilter<T, E> parent) {
		this.parent = parent;
	}

	/**
	 * Get the children filters.
	 *
	 * @return the children filters as a set
	 */
	public Set<T> getChildren() {
		return children;
	}

	/**
	 * Add the given filter as a child.
	 *
	 * @param childFilter the filter to be added as a child
	 */
	public void addChild(final T childFilter) {
		children.add(childFilter);
		childFilter.setParent(this);
	}

	/**
	 * Returns <code>true</code> if this price filter contains the given price filter. A filter
	 * contains another if they are equal as well.
	 * 
	 * @param rangeFilter the price filter
	 * @return <code>true</code> if this price filter contains the given price filter
	 */
	public boolean contains(final RangeFilter<T, E> rangeFilter) {
		if (getLowerValue() == null && getUpperValue() == null) {
			return true;
		} else if (getLowerValue() == null) {
			if (rangeFilter.getUpperValue() == null) {
				// they have unbounded upper bound and we don't -- overlap
				return false;
			}
			return getUpperValue().compareTo(rangeFilter.getUpperValue()) >= 0;
		} else if (getUpperValue() == null) {
			if (rangeFilter.getLowerValue() == null) {
				// they have unbounded lower bound and we don't -- overlap
				return false;
			}
			return getLowerValue().compareTo(rangeFilter.getLowerValue()) <= 0;
		} else if (rangeFilter.getLowerValue() == null || rangeFilter.getUpperValue() == null) {
			// they have an unbounded end, but both ours are defined
			return false;
		}
		return getLowerValue().compareTo(rangeFilter.getLowerValue()) <= 0
				&& getUpperValue().compareTo(rangeFilter.getUpperValue()) >= 0;
	}

	/**
	 * Get the localized display map.
	 *
	 * @return the displayMap
	 */
	public Map<Locale, FilterDisplayInfo> getLocalizedDisplayMap() {
		return localizedDisplayMap;
	}

	/**
	 * Set the localized display map.
	 *
	 * @param displayMap the displayMap to set
	 */
	public void setLocalizedDisplayMap(final Map<Locale, FilterDisplayInfo> displayMap) {
		this.localizedDisplayMap = displayMap;
	}

	/**
	 * Get the localized display info map.
	 *
	 * @param locale the given locale.
	 * @return the display info for this locale
	 */
	public FilterDisplayInfo getDisplayInfo(final Locale locale) {
		FilterDisplayInfo displayInfo = null;
		if (this.getLocalizedDisplayMap() != null) {
			//if (this.isLocalized()) {
			displayInfo = this.getLocalizedDisplayMap().get(locale);
			if (displayInfo == null) {
				displayInfo =  this.getLocalizedDisplayMap().get(null);
			}
		}
		return displayInfo;
	}

	/**
	 * Add a localized display info in the localized map.
	 *
	 * @param locale the given locale.
	 * @param displayInfo the displayInfo.
	 */
	public void addDisplayInfo(final Locale locale, final FilterDisplayInfo displayInfo) {
		if (this.getLocalizedDisplayMap() == null) {
			this.setLocalizedDisplayMap(new HashMap<Locale, FilterDisplayInfo>());
		}
		this.getLocalizedDisplayMap().put(locale, displayInfo);
	}
	
	/**
	 * Compares this filter with the specified other filter for order. Lower filters will be given
	 * a higher rank. For filters that overlap, the lower value is used to compare (lowest value
	 * is ranked higher). If they have the same lower value, the biggest range will be ranked
	 * higher. Higher rank is denoted by a positive integer, whereas lower rank is a negative one.
	 * Filters that are equal are defined to be zero.
	 * 
	 * @param otherFilter the given filter
	 * @return a negative integer, zero, or a positive integer as the other price filter is less
	 *         than, equal to, or greater than the specified object
	 */
	public int compareTo(final T otherFilter) {
		if (this == otherFilter) {
			return 0;
		}
		
		final int upperComp = compareValues(getLowerValue(), otherFilter.getLowerValue());
		final int lowerComp = compareValues(getUpperValue(), otherFilter.getUpperValue());
		
		// same range
		if (lowerComp == 0 && upperComp == 0) {
			return 0;
		}
		
		// lower bounds the same
		if (lowerComp == 0) {
			return upperComp;
		}
		return lowerComp;
	}

	private int compareValues(final E value1, final E value2) {
		if (value1 == null && value2 == null) {
			return 0;
		} else if (value1 == null) {
			return Integer.MAX_VALUE;
		} else if (value2 == null) {
			return Integer.MIN_VALUE;
		}
		return value1.compareTo(value2);
	}

	/**
	 * Set an alias for the attribute value range.
	 * 
	 * @param alias an SEO-friendly alias
	 */
	public void setAlias(final String alias) {
		this.alias = alias;
	}

	/**
	 * Get the SEO alias for the attribute value range.
	 * 
	 * @return an SEO friendly alias for the attribute value range
	 */
	public String getAlias() {
		return alias;
	}
}

