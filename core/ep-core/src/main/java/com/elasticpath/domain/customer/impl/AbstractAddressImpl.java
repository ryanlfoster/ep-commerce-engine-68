/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.Index;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Implementation of a street address.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidDuplicateLiterals" })
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ORDER_ADDRESSES, attributes = { @FetchAttribute(name = "firstName"),
				@FetchAttribute(name = "lastName"), @FetchAttribute(name = "city"), @FetchAttribute(name = "country"),
				@FetchAttribute(name = "faxNumber"), @FetchAttribute(name = "phoneNumber"), @FetchAttribute(name = "subCountry"),
				@FetchAttribute(name = "street1"), @FetchAttribute(name = "street2"), @FetchAttribute(name = "zipOrPostalCode"),
				@FetchAttribute(name = "commercialAddress") , @FetchAttribute(name = "organization") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_SEARCH, attributes = { @FetchAttribute(name = "firstName"),
				@FetchAttribute(name = "lastName"), @FetchAttribute(name = "phoneNumber"), @FetchAttribute(name = "zipOrPostalCode") }),
		@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = { @FetchAttribute(name = "firstName"),
				@FetchAttribute(name = "lastName"), @FetchAttribute(name = "city"), @FetchAttribute(name = "country"),
				@FetchAttribute(name = "faxNumber"), @FetchAttribute(name = "phoneNumber"), @FetchAttribute(name = "subCountry"),
				@FetchAttribute(name = "street1"), @FetchAttribute(name = "street2"), @FetchAttribute(name = "zipOrPostalCode"),
				@FetchAttribute(name = "commercialAddress") , @FetchAttribute(name = "organization") }),
		@FetchGroup(name = FetchGroupConstants.CUSTOMER_ADDRESSES, attributes = { @FetchAttribute(name = "firstName"),
				@FetchAttribute(name = "lastName"), @FetchAttribute(name = "city"), @FetchAttribute(name = "country"),
				@FetchAttribute(name = "faxNumber"), @FetchAttribute(name = "phoneNumber"), @FetchAttribute(name = "subCountry"),
				@FetchAttribute(name = "street1"), @FetchAttribute(name = "street2"), @FetchAttribute(name = "zipOrPostalCode"),
				@FetchAttribute(name = "commercialAddress"), @FetchAttribute(name = "organization")   })
})
@DataCache(enabled = false)
public abstract class AbstractAddressImpl extends AbstractEntityImpl implements Address {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final String COMMA_SPACE = ", ";

	private String firstName;

	private String lastName;

	private String city;

	private String country;

	private String faxNumber;

	private String phoneNumber;

	private String subCountry;

	private String street1;

	private String street2;

	private String zipOrPostalCode;

	private boolean commercialAddress = false;

	private String guid;

	private String organization;
	
	/**
	 * Copies all the fields from another address into this address.
	 *
	 * @param address the address from which to copy fields
	 */
	@Override
	public void copyFrom(final Address address) {
		this.setFirstName(address.getFirstName());
		this.setLastName(address.getLastName());
		this.setCity(address.getCity());
		this.setCountry(address.getCountry());
		this.setFaxNumber(address.getFaxNumber());
		this.setPhoneNumber(address.getPhoneNumber());
		this.setSubCountry(address.getSubCountry());
		this.setStreet1(address.getStreet1());
		this.setStreet2(address.getStreet2());
		this.setZipOrPostalCode(address.getZipOrPostalCode());
		this.setCommercialAddress(address.isCommercialAddress());
		this.setOrganization(address.getOrganization());
	}

	/**
	 * Gets the last name associated with this <code>Address</code>.
	 *
	 * @return the last name
	 */
	@Basic
	@Column(name = "LAST_NAME")
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets the last name associated with this <code>Address</code>.
	 *
	 * @param lastName the new phone number.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the first name associated with this <code>Address</code>.
	 *
	 * @return the first name
	 */
	@Basic
	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Sets the first name associated with this <code>Address</code>.
	 *
	 * @param firstName the new phone number.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the streetname of this <code>Address</code>.
	 *
	 * @return the streetname.
	 */
	@Basic
	@Column(name = "STREET_1")
	public String getStreet1() {
		return this.street1;
	}

	/**
	 * Sets the streetname of this <code>Address</code>.
	 *
	 * @param street the new streetname.
	 */
	public void setStreet1(final String street) {
		this.street1 = street;
	}

	/**
	 * Gets the streetname of this <code>Address</code>.
	 *
	 * @return the streetname.
	 */
	@Basic
	@Column(name = "STREET_2")
	public String getStreet2() {
		return this.street2;
	}

	/**
	 * Sets the streetname of this <code>Address</code>.
	 *
	 * @param street the new streetname.
	 */
	public void setStreet2(final String street) {
		this.street2 = street;
	}

	/**
	 * Gets the country of this <code>Address</code>.
	 *
	 * @return the country.
	 */
	@Basic
	@Column(name = "COUNTRY")
	public String getCountry() {
		return this.country;
	}

	/**
	 * Sets the country of this <code>Address</code>.
	 *
	 * @param country the new country.
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Gets the phone number associated with this <code>Address</code>.
	 *
	 * @return the phone number.
	 */
	@Basic
	@Column(name = "PHONE_NUMBER")
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Sets the phone number associated with this <code>Address</code>.
	 *
	 * @param phoneNumber the new phone number.
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Gets the fax number associated with this <code>Address</code>.
	 *
	 * @return the fax number.
	 */
	@Basic
	@Column(name = "FAX_NUMBER")
	public String getFaxNumber() {
		return this.faxNumber;
	}

	/**
	 * Sets the fax number associated with this <code>Address</code>.
	 *
	 * @param faxNumber the new fax number.
	 */
	public void setFaxNumber(final String faxNumber) {
		this.faxNumber = faxNumber;
	}

	/**
	 * Gets the city of this <code>Address</code>.
	 *
	 * @return the city.
	 */
	@Basic
	@Column(name = "CITY")
	public String getCity() {
		return this.city;
	}

	/**
	 * Sets the city of this <code>Address</code>.
	 *
	 * @param city the new city.
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Gets the state or province of this <code>Address</code>.
	 *
	 * @return the state or province.
	 */
	@Basic
	@Column(name = "SUB_COUNTRY")
	public String getSubCountry() {
		return this.subCountry;
	}

	/**
	 * Sets the state or province of this <code>Address</code>.
	 *
	 * @param subCountry the new state or province.
	 */
	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	/**
	 * Gets the zip/postal code of this <code>Address</code>.
	 *
	 * @return the zip/postal code.
	 */
	@Basic
	@Column(name = "ZIP_POSTAL_CODE")
	public String getZipOrPostalCode() {
		return this.zipOrPostalCode;
	}

	/**
	 * Sets the zip/postal code of this <code>Address</code>.
	 *
	 * @param zipOrPostalCode the new zip/postal code.
	 */
	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	/**
	 * True if this <code>Address</code> is a commercial address.
	 *
	 * @return true if the address is commercial.
	 */
	@Basic
	@Column(name = "COMMERCIAL")
	public boolean isCommercialAddress() {
		return this.commercialAddress;
	}

	/**
	 * Sets whether this <code>Address</code> is a commercial address.
	 *
	 * @param commercialAddress Set to true for commercial addresses.
	 */
	public void setCommercialAddress(final boolean commercialAddress) {
		this.commercialAddress = commercialAddress;
	}

	/**
	 * Gets the organization of this <code>Address</code>.
	 * 
	 * @return the organization.
	 */
	@Basic
	@Column(name = "ORGANIZATION")
	public String getOrganization() {
		return this.organization;
	}

	/**
	 * Sets the organization of this <code>Address</code>.
	 * 
	 * @param organization the new organization.
	 */
	public void setOrganization(final String organization) {
		this.organization = organization;
	}
	
	/**
	 * Return the string represents the address's region path info, in the format of "[zip/postal code] [city] [subcountry] [country]". An example
	 * will be "V5Y 1N3 Vancouver BC CA".
	 * 
	 * @return the string represents the address's region path info
	 */
	@Transient
	public String getRegionPathStr() {
		final StringBuffer regionPathSb = new StringBuffer();
		regionPathSb.append(getZipOrPostalCode()).append(' ');
		// This should only happen for the temporary address used for shipping/tax estimation
		if (getCity() != null) {
			regionPathSb.append(getCity()).append(' ');
		}
		if (getSubCountry() != null) {
			regionPathSb.append(getSubCountry()).append(' ');
		}
		regionPathSb.append(this.getCountry());
		return regionPathSb.toString();
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH, unique = true)
	@Index(name = "GUID", unique = true)
	public String getGuid() {
		return this.guid;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(this.lastName);
		result = prime * result + ObjectUtils.hashCode(this.firstName);
		result = prime * result + ObjectUtils.hashCode(this.street1);
		result = prime * result + ObjectUtils.hashCode(this.street2);
		result = prime * result + ObjectUtils.hashCode(this.city);
		result = prime * result + ObjectUtils.hashCode(this.country);
		result = prime * result + ObjectUtils.hashCode(this.faxNumber);
		result = prime * result + ObjectUtils.hashCode(this.phoneNumber);
		result = prime * result + ObjectUtils.hashCode(this.subCountry);
		result = prime * result + ObjectUtils.hashCode(this.zipOrPostalCode);
		result = prime * result + ObjectUtils.hashCode(this.organization);
		
		return result;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof AbstractAddressImpl)) {
			return false;
		}

		final AbstractAddressImpl address = (AbstractAddressImpl) other;
		return (StringUtils.equals(this.lastName, address.lastName)
				&& StringUtils.equals(this.firstName, address.firstName)
				&& StringUtils.equals(this.street1, address.street1)
				&& StringUtils.equals(this.street2, address.street2)
				&& StringUtils.equals(this.city, address.city)
				&& StringUtils.equals(this.country, address.country)
				&& StringUtils.equals(this.faxNumber, address.faxNumber)
				&& StringUtils.equals(this.phoneNumber, address.phoneNumber)
				&& StringUtils.equals(this.subCountry, address.subCountry)
				&& StringUtils.equals(this.zipOrPostalCode, address.zipOrPostalCode))
				&& StringUtils.equals(this.organization, address.organization);
	}

	@Override
	public String toString() {
		return getFirstName() + getLastName() + "\n"
		+ getStreet1() + getStreet2() + "\n"
		+ getCity() + "\n"
		+ getSubCountry() + ',' + getCountry() + getZipOrPostalCode() + "\n"
		+ "\nPhone: " + getPhoneNumber()
		+ "\nFax:" + getFaxNumber()
		+ "Commercial?: " + isCommercialAddress()
		+ "Organization: " + getOrganization();
	}

	@Override
	public String toPlainString() {
		return getStreet1() + COMMA_SPACE + getCity() + COMMA_SPACE
				+ getSubCountry() + COMMA_SPACE + getCountry() + COMMA_SPACE
				+ getZipOrPostalCode();
	}

	@Override
	@Transient
	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		if (StringUtils.isEmpty(this.getFirstName())) {
			if (!StringUtils.isEmpty(this.getLastName())) {
				fullName.append(this.getLastName());
			}
		} else {
			fullName.append(this.getFirstName());
			if (!StringUtils.isEmpty(this.getLastName())) {
				fullName.append(" ");
				fullName.append(this.getLastName());
			}
		}
		return fullName.toString();
	}

}
