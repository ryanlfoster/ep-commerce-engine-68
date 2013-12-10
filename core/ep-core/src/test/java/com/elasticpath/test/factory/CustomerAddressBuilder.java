// CHECKSTYLE:OFF
/**
 * Source code generated by Fluent Builders Generator
 * Do not modify this file
 * See generator home page at: http://code.google.com/p/fluent-builders-generator-eclipse-plugin/
 */

package com.elasticpath.test.factory;

import com.elasticpath.domain.customer.impl.CustomerAddressImpl;

public class CustomerAddressBuilder extends CustomerAddressImplBuilderBase<CustomerAddressBuilder> {
	public static CustomerAddressBuilder newCustomerAddress() {
		return new CustomerAddressBuilder();
	}

	public CustomerAddressBuilder() {
		super(new CustomerAddressImpl());
	}

	public CustomerAddressImpl build() {
		return getInstance();
	}
}

@SuppressWarnings("unchecked")
class CustomerAddressImplBuilderBase<GeneratorT extends CustomerAddressImplBuilderBase<GeneratorT>> {
	private final CustomerAddressImpl instance;

	protected CustomerAddressImplBuilderBase(final CustomerAddressImpl aInstance) {
		instance = aInstance;
	}

	protected CustomerAddressImpl getInstance() {
		return instance;
	}

	public GeneratorT withUidPk(final long aValue) {
		instance.setUidPk(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withLastName(final String aValue) {
		instance.setLastName(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withFirstName(final String aValue) {
		instance.setFirstName(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withStreet1(final String aValue) {
		instance.setStreet1(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withStreet2(final String aValue) {
		instance.setStreet2(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withCountry(final String aValue) {
		instance.setCountry(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withPhoneNumber(final String aValue) {
		instance.setPhoneNumber(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withFaxNumber(final String aValue) {
		instance.setFaxNumber(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withCity(final String aValue) {
		instance.setCity(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withSubCountry(final String aValue) {
		instance.setSubCountry(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withZipOrPostalCode(final String aValue) {
		instance.setZipOrPostalCode(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withCommercialAddress(final boolean aValue) {
		instance.setCommercialAddress(aValue);

		return (GeneratorT) this;
	}

	public GeneratorT withGuid(final String aValue) {
		instance.setGuid(aValue);

		return (GeneratorT) this;
	}
	
	public GeneratorT withOrganization(final String aValue) {
		instance.setOrganization(aValue);
		return (GeneratorT) this;
	}

}