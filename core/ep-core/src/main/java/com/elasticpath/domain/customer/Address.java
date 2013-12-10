/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.validation.constraints.NotBlank;
import com.elasticpath.validation.constraints.ValidCountry;

/**
 * <code>Address</code> represents a North American address.
 */
@ValidCountry
public interface Address extends Persistable {

	/** Maximum length for a short sized field. */
	int SHORT_MAXLENGTH = 50;

	/** Maximum length for a medium sized field. */
	int MEDIUM_MAXLENGTH = 100;

	/** Maximum length for a long sized field. */
	int LONG_MAXLENGTH = 200;

	/**
	 * Copies all the fields from another address into this address.
	 *
	 * @param address the address from which to copy fields
	 */
	void copyFrom(final Address address);
	
	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	String getGuid();

	/**
	 * Set the guid.
	 * @param guid the guid to set.
	 */
	void setGuid(String guid);
	
	/**
	 * Gets the last name associated with this <code>Address</code>.
	 * 
	 * @return the last name
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = MEDIUM_MAXLENGTH)
	String getLastName();

	/**
	 * Sets the last name associated with this <code>Address</code>.
	 * 
	 * @param lastName the last name.
	 */
	void setLastName(final String lastName);

	/**
	 * Gets the first name associated with this <code>Address</code>.
	 * 
	 * @return the first name
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = MEDIUM_MAXLENGTH)
	String getFirstName();

	/**
	 * Sets the first name associated with this <code>Address</code>.
	 * 
	 * @param firstName the first name.
	 */
	void setFirstName(final String firstName);

	/**
	 * Gets the phone number associated with this <code>Address</code>.
	 * 
	 * @return the phone number.
	 * @domainmodel.property
	 */
	@Size(min = 0, max = SHORT_MAXLENGTH)
	String getPhoneNumber();

	/**
	 * Sets the phone number associated with this <code>Address</code>.
	 * 
	 * @param phoneNumber the new phone number.
	 */
	void setPhoneNumber(final String phoneNumber);

	/**
	 * Gets the fax number associated with this <code>Address</code>.
	 * 
	 * @return the fax number.
	 * @domainmodel.property
	 */
	@Size(max = SHORT_MAXLENGTH)
	String getFaxNumber();

	/**
	 * Sets the fax number associated with this <code>Address</code>.
	 * 
	 * @param faxNumber the new fax number.
	 */
	void setFaxNumber(final String faxNumber);

	/**
	 * Gets the streetname of this <code>Address</code>.
	 * 
	 * @return the streetname.
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = LONG_MAXLENGTH)
	String getStreet1();

	/**
	 * Sets the streetname of this <code>Address</code>.
	 * 
	 * @param street the new streetname.
	 */
	void setStreet1(final String street);

	/**
	 * Gets the streetname of this <code>Address</code>.
	 * 
	 * @return the streetname.
	 * @domainmodel.property
	 */
	@Size(max = LONG_MAXLENGTH)
	String getStreet2();

	/**
	 * Sets the streetname of this <code>Address</code>.
	 * 
	 * @param street the new streetname.
	 */
	void setStreet2(final String street);

	/**
	 * Gets the city of this <code>Address</code>.
	 * 
	 * @return the city.
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = LONG_MAXLENGTH)
	String getCity();

	/**
	 * Sets the city of this <code>Address</code>.
	 * 
	 * @param city the new city.
	 */
	void setCity(final String city);

	/**
	 * Gets the state, province, or other region of the country for this <code>Address</code>.
	 * 
	 * @return the state, province, or other region of the country
	 * @domainmodel.property
	 */
	@Size(max = LONG_MAXLENGTH)
	String getSubCountry();

	/**
	 * Sets the state, province, or other region of the country for this <code>Address</code>.
	 * 
	 * @param subCountry the new state, province, or other region
	 */
	void setSubCountry(final String subCountry);

	/**
	 * Gets the zip/postal code of this <code>Address</code>.
	 * 
	 * @return the zip/postal code.
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = SHORT_MAXLENGTH)
	String getZipOrPostalCode();

	/**
	 * Sets the zip/postal code of this <code>Address</code>.
	 * 
	 * @param zipOrPostalCode the new zip/postal code.
	 */
	void setZipOrPostalCode(final String zipOrPostalCode);

	/**
	 * Gets the country of this <code>Address</code>.
	 * 
	 * @return the country.
	 * @domainmodel.property
	 */
	@NotNull
	@NotBlank
	@Size(min = 1, max = LONG_MAXLENGTH)
	String getCountry();

	/**
	 * Sets the country of this <code>Address</code>.
	 * 
	 * @param country the new country.
	 */
	void setCountry(final String country);

	/**
	 * True if this <code>Address</code> is a commercial address.
	 * 
	 * @return true if the address is commercial.
	 * @domainmodel.property
	 */
	boolean isCommercialAddress();

	/**
	 * Sets whether this <code>Address</code> is a commercial address.
	 * 
	 * @param commercialAddress Set to true for commercial addresses.
	 */
	void setCommercialAddress(final boolean commercialAddress);

	/**
	 * Gets the organization of this <code>Address</code>.
	 * 
	 * @return the organization.
	 * @domainmodel.property
	 */
	@Size(max = LONG_MAXLENGTH)
	String getOrganization();

	/**
	 * Sets the organization of this <code>Address</code>.
	 * 
	 * @param organization the new organization.
	 */
	void setOrganization(final String organization);
	
	/**
	 * Return the string reprents the address's region path info, starting from city. An example will be "Vancouver BC CA".
	 * 
	 * @return the string reprents the address's region path info
	 */
	String getRegionPathStr();
	
	/**
	 * Represents this address as plain string in the form: street1, city, subcountry, country, zip code.
	 * 
	 * @return a string representing this address object
	 */
	String toPlainString();

	/**
	 * Gets the full name associated with this <code>Address</code>.
	 * This is normally first name followed by last name.
	 *
	 * @return the full name 
	 */
	String getFullName();
}
