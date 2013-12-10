package com.elasticpath.common.dto.cmuser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link CmUser}.
 */
@XmlRootElement(name = CmUserDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
@SuppressWarnings("PMD.TooManyFields")
public class CmUserDTO implements Dto {

	private static final long serialVersionUID = 7998343147880699250L;

	/**
	 * Root element name for {@link CmUserDTO}.
	 */
	public static final String ROOT_ELEMENT = "cmuser";

	@XmlAttribute(name = "guid", required = true)
	private String guid;
	
	@XmlElement(name = "user_name", required = true)
	private String userName;

	@XmlElement(name = "email", required = true)
	private String email;

	@XmlElement(name = "first_name", required = false)
	private String firstName;
	
	@XmlElement(name = "last_name", required = false)
	private String lastName;
	
	@XmlElement(name = "password", required = true)
	private String password;
	
	@XmlElement(name = "creation_date", required = true)
	private Date creationDate;

	@XmlElement(name = "last_login", required = false)
	private Date lastLoginDate;
	
	@XmlElement(name = "last_password_change", required = false)
	private Date lastChangedPasswordDate;

	@XmlElement(name = "last_modified", required = true)
	private Date lastModifiedDate;
	
	@XmlElement(name = "total_failed_login_attempts", required = true)
	private int failedLoginAttempts;
	
	@XmlElement(name = "enabled", required = true)
	private boolean enabled;

	@XmlElement(name = "has_temporary_password", required = true)
	private boolean isTemporaryPassword;
	
	@XmlElementWrapper(name = "user_password_history_items")
	@XmlElement(name = "user_password_history_item")	
	private final List<UserPasswordHistoryItemDTO> userPasswordHistoryItems = new ArrayList<UserPasswordHistoryItemDTO>();

	@XmlElement(name = "has_access_to_all_warehouses", required = true)
	private boolean allWarehousesAccess;

	@XmlElement(name = "has_access_to_all_catalogs", required = true)
	private boolean allCatalogsAccess;
	
	@XmlElement(name = "has_access_to_all_stores", required = true)
	private boolean allStoresAccess;
	
	@XmlElement(name = "has_access_to_all_price_lists", required = true)
	private boolean allPriceListsAccess;

	@XmlElementWrapper(name = "user_role_guids")
	@XmlElement(name = "user_role_guids")
	private Collection<String> userRoleGuids = new ArrayList<String>();	
	
	@XmlElementWrapper(name = "price_list_guids")
	@XmlElement(name = "price_list_guid")
	private Collection<String> accessiblePriceListGuids = new ArrayList<String>();	

	@XmlElementWrapper(name = "catalog_codes")
	@XmlElement(name = "catalog_code")
	private Set<String> accessibleCatalogCodes = new HashSet<String>();	
	
	@XmlElementWrapper(name = "store_codes")
	@XmlElement(name = "store_code")
	private Set<String> accessibleStoreCodes = new HashSet<String>();	

	@XmlElementWrapper(name = "warehouse_codes")
	@XmlElement(name = "warehouse_code")
	private Set<String> accessibleWarehouseCodes = new HashSet<String>();

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param lastLoginDate the lastLoginDate to set
	 */
	public void setLastLoginDate(final Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	/**
	 * @return the lastLoginDate
	 */
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	/**
	 * @param lastChangedPasswordDate the lastChangedPasswordDate to set
	 */
	public void setLastChangedPasswordDate(final Date lastChangedPasswordDate) {
		this.lastChangedPasswordDate = lastChangedPasswordDate;
	}

	/**
	 * @return the lastChangedPasswordDate
	 */
	public Date getLastChangedPasswordDate() {
		return lastChangedPasswordDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param failedLoginAttempts the failedLoginAttempts to set
	 */
	public void setFailedLoginAttempts(final int failedLoginAttempts) {
		this.failedLoginAttempts = failedLoginAttempts;
	}

	/**
	 * @return the failedLoginAttempts
	 */
	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	/**
	 * @return the isEnabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the isTemporaryPassword
	 */
	public boolean hasTemporaryPassword() {
		return isTemporaryPassword;
	}

	/**
	 * @param isTemporaryPassword the isTemporaryPassword to set
	 */
	public void setHasTemporaryPassword(final boolean isTemporaryPassword) {
		this.isTemporaryPassword = isTemporaryPassword;
	}

	/**
	 * @return the userPasswordHistoryItems
	 */
	public List<UserPasswordHistoryItemDTO> getUserPasswordHistoryItems() {
		return userPasswordHistoryItems;
	}

	/**
	 * @param allWarehousesAccess the allWarehousesAccess to set
	 */
	public void setAllWarehousesAccess(final boolean allWarehousesAccess) {
		this.allWarehousesAccess = allWarehousesAccess;
	}

	/**
	 * @return the allWarehousesAccess
	 */
	public boolean isAllWarehousesAccess() {
		return allWarehousesAccess;
	}

	/**
	 * @param allCatalogsAccess the allCatalogsAccess to set
	 */
	public void setAllCatalogsAccess(final boolean allCatalogsAccess) {
		this.allCatalogsAccess = allCatalogsAccess;
	}

	/**
	 * @return the allCatalogsAccess
	 */
	public boolean isAllCatalogsAccess() {
		return allCatalogsAccess;
	}

	/**
	 * @param allStoresAccess the allStoresAccess to set
	 */
	public void setAllStoresAccess(final boolean allStoresAccess) {
		this.allStoresAccess = allStoresAccess;
	}

	/**
	 * @return the allStoresAccess
	 */
	public boolean isAllStoresAccess() {
		return allStoresAccess;
	}

	/**
	 * @param allPriceListsAccess the allPriceListsAccess to set
	 */
	public void setAllPriceListsAccess(final boolean allPriceListsAccess) {
		this.allPriceListsAccess = allPriceListsAccess;
	}

	/**
	 * @return the allPriceListsAccess
	 */
	public boolean isAllPriceListsAccess() {
		return allPriceListsAccess;
	}

	/**
	 * @param userRoleGuids the userRoleGuids to set
	 */
	public void setUserRoleGuids(final Collection<String> userRoleGuids) {
		this.userRoleGuids = userRoleGuids;
	}

	/**
	 * @return the userRoleGuids
	 */
	public Collection<String> getUserRoleGuids() {
		return userRoleGuids;
	}

	/**
	 * @param accessiblePriceListGuids the accessiblePriceListGuids to set
	 */
	public void setAccessiblePriceListGuids(final Collection<String> accessiblePriceListGuids) {
		this.accessiblePriceListGuids = accessiblePriceListGuids;
	}

	/**
	 * @return the accessiblePriceListGuids
	 */
	public Collection<String> getAccessiblePriceListGuids() {
		return accessiblePriceListGuids;
	}

	/**
	 * @param accessibleCatalogCodes the accessibleCatalogCodes to set
	 */
	public void setAccessibleCatalogCodes(final Set<String> accessibleCatalogCodes) {
		this.accessibleCatalogCodes = accessibleCatalogCodes;
	}

	/**
	 * @return the accessibleCatalogCodes
	 */
	public Set<String> getAccessibleCatalogCodes() {
		return accessibleCatalogCodes;
	}

	/**
	 * @param accessibleStoreCodes the accessibleStoreCodes to set
	 */
	public void setAccessibleStoreCodes(final Set<String> accessibleStoreCodes) {
		this.accessibleStoreCodes = accessibleStoreCodes;
	}

	/**
	 * @return the accessibleStoreCodes
	 */
	public Set<String> getAccessibleStoreCodes() {
		return accessibleStoreCodes;
	}

	/**
	 * @param accessibleWarehouseCodes the accessibleWarehouseCodes to set
	 */
	public void setAccessibleWarehouseCodes(final Set<String> accessibleWarehouseCodes) {
		this.accessibleWarehouseCodes = accessibleWarehouseCodes;
	}

	/**
	 * @return the accessibleWarehouseCodes
	 */
	public Set<String> getAccessibleWarehouseCodes() {
		return accessibleWarehouseCodes;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}	

}
