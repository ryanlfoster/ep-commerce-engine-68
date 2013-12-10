package com.elasticpath.plugin.payment.dto.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.plugin.payment.dto.AddressDto;

/**
 * Implementation of {@link AddressDto}. Used in Payment Gateways.
 */
public class AddressDtoImpl implements AddressDto {

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


	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getStreet1() {
		return this.street1;
	}

	@Override
	public void setStreet1(final String street) {
		this.street1 = street;
	}

	@Override
	public String getStreet2() {
		return this.street2;
	}

	@Override
	public void setStreet2(final String street) {
		this.street2 = street;
	}

	@Override
	public String getCountry() {
		return this.country;
	}

	@Override
	public void setCountry(final String country) {
		this.country = country;
	}

	@Override
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	@Override
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String getFaxNumber() {
		return this.faxNumber;
	}

	@Override
	public void setFaxNumber(final String faxNumber) {
		this.faxNumber = faxNumber;
	}

	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public void setCity(final String city) {
		this.city = city;
	}

	@Override
	public String getSubCountry() {
		return this.subCountry;
	}

	@Override
	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	@Override
	public String getZipOrPostalCode() {
		return this.zipOrPostalCode;
	}

	@Override
	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	@Override
	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		fullName.append(this.getFirstName());
		fullName.append(" ");
		fullName.append(this.getLastName());
		return fullName.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
