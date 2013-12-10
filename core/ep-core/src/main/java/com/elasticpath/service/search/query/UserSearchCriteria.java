package com.elasticpath.service.search.query;

import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * Represents criteria for users search.
 */
public class UserSearchCriteria extends AbstractSearchCriteriaImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String userRoleName;

	private String userName;

	private String lastName;

	private String firstName;

	private String email;

	private String catalogCode;

	private String storeCode;

	private UserStatus userStatus;

	/**
	 * @return the userStatus
	 */
	public UserStatus getUserStatus() {
		return userStatus;
	}

	/**
	 * @param userStatus the userStatus to set
	 */
	public void setUserStatus(final UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
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
	 * @return the userRole name
	 */
	public String getUserRoleName() {
		return userRoleName;
	}

	/**
	 * @param userRoleName the userRole name to set
	 */
	public void setUserRoleName(final String userRoleName) {
		this.userRoleName = userRoleName;
	}

	/**
	 * @return the catalogCode
	 */
	public String getCatalogCode() {
		return catalogCode;
	}

	/**
	 * @param catalogCode the catalogCode to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 * @return the storeCode
	 */
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * @param storeCode the storeCode to set
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (!isStringValid(userName)) {
			userName = null;
		}
		if (!isStringValid(lastName)) {
			lastName = null;
		}
		if (!isStringValid(firstName)) {
			firstName = null;
		}
		if (!isStringValid(email)) {
			email = null;
		}
		if (!isStringValid(catalogCode)) {
			catalogCode = null;
		}

		if (!isStringValid(storeCode)) {
			storeCode = null;
		}
	}

	/**
	 * Returns <code>true</code> if this <code>UserSearchCriteria</code> has no search criteria, <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if this <code>UserSearchCriteria</code> has no search criteria, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		boolean empty = true;
		empty &= userRoleName == null;
		empty &= userStatus == null;
		empty &= !isStringValid(userName);
		empty &= !isStringValid(lastName);
		empty &= !isStringValid(firstName);
		empty &= !isStringValid(email);
		empty &= !isStringValid(catalogCode);
		empty &= !isStringValid(storeCode);
		return empty;
	}

	/**
	 * Clears this <code>UserSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		userName = null;

		lastName = null;

		firstName = null;

		email = null;

		userStatus = null;

		userRoleName = null;

		catalogCode = null;

		storeCode = null;
	}

	/**
	 * Returns the index type this criteria deals with.
	 * 
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return IndexType.CMUSER;
	}

}