/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKeyAction;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpPasswordValidationException;
import com.elasticpath.commons.security.CmPasswordPolicy;
import com.elasticpath.commons.security.PasswordPolicy;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.domain.cmuser.UserPermission;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.service.misc.TimeService;

/**
 * The default implementation of <code>CmUser</code>.
 */
@Entity
@Table(name = CmUserImpl.TABLE_NAME)
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyFields", "PMD.ExcessiveClassLength",
					"PMD.TooManyMethods" })
@DataCache(enabled = false)
public class CmUserImpl extends AbstractLegacyEntityImpl implements CmUser, Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private static final String USER_UID = "USER_UID";

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCMUSER";

	private static final int IS_ENABLED_MASK = 1;

	private static final int IS_TEMPORARY_PASSWORD_MASK = 2;

	private static final Logger LOG = Logger.getLogger(CmUserImpl.class);

	private static final int MAX_NAME_LENGTH = 100;

	private String userName;

	private String email;

	private String firstName;

	private String lastName;

	private String clearTextPassword;

	private String confirmClearTextPassword;

	private String password;

	private Collection<UserRole> userRoles;

	private Date creationDate;

	private Date lastLoginDate;

	private Date lastChangedPasswordDate;
	
	private Date lastModifiedDate;

	private int failedLoginAttempts;

	private int status = IS_ENABLED_MASK; // NOPMD

	private long uidPk;

	private boolean allStoresAccess;

	private boolean allWarehousesAccess;

	private boolean allCatalogsAccess;
	
	private boolean allPriceListsAccess;

	private Set<Store> accessibleStores;

	private Set<Warehouse> accessibleWarehouses;

	private Set<Catalog> accessibleCatalogs;

	private List<UserPasswordHistoryItem> passwordHistoryItems;
	
	private Collection<String> accessiblePriceLists;

	private String guid;
	
	/**
	 * The default constructor.
	 */
	public CmUserImpl() {
		super();
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();
		if (this.getCreationDate() == null) {
			this.setCreationDate(new Date());
		}
		if (this.getUserRoles() == null) {
			this.setUserRoles(new HashSet<UserRole>());
		}

		if (this.getStores() == null) {
			this.setStores(new HashSet<Store>());
		}

		if (this.getWarehouses() == null) {
			this.setWarehouses(new HashSet<Warehouse>());
		}

		if (this.getCatalogs() == null) {
			this.setCatalogs(new HashSet<Catalog>());
		}
	}

	/**
	 * Gets the user name for this <code>CmUser</code>.
	 * 
	 * @return the user name.
	 */
	@Basic
	@Column(name = "USER_NAME", nullable = false, unique = true)
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Sets the user name for this <code>CmUser</code>.
	 * 
	 * @param userName the new user name.
	 * @throws EpDomainException if the given identifier is <code>null</code>.
	 */
	public void setUserName(final String userName) throws EpDomainException {
		this.userName = userName;
	}

	/**
	 * Gets the email address of this <code>Customer</code>.
	 * 
	 * @return the email address.
	 */
	@Basic
	@Column(name = "EMAIL", nullable = false, unique = true)
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the email address of this <code>Customer</code>.
	 * 
	 * @param email the new email address.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Gets the <code>Customer</code>'s first name.
	 * 
	 * @return the first name.
	 */
	@Basic
	@Column(name = "FIRST_NAME", length = MAX_NAME_LENGTH, nullable = false, unique = true)
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Sets the <code>Customer</code>'s first name.
	 * 
	 * @param firstName the new first name.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the <code>Customer</code>'s last name.
	 * 
	 * @return the last name.
	 */
	@Basic
	@Column(name = "LAST_NAME", nullable = false, unique = true)
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets the <code>Customer</code>'s last name.
	 * 
	 * @param lastName the new last name.
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the encrypted password.
	 * 
	 * @return the encrypted password.
	 */
	@Basic
	@Column(name = "PASSWORD", nullable = false, unique = true)
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the encrypted password. By default, the clear-text user input password will be encrypted using the SHA1 secure hash algorithm.
	 * 
	 * @param password the encrypted password.
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Sets the clear-text password. The password will be encrypted using a secure hash like MD5 or SHA1 and saved as password.
	 * 
	 * @param clearTextPassword the clear-text password.
	 */
	@Transient
	public void setClearTextPassword(final String clearTextPassword) {
		this.clearTextPassword = clearTextPassword;
	}

	@Override
	@Transient
	public void setCheckedClearTextPassword(final String clearTextPassword) {
		this.clearTextPassword = clearTextPassword;
		ValidationResult validationResult = getCmPasswordPolicy().validate(this);
		if (validationResult.isValid()) {
			addCurrentPasswordToHistory();
			setClearTextPassword(clearTextPassword);
			setPasswordUsingPasswordEncoder(clearTextPassword);
			setLastChangedPasswordDate(getTimeService().getCurrentTime());
			setTemporaryPassword(false);
		} else {
			throw new EpPasswordValidationException("Password didn't pass validation");
		}
	}

	private void addCurrentPasswordToHistory() {
		if (!isTemporaryPassword() && getPassword() != null) {
			final List<UserPasswordHistoryItem> passwordHistoryItems = getPasswordHistoryItems();
			if ((passwordHistoryItems.size() >= getCmPasswordPolicy().getPasswordHistoryLength() - 1) 
					&& !passwordHistoryItems.isEmpty()) {
				passwordHistoryItems.remove(0);
			}

			final UserPasswordHistoryItem currentPasswordToHistory = getBean(ContextIdNames.USER_PASSWORD_HISTORY_ITEM);
			currentPasswordToHistory.setOldPassword(getPassword());
			currentPasswordToHistory.setExpirationDate(getTimeService().getCurrentTime());
			passwordHistoryItems.add(currentPasswordToHistory);
		}
	}

	/**
	 * Gets the clear-text password (only available at the creation time).
	 * 
	 * @return the clear-text password.
	 */
	@Transient
	public String getClearTextPassword() {
		return this.clearTextPassword;
	}

	/**
	 * Sets the confirm clear-text password. This is to compare with the ClearTextPassword and make sure they are the same.
	 * 
	 * @param confirmClearTextPassword the user confirmClearTextPassword.
	 */
	public void setConfirmClearTextPassword(final String confirmClearTextPassword) {
		this.confirmClearTextPassword = confirmClearTextPassword;
	}

	/**
	 * Gets the clear-text confirm password (only available at the creation time).
	 * 
	 * @return the clear-text confirm password.
	 */
	@Transient
	public String getConfirmClearTextPassword() {
		return this.confirmClearTextPassword;
	}

	/**
	 * Sets the password using the PasswordEncoder. TODO: use a salt for encoding.
	 * 
	 * @param clearTextPassword the new clearText password.
	 */
	void setPasswordUsingPasswordEncoder(final String clearTextPassword) {
		if (clearTextPassword == null) {
			setPassword(null);
		} else {
			try {
				final PasswordEncoder passwordEncoder = getBean(ContextIdNames.CM_PASSWORDENCODER);
				setPassword(passwordEncoder.encodePassword(clearTextPassword, null));
			} catch (Exception exception) {
				LOG.error("Unable to set user's password.", exception);
			}
		}
	}

	/**
	 * Reset the customer's password and set Account UnLocked and ChangePasswordRequired.
	 * 
	 * @return the reseted password
	 */
	public String resetPassword() {
		PasswordGenerator passwordGenerator = getCmPasswordPolicy().getPasswordGenerator();
		final String newPassword = passwordGenerator.getPassword();
		addCurrentPasswordToHistory();
		setClearTextPassword(newPassword);
		setPasswordUsingPasswordEncoder(clearTextPassword);
		setTemporaryPassword(true);
		resetFailedLoginAttempts();
		return newPassword;
	}

	/**
	 * Convenience method for determining whether this user has the CM role.
	 * 
	 * @return true if this cmUser has access to Commerce Manager functionality.
	 */
	@Transient
	public boolean isCmAccess() {
		return this.isSuperUser() || this.hasUserRole(UserRole.CMUSER);
	}

	/**
	 * Convenience method for determining whether this user has the Web Services role.
	 * 
	 * @return true if this cmUser has access to Web Services.
	 */
	@Transient
	public boolean isWsAccess() {
		return this.isSuperUser() || this.hasUserRole(UserRole.WSUSER);
	}

	/**
	 * Sets whether this <code>CmUser</code> is enabled.
	 * 
	 * @param enabled true if user account is enabled, false if not
	 */
	public void setEnabled(final boolean enabled) {
		setStatusFlag(IS_ENABLED_MASK, enabled);
	}

	/**
	 * Resets failed login attempts. Note: After this action user will be unlocked.
	 */
	public void resetFailedLoginAttempts() {
		setFailedLoginAttempts(0);
	}

	/**
	 * Sets whether this <code>CmUser</code> has temporary password.
	 * 
	 * @param isTemporaryPassword true if user has temporary password, false if not
	 */
	public void setTemporaryPassword(final boolean isTemporaryPassword) {
		setStatusFlag(IS_TEMPORARY_PASSWORD_MASK, isTemporaryPassword);
	}

	private void setStatusFlag(final int mask, final boolean isEnabled) {
		if (isEnabled) {
			setStatus(getStatus() | mask);
		} else {
			setStatus(getStatus() & ~mask);
		}
	}

	/**
	 * Gets the <code>UserRole</code>s associated with this <code>CmUser</code>.
	 * 
	 * @return the set of userRoles.
	 */
	@ManyToMany(targetEntity = UserRoleImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.EAGER)
	@JoinTable(name = "TCMUSERROLEX", joinColumns = @JoinColumn(name = "CM_USER_UID", nullable = false), 
			inverseJoinColumns = @JoinColumn(name = "USER_ROLE_UID", nullable = false))
	@ElementForeignKey(deleteAction = ForeignKeyAction.CASCADE)
	public Collection<UserRole> getUserRoles() {
		return this.userRoles;
	}

	/**
	 * Sets the <code>UserRole</code>s associated with this <code>CmUser</code>.
	 * 
	 * @param userRoles the new set of userRoles.
	 */
	public void setUserRoles(final Collection<UserRole> userRoles) {
		// if (userRoles == null) {
		// throw new EpDomainException("Null userRoles cannot be set. The user
		// should at least has the default User role.");
		// }
		this.userRoles = userRoles;
	}

	/**
	 * Adds an <code>UserRole</code> to the list of userRoles.
	 * 
	 * @param userRole the userRole to add.
	 */
	public void addUserRole(final UserRole userRole) {
		if (userRole == null) {
			throw new EpDomainException("Null user role cannot be added.");
		}
		if (!this.getUserRoles().contains(userRole)) {
			this.getUserRoles().add(userRole);
		}
	}

	/**
	 * Removes an <code>UserRole</code> from the list of userRoles.
	 * 
	 * @param userRole the userRole to remove.
	 */
	public void removeUserRole(final UserRole userRole) {
		if (userRole == null) {
			throw new EpDomainException("Null user role cannot be added.");
		}
		this.getUserRoles().remove(userRole);
	}

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> has the userRole with the given userRoleID.
	 * 
	 * @param userRoleID - userRole ID.
	 * @return true if cmUser belongs to userRole with the given userRoleID; otherwise, false.
	 */
	public boolean hasUserRole(final long userRoleID) {
		boolean status = false;
		if (userRoleID > 0 && this.getUserRoles() != null && !this.getUserRoles().isEmpty()) {
			Iterator<UserRole> userRoleIter = this.getUserRoles().iterator();
			while (userRoleIter.hasNext()) {
				if (userRoleID == userRoleIter.next().getUidPk()) {
					status = true;
					break;
				}
			}

		}
		return status;
	}

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> has the userRole with the given name.
	 * 
	 * @param userRoleName - userRole name.
	 * @return true if cmUser belongs to userRole with the given userRole name; otherwise, false.
	 */
	public boolean hasUserRole(final String userRoleName) {
		boolean status = false;
		if (userRoleName != null && this.getUserRoles() != null && !this.getUserRoles().isEmpty()) {
			Iterator<UserRole> userRoleIter = this.getUserRoles().iterator();
			while (userRoleIter.hasNext()) {
				if (userRoleName.equals(userRoleIter.next().getName())) {
					status = true;
					break;
				}
			}

		}
		return status;
	}

	/**
	 * Return a boolean that indicates whether this <code>CmUser</code> has been granted the given authority value, whether as a UserRole or as a
	 * UserPermission.
	 * 
	 * @param authority - authority value.
	 * @return true if cmUser has the permission with the given authority value; otherwise, false.
	 */
	public boolean hasPermission(final String authority) {
		boolean status = false;
		if (authority != null && this.getUserRoles() != null && !this.getUserRoles().isEmpty()) {
			Iterator<UserRole> userRoleIter = this.getUserRoles().iterator();
			while (userRoleIter.hasNext()) {
				final UserRole userRole = userRoleIter.next();
				if (userRole.isSuperUserRole()) {
					status = true;
					break;
				}
				final Set<UserPermission> userPermissions = userRole.getUserPermissions();
				for (UserPermission userPermission : userPermissions) {
					if (authority.equals(userPermission.getAuthority())) {
						status = true;
						break;
					}
				}
			}
		}
		return status;
	}

	/**
	 * Decide whether a user has the SuperUser role.
	 * 
	 * @return true if the user has the superuser role, false if not
	 */
	@Transient
	public boolean isSuperUser() {
		if (this.getUserRoles() != null && !this.getUserRoles().isEmpty()) {
			for (UserRole role : getUserRoles()) {
				if (role.isSuperUserRole()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets this <code>cmUser</code>'s creationDate.
	 * 
	 * @return cmUser's creation date.
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Sets this <code>cmUser</code>'s creationDate.
	 * 
	 * @param creationDate cmUser's creationDate.
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets this <code>cmUser</code>'s last login date.
	 * 
	 * @return cmUser's last login date.
	 */
	@Basic
	@Column(name = "LAST_LOGIN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastLoginDate() {
		return this.lastLoginDate;
	}

	/**
	 * Sets this <code>cmUser</code>'s last login date.
	 * 
	 * @param lastLoginDate cmUser's last login date.
	 */
	public void setLastLoginDate(final Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	/**
	 * Gets this <code>cmUser</code>'s last changed password date.
	 * 
	 * @return cmUser's last changed password date.
	 */
	@Basic
	@Column(name = "LAST_CHANGED_PASSWORD_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastChangedPasswordDate() {
		return this.lastChangedPasswordDate;
	}

	/**
	 * Sets this <code>cmUser</code>'s last changed password date.
	 * 
	 * @param lastChangedPasswordDate cmUser's last changed password date.
	 */
	public void setLastChangedPasswordDate(final Date lastChangedPasswordDate) {
		this.lastChangedPasswordDate = lastChangedPasswordDate;
	}
	
	/**
	 * Gets this <code>cmUser</code>'s last modified date.
	 * 
	 * @return cmUser's last modified date.
	 */
	@Basic
	@Column(name = "LAST_MODIFIED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/**
	 * Sets this <code>cmUser</code>'s last modified date.
	 * 
	 * @param lastModifiedDate cmUser's last modified date.
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Gets this <code>cmUser</code>'s failed login attempts number.
	 * 
	 * @return failed login attempts number
	 */
	@Basic
	@Column(name = "FAILED_LOGIN_ATTEMPTS")
	public int getFailedLoginAttempts() {
		return this.failedLoginAttempts;
	}

	/**
	 * Sets this <code>cmUser</code>'s failed login attempts number.
	 * 
	 * @param failedLoginAttempts failed login attempts number
	 */
	public void setFailedLoginAttempts(final int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	/**
	 * Adds +1 to failed login attempts.
	 */
	public void addFailedLoginAttempt() {
		setFailedLoginAttempts(failedLoginAttempts + 1);
	}


	/**
	 * Indicates whether the user's account has expired. An expired account cannot be authenticated.
	 * 
	 * @return <code>true</code> if the user's account is valid (ie non-expired), <code>false</code> if no longer valid (ie expired)
	 */
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 * 
	 * @return the authorities (never <code>null</code>)
	 */
	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthority = new ArrayList<GrantedAuthority>();
		for (UserRole userRole : this.getUserRoles()) {
			grantedAuthority.add(userRole);
			Iterator<UserPermission> userPermissionIter = userRole
					.getUserPermissions().iterator();
			while (userPermissionIter.hasNext()) {
				grantedAuthority.add(userPermissionIter.next());
			}
		}
		return grantedAuthority;
	}

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired credentials prevent authentication.
	 * 
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired), <code>false</code> if no longer valid (ie expired)
	 */
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
	 * 
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	@Transient
	public boolean isAccountNonLocked() {
		return getAccountPasswordPolicy().validate(this).isValid();
	}

	@Transient
	private CmPasswordPolicy getCmPasswordPolicy() {
		return getBean("cmPasswordPolicy");
	}

	@Transient
	private PasswordPolicy getMaximumAgePasswordPolicy() {
		return getBean("maximumAgePasswordPolicy");
	}

	@Transient
	private PasswordPolicy getAccountPasswordPolicy() {
		return getBean("accountPasswordPolicy");
	}

	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
	 * 
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	@Transient
	public boolean isEnabled() {
		return isStatusFlagEnabled(IS_ENABLED_MASK);
	}

	/**
	 * Indicates whether the user has temporary password.
	 * 
	 * @return <code>true</code> if the user has temporary password, <code>false</code> otherwise
	 */
	@Transient
	public boolean isTemporaryPassword() {
		return isStatusFlagEnabled(IS_TEMPORARY_PASSWORD_MASK);
	}

	/**
	 * Indicates is password expired, uses elasticPath object to obtain maximum password age.
	 * 
	 * @return true if password is expired and false otherwise
	 */
	@Transient
	public boolean isPasswordExpired() {
		return !getMaximumAgePasswordPolicy().validate(this).isValid();
	}

	/**
	 * Gets the list of user's password history items.
	 * 
	 * @return the list of <code>UserPasswordHistoryItem</code> instances
	 */
	@OneToMany(targetEntity = UserPasswordHistoryItemImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CM_USER_UID", nullable = false)
	@ElementForeignKey(name = "TPASSWORDHISTORY_IBFK_1")
	@ElementDependent
	@OrderBy("expirationDate")
	public List<UserPasswordHistoryItem> getPasswordHistoryItems() {
		if (passwordHistoryItems == null) {
			passwordHistoryItems = new ArrayList<UserPasswordHistoryItem>();
		}
		return passwordHistoryItems;
	}

	/**
	 * Sets the list of user's password history items.
	 * 
	 * @param passwordHistoryItems the list of <code>UserPasswordHistoryItem</code> instances
	 */
	protected void setPasswordHistoryItems(final List<UserPasswordHistoryItem> passwordHistoryItems) {
		this.passwordHistoryItems = passwordHistoryItems;
	}

	private boolean isStatusFlagEnabled(final int mask) {
		return ((getStatus() & mask) == mask);
	}

	/**
	 * Gets the user status, this field contains information about all statuses. First bit used for enabled/disabled statuses, second bit for locked
	 * status and third for first time login status.
	 * 
	 * @return the user status
	 */
	@Basic
	@Column(name = "STATUS")
	public int getStatus() {
		return this.status;
	}

	/**
	 * Sets the user status, this field should contain information about all statuses. First bit used for enabled/disabled statuses, second bit for
	 * locked status and third for first time login status.
	 * 
	 * @param status the user status
	 */
	public void setStatus(final int status) {
		this.status = status;
	}

	/**
	 * Returns the username used to authenticate the user. Cannot return <code>null</code>.
	 * 
	 * @return the username (never <code>null</code>)
	 */
	@Transient
	public String getUsername() {
		return this.getUserName();
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH, unique = true)
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
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof CmUserImpl)) {
			return false;
		}
		
		CmUserImpl user = (CmUserImpl) other;
		return ObjectUtils.equals(userName, user.userName);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(userName);
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

	/**
	 * True if this <code>CmUser</code> has access to all stories. This method is used in conjunction with <code>getStores()</code> method.
	 * Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Store)</code> to check if a specific store is authorized for
	 * cmuser.
	 * 
	 * @return true if this cmUser has access to all stories.
	 */
	@Basic
	@Column(name = "All_STORE_ACCESS")
	public boolean isAllStoresAccess() {
		return this.allStoresAccess;
	}

	/**
	 * Sets whether this <code>CmUser</code> has access to all stories.
	 * 
	 * @param allStoresAccess Set to true if access to all stories.
	 */
	public void setAllStoresAccess(final boolean allStoresAccess) {
		this.allStoresAccess = allStoresAccess;
	}
	
	/**
	 * True if this <code>CmUser</code> has access to all price lists. 
	 * This method is used in conjunction with <code>getStores()</code> method.
	 * Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(PriceListDescriptor)</code>   
	 * and <code>com.elasticpath.cmclient.core.service.isAuthorized(PriceListDescriptorDTO)</code>
	 * to check if a specific store is authorized for cmuser.
	 * 
	 * @return true if this cmUser has access to all  price lists.
	 */
	@Basic
	@Column(name = "ALL_PRICELIST_ACCESS")
	public boolean isAllPriceListsAccess() {
		return this.allPriceListsAccess;
	}

	/**
	 * Sets whether this <code>CmUser</code> has access to all price lists.
	 * 
	 * @param allPriceListsAccess Set to true if access to all price listst.
	 */
	public void setAllPriceListsAccess(final boolean allPriceListsAccess) {
		this.allPriceListsAccess = allPriceListsAccess;
	}
	

	/**
	 * True if this <code>CmUser</code> has access to all warehouses. This method is used in conjunction with <code>getWarehouses()</code>
	 * method. Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Warehouse)</code> to check if a specific warehouse is
	 * authorized for cmuser.
	 * 
	 * @return true if this cmUser has access to all warehouses.
	 */
	@Basic
	@Column(name = "All_WAREHOUSE_ACCESS")
	public boolean isAllWarehousesAccess() {
		return this.allWarehousesAccess;
	}

	/**
	 * Sets whether this <code>CmUser</code> has access to all warehouses.
	 * 
	 * @param allWarehousesAccess Set to true if access to all warehouses.
	 */
	public void setAllWarehousesAccess(final boolean allWarehousesAccess) {
		this.allWarehousesAccess = allWarehousesAccess;
	}

	/**
	 * True if this <code>CmUser</code> has access to all categories. This method is used in conjunction with <code>getCatalogs()</code> method.
	 * Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Catalog)</code> to check if a specific catalog is authorized for
	 * cmuser.
	 * 
	 * @return true if this cmUser has access to all categories.
	 */
	@Basic
	@Column(name = "All_CATALOG_ACCESS")
	public boolean isAllCatalogsAccess() {
		return this.allCatalogsAccess;
	}

	/**
	 * Sets whether this <code>CmUser</code> has access to all catalogs.
	 * 
	 * @param allCatalogsAccess boolean.
	 */
	public void setAllCatalogsAccess(final boolean allCatalogsAccess) {
		this.allCatalogsAccess = allCatalogsAccess;
	}

	/**
	 * Return all COMPLETE stores to which the user has access. 
	 * This method is used in conjunction with <code>isAllStoresAccess()</code> method. 
	 * Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Store)</code> 
	 * to check if a specific store is authorized for cmuser. IMPORTANT: The returned objects are readonly!
	 * 
	 * @return set of stores to which the user has access.
	 */
	@ManyToMany(targetEntity = StoreImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "TCMUSERSTORE", joinColumns = { @JoinColumn(name = USER_UID) }, 
			inverseJoinColumns = @JoinColumn(name = "STORE_UID", nullable = false))
	@ElementForeignKey(deleteAction = ForeignKeyAction.CASCADE)
	public Set<Store> getStores() {
		return this.accessibleStores;
	}

	/**
	 * Sets accessible stores for the user.
	 * 
	 * @param accessibleStores the stores to which the user has access.
	 */
	protected void setStores(final Set<Store> accessibleStores) {
		this.accessibleStores = accessibleStores;
	}

	/**
	 * Return all warehouses to which the user has access. This method is used in conjunction with <code>isAllWarehousesAccess()</code> method.
	 * Please refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Warehouse)</code> to check if a specific warehouse is authorized
	 * for cmuser.
	 * IMPORTANT: The returned objects are readonly!
	 * 
	 * @return set of warehouses.
	 */

	@ManyToMany(targetEntity = WarehouseImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "TCMUSERWAREHOUSE", joinColumns = { @JoinColumn(name = USER_UID) }, 
			inverseJoinColumns = @JoinColumn(name = "WAREHOUSE_UID", nullable = false))
	@ElementForeignKey(deleteAction = ForeignKeyAction.CASCADE)
	public Set<Warehouse> getWarehouses() {
		return this.accessibleWarehouses;
	}

	/**
	 * Sets accessible warehouses for the user.
	 * 
	 * @param accessibleWarehouses Warehouses set.
	 */
	protected void setWarehouses(final Set<Warehouse> accessibleWarehouses) {
		this.accessibleWarehouses = accessibleWarehouses;
	}

	/**
	 * Return all catalogs to which the user has access. This method is used in conjunction with <code>isAllCatalogsAccess()</code> method. Please
	 * refer to <code>com.elasticpath.cmclient.core.service.isAuthorized(Catalog)</code> to check if a specific catalog is authorized for cmuser.
	 * IMPORTANT: The returned objects are readonly!
	 * 
	 * @return set of catalogs to which the user has access
	 */

	@ManyToMany(targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "TCMUSERCATALOG", joinColumns = { @JoinColumn(name = USER_UID) }, 
			inverseJoinColumns = @JoinColumn(name = "CATALOG_UID", nullable = false))
	@ElementForeignKey(deleteAction = ForeignKeyAction.CASCADE)
	public Set<Catalog> getCatalogs() {
		return this.accessibleCatalogs;
	}

	/**
	 * Sets accessible catalogs for the user.
	 * 
	 * @param accessibleCatalogs Catalogs to which the user has access
	 */
	protected void setCatalogs(final Set<Catalog> accessibleCatalogs) {
		this.accessibleCatalogs = accessibleCatalogs;
	}
	
	/**
	 * Get the collection of accessable price lists guids.
	 * @return collection of accessable price lists guids.
	 */
	@PersistentCollection (elementCascade = {CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name = "TCMUSERPRICELIST", joinColumns = { @JoinColumn(name = USER_UID) }, 
		inverseJoinColumns = @JoinColumn(name = "PRICELIST_GUID", nullable = false))
	public Collection<String> getPriceLists() {
		if (accessiblePriceLists == null) {
			accessiblePriceLists = new ArrayList<String>();
		}
		return accessiblePriceLists;
	}

	/**
	 * Set the collection of accessable price lists guids. 
	 * @param accessiblePriceLists Price lists to which the user has access. 
	 */
	protected void setPriceLists(final Collection<String> accessiblePriceLists) {
		this.accessiblePriceLists = accessiblePriceLists;
	}
	
	/**
	 * Adds an accessible price list to this user.
	 * 
	 * @param priceListGuid price list guid
	 */
	public void addPriceList(final String priceListGuid) {
		this.getPriceLists().add(priceListGuid);
	}
	
	/**
	 * Removes an accessible price list from this user.
	 * 
     * @param priceListGuid price list guid
	 */
	public void removePriceList(final String priceListGuid) {
		this.getPriceLists().remove(priceListGuid);
	}
	
	


	/**
	 * Removes an accessible store from this user.
	 * 
	 * @param store store.
	 */
	public void removeStore(final Store store) {
		this.getStores().remove(store);
	}

	/**
	 * Removes an accessible warehouse from this user.
	 * 
	 * @param warehouse warehouse.
	 */
	public void removeWarehouse(final Warehouse warehouse) {
		this.getWarehouses().remove(warehouse);
	}

	/**
	 * Removes an accessible catalog from this user.
	 * 
	 * @param catalog catalog
	 */
	public void removeCatalog(final Catalog catalog) {
		this.getCatalogs().remove(catalog);
	}

	/**
	 * Adds an accessible store to this user.
	 * 
	 * @param store store.
	 */
	public void addStore(final Store store) {
		this.getStores().add(store);
	}

	/**
	 * Adds an accessible warehouse to this user.
	 * 
	 * @param warehouse warehouse.
	 */
	public void addWarehouse(final Warehouse warehouse) {
		this.getWarehouses().add(warehouse);
	}

	/**
	 * Adds an accessible catalog to this user.
	 * 
	 * @param catalog catalog.
	 */
	public void addCatalog(final Catalog catalog) {
		this.getCatalogs().add(catalog);
	}

	@Override
	@Transient
	public String getUserPassword() {
		return getClearTextPassword();
	}
	
	/**
	 * Gets the user status.
	 * 
	 * @return user status
	 */
	@Transient
	public UserStatus getUserStatus() {
		if (!isEnabled()) {
			return UserStatus.DISABLED;
		}
		if (!isAccountNonLocked()) {
			return UserStatus.LOCKED;
		}
		return UserStatus.ENABLED;
	}

	@Transient
	public TimeService getTimeService() {
		return getBean(ContextIdNames.TIME_SERVICE);
	}
}
