package com.elasticpath.service.search.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * A criteria for advanced customer search.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CustomerSearchCriteria extends AbstractSearchCriteriaImpl {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String firstName;

	private String lastName;

	private String customerNumber;

	private String email;

	private String phoneNumber;

	private String userId;

	private Date fromDate;

	private String zipOrPostalCode;

	private Collection<String> storeCodes;

	private boolean userIdAndEmailMutualSearch;
	
	private String guid;
	
	/**
	 * Returns the first name.
	 * 
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName the first name
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Returns the last name.
	 * 
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName the last name
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns the customer number.
	 * 
	 * @return the customer number
	 */
	public String getCustomerNumber() {
		return customerNumber;
	}

	/**
	 * Sets the customer number.
	 * 
	 * @param customerNumber the customer number
	 */
	public void setCustomerNumber(final String customerNumber) {
		this.customerNumber = customerNumber;
	}

	/**
	 * Returns the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 * 
	 * @param email the email
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Sets the phone number.
	 * 
	 * @param phoneNumber the phone number
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Returns the phone number.
	 * 
	 * @return the phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Returns the user Id.
	 * 
	 * @return the user Id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user Id.
	 * 
	 * @param userId the user Id
	 */
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	/**
	 * Returns the from date.
	 * 
	 * @return the from date.
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 * 
	 * @param fromDate the create time.
	 */
	public void setFromDate(final Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Sets zip or postal code depending on the applicability.
	 * 
	 * @return String representing the zip or postal code
	 */
	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	/**
	 * Gets zip or postal code depending on the applicability.
	 * 
	 * @param zipOrPostalCode zip or postal code
	 */
	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	/**
	 * Sets store code depending on the applicability.
	 * 
	 * @return String representing the store code
	 */
	public Collection<String> getStoreCodes() {
		return storeCodes;
	}

	/**
	 * Gets store code depending on the applicability.
	 * 
	 * @param storeCodes the store code
	 */
	public void setStoreCodes(final Collection<String> storeCodes) {
		this.storeCodes = storeCodes;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void optimize() {
		if (!isStringValid(firstName)) {
			firstName = null;
		}
		if (!isStringValid(lastName)) {
			lastName = null;
		}
		if (!isStringValid(customerNumber)) {
			customerNumber = null;
		}
		if (!isStringValid(email)) {
			email = null;
		}
		if (!isStringValid(phoneNumber)) {
			phoneNumber = null;
		}
		if (!isStringValid(userId)) {
			userId = null;
		}
		if (!isStringValid(zipOrPostalCode)) {
			zipOrPostalCode = null;
		}
		if (CollectionUtils.isNotEmpty(storeCodes)) {
			Set<String> tempStoreCodes = new HashSet<String>();
			for (String storeCode : storeCodes) {
				if (isStringValid(storeCode)) {
					tempStoreCodes.add(storeCode);
				}
			}
			storeCodes = tempStoreCodes;
		} else {
			storeCodes = null;
		}
		if (!isStringValid(guid)) {
			guid = null;
		}
	}
	
	@Override
	@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
	public SearchCriteria clone() throws CloneNotSupportedException {
		CustomerSearchCriteria searchCriteria = (CustomerSearchCriteria) super.clone();
		
		if (this.storeCodes != null) {
			List<String> storeCodes = new ArrayList<String>();
			storeCodes.addAll(this.storeCodes);
			searchCriteria.setStoreCodes(storeCodes);
		}
		if (fromDate != null) {
			searchCriteria.setFromDate(new Date(fromDate.getTime()));
		}
		
		return searchCriteria;
	}
	
	/**
	 * Returns the index type this criteria deals with.
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return IndexType.CUSTOMER;
	}

	/**
	 * Gets the boolean value for enabling/disabling mutual term search on both userId and email fields.
	 * 
	 * @return true if this mode is enabled
	 */
	public boolean isUserIdAndEmailMutualSearch() {
		return userIdAndEmailMutualSearch;
	}
	
	/**
	 * Sets the boolean value for enabling/disabling mutual term search on both userId and email fields.
	 * 
	 * @param mutualSearch specifies whether the mutual search for email and user id should be enabled
	 */
	public void setUserIdAndEmailMutualSearch(final boolean mutualSearch) {
		this.userIdAndEmailMutualSearch = mutualSearch;
	}
	
	/**
	 * Clears this <code>CustomerSearchCriteria</code> and resets all criteria to their default values.
	 */
	public void clear() {
		this.email = null;
		this.customerNumber = null;
		this.userId = null;
		this.firstName = null;
		this.lastName = null;
		this.zipOrPostalCode = null;
		this.phoneNumber = null;
		this.storeCodes = null;
		this.guid = null;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid the new guid
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the guid.
	 *
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

}
