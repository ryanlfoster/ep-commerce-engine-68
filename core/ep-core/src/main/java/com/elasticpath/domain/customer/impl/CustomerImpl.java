/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Dependent;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerAuthentication;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.customer.CustomerService;

/**
 * The default implementation of <code>Customer</code>.
 */
@SuppressWarnings({
		"PMD.UselessOverridingMethod", "PMD.ExcessiveClassLength", "PMD.TooManyMethods", "PMD.ExcessivePublicCount", "PMD.ExcessiveImports" })
@Entity
@Table(name = CustomerImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.CUSTOMER,
			attributes = { @FetchAttribute(name = "profileValueMap"),
						   @FetchAttribute(name = "userId") }),
   @FetchGroup(name = FetchGroupConstants.ORDER_SEARCH,
			attributes = { @FetchAttribute(name = "userId") })
})
@DataCache(enabled = false)
public class CustomerImpl extends AbstractLegacyEntityImpl implements Customer, Serializable, PersistenceInterceptor {

	private static final Logger LOG = Logger.getLogger(CustomerImpl.class);

	/**
	 * Allocation size for JPA_GENERATED_KEYS id.
	 */
	private static final int ALLOCATION_SIZE = 1000;

	private static final int CURRENCY_LENGTH_ISO4217 = 3;

	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMER";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_FIRST_NAME = "CP_FIRST_NAME";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_LAST_NAME = "CP_LAST_NAME";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_EMAIL = "CP_EMAIL";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_PREF_LOCALE = "CP_PREF_LOCALE";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_PREF_CURR = "CP_PREF_CURR";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_ANONYMOUS_CUST = "CP_ANONYMOUS_CUST";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_PHONE = "CP_PHONE";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_FAX = "CP_FAX";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_GENDER = "CP_GENDER";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_COMPANY = "CP_COMPANY";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_DOB = "CP_DOB";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_HTML_EMAIL = "CP_HTML_EMAIL";

	/**
	 * System attribute key.
	 */
	public static final String ATT_KEY_CP_BE_NOTIFIED = "CP_BE_NOTIFIED";

	private static final String CUSTOMER_UID = "CUSTOMER_UID";

	private List<CustomerAddress> addresses = new ArrayList<CustomerAddress>();

	private String userId;

	private Date creationDate;

	private Date lastEditDate;

	private List<CustomerGroup> customerGroups = new ArrayList<CustomerGroup>();

	private CustomerAddress preferredShippingAddress;

	private CustomerAddress preferredBillingAddress;

	private int status = Customer.STATUS_ACTIVE;

	private CustomerProfile customerProfile;

	private CustomerAuthentication customerAuthentication;

	private String storeCode;

	private long uidPk;

	private String guid;

	private Collection<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

	private PaymentMethod defaultPaymentMethod;

	@Override
	public void initialize() {
		super.initialize();
		if (getCreationDate() == null) {
			setCreationDate(new Date());
		}
		if (getLastEditDate() == null) {
			setLastEditDate(new Date());
		}
		if (getCustomerAuthenticationInternal() == null) {
			initCustomerAuthentication();
		}
	}

	@Override
	@Basic(optional = false)
	@Column(name = "USER_ID", nullable = false)
	public String getUserId() {
		return userId;
	}

	@Override
	public void setUserId(final String userId) {
		this.userId = userId;
	}

	@Override
	@Transient
	public String getEmail() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_EMAIL);
	}

	@Override
	@Transient
	public boolean isEmailRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_EMAIL);
	}

	/**
	 * {@inheritDoc} <br>
	 * Sets the userId as the email if userId is null.
	 */
	@Override
	public void setEmail(final String email) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_EMAIL, email);
		if (StringUtils.isBlank(getUserId())) {
			setUserIdBasedOnUserIdMode(email);
		}
	}

	@Override
	public void setUserIdAsEmail() {
		setUserIdBasedOnUserIdMode(getEmail());
	}

	@Override
	@SuppressWarnings("deprecation")
	public void setUserIdBasedOnUserIdMode(final String userId) {
		if (StringUtils.isBlank(userId)) {
			LOG.debug("Blank user ID.");
		}
		// Generate the userId according to user Id mode
		final int userIdMode = getUserIdMode();
		if (userIdMode == WebConstants.USE_EMAIL_AS_USER_ID_MODE) {
			setUserId(userId);
		} else if (userIdMode == WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE) {
			setUserId(userId + getUtility().getRandomStringWithLength(SUFFIX_LENGTH));
		}
	}

	/**
	 * Gets user ID mode.
	 *
	 * @return user ID mode.
	 */
	@Transient
	protected int getUserIdMode() {
		return getCustomerService().getUserIdMode();
	}

	@Override
	@OneToMany(targetEntity = CustomerAddressImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = CUSTOMER_UID, nullable = false)
	@ElementForeignKey(name = "taddress_ibfk_1")
	@ElementDependent
	public List<CustomerAddress> getAddresses() {
		return addresses;
	}

	@Override
	public void setAddresses(final List<CustomerAddress> addresses) throws EpDomainException {
		this.addresses = addresses;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EpDomainException if the given <code>CustomerAddress</code> is <code>null</code>.
	 */
	@Override
	public void addAddress(final CustomerAddress address) throws EpDomainException {
		if (address == null) {
			throw new EpDomainException("Null address cannot be added.");
		}
		if (!getAddresses().contains(address)) {
			getAddresses().add(address);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EpDomainException if the given <code>Customer</code> is <code>null</code>.
	 */
	@Override
	public void removeAddress(final CustomerAddress address) throws EpDomainException {
		if (address == null) {
			throw new EpDomainException("Null address cannot be removed.");
		}
		getAddresses().remove(address);
		if (address == getPreferredShippingAddress()) {
			setPreferredShippingAddress(null);
		}
		if (address == getPreferredBillingAddress()) {
			setPreferredBillingAddress(null);
		}
	}

	@ManyToOne(targetEntity = CustomerAddressImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE })
	@JoinColumn(name = "PREF_SHIP_ADDRESS_UID")
	@ForeignKey(name = "FK_C_SHIPADDRESS", enabled = true)
	protected CustomerAddress getPreferredShippingAddressInternal() {
		return preferredShippingAddress;
	}

	protected void setPreferredShippingAddressInternal(final CustomerAddress preferredShippingAddress) {
		this.preferredShippingAddress = preferredShippingAddress;
	}

	@Override
	public void setPreferredShippingAddress(final CustomerAddress address) {
		if (address != null) {
			addAddress(address);
		}
		setPreferredShippingAddressInternal(address);
	}

	@Override
	@Transient
	public CustomerAddress getPreferredShippingAddress() {
		return getPreferredShippingAddressInternal();
	}

	@ManyToOne(targetEntity = CustomerAddressImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE })
	@JoinColumn(name = "PREF_BILL_ADDRESS_UID")
	@ForeignKey(name = "FK_C_BILLADDRESS", enabled = true)
	protected CustomerAddress getPreferredBillingAddressInternal() {
		return preferredBillingAddress;
	}

	protected void setPreferredBillingAddressInternal(final CustomerAddress preferredBillingAddress) {
		this.preferredBillingAddress = preferredBillingAddress;
	}

	@Override
	public void setPreferredBillingAddress(final CustomerAddress address) {
		if (address != null) {
			addAddress(address);
		}
		setPreferredBillingAddressInternal(address);
	}

	@Override
	@Transient
	public CustomerAddress getPreferredBillingAddress() {
		return getPreferredBillingAddressInternal();
	}

	@Override
	@Transient
	public Locale getPreferredLocale() {
		String localeStr = getCustomerProfile().getStringProfileValue(ATT_KEY_CP_PREF_LOCALE);
		if (localeStr == null || localeStr.length() == 0) {
			return null;
		}
		final StringTokenizer stringTokenizer = new StringTokenizer(localeStr.trim(), "_");
		int size = stringTokenizer.countTokens();
		if (size == 2) { // eg. en_US, or en_
			String language = stringTokenizer.nextToken();
			String country = stringTokenizer.nextToken();
			if (country != null && country.length() > 0) {
				return new Locale(language, country);
			}
		}
		return new Locale(localeStr.trim());
	}

	@Override
	@Transient
	public boolean isPreferredLocaleRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_PREF_LOCALE);
	}

	@Override
	public void setPreferredLocale(final Locale preferredLocale) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_PREF_LOCALE, preferredLocale.toString());
	}

	@Override
	@Transient
	public Currency getPreferredCurrency() {
		Currency currency = null;
		String currencyStr = getCustomerProfile().getStringProfileValue(ATT_KEY_CP_PREF_CURR);
		if (currencyStr != null && currencyStr.length() == CURRENCY_LENGTH_ISO4217) {
			currency = Currency.getInstance(currencyStr.trim());
		}
		return currency;
	}

	@Override
	@Transient
	public boolean isPreferredCurrencyRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_PREF_CURR);
	}

	@Override
	public void setPreferredCurrency(final Currency preferredCurrency) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_PREF_CURR, preferredCurrency.getCurrencyCode());
	}

	@Override
	@Transient
	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		if (isAnonymous() && getPreferredBillingAddress() != null) {
			fullName.append(getPreferredBillingAddress().getFirstName());
			fullName.append(" ");
			fullName.append(getPreferredBillingAddress().getLastName());
		} else {
			if (getFirstName() == null) {
				if (getLastName() != null) {
					fullName.append(getLastName());
				}
			} else {
				fullName.append(getFirstName());
				if (getLastName() != null) {
					fullName.append(" ");
					fullName.append(getLastName());
				}
			}
		}
		return fullName.toString();
	}

	@Override
	@Transient
	public String getFirstName() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_FIRST_NAME);
	}

	@Override
	@Transient
	public boolean isFirstNameRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_FIRST_NAME);
	}

	@Override
	public void setFirstName(final String firstName) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_FIRST_NAME, firstName);
	}

	@Override
	@Transient
	public String getLastName() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_LAST_NAME);
	}

	@Override
	@Transient
	public boolean isLastNameRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_LAST_NAME);
	}

	@Override
	public void setLastName(final String lastName) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_LAST_NAME, lastName);
	}

	@Override
	@Transient
	public String getPassword() {
		return getCustomerAuthentication().getPassword();
	}

	@Override
	public void setPassword(final String password) {
		getCustomerAuthentication().setPassword(password);
	}

	@Override
	public void setClearTextPassword(final String clearTextPassword) {
		getCustomerAuthentication().setClearTextPassword(clearTextPassword);
	}

	@Override
	@Transient
	public String getClearTextPassword() {
		return getCustomerAuthentication().getClearTextPassword();
	}

	@Override
	public String resetPassword() {
		final PasswordGenerator passwordGenerator = getBean("passwordGenerator");
		final String newPassword = passwordGenerator.getPassword();
		setClearTextPassword(newPassword);
		return newPassword;
	}

	@Override
	@Transient
	public boolean isAnonymous() {
		Boolean anonymous = (Boolean) getCustomerProfile().getProfileValue(ATT_KEY_CP_ANONYMOUS_CUST);

		if (anonymous == null) {
			return false;
		}
		return anonymous.booleanValue();
	}

	@Override
	@Transient
	public boolean isRegistered() {
		if (isPersisted() && !isAnonymous()) {
			return true;
		}
		return false;
	}

	@Override
	public void setAnonymous(final boolean anonymous) {
		getCustomerProfile().setProfileValue(ATT_KEY_CP_ANONYMOUS_CUST, Boolean.valueOf(anonymous));
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_EDIT_DATE", nullable = false)
	public Date getLastEditDate() {
		return lastEditDate;
	}

	@Override
	public void setLastEditDate(final Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	@Override
	@Transient
	public Date getDateOfBirth() {
		return (Date) (getCustomerProfile().getProfileValue(ATT_KEY_CP_DOB));
	}

	@Override
	public void setDateOfBirth(final Date dateOfBirth) {
		getCustomerProfile().setProfileValue(ATT_KEY_CP_DOB, dateOfBirth);
	}

	@Override
	@ManyToMany(targetEntity = CustomerGroupImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE },
				fetch = FetchType.EAGER)
	@JoinTable(name = "TCUSTOMERGROUPX",
			joinColumns = @JoinColumn(name = CUSTOMER_UID, nullable = false),
			inverseJoinColumns = @JoinColumn(name = "CUSTOMERGROUP_UID", nullable = false)
	)
	public List<CustomerGroup> getCustomerGroups() {
		return customerGroups;
	}

	@Override
	public void setCustomerGroups(final List<CustomerGroup> customerGroups) {
		this.customerGroups = customerGroups;
	}

	@Override
	public boolean belongsToCustomerGroup(final long customerGroupID) {
		boolean status = false;
		if (getCustomerGroups() != null && !getCustomerGroups().isEmpty()) {
			for (int i = 0; i < getCustomerGroups().size(); i++) {
				if (customerGroupID == getCustomerGroups().get(i).getUidPk()) {
					status = true;
					break;
				}
			}

		}
		return status;
	}

	@Override
	public boolean belongsToCustomerGroup(final String groupName) {
		boolean status = false;
		if (groupName != null && getCustomerGroups() != null && !getCustomerGroups().isEmpty()) {
			for (int i = 0; i < getCustomerGroups().size(); i++) {
				if (groupName.equals(getCustomerGroups().get(i).getName())) {
					status = true;
					break;
				}
			}

		}
		return status;
	}

	@Override
	public void addCustomerGroup(final CustomerGroup customerGroup) {
		if (customerGroup == null) {
			throw new EpDomainException("Null customer group cannot be added.");
		}
		if (!getCustomerGroups().contains(customerGroup)) {
			getCustomerGroups().add(customerGroup);
		}
	}

	@Override
	public void removeCustomerGroup(final CustomerGroup customerGroup) {
		if (customerGroup == null) {
			throw new EpDomainException("Null customer group cannot be added.");
		}
		getCustomerGroups().remove(customerGroup);
	}

	@Override
	@Transient
	public String getPhoneNumber() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_PHONE);
	}

	@Override
	@Transient
	public boolean isPhoneNumberRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_PHONE);
	}

	@Override
	public void setPhoneNumber(final String phoneNumber) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_PHONE, phoneNumber);
	}

	@Override
	@Transient
	public char getGender() {
		char gender = GENDER_NOT_SELECTED;
		String genderString = getCustomerProfile().getStringProfileValue(ATT_KEY_CP_GENDER);
		if (genderString != null && genderString.length() == 1) { // there should be exactly one character
			gender = genderString.charAt(0);
		}
		return gender;
	}

	@Override
	@Transient
	public boolean isGenderRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_GENDER);
	}

	@Override
	public void setGender(final char gender) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_GENDER, String.valueOf(gender));
	}

	@Override
	@Transient
	public String getCompany() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_COMPANY);
	}

	@Override
	@Transient
	public boolean isCompanyRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_COMPANY);
	}

	@Override
	public void setCompany(final String company) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_COMPANY, company);
	}

	@Override
	@Basic
	@Column(name = "STATUS")
	public int getStatus() {
		return status;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EpDomainException if the status is invalid.
	 */
	@Override
	public void setStatus(final int status) {
		// for JPA setter
		if (status == 0) {
			return;
		}
		if (status != Customer.STATUS_ACTIVE && status != Customer.STATUS_DISABLED && status != Customer.STATUS_PENDING_APPROVAL) {
			throw new EpDomainException("Invalid customer status.");
		}
		this.status = status;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return (getStatus() == Customer.STATUS_ACTIVE);
	}

	@Override
	@Transient
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> grantedAuthority = new ArrayList<GrantedAuthority>();
		Iterator<CustomerGroup> cgIter = getCustomerGroups().iterator();
		while (cgIter.hasNext()) {
			CustomerGroup customerGroup = cgIter.next();
			Iterator<CustomerRole> customerRoleIter = customerGroup.getCustomerRoles().iterator();
			while (customerRoleIter.hasNext()) {
				grantedAuthority.add(customerRoleIter.next());
			}
		}
		return grantedAuthority;
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isEnabled() {
		return (getStatus() == Customer.STATUS_ACTIVE);
	}

	/**
	 * Returns the username used to authenticate the user. Cannot return <code>null</code>.
	 *
	 * @return the username (never <code>null</code>)
	 */
	@Override
	@Transient
	public String getUsername() {
		String userName = null;
		int userIdMode = getUserIdMode();
		if (userIdMode == WebConstants.GENERATE_UNIQUE_PERMANENT_USER_ID_MODE) {
			userName = getEmail();
		} else {
			userName = getUserId();
		}
		return userName;
	}

	@Override
	@Transient
	public CustomerAddress getAddressByUid(final long addressUid) {
		for (CustomerAddress currAddress : getAddresses()) {
			if (currAddress.getUidPk() == addressUid) {
				return currAddress;
			}
		}
		return null;
	}

	@Override
	@Transient
	public CustomerAddress getAddressByGuid(final String addressGuid) {
		for (Object element : getAddresses()) {
			CustomerAddress currAddress = (CustomerAddress) element;
			if (currAddress.getGuid() != null && currAddress.getGuid().equals(addressGuid)) {
				return currAddress;
			}
		}
		return null;
	}

	@Override
	@Transient
	public CustomerCreditCard getCreditCardByUid(final long creditCardUid) {
		for (CustomerCreditCard currCreditCard : getCreditCards()) {
			if (currCreditCard.getUidPk() == creditCardUid) {
				return currCreditCard;
			}
		}
		return null;
	}

	@Override
	@Transient
	public CustomerCreditCard getCreditCardByGuid(final String creditCardGuid) {
		for (CustomerCreditCard currentCreditCard : getCreditCards()) {
			if (StringUtils.equals(creditCardGuid, currentCreditCard.getGuid())) {
				return currentCreditCard;
			}
		}
		return null;
	}

	@Override
	@Transient
	public CustomerCreditCard getPreferredCreditCard() {
		List<CustomerCreditCard> customerCreditCards = getCreditCards();
		for (CustomerCreditCard currentCreditCard : customerCreditCards) {
			if (currentCreditCard.isDefaultCard()) {
				return currentCreditCard;
			}
		}
		return null;
	}
	
	@Override
	@Transient
	public CustomerPaymentMethodsImpl getPaymentMethods() {
		return new CustomerPaymentMethodsImpl(this);
	}

	@Override
	@Transient
	public List<CustomerCreditCard> getCreditCards() {
		List<CustomerCreditCard> filteredPaymentMethods = new ArrayList<CustomerCreditCard>();
		
		for (PaymentMethod method : getPaymentMethods().all()) {
			if (method instanceof CustomerCreditCard) {
				filteredPaymentMethods.add((CustomerCreditCard) method);
			}
		}
		
		return Collections.unmodifiableList(filteredPaymentMethods);
	}

	@Override
	public void setCreditCards(final List<CustomerCreditCard> creditCards) {
		getPaymentMethods().removeAll(new ArrayList<PaymentMethod>(getCreditCards()));

		if (creditCards != null) {
			getPaymentMethods().addAll(new ArrayList<PaymentMethod>(creditCards));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EpDomainException if the given <code>CustomerCreditCard</code> is <code>null</code>.
	 */
	@Override
	public void addCreditCard(final CustomerCreditCard creditCard) throws EpDomainException {
		if (creditCard == null) {
			throw new EpDomainException("Null credit card cannot be added.");
		}
		if (!getPaymentMethods().contains(creditCard)) {
			getPaymentMethods().add(creditCard);
		}

		updateCreditCard(creditCard);
	}

	/**
	 * Checks that a credit card has been set as the default card. <br>
	 * If no card is the default card, a card is set to be the default. <br>
	 * This will also ensure that only one card has been set as the default.
	 */
	protected void checkDefaultCardSet() {
		boolean defaultFound = false;
		for (CustomerCreditCard currCreditCard : getCreditCards()) {
			if (currCreditCard.isDefaultCard()) {
				if (defaultFound) {
					currCreditCard.setDefaultCard(false);
				} else {
					defaultFound = true;
				}
			}
		}
		if (!defaultFound && getCreditCards().size() > 0) {
			getCreditCards().get(0).setDefaultCard(true);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EpDomainException if the given <code>CustomerCreditCard</code> is <code>null</code>.
	 */
	@Override
	public void removeCreditCard(final CustomerCreditCard creditCard) throws EpDomainException {
		if (creditCard == null) {
			throw new EpDomainException("Null credit card cannot be removed.");
		}
		getPaymentMethods().remove(creditCard);
		checkDefaultCardSet();
	}

	/**
	 * {@inheritDoc} <br>
	 * Note that if the card is not already in the customer's set of cards it will be added.
	 */
	@Override
	public void updateCreditCard(final CustomerCreditCard creditCard) {
		// Add this card to the list if it is not already
		if (!getPaymentMethods().contains(creditCard)) {
			addCreditCard(creditCard);
		}
		if (creditCard.isDefaultCard()) {
			// Make sure this card is the only one with the default flag set
			for (CustomerCreditCard currCreditCard : getCreditCards()) {
				if (!currCreditCard.equals(creditCard) && currCreditCard.isDefaultCard()) {
					currCreditCard.setDefaultCard(false);
				}
			}
		}

		checkDefaultCardSet();
	}

	@Override
	public void setPreferredCreditCard(final CustomerCreditCard preferredCreditCard) {
		Iterator<CustomerCreditCard> cardIter = getCreditCards().iterator();
		CustomerCreditCard card;
		while (cardIter.hasNext()) {
			card = cardIter.next();
			if (card.getUidPk() == preferredCreditCard.getUidPk()) {
				card.setDefaultCard(true);
			} else {
				card.setDefaultCard(false);
			}
		}
	}

	/**
	 * Update the last modified date.
	 */
	@Override
	public void executeBeforePersistAction() {
		checkDefaultCardSet();
	}

	@Override
	@Transient
	public CustomerProfile getCustomerProfile() {
		if (customerProfile == null) {
			initializeCustomerProfile();
		}
		return customerProfile;
	}

	private void initializeCustomerProfile() {
		customerProfile = getBean(ContextIdNames.CUSTOMER_PROFILE);
		customerProfile.setProfileValueBeanId(ContextIdNames.CUSTOMER_PROFILE_VALUE);
		customerProfile.setProfileValueMap(new HashMap<String, AttributeValue>());
	}

	@Override
	public void setCustomerProfile(final CustomerProfile customerProfile) {
		this.customerProfile = customerProfile;
	}


	@OneToOne(targetEntity = CustomerAuthenticationImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "AUTHENTICATION_UID")
	public CustomerAuthentication getCustomerAuthenticationInternal() {
		return customerAuthentication;
	}

	public void setCustomerAuthenticationInternal(final CustomerAuthentication customerAuthentication) {
		this.customerAuthentication = customerAuthentication;
	}

	@Override
	@Transient
	public CustomerAuthentication getCustomerAuthentication() {
		if (getCustomerAuthenticationInternal() == null) {
			initCustomerAuthentication();
		}

		return getCustomerAuthenticationInternal();
	}

	private void initCustomerAuthentication() {
		CustomerAuthentication customerAuthentication = getBean(ContextIdNames.CUSTOMER_AUTHENTICATION);
		setCustomerAuthenticationInternal(customerAuthentication);
	}

	@Override
	public void setCustomerAuthentication(final CustomerAuthentication customerAuthentication) {
		setCustomerAuthenticationInternal(customerAuthentication);
	}

	@Override
	@Transient
	public boolean isToBeNotified() {
		Boolean toBeNotified = (Boolean) getCustomerProfile().getProfileValue(ATT_KEY_CP_BE_NOTIFIED);

		if (toBeNotified == null) {
			return false;
		}
		return toBeNotified.booleanValue();
	}

	@Override
	public void setToBeNotified(final boolean toBeNotified) {
		getCustomerProfile().setProfileValue(ATT_KEY_CP_BE_NOTIFIED, Boolean.valueOf(toBeNotified));
	}

	@Override
	@OneToMany(targetEntity = CustomerProfileValueImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "localizedAttributeKey")
	@ElementDependent
	@ElementJoinColumn(name = CUSTOMER_UID)
	@ElementForeignKey(name = "tcustomerprofilevalue_ibfk_2")
	public Map<String, AttributeValue> getProfileValueMap() {
		return getCustomerProfile().getProfileValueMap();
	}

	@Override
	public void setProfileValueMap(final Map<String, AttributeValue> profileValueMap) {
		getCustomerProfile().setProfileValueMap(profileValueMap);
	}

	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@Transient
	public boolean isHtmlEmailPreferred() {
		Boolean isHtmlEmailPreferred = (Boolean) getCustomerProfile().getProfileValue(ATT_KEY_CP_HTML_EMAIL);

		if (isHtmlEmailPreferred == null) {
			return false;
		}
		return isHtmlEmailPreferred.booleanValue();
	}

	@Override
	public void setHtmlEmailPreferred(final boolean isHtmlEmail) {
		getCustomerProfile().setProfileValue(ATT_KEY_CP_HTML_EMAIL, Boolean.valueOf(isHtmlEmail));
	}

	@Override
	@Basic
	@Column(name = "STORECODE", nullable = false)
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(
			name = TABLE_NAME,
			table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID",
			valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME,
			allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Transient
	public String getFaxNumber() {
		return getCustomerProfile().getStringProfileValue(ATT_KEY_CP_FAX);
	}

	@Override
	@Transient
	public boolean isFaxNumberRequired() {
		return getCustomerProfile().isProfileValueRequired(ATT_KEY_CP_FAX);
	}

	@Override
	public void setFaxNumber(final String faxNumber) {
		getCustomerProfile().setStringProfileValue(ATT_KEY_CP_FAX, faxNumber);
	}

	@Override
	@Transient
	public Date getLastModifiedDate() {
		return getLastEditDate();
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		setLastEditDate(lastModifiedDate);
	}

	@Transient
	protected CustomerService getCustomerService() {
		return getBean(ContextIdNames.CUSTOMER_SERVICE);
	}

	@Override
	public CustomerRoleMapper getCustomerRoleMapper() {
		return new CustomerRoleMapper(this);
	}

	/**
	 * Gets a customer's default payment method.
	 * @return the default payment method
	 */
	@OneToOne(targetEntity = AbstractPaymentMethodImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "DEFAULT_PAYMENT_METHOD_UID")
	@Dependent
	PaymentMethod getDefaultPaymentMethod() {
		return defaultPaymentMethod;
	}

	/**
	 * Sets the default payment method.
	 * @param defaultPaymentMethod the method to set as default
	 */
	void setDefaultPaymentMethod(final PaymentMethod defaultPaymentMethod) {
		this.defaultPaymentMethod = defaultPaymentMethod;
	}

	/**
	 * Returns a list of payment methods.
	 * @return the list of payment methods
	 */
	@OneToMany(targetEntity = AbstractPaymentMethodImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "TCUSTOMERPAYMENTMETHOD",
			joinColumns = @JoinColumn(name = "CUSTOMER_UID"),
			inverseJoinColumns = @JoinColumn(name = "PAYMENT_METHOD_UID"))
	@ElementDependent
	Collection<PaymentMethod> getPaymentMethodsInternal() {
		return paymentMethods;
	}

	/**
	 * Sets the list of payment methods. Method visibility set to private because this is only required by OpenJPA.
	 * @param paymentMethods the list of payment methods
	 */
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private void setPaymentMethodsInternal(final Collection<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}
}
