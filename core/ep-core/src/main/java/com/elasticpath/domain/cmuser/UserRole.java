/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.persistence.api.Entity;

/**
 * <code>UserRole</code> represents a user's role.
 */
public interface UserRole extends Entity, GrantedAuthority {

	/** The default role authority prefix (to integrate with spring security framework. */
	String ROLE_PREFIX = "ROLE_";
	
	/** The SUPER userRole name. cmUser with SUPERUSER role has unlimited access :). */
	String SUPERUSER = "SUPERUSER";
	
	/** Role for Commerce Manager Users. */
	String CMUSER = "CMUSER";
	
	/** Role for Web Services Users. */
	String WSUSER = "WSUSER";	
	
	/**
	 * Gets the name of this <code>UserRole</code>.
	 *
	 * @return the userRole name.
	 */
	String getName();

	/**
	 * Sets the name of this <code>UserRole</code>.
	 *
	 * @param name the userRole name.
	 */
	void setName(final String name);

	/**
	 * Gets the description of this <code>UserRole</code>.
	 *
	 * @return the userRole description.
	 */
	String getDescription();

	/**
	 * Sets the description of this <code>UserRole</code>.
	 *
	 * @param description the userRole description.
	 */
	void setDescription(final String description);

	/**
	 * Gets the <code>Permission</code>s associated with this <code>Role</code>.
	 *
	 * @return the set of userPermissions.
	 */
	Set<UserPermission> getUserPermissions();

	/**
	 * Sets the <code>Permission</code>s associated with this <code>UserRole</code>.
	 *
	 * @param userPermissions the new set of userPermissions.
	 */
	void setUserPermissions(final Set<UserPermission> userPermissions);

	/**
	 * Adds a <code>UserPermission</code> to this UserRole.
	 *
	 * @param permission the permission to add
	 */
	void addUserPermission(final UserPermission permission);

	/**
	 * Remove a <code>UserPermission</code> from this UserRole.
	 *
	 * @param permission the permission to remove
	 */
	void removeUserPermission(final UserPermission permission);

	/**
	 * Return true if this is the SUPERUSER role.
	 *
	 * @return true if this is the SUPERUSER role; otherwise, false.
	 */
	boolean isSuperUserRole();
	
	/**
	 * Return true if this role is an unmodifiable (hardcoded) role.
	 * 
	 * @return true if this is an unmodifiable (hardcoded) role, otherwise false
	 */
	boolean isUnmodifiableRole();
}
