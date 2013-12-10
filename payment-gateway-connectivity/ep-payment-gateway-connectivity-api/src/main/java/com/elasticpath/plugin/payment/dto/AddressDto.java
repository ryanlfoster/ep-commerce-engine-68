package com.elasticpath.plugin.payment.dto;

/**
 * DTO for Customer Addresses for use in Payment Gateways.
 */
public interface AddressDto {
	/**
	 * Gets the last name associated with this <code>Address</code>.
	 * 
	 * @return the last name
	 * @domainmodel.property
	 */
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
	String getCountry();

	/**
	 * Sets the country of this <code>Address</code>.
	 * 
	 * @param country the new country.
	 */
	void setCountry(final String country);

	/**
	 * Gets the full name associated with this <code>Address</code>.
	 * This is normally first name followed by last name.
	 *
	 * @return the full name 
	 */
	String getFullName();
}
