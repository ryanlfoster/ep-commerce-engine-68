/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser;

import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.persistence.api.Entity;

/**
 * <code>CmUser</code> represents a person with an account in the system for accessing the Commerce Manager or web services.
 */
public interface CmUser extends Entity, UserDetails, PasswordHolder, GrantableObjects {

	/**
	 * Gets the user name for this <code>CmUser</code>.
	 * 
	 * @return the user name.
	 */
	String getUserName();

	/**
	 * Sets the user name for this <code>CmUser</code>.
	 * 
	 * @param userName the new user name.
	 */
	void setUserName(final String userName);

	/**
	 * Gets the email address of this <code>CmUser</code>.
	 * 
	 * @return the email address.
	 */
	String getEmail();

	/**
	 * Sets the email address of this <code>CmUser</code>.
	 * 
	 * @param email the new email address.
	 */
	void setEmail(final String email);

	/**
	 * Gets the <code>CmUser</code>'s first name.
	 * 
	 * @return the first name.
	 */
	String getFirstName();

	/**
	 * Sets the <code>CmUser</code>'s first name.
	 * 
	 * @param firstName the new first name.
	 */
	void setFirstName(final String firstName);

	/**
	 * Gets the <code>CmUser</code>'s last name.
	 * 
	 * @return the last name.
	 */
	String getLastName();

	/**
	 * Sets the <code>CmUser</code>'s last name.
	 * 
	 * @param lastName the new last name.
	 */
	void setLastName(final String lastName);

	/**
	 * Gets the encrypted password.
	 * 
	 * @return the encrypted password.
	 */
	String getPassword();

	/**
	 * Sets the encrypted password. By default, the clear-text user input password will be encrypted using the SHA1 secure hash algorithm
	 * 
	 * @param password the encrypted password.
	 */
	void setPassword(String password);

	/**
	 * Sets the clear-text password.
	 * Before resetting password method checks that it is not the same as one of previous four
	 * Then new password will be encrypted using a secure hash like MD5 or SHA1 and saved as a password
	 * Old password will be added to user's password history and old password with latest date
	 * will be removed from history (it is the first item in the list because they are held in
	 * ascending order)
	 * 
	 * @param clearTextPassword the clear-text password.
	 */
	void setCheckedClearTextPassword(final String clearTextPassword);

	/**
	 * Gets the clear-text password (only available at the creation time).
	 * 
	 * @return the clear-text password.
	 */
	String getClearTextPassword();

	/**
	 * Sets the clear-text password.
	 * 
	 * @param clearTextPassword the clear-text password.
	 */
	void setClearTextPassword(final String clearTextPassword);

	/**
	 * Sets the confirm clear-text password. This is to compare with the ClearTextPassword to ensure they are the same.
	 * 
	 * @param confirmClearTextPassword the user confirmClearTextPassword.
	 */
	void setConfirmClearTextPassword(final String confirmClearTextPassword);

	/**
	 * Gets the clear-text confirm password (only available at the creation time).
	 * 
	 * @return the clear-text confirm password.
	 */
	String getConfirmClearTextPassword();

	/**
	 * Reset the CmUser's password.
	 * 
	 * @return the reseted password
	 */
	String resetPassword();

	/**
	 * Convenience method for determining whether this user has the CM role.
	 * 
	 * @return true if this cmUser has access to Commerce Manager functionality.
	 */
	boolean isCmAccess();

	/**
	 * Convenience method for determining whether this user has the Web Services role.
	 * 
	 * @return true if this cmUser has access to Web Services.
	 */
	boolean isWsAccess();

	/**
	 * Indicates whether the user has temporary password.
	 * 
	 * @return <code>true</code> if the user has temporary password, <code>false</code> otherwise
	 */
	boolean isTemporaryPassword();
	
	/**
	 * Sets whether this <code>CmUser</code> has temporary password.
	 * 
	 * @param isTemporaryPassword true if user has temporary password, false if not
	 */
	void setTemporaryPassword(final boolean isTemporaryPassword);

	
	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
	 * 
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	boolean isEnabled();
	
	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
	 * 
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	boolean isAccountNonLocked();

	/**
	 * Sets whether this <code>CmUser</code> is enabled.
	 * 
	 * @param enabled true if user account is enabled, false if not
	 */
	void setEnabled(final boolean enabled);
		
	/**
	 * Indicates is password expired, uses elasticPath object to obtain maximum password age. 
	 * 
	 * @return true if password is expired and false otherwise
	 */
	boolean isPasswordExpired();
	
	/**
	 * Gets the <code>UserRole</code>s associated with this <code>CmUser</code>.
	 * 
	 * @return the set of userRoles.
	 */
	Collection<UserRole> getUserRoles();

	/**
	 * Decide whether a user has the SuperUser role.
	 * 
	 * @return true if the user has the superuser role, false if not
	 */
	boolean isSuperUser();

	/**
	 * Sets the <code>UserRole</code>s associated with this <code>CmUser</code>.
	 * 
	 * @param userRoles the new set of userRoles.
	 */
	void setUserRoles(final Collection<UserRole> userRoles);

	/**
	 * Adds an <code>UserRole</code> to the list of userRoles.
	 * 
	 * @param userRole the userRole to add.
	 */
	void addUserRole(final UserRole userRole);

	/**
	 * Removes an <code>UserRole</code> from the list of userRoles.
	 * 
	 * @param userRole the userRole to remove.
	 */
	void removeUserRole(final UserRole userRole);

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> has the userRole with the given userRoleID.
	 * 
	 * @param userRoleID - userRole ID.
	 * @return true if cmUser belongs to userRole with the given userRoleID; otherwise, false.
	 */
	boolean hasUserRole(final long userRoleID);

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> has the userRole with the given name.
	 * 
	 * @param userRoleName - userRole name.
	 * @return true if cmUser belongs to userRole with the given userRole name; otherwise, false.
	 */
	boolean hasUserRole(final String userRoleName);

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> 
	 * has the permission with the given authority value.
	 * 
	 * @param authority - authority value.
	 * @return true if cmUser has the permission with the given authority value; otherwise, false.
	 */
	boolean hasPermission(final String authority);

	/**
	 * Gets this <code>cmUser</code>'s creationDate.
	 * 
	 * @return cmUser's creation date.
	 */
	Date getCreationDate();

	/**
	 * Sets this <code>cmUser</code>'s creationDate.
	 * 
	 * @param creationDate cmUser's creationDate.
	 */
	void setCreationDate(final Date creationDate);

	/**
	 * Gets this <code>cmUser</code>'s last login date.
	 * 
	 * @return cmUser's last login date.
	 */
	Date getLastLoginDate();

	/**
	 * Sets this <code>cmUser</code>'s last login date.
	 * 
	 * @param lastLoginDate cmUser's last login date.
	 */
	void setLastLoginDate(final Date lastLoginDate);

	/**
	 * Gets this <code>cmUser</code>'s last changed password date.
	 * 
	 * @return cmUser's last changed password date.
	 */
	Date getLastChangedPasswordDate();

	/**
	 * Sets this <code>cmUser</code>'s last changed password date.
	 * 
	 * @param lastChangedPasswordDate cmUser's last changed password date.
	 */
	void setLastChangedPasswordDate(final Date lastChangedPasswordDate);
	
	/**
	 * Gets this <code>cmUser</code>'s last modified date.
	 * 
	 * @return cmUser's last modified date.
	 */
	Date getLastModifiedDate();

	/**
	 * Sets this <code>cmUser</code>'s last modified date.
	 * 
	 * @param lastModifiedDate cmUser's last modified date.
	 */
	void setLastModifiedDate(final Date lastModifiedDate);

	/**
	 * Sets this <code>cmUser</code>'s failed login attempts number.
	 * 
	 * @param failedLoginAttempts failed login attempts number
	 */
	void setFailedLoginAttempts(final int failedLoginAttempts);
	
	/**
	 * Resets failed login attempts. Note: After this action user will be unlocked.
	 */
	void resetFailedLoginAttempts();
	
	/**
	 * Adds +1 to failed login attempts.
	 */
	void addFailedLoginAttempt();

	
	/**
	 * Gets the user status.
	 * 
	 * @return user status
	 */
	UserStatus getUserStatus();

}