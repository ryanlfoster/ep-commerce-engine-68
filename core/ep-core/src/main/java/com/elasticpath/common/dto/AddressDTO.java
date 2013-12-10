package com.elasticpath.common.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is a DTO for CustomerAddress.
 */
@XmlRootElement(name = AddressDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class AddressDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant used to enclose an address.
	 */
	public static final String ROOT_ELEMENT = "address";

	@XmlAttribute(required = true)
	private String guid;

	@XmlElement(name = "last_name")
	private String lastName;

	@XmlElement(name = "first_name")
	private String firstName;

	@XmlElement(name = "phone_number")
	private String phoneNumber;

	@XmlElement(name = "fax_number")
	private String faxNumber;

	@XmlElement(name = "street1")
	private String street1;

	@XmlElement(name = "street2")
	private String street2;

	@XmlElement(name = "city")
	private String city;

	@XmlElement(name = "subCountry")
	private String subCountry;

	@XmlElement(name = "zip_postal_code")
	private String zipOrPostalCode;

	@XmlElement
	private String country;

	@XmlElement(name = "commercial", required = true)
	private boolean commercialAddress;

	@XmlElement(name = "organization")
	private String organization;
	
	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(final String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(final String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(final String street2) {
		this.street2 = street2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getSubCountry() {
		return subCountry;
	}

	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	public void setZipOrPostalCode(final String zipPostalCode) {
		this.zipOrPostalCode = zipPostalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public boolean isCommercialAddress() {
		return commercialAddress;
	}

	public void setCommercialAddress(final boolean commercial) {
		this.commercialAddress = commercial;
	}

	 
	public String getOrganization() {
		return organization;
	}

	public void setOrganization(final String organization) {
		this.organization = organization;
	}

}
