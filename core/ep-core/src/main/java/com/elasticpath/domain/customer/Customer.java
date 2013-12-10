/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.userdetails.UserDetails;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.customer.impl.CustomerRoleMapper;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.validation.constraints.CustomerUsernameUserIdModeEmail;
import com.elasticpath.validation.constraints.EpEmail;
import com.elasticpath.validation.constraints.NotBlank;
import com.elasticpath.validation.constraints.RegisteredCustomerPasswordNotBlankWithSize;
import com.elasticpath.validation.groups.PasswordChange;

/**
 * A <code>Customer</code> is someone with an account in the system for making orders through the Store Front.
 */
@RegisteredCustomerPasswordNotBlankWithSize(
		min = Customer.MINIMUM_PASSWORD_LENGTH, max = GlobalConstants.SHORT_TEXT_MAX_LENGTH, groups = PasswordChange.class)
public interface Customer extends Entity, UserDetails, DatabaseLastModifiedDate {

	/** The minimum length of a password. */
	int MINIMUM_PASSWORD_LENGTH = 8;

	/** The gender - female. */
	char GENDER_FEMALE = 'F';

	/** The gender - male. */
	char GENDER_MALE = 'M';

	/** The gender - not-selected. */
	char GENDER_NOT_SELECTED = '-';

	/** The status - Active. */
	int STATUS_ACTIVE = 1;

	/** The status - Disabled. */
	int STATUS_DISABLED = 2;

	/** The status - Pending Approval. */
	int STATUS_PENDING_APPROVAL = 3;

	/** User Id suffix length. */
	int SUFFIX_LENGTH = 4;

	/**
	 * Gets the user identifier for this <code>Customer</code>.
	 *
	 * @return the user identifier.
	 */
	String getUserId();

	/**
	 * Sets the user identifier for this <code>Customer</code>.
	 *
	 * @param userId the new user identifier.
	 */
	void setUserId(String userId);

	/**
	 * Gets the email address of this <code>Customer</code>.
	 *
	 * @return the email address.
	 */
	@EpEmail
	String getEmail();

	/**
	 * Sets the email address of this <code>Customer</code>.
	 *
	 * @param email the new email address.
	 */
	void setEmail(String email);

	/**
	 * Gets the <code>Customer</code>'s full name.
	 *
	 * @return the full name.
	 */
	String getFullName();

	/**
	 * Gets the <code>Customer</code>'s first name.
	 *
	 * @return the first name.
	 */
	String getFirstName();

	/**
	 * Sets the <code>Customer</code>'s first name.
	 *
	 * @param firstName the new first name.
	 */
	void setFirstName(String firstName);

	/**
	 * Gets the <code>Customer</code>'s last name.
	 *
	 * @return the last name.
	 */
	String getLastName();

	/**
	 * Sets the <code>Customer</code>'s last name.
	 *
	 * @param lastName the new last name.
	 */
	void setLastName(String lastName);

	/**
	 * Get the username.
	 *
	 * @return the username
	 */
	@Override
	@NotNull
	@NotBlank
	@Size(max = GlobalConstants.SHORT_TEXT_MAX_LENGTH)
	@CustomerUsernameUserIdModeEmail
	String getUsername();

	/**
	 * Gets the encrypted password.
	 *
	 * @return the encrypted password.
	 */
	@Override
	String getPassword();

	/**
	 * Sets the encrypted password. <br>
	 * By default, the clear-text user input password will be encrypted using the SHA1 secure hash algorithm
	 *
	 * @param password the encrypted password.
	 */
	void setPassword(String password);

	/**
	 * Sets the clear-text password. <br>
	 * The password will be encrypted using a secure hash like MD5 or SHA1 and saved as password.
	 *
	 * @param clearTextPassword the clear-text password.
	 */
	void setClearTextPassword(String clearTextPassword);

	/**
	 * Gets the clear-text password (only available at creation time).
	 *
	 * @return the clear-text password.
	 */
	String getClearTextPassword();

	/**
	 * Reset the customer's password.
	 *
	 * @return the reseted password
	 */
	String resetPassword();

	/**
	 * Gets the <code>CustomerAddress</code>es associated with this <code>Customer</code>.
	 *
	 * @return the list of addresses.
	 */
	@Valid
	List<CustomerAddress> getAddresses();

	/**
	 * Sets the <code>CustomerAddress</code>es associated with this <code>Customer</code>.
	 *
	 * @param addresses the new list of addresses.
	 */
	void setAddresses(List<CustomerAddress> addresses);

	/**
	 * Adds an <code>CustomerAddress</code> to the list of addresses.
	 *
	 * @param address the address to add.
	 */
	void addAddress(CustomerAddress address);

	/**
	 * Removes an <code>CustomerAddress</code> from the list of addresses.
	 *
	 * @param address the address to remove.
	 */
	void removeAddress(CustomerAddress address);

	/**
	 * Set the preferred billing address.
	 *
	 * @param address the <code>CustomerAddress</code>
	 */
	void setPreferredBillingAddress(CustomerAddress address);

	/**
	 * Get the preferred billing address .This {@link Address} will be added to the list of known addresses for the
	 * customer, i.e. it will be added to {@link #getAddresses()}.
	 *
	 * @return the preferred shipping address
	 */
	CustomerAddress getPreferredBillingAddress();

	/**
	 * Set the preferred shipping address. This {@link Address} will be added to the list of known addresses for the
	 * customer, i.e. it will be added to {@link #getAddresses()}.
	 *
	 * @param address the <code>CustomerAddress</code>
	 */
	void setPreferredShippingAddress(CustomerAddress address);

	/**
	 * Get the preferred shipping address.
	 *
	 * @return the preferred shipping address
	 */
	CustomerAddress getPreferredShippingAddress();

	/**
	 * Get the preferred locale of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Locale</code>
	 */
	Locale getPreferredLocale();

	/**
	 * Set the preferred locale of the customer corresponding to the shopping cart.
	 *
	 * @param preferredLocale the <code>Locale</code>
	 */
	void setPreferredLocale(Locale preferredLocale);

	/**
	 * Get the preferred currency of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getPreferredCurrency();

	/**
	 * Set the preferred currency of the customer corresponding to the shopping cart.
	 *
	 * @param preferredCurrency the <code>Currency</code>
	 */
	void setPreferredCurrency(Currency preferredCurrency);

	/**
	 * Gets the flag indicating whether this customer is anonymous or not. <br>
	 * Anonymous customers do not have a password and their email address does not need to be unique.
	 *
	 * @return true if the customer is anonymous; otherwise false.
	 */
	boolean isAnonymous();

	/**
	 * Gets the flag indicating whether this customer is a 'real' registered customer or not. <br>
	 * Registered customers are not anonymous and have a persisted record in the database.
	 *
	 * @return true if the customer is registered; otherwise false.
	 */
	boolean isRegistered();

	/**
	 * Sets the anonymous status of the customer. <br>
	 * Anonymous customers do not have a password and their email address does not need to be unique.
	 *
	 * @param anonymous - true if the customer is anonymous; otherwise false.
	 */
	void setAnonymous(boolean anonymous);

	/**
	 * Gets the customer creation date.
	 *
	 * @return customer creation date.
	 */
	Date getCreationDate();

	/**
	 * Sets the customer creation date.
	 *
	 * @param creationDate customer creation date.
	 */
	void setCreationDate(Date creationDate);

	/**
	 * Gets the customer last edit date.
	 *
	 * @return customer last edit date.
	 */
	Date getLastEditDate();

	/**
	 * Sets the customer last edit date.
	 *
	 * @param lastEditDate customer last edit date.
	 */
	void setLastEditDate(Date lastEditDate);

	/**
	 * Gets the customer's date of birth.
	 *
	 * @return customer's date of birth.
	 */
	Date getDateOfBirth();

	/**
	 * Sets the customer's date of birth.
	 *
	 * @param dateOfBirth customer's date of birth.
	 */
	void setDateOfBirth(Date dateOfBirth);

	/**
	 * Get the <code>CustomerGroup</code>s associated with this customer.
	 *
	 * @return list of customerGroups.
	 */
	List<CustomerGroup> getCustomerGroups();

	/**
	 * Sets the <code>CustomerGroup</code>s associated with this <code>Customer</code>.
	 *
	 * @param customerGroups - the list of customerGroups that the current user is in.
	 */
	void setCustomerGroups(List<CustomerGroup> customerGroups);

	/**
	 * Return a boolean that indicates whether the customer belongs to customerGroup with the given customerGroupID.
	 *
	 * @param customerGroupID - customerGroup ID.
	 * @return true if the customer belongs to a customerGroup with the given customerGroupID; otherwise, false.
	 */
	boolean belongsToCustomerGroup(long customerGroupID);

	/**
	 * Return a boolean that indicates whether the customer belongs to customerGroup with the given name.
	 *
	 * @param groupName - customerGroup name.
	 * @return true if the customer belongs to a customerGroup with the given customerGroup name; otherwise, false.
	 */
	boolean belongsToCustomerGroup(String groupName);

	/**
	 * Adds a <code>CustomerGroup</code> to the list of customertGroups.
	 *
	 * @param customerGroup the customerGroup to add.
	 */
	void addCustomerGroup(CustomerGroup customerGroup);

	/**
	 * Removes an <code>CustomerGroup</code> from the list of customerGroups.
	 *
	 * @param customerGroup the customerGroup to remove.
	 */
	void removeCustomerGroup(CustomerGroup customerGroup);

	/**
	 * Gets the phone number associated with this <code>Customer</code>.
	 *
	 * @return the phone number.
	 */
	String getPhoneNumber();

	/**
	 * Sets the phone number associated with this <code>Customer</code>.
	 *
	 * @param phoneNumber the new phone number.
	 */
	void setPhoneNumber(String phoneNumber);

	/**
	 * Gets the gender of this <code>Customer</code>.
	 *
	 * @return the customer's gender ('F' for female, or 'M' for male).
	 */
	char getGender();

	/**
	 * Sets the gender of this <code>Customer</code>.
	 *
	 * @param gender the customer's gender ('F' for female, or 'M' for male).
	 */
	void setGender(char gender);

	/**
	 * Gets the company associated with this <code>Customer</code>.
	 *
	 * @return the company.
	 */
	String getCompany();

	/**
	 * Sets the company associated with this <code>Customer</code>.
	 *
	 * @param company the new company.
	 */
	void setCompany(String company);

	/**
	 * Gets the status of this <code>Customer</code>.
	 *
	 * @return the status.
	 */
	int getStatus();

	/**
	 * Sets the status of this <code>Customer</code>.
	 *
	 * @param status the custome's status.
	 */
	void setStatus(int status);

	/**
	 * Returns the customer's address with the matching UID. <br>
	 * If no matching address is found then null is returned.
	 *
	 * @param addressUid the uidPk of the address to be retrieved
	 * @return a <code>CustomerAddress</code> or <code>null</code> if no matching address is found
	 */
	CustomerAddress getAddressByUid(long addressUid);

	/**
	 * Returns the customer's address with the matching GUID. <br>
	 * If no matching address is found then null is returned.
	 *
	 * @param addressGuid the guid of the address to be retrieved
	 * @return a <code>CustomerAddress</code> or <code>null</code> if no matching address is found
	 */
	CustomerAddress getAddressByGuid(String addressGuid);

	/**
	 * Returns the customer's credit card with the matching UID. <br>
	 * If no matching credit card is found then null is returned.
	 *
	 * @param creditCardUid the uidPk of the credit card to be retrieved
	 * @return a <code>CustomerCreditCard</code> or <code>null</code> if no matching credit card is found
	 */
	CustomerCreditCard getCreditCardByUid(long creditCardUid);

	/**
	 * Gets the credit card by GUID. <br>
	 * If no matching credit card is found then null is returned.
	 *
	 * @param creditCardGuid the credit card guid
	 * @return a <code>CustomerCreditCard</code> or <code>null</code> if no matching credit card is found
	 */
	CustomerCreditCard getCreditCardByGuid(String creditCardGuid);

	/**
	 * Gets the default credit card.
	 * @return the default credit card, null if no default card is present.
	 */
	CustomerCreditCard getPreferredCreditCard();
	
	/**
	 * @return the customer's credit cards
	 */
	List<CustomerCreditCard> getCreditCards();

	/**
	 * @param creditCards the credit cards to set
	 */
	void setCreditCards(List<CustomerCreditCard> creditCards);

	/**
	 * Add a credit card to this customer.
	 *
	 * @param creditCard the credit card to add
	 */
	void addCreditCard(CustomerCreditCard creditCard);

	/**
	 * Removes a <code>CustomerCreditCard</code> from the list of credit cards.
	 *
	 * @param creditCard the credit card to remove.
	 */
	void removeCreditCard(CustomerCreditCard creditCard);

	/**
	 * Notifies a <code>Customer</code> that a credit card has been updated (The credit card default flags will be set accordingly).
	 *
	 * @param creditCard the credit card that was updated
	 */
	void updateCreditCard(CustomerCreditCard creditCard);

	/**
	 * Sets the customer's default (preferred) credit card.
	 *
	 * @param preferredCreditCard the credit card to be used by default
	 */
	void setPreferredCreditCard(CustomerCreditCard preferredCreditCard);

	/**
	 * Get the customer profile.
	 *
	 * @return the domain model's <code>CustomerProfile</code>
	 */
	@Valid
	CustomerProfile getCustomerProfile();

	/**
	 * Set the customer profile.
	 *
	 * @param customerProfile the <code>CustomerProfile</code>
	 */
	void setCustomerProfile(CustomerProfile customerProfile);

	/**
	 * Get the customer authentication.
	 *
	 * @return the domain model's <code>CustomerAuthentication</code>
	 */
	CustomerAuthentication getCustomerAuthentication();

	/**
	 * Set the customer authentication.
	 *
	 * @param customerAuthentication the <code>CustomerAuthentication</code>
	 */
	void setCustomerAuthentication(CustomerAuthentication customerAuthentication);

	/**
	 * Indicates whether the user wishes to be notified of news.
	 *
	 * @return true if need to be notified, false otherwise
	 */
	boolean isToBeNotified();

	/**
	 * Set whether the user wishes to be notified of news.
	 *
	 * @param toBeNotified set to true to indicate that need to be notified of news
	 */
	void setToBeNotified(boolean toBeNotified);

	/**
	 * Indicates whether the user wishes to receive emails in HTML format.
	 *
	 * @return true if HTML email format is preferred
	 */
	boolean isHtmlEmailPreferred();

	/**
	 * Set whether the user wishes to receive emails in HTML format.
	 *
	 * @param isHtmlEmail set to true to indicate HTML email preference
	 */
	void setHtmlEmailPreferred(boolean isHtmlEmail);

	/**
	 * Get the profile value map.
	 *
	 * @return the map
	 */
	Map<String, AttributeValue> getProfileValueMap();

	/**
	 * Set the profile value map.
	 *
	 * @param profileValueMap the map
	 */
	void setProfileValueMap(Map<String, AttributeValue> profileValueMap);

	/**
	 * @return store code for which the customer is associated
	 */
	String getStoreCode();

	/**
	 * Sets the code for which the customer is associated.
	 *
	 * @param code store to associate with
	 */
	void setStoreCode(String code);

	/**
	 * Gets the fax number associated with this <code>Customer</code>.
	 *
	 * @return the fax number.
	 */
	String getFaxNumber();

	/**
	 * Sets the fax number associated with this <code>Customer</code>.
	 *
	 * @param faxNumber the new fax number.
	 */
	void setFaxNumber(String faxNumber);

	/**
	 * Gets the <code>Customer</code>'s email address required-ness flag.
	 *
	 * @return true if email address is required, false if not required.
	 */
	boolean isEmailRequired();

	/**
	 * Gets the <code>Customer</code>'s preferred locale required-ness flag.
	 *
	 * @return true if preferred locale is required, false if not required.
	 */
	boolean isPreferredLocaleRequired();

	/**
	 * Gets the <code>Customer</code>'s preferred currency required-ness flag.
	 *
	 * @return true if preferred currency is required, false if not required.
	 */
	boolean isPreferredCurrencyRequired();

	/**
	 * Gets the <code>Customer</code>'s first name required-ness flag.
	 *
	 * @return true if first name is required, false if not required.
	 */
	boolean isFirstNameRequired();

	/**
	 * Gets the <code>Customer</code>'s last name required-ness flag.
	 *
	 * @return true if last name is required, false if not required.
	 */
	boolean isLastNameRequired();

	/**
	 * Gets the <code>Customer</code>'s phone number required-ness flag.
	 *
	 * @return true if phone number is required, false if not required.
	 */
	boolean isPhoneNumberRequired();

	/**
	 * Gets the <code>Customer</code>'s gender required-ness flag.
	 *
	 * @return true if gender is required, false if not required.
	 */
	boolean isGenderRequired();

	/**
	 * Gets the <code>Customer</code>'s company required-ness flag.
	 *
	 * @return true if company is required, false if not required.
	 */
	boolean isCompanyRequired();

	/**
	 * Gets the <code>Customer</code>'s fax number required-ness flag.
	 *
	 * @return true if fax number is required, false if not required.
	 */
	boolean isFaxNumberRequired();

	/**
	 * Sets user ID as user's email.
	 */
	void setUserIdAsEmail();

	/**
	 * Sets user ID based on the user ID mode.
	 *
	 * @param userId the user ID
	 */
	void setUserIdBasedOnUserIdMode(String userId);

	/**
	 * Gets a {@link CustomerRoleMapper} associated with this Customer. 
	 * @return the associated {@link CustomerRoleMapper}
	 */
	CustomerRoleMapper getCustomerRoleMapper();

	/**
	 * Gets this customer's payment tokens.
	 * @return this customer's payment tokens
	 */
	CustomerPaymentMethods getPaymentMethods();
}
