/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.util.Collections;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;

/**
 * The default implementation of <code>CustomerGroup</code>.
 *
 */
@Entity
@Table(name = CustomerGroupImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerGroupImpl extends AbstractLegacyEntityImpl implements CustomerGroup {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERGROUP";

	private String name;

	private Set<CustomerRole> customerRoles;

	private long uidPk;

	private String guid;

	/** Gets the name for this <code>CustomerGroup</code>.
	 *
	 * @return the name of the customer group.
	 */
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name for this <code>CustomerGroup</code>.
	 *
	 * @param name the new user identifier.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @return the set of customerRoles.ROLE_CUSTOMER
	 */
	@OneToMany(targetEntity = CustomerRoleImpl.class, cascade = { CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH }, 
			   fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "CUSTOMER_GROUP_UID", nullable = false)
	protected Set<CustomerRole> getCustomerRolesInternal() {
		return this.customerRoles;
	}

	/**
	 * Gets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @return the set of customerRoles.ROLE_CUSTOMER
	 */
	@Transient
	public Set<CustomerRole> getCustomerRoles() {
		Set<CustomerRole> customerRolesInternal = getCustomerRolesInternal();
		if (customerRolesInternal == null) {
			return Collections.emptySet();
		}
		for (CustomerRole customerRole : customerRolesInternal) {
			final CustomerRole newCustomerRole = getBean(customerRole.getAuthority());
			customerRole.copyFrom(newCustomerRole);
		}
		return customerRolesInternal;
	}
	
	/**
	 * Sets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @param customerRoles the new set of customerRoles.
	 */
	protected void setCustomerRolesInternal(final Set<CustomerRole> customerRoles) {
		this.customerRoles = customerRoles;
	}

	/**
	 * Sets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @param customerRoles the new set of customerRoles.
	 */
	@Transient
	public void setCustomerRoles(final Set<CustomerRole> customerRoles) {
		if (customerRoles == null) {
			return;
		}

		for (CustomerRole customerRole : customerRoles) {
			final CustomerRole newCustomerRole = getBean(customerRole.getAuthority());
			customerRole.copyFrom(newCustomerRole);
		}
		setCustomerRolesInternal(customerRoles);
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
