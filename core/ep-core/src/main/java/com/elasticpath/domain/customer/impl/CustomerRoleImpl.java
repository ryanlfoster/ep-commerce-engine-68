package com.elasticpath.domain.customer.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * The default implementation of <code>CustomerRole</code>.
 */
@Entity
@Table(name = CustomerRoleImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerRoleImpl extends AbstractLegacyPersistenceImpl implements CustomerRole {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERGROUPROLEX";

	private String authority;

	private long uidPk;

	/**
	 * Initializes the <code>CustomerRole</code> object given its authority. Call setElasticPath before initializing.
	 */
	public void init() {
		if (this.getAuthority() == null) {
			throw new EpDomainException("Authority for CustomerRole is not set.");
		}
	}

	/**
	 * Gets the authority for this <code>Permission</code>.
	 *
	 * @return the authority as an identifier of the permission.
	 */
	@Basic
	@Column(name = "CUSTOMER_ROLE", nullable = false)
	public String getAuthority() {
		return this.authority;
	}

	/**
	 * Sets the authority for this <code>Permission</code>.
	 *
	 * @param authority the identifier of the permission.
	 * @throws EpDomainException if the given authority is null or an empty string.
	 */
	public void setAuthority(final String authority) throws EpDomainException {
		if (authority == null) {
			//throw new EpDomainException("CustomerRole authority can not be null.");
			return;
		}
		if (authority.trim().length() == 0) {
			throw new EpDomainException("Empty String is not allowed as CustomerRole authority.");
		}
		this.authority = authority;
	}


	/**
	 * Copy the customer role properties.
	 *
	 * @param customerRole the customer role copy from
	 */
	public void copyFrom(final CustomerRole customerRole) {
		this.setAuthority(customerRole.getAuthority());
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
}
