package com.elasticpath.domain.misc.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shipping.Region;

/**
 * This is a comparator to used for <code>Region</code>. The comparasion will be based on the result of countryCode comparasion, plus the result
 * of subCountryCode comparasion if the countryCode are the same.
 */
public class RegionCodeComparatorImpl implements Comparator<Region>, Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 * 
	 * @param region1 the first region to be compared.
	 * @param region2 the second region to be compared.
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
	 * @throws EpSystemException if the property to be used for comparison is null.
	 */
	public int compare(final Region region1, final Region region2) {

		vaidateObject(region1);
		vaidateObject(region2);
		return region1.getCountryCode().compareTo(region2.getCountryCode());
	}

	private void vaidateObject(final Region region) {
		if (region == null || region.getCountryCode() == null) {
			throw new EpSystemException("Failed to compare Region object with null countryCode.");
		}
	}
}
