/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.cmuser.impl;

import java.util.HashSet;
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

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * The default implementation of <code>UserRole</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
@Entity
@Table(name = UserRoleImpl.TABLE_NAME)
@DataCache(enabled = false)
public class UserRoleImpl extends AbstractEntityImpl implements UserRole {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TUSERROLE";

	private String name;

	private String description;

	private Set<UserPermission> userPermissions = new HashSet<UserPermission>();

	private long uidPk;

	private String guid;

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();
		if (getUserPermissions() == null) {
			setUserPermissions(new HashSet<UserPermission>());
		}
	}

	/**
	 * Gets the name of this <code>UserRole</code>.
	 *
	 * @return the userRole name.
	 */
	@Basic
	@Column(name = "Name", nullable = false, unique = true)
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this <code>UserRole</code>.
	 *
	 * @param name the userRole name.
	 * @throws EpDomainException if the given name is null or an empty string.
	 */
	public void setName(final String name) throws EpDomainException {
		if (name == null) {
			// throw new EpDomainException("UserRole name can not be null.");
			return;
		}
		if (name.trim().length() == 0) {
			throw new EpDomainException("Empty String is not allowed as userRole name.");
		}
		if (name.startsWith("ROLE_")) {
			this.name = "ROLE_" + name;
		} else {
			this.name = name;
		}
	}

	/**
	 * Gets the description of this <code>UserRole</code>.
	 *
	 * @return the userRole description.
	 */
	@Column(name = "DESCRIPTION", nullable = false)
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description of this <code>UserRole</code>.
	 *
	 * @param description the userRole description.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the <code>Permission</code>s associated with this <code>Role</code>.
	 *
	 * @return the set of userPermissions.
	 */
	@OneToMany(targetEntity = UserPermissionImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "ROLE_UID", nullable = false)
	@ElementDependent
	@ElementForeignKey(name = "TUSERROLEPERMISSIONX_IBFK_1")
	public Set<UserPermission> getUserPermissions() {
		return this.userPermissions;
	}

	/**
	 * Sets the <code>Permission</code>s associated with this <code>UserRole</code>.
	 *
	 * @param userPermissions the new set of userPermissions.
	 */
	public void setUserPermissions(final Set<UserPermission> userPermissions) {
		if (userPermissions == null) {
			// throw new EpDomainException("Null userPermissions cannot be set.");
			return;
		}
		this.userPermissions = userPermissions;
	}

	/**
	 * Return true if this is the SUPERUSER role.
	 *
	 * @return true if this is the SUPERUSER role; otherwise, false.
	 */
	@Transient
	public boolean isSuperUserRole() {
		if (SUPERUSER.equals(getName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return true if this role is an unmodifiable (hardcoded) role.
	 * 
	 * @return true if this is an unmodifiable (hardcoded) role, otherwise false
	 */
	@Transient
	public boolean isUnmodifiableRole() {
		return isSuperUserRole() || CMUSER.equals(getName()) || WSUSER.equals(getName());
	}

	/**
	 * Gets the authority for this <code>UserRole</code>.
	 *
	 * @return the authority as an identifier of the userRole.
	 */
	@Transient
	public String getAuthority() {
		return ROLE_PREFIX + getName().toUpperCase();
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", length = GUID_LENGTH, nullable = false, unique = true)
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

	@Override
	public void addUserPermission(final UserPermission permission) {
		getUserPermissions().add(permission);
	}

	@Override
	public void removeUserPermission(final UserPermission permission) {
		getUserPermissions().remove(permission);
	}

	@Override
	public String toString() {
		return getName() + ":" + getDescription();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(description);
		result = prime * result + ObjectUtils.hashCode(name);
		result = prime * result + ObjectUtils.hashCode(userPermissions);
		return result;
	}

	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof UserRoleImpl)) {
			return false;
		}
		final UserRoleImpl other = (UserRoleImpl) obj;
		return (ObjectUtils.equals(description, other.description)
				&& ObjectUtils.equals(name, other.name)
				&& ObjectUtils.equals(userPermissions, other.userPermissions));
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
