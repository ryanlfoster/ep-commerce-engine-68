package com.elasticpath.domain.shipping.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;

/**
 * A ShippingRegion represents a region that will be associated with one or more shipping services. It could be composed of countries and
 * country/subCountry combinations. When persisted, the region definition will be saved in a string of format: [countryCode(\(subCountryCode
 * (,subCountryCode)*\))](,[countryCode(\(subCountryCode (,subCountryCode)*\))]?)+ For example, [CA(AB,BC)],[US]
 */
@Entity
@Table(name = ShippingRegionImpl.TABLE_NAME)
@DataCache(enabled = true)
public class ShippingRegionImpl extends AbstractLegacyEntityImpl implements ShippingRegion {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Regular expression for parsing the persisted region string. */
	private static final Pattern REGION_LIST_PATTERN = Pattern.compile("(\\[[A-Z]{2}[^\\[\\]]*])");

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPPINGREGION";

	private String name;

	private String regionStr;

	private Map<String, Region> regionMap;

	private long uidPk;

	private String guid;

	/**
	 * Get the shipping region name.
	 *
	 * @return the parameter name
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	/**
	 * Set the shipping region name.
	 *
	 * @param name the parameter name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the string representation of the region definition (for persistence). Hide this method from interface.
	 *
	 * @return the string representation of the region definition.
	 */
	@Basic
	@Column(name = "REGION_STR")
	public String getRegionStr() {
		return this.regionStr;
	}

	/**
	 * Set the string representation of the region definition (for persistence). Hide this method from interface.
	 *
	 * @param regionStr the string representation of the region definition
	 */
	public void setRegionStr(final String regionStr) {
		this.regionStr = regionStr;
	}

	/**
	 * Get the Map of regions assoicated with this shippingregion.
	 * The entry of the regionMap is countryCode -> <code>Region</code>.
	 *
	 * @return the map of regions assoicated with this shippingregion.
	 */
	@Transient
	public Map<String, Region> getRegionMap() {
		if (this.regionMap == null) {
			// parse the regionStr and instantiate the map of region instance.
			this.regionMap = new HashMap<String, Region>();
			final Matcher regionListMatch = REGION_LIST_PATTERN.matcher(getRegionStr());
			while (regionListMatch.find()) {
				Region region = getBean(ContextIdNames.REGION);
				region.fromString(regionListMatch.group());
				Region existingRegion = this.regionMap.get(region.getCountryCode());
				if (existingRegion == null) {
					this.regionMap.put(region.getCountryCode(), region);
				} else {
					// merge the subCountryCodeList of the two region with the same country code.
					existingRegion.mergeSubCountryCodeList(region.getSubCountryCodeList());
				}
			}
		}
		return this.regionMap;
	}

	/**
	 * Set the Map of regions assoicated with this shippingregion.
	 *
	 * @param regionMap the map of regions to be assoicated with this shippingregion.
	 */
	public void setRegionMap(final Map<String, Region> regionMap) {
		this.regionMap = regionMap;
		// Collections.sort(regionMap.values(), new RegionCodeComparatorImpl());

		// Update the regionStr correspondingly.
		final StringBuffer regionStrBuf = new StringBuffer("");
		for (final String countryCode : regionMap.keySet()) {
			final Region region = this.regionMap.get(countryCode);
			regionStrBuf.append(region.toString());
		}
		setRegionStr(regionStrBuf.toString());
	}

	/**
	 * Check if the given shippingAdress is in the range of this <code>ShippingRegion</code>.
	 *
	 * @param shippingAddress the shippingAddress to be evaluated. Assume shippingAddress always carries a country code, but subCountry could be
	 *            null.
	 * @return status of whether the given shippingAdress is in the range of this <code>ShippingRegion</code>.
	 */
	public boolean isInShippingRegion(final Address shippingAddress) {
		boolean isInRegion = false;
		for (String countryCode : getRegionMap().keySet()) {
			if (shippingAddress.getCountry().equals(countryCode)) {
				Region region = getRegionMap().get(countryCode);

				// If the address does not carry subCountry info
				if (shippingAddress.getSubCountry() == null) {
					if (region.getSubCountryCodeList().isEmpty()) {
						isInRegion = true;
						break;
					}
					throw new EpDomainException("Invalid shippingAddress - must contain subcountry info.");
				}

				if (region.getSubCountryCodeList() == null || region.getSubCountryCodeList().isEmpty()) {
					isInRegion = true;
					break;
				}

				for (String subCountryCode : region.getSubCountryCodeList()) {
					if (shippingAddress.getSubCountry().equals(subCountryCode)) {
						isInRegion = true;
						break;
					}
				}
			}
		}
		return isInRegion;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH)
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Compares this ShippingRegion with the specified object for order.
	 *
	 * This implementation will order ShippingRegions in ascending alphabetical
	 * order based on the Name.
	 *
	 * @param region the region being compared
	 * @return a negative integer, zero, or a positive integer as this object is
	 * less than, equal to, or greater than the specified object.
	 * @throws ClassCastException if the specified object type prevents it from
	 * being compared to this object.
	 */
	public int compareTo(final ShippingRegion region) {
		return getName().compareTo(region.getName());
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
    /**
     * String representation of this object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
            final StringBuffer sbf = new StringBuffer();
            sbf.append("Shipping Region -> name:").append(this.getName());
            sbf.append(" region:").append(this.getRegionStr());
            return sbf.toString();
    }

}
