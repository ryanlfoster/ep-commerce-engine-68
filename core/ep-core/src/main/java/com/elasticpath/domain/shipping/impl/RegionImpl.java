package com.elasticpath.domain.shipping.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shipping.Region;

/**
 * A Region represents a geography definition of an area. For now, it is composed of country and a subcountry, i.e. CA(country) and BC(subcountry).
 */
public class RegionImpl extends AbstractEpDomainImpl implements Region {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Pattern REGION_PATTERN = Pattern.compile("^(,\\s*)*\\[*([A-Z]{2})\\(([^\\)]+)*\\)\\]$");

	private static final int COUNTRY_CODE_GROUP_NO = 2;

	private static final int SUBCOUNTRY_CODE_GROUP_NO = 3;

	private String countryCode;

	private List<String> subCountryCodeList;

	/**
	 * Default constructor.
	 */
	public RegionImpl() {
		super();
		subCountryCodeList = new ArrayList<String>();
	}

	/**
	 * Constructor.
	 *
	 * @param countryCode - the ISO country code for this region.
	 */
	public RegionImpl(final String countryCode) {
		this();
		this.countryCode = countryCode;
	}

	/**
	 * Constructor.
	 *
	 * @param countryCode - the ISO country code for this region.
	 * @param subCountryCodeList - the list of ISO subCountry code for this region.
	 */
	public RegionImpl(final String countryCode, final List<String> subCountryCodeList) {
		this();
		this.countryCode = countryCode;
		this.subCountryCodeList = subCountryCodeList;
	}

	/**
	 * Get the region's country code (ISO country code).
	 *
	 * @return the region's country code.
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * Set the region's country code (ISO country code).
	 *
	 * @param countryCode the region's country code.
	 */
	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Get the region's subcountry code list.
	 *
	 * @return the region's subcountry code list.
	 */
	public List<String> getSubCountryCodeList() {
		return subCountryCodeList;
	}

	/**
	 * Set the region's subcountry code list.
	 *
	 * @param subCountryCodeList the region's subcountry code.
	 */
	public void setSubCountryCodeList(final List<String> subCountryCodeList) {
		if (subCountryCodeList == null) {
			throw new EpDomainException("Null subCountryCodeList cannot be set. Set it to an empty list instead.");
		}
		this.subCountryCodeList = subCountryCodeList;
	}

	/**
	 * Merge the given additionalSubCountryCodeList into the existing subCountryCodeList of this <code>Region</code> instance.
	 *
	 * @param additionalSubCountryCodeList - the additional subCountryCodeList to be merged in.
	 */
	public void mergeSubCountryCodeList(final List<String> additionalSubCountryCodeList) {
		if (subCountryCodeList == null || subCountryCodeList.isEmpty()) {
			subCountryCodeList = additionalSubCountryCodeList;
		} else {
			for (final String newSubCountryCode : additionalSubCountryCodeList) {
				if (!subCountryCodeList.contains(newSubCountryCode)) {
					subCountryCodeList.add(newSubCountryCode);
				}
			}

		}
	}

	/**
	 * Return the String representation of this <code>Region</code>.
	 *
	 * @return string representation of the region.
	 */
	public String toString() {
		if (countryCode == null || countryCode.trim().length() == 0) {
			throw new EpDomainException("Failed to get the string representation of an empty region.");
		}

		final StringBuffer regionStrBuf = new StringBuffer("[");
		regionStrBuf.append(countryCode);
		regionStrBuf.append('(');
		if (getSubCountryCodeList().size() > 0) {
			for (final String code : getSubCountryCodeList()) {
				regionStrBuf.append(code);
				regionStrBuf.append(',');
			}
			regionStrBuf.deleteCharAt(regionStrBuf.lastIndexOf(","));
		}
		regionStrBuf.append(")]");
		return regionStrBuf.toString();
	}

	/**
	 * Return the <code>Region</code> from parsing the given string representation.
	 *
	 * @param regionStr - the String representation of the <code>Region</code>
	 * @return Region - the instance of <code>Region</code>
	 */
	public Region fromString(final String regionStr) {
		final Matcher regionMatch = REGION_PATTERN.matcher(regionStr);
		if (regionMatch.matches()) {
			final String countryCode = regionMatch.group(COUNTRY_CODE_GROUP_NO);
			setCountryCode(countryCode);
			if (regionMatch.group(SUBCOUNTRY_CODE_GROUP_NO) != null) {
				String[] subCountryArray = regionMatch.group(SUBCOUNTRY_CODE_GROUP_NO).split(",");
				setSubCountryCodeList(Arrays.asList(subCountryArray));
			}
		} else {
			throw new EpDomainException("Invalid region string representation: " + regionStr);
		}
		return this;
	}
}
