package com.elasticpath.domain.auth;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * A memento for a {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}.
 */
@Embeddable
public class UserAuthenticationMemento {

	private String customerGuid;

	private String role;

	private String credentials;

	private String storeCode;

	/**
	 * Gets the guid.
	 *
	 * @return the guid
	 */
	@Basic
	@Column(name = "CUSTOMER_GUID", nullable = false)
	public String getCustomerGuid() {
		return customerGuid;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid the new guid
	 */
	public void setCustomerGuid(final String guid) {
		this.customerGuid = guid;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	@Basic
	@Column(name = "CUSTOMER_ROLE", nullable = false)
	public String getRole() {
		return role;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRole(final String role) {
		this.role = role;
	}

	/**
	 * Gets the credentials.
	 *
	 * @return the credentials
	 */
	@Basic
	@Column(name = "CREDENTIALS", nullable = true)
	public String getCredentials() {
		return credentials;
	}

	/**
	 * Sets the credentials.
	 *
	 * @param credentials the new credentials
	 */
	public void setCredentials(final String credentials) {
		this.credentials = credentials;
	}

	/**
	 * Gets the store code.
	 *
	 * @return the store code
	 */
	@Basic
	@Column(name = "STORECODE", nullable = false)
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * Sets the store code.
	 *
	 * @param storeCode the new store code
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}
}
