/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpDateBindException;
import com.elasticpath.commons.exception.EpNonConsistentDomainFieldException;
import com.elasticpath.commons.util.impl.ConverterUtils;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Represents customer payment information.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity" })
@Entity
@Table(name = OrderPaymentImpl.TABLE_NAME)
@DataCache(enabled = false)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.ORDER_INDEX, attributes = {
			@FetchAttribute(name = "currencyCode")
		}),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = {
		@FetchAttribute(name = "paymentMethod"),
		@FetchAttribute(name = "displayValue"),
		@FetchAttribute(name = "expiryMonth"),
		@FetchAttribute(name = "expiryYear"),
		@FetchAttribute(name = "currencyCode"),
		@FetchAttribute(name = "cardType"),
		@FetchAttribute(name = "internalCreditCardNumber")
	}, postLoad = true)
})
public class OrderPaymentImpl extends AbstractLegacyPersistenceImpl implements OrderPayment, DatabaseLastModifiedDate {
	private static final long serialVersionUID = 5000000001L;

	private static final int MEDIUM_TEXT_LENGTH = 100;

	private static final int SHORT_TEXT_LENGTH = 50;

	private static final int YEAR_LENGTH = 4;

	private static final int CURRENCY_LENGTH = 10;

	private static final int TRANS_TYPE_LENGTH = 20;

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final String FIRST_DAY = "01"; //$NON-NLS-1$

	private static final String DATE_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERPAYMENT";

	private String cardType;

	private String cardHolderName;

	private String expiryMonth;

	private String expiryYear;

	private Date startDate;

	private String issueNumber;

	private String cvv2Code;

	private BigDecimal amount;

	private PaymentType paymentMethod;

	private String authorizationCode;

	private String referenceId;

	private String requestToken;

	private String currencyCode;

	private String email;

	private String transactionType;

	private OrderPaymentStatus status;

	private String gatewayToken;

	private String token;
	
	private Date createdDate;

	private GiftCertificate giftCertificate;

	private String ipAddress;

	private PayerAuthValidationValue payerAuthValidationValue;

	private long uidPk;

	private OrderShipment orderShipment;

	private CreditCardNumber creditCardNumber;
	
	private Date lastModifiedDate;
	
	private boolean paymentForSubscriptions = false;

	private String displayValue;

	@Transient
	private CreditCardNumber getCreditCardNumber() {
		if (this.getInternalCreditCardNumber() == null) {
			this.setInternalCreditCardNumber(new CreditCardNumber());
		}		
		return this.getInternalCreditCardNumber();
	}
	
	@Embedded
	private CreditCardNumber getInternalCreditCardNumber() {
		return this.creditCardNumber;
	}
	
	private void setInternalCreditCardNumber(final CreditCardNumber creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	/**
	 * Get the related orderShipment. The ordershipment maybe null when the payment is on the order level and not related to a
	 * shipment.
	 * 
	 * @return the orderShipment
	 */
	@ManyToOne(targetEntity = AbstractOrderShipmentImpl.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "ORDERSHIPMENT_UID")
	@ForeignKey(name = "torderpayment_ibfk_3")
	public OrderShipment getOrderShipment() {
		return orderShipment;
	}

	/**
	 * Set the orderShipment for the payment.
	 * 
	 * @param orderShipment the orderShipment to set
	 */
	public void setOrderShipment(final OrderShipment orderShipment) {
		this.orderShipment = orderShipment;
	}

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Constructor.
	 */
	public OrderPaymentImpl() {
		super();
	}

	/**
	 * Get the vendor/brand of the credit card (e.g. VISA).
	 * 
	 * @return the type
	 */
	@Basic
	@Column(name = "CARD_TYPE", length = SHORT_TEXT_LENGTH)
	public String getCardType() {
		return this.cardType;
	}

	/**
	 * Set the vendor/brand of the credit card (e.g. VISA).
	 * 
	 * @param cardType the card type
	 */
	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	/**
	 * Set the card holder name.
	 * 
	 * @return the name on the card
	 */
	@Basic
	@Column(name = "CARD_HOLDER_NAME", length = MEDIUM_TEXT_LENGTH)
	public String getCardHolderName() {
		return this.cardHolderName;
	}

	/**
	 * Get the card holder name.
	 * 
	 * @param cardHolderName the name on the card
	 */
	public void setCardHolderName(final String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	/**
	 * Get the encrypted credit cart number.
	 * 
	 * @return the credit card number.
	 */
	@Transient
	public String getCardNumber() {
		return this.getCreditCardNumber().getEncryptedCardNumber();
	}

	/**
	 * Decrypts and returns the full credit card number. Access to this method should be restricted.
	 * 
	 * @return the decrypted credit card number
	 */
	@Transient
	public String getUnencryptedCardNumber() {
		return this.getCreditCardNumber().getFullCardNumber();
	}

	@Override
	@Transient
	public String getMaskedCardNumber() {
		return getDisplayValue();
	}

	@Override
	@Basic
	@Column(name = "MASKED_CARD_NUMBER")
	public String getDisplayValue() {
		return displayValue;
	}

	@Override
	public void setDisplayValue(final String displayValue) {
		this.displayValue = displayValue;
	}

	/**
	 * Set the credit card number.
	 * 
	 * @param number the credit card number
	 */
	@Transient
	public void setUnencryptedCardNumber(final String number) {
		// we need this field for spring validation. can we overcome this problem?
		this.getCreditCardNumber().setFullCardNumber(number);
		setDisplayValue(getCreditCardNumber().getMaskedCardNumber());
	}

	/**
	 * Get the two-digit expiry date year.
	 * 
	 * @return the expiry date year
	 */
	@Basic
	@Column(name = "EXPIRY_YEAR", length = YEAR_LENGTH)
	public String getExpiryYear() {
		checkDate();
		return this.expiryYear;
	}

	/**
	 * Set the two-digit expiry date year.
	 * 
	 * @param expiryYear the expiry date year
	 */
	public void setExpiryYear(final String expiryYear) {
		checkDate();
		this.expiryYear = expiryYear;
	}

	/**
	 * Get the two-digit expiry date month.
	 * 
	 * @return the two-digit expiry date month
	 */
	@Basic
	@Column(name = "EXPIRY_MONTH", length = 2)
	public String getExpiryMonth() {
		checkDate();
		return this.expiryMonth;
	}

	/**
	 * Set the expiry two-digit date month.
	 * 
	 * @param expiryMonth the two digit expiry date month
	 */
	public void setExpiryMonth(final String expiryMonth) {
		checkDate();
		this.expiryMonth = expiryMonth;
	}

	/**
	 * Get the card start date Used by some U.K. cards.
	 * 
	 * @return the start date
	 */
	@Basic
	@Temporal(TemporalType.DATE)
	@Column(name = "START_DATE")
	public Date getStartDate() {
		checkDate();
		return this.startDate;
	}

	/**
	 * Set the cart start date used by some U.K. cards.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		checkDate();
		this.startDate = startDate;
	}
	
	/**
	 * Creates a Date object from a zero-based month and a year, using the default locale.
	 * @param month the two-digit integer representing the month (zero-based)
	 * @param year the four-digit integer representing the year
	 * @return a Date representing the given month and year, or null if either
	 * of the given Strings is blank.
	 * @throws EpDateBindException if there is a parsing error
	 */
	Date createDate(final String month, final String year) {
		final String datePattern = "MMyyyy";
		if (!StringUtils.isBlank(month) && !StringUtils.isBlank(year)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(datePattern, Locale.getDefault());
				return sdf.parse(month + year);
			} catch (ParseException pe) {
				throw new EpDateBindException("Invalid month or year strings. Month:" + month + " Year:" + year, pe);
			}
		}
		return null;
	}

	/**
	 * Get the issue number used by some U.K. cards.
	 * 
	 * @return the issue number
	 */
	@Basic
	@Column(name = "ISSUE_NUMBER", length = MEDIUM_TEXT_LENGTH)
	public String getIssueNumber() {
		return this.issueNumber;
	}

	/**
	 * Set the issue number used by some U.K. cards.
	 * 
	 * @param issueNumber the issue number
	 */
	public void setIssueNumber(final String issueNumber) {
		this.issueNumber = issueNumber;
	}

	/**
	 * Get the card security code (found near the signature on the back of the card).
	 * 
	 * @return the card security code
	 */
	@Transient
	public String getCvv2Code() {
		return this.cvv2Code;
	}

	/**
	 * Set the security code (found near the signature on the back of the card).
	 * 
	 * @param cvv2Code the security code
	 */
	public void setCvv2Code(final String cvv2Code) {
		this.cvv2Code = cvv2Code;
	}

	/**
	 * Sets whether or not this <code>OrderPayment</code>'s credit card number should
	 * be encrypted before it is persisted. Specify <code>true</code> to encrypt the
	 * credit card number before persisting, <code>false</code> otherwise.
	 * <strong>Note that if false is specified, the number will be masked before it
	 * is stored unencrypted</strong>.
	 * @param shouldEncryptCreditCard whether or not the credit card should be encrypted
	 */
	public void setShouldStoreEncryptedCreditCard(final boolean shouldEncryptCreditCard) {
		this.getCreditCardNumber().setShouldPersistEncrypted(shouldEncryptCreditCard);
	}
	
	/**
	 * Returns <code>true</code> if the credit card number is being persisted encrypted, <code>false</code> otherwise.
	 * Note that if <code>false</code> is returned, the number is being persisted masked. 
	 *
	 * @return whether or not the credit card is being persisted encrypted
	 */
	@Transient
	public boolean isShouldStoreEncryptedCreditCard() {
		return this.getCreditCardNumber().isShouldPersistEncrypted();
	}
	
	/**
	 * Returns <code>true</code> to indicate that this {@link OrderPayment}'s credit card is stored encrypted;
	 * <code>false</code> to indicate that the credit card is stored masked.
	 * 
	 * @return <code>true</code> to indicate that this {@link OrderPayment}'s credit card is stored encrypted;
	 * <code>false</code> to indicate that the credit card is stored masked.
	 */
	@Transient
	public boolean isEncryptedCreditCardStored() {
		return (this.getUnencryptedCardNumber() != null);
	}

	/**
	 * Get the payment method. A payment method could be the name of the payment processor/gateway.
	 * 
	 * @return the payment method
	 */
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "PAYMENT_GATEWAY", length = MEDIUM_TEXT_LENGTH)
	public PaymentType getPaymentMethod() {
		return paymentMethod;
	}

	/**
	 * Set the payment method.
	 * 
	 * @param paymentMethod the payment method
	 */
	public void setPaymentMethod(final PaymentType paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * Get the amount of this payment.
	 * 
	 * @return the amount
	 */
	@Basic
	@Column(name = "AMOUNT", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * Set the amount of this payment.
	 * 
	 * @param amount the amount
	 */
	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Get the reference id.
	 * 
	 * @return the reference id.
	 */
	@Basic
	@Column(name = "REFERENCE_ID", length = SHORT_TEXT_LENGTH)
	public String getReferenceId() {
		return this.referenceId;
	}

	/**
	 * Set the reference id.
	 * 
	 * @param referenceId the reference id
	 */
	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	/**
	 * Get the request token.
	 * 
	 * @return the request token
	 */
	@Basic
	@Column(name = "REQUEST_TOKEN")
	public String getRequestToken() {
		return this.requestToken;
	}

	/**
	 * Set the request token.
	 * 
	 * @param requestToken the request token
	 */
	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
	}

	/**
	 * Get the authorization code.
	 * 
	 * @return the authorization code
	 */
	@Basic
	@Column(name = "AUTHORIZATION_CODE", length = SHORT_TEXT_LENGTH)
	public String getAuthorizationCode() {
		return this.authorizationCode;
	}

	/**
	 * Set the authorization code.
	 * 
	 * @param authorizationCode the authorization code
	 */
	public void setAuthorizationCode(final String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	/**
	 * Get the currency code (e.g. CAD or USD).
	 * 
	 * @return the currency code
	 */
	@Basic
	@Column(name = "CURRENCY", length = CURRENCY_LENGTH)
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	/**
	 * Set the currency code.
	 * 
	 * @param currencyCode the currency code code
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * Get the customer's email address (Required for card processing).
	 * 
	 * @return the customer email address
	 */
	@Basic
	@Column(name = "EMAIL", length = MEDIUM_TEXT_LENGTH)
	public String getEmail() {
		return this.email;
	}

	/**
	 * Set the customer's email address (Required for card processing).
	 * 
	 * @param email the customer's email address
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @return the payment transaction type
	 */
	@Basic
	@Column(name = "TRANSACTION_TYPE", length = TRANS_TYPE_LENGTH)
	public String getTransactionType() {
		return this.transactionType;
	}

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 * 
	 * @param transactionType the payment transaction type
	 */
	public void setTransactionType(final String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * Get the status of the order payment.
	 * 
	 * @return the order payment status
	 */
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	public OrderPaymentStatus getStatus() {
		return this.status;
	}

	/**
	 * Set the status of the order payment.
	 * 
	 * @param status the status of the order payment
	 */
	public void setStatus(final OrderPaymentStatus status) {
		this.status = status;
	}

	/**
	 * Store the temporary token needed by some payment gateway, i.e. PayPal to complete the process.
	 * 
	 * @param gatewayToken payment gateway token
	 */
	public void setGatewayToken(final String gatewayToken) {
		this.gatewayToken = gatewayToken;
	}

	/**
	 * Return the payment gateway token.
	 * 
	 * @return the temporary payment gateway token.
	 */
	@Transient
	public String getGatewayToken() {
		return this.gatewayToken;
	}

	@Override
	public void usePaymentToken(final PaymentToken paymentToken) {
		this.displayValue = paymentToken.getDisplayValue();
		this.token = paymentToken.getValue();
	}

	@Override
	public PaymentToken extractPaymentToken() {
		return new PaymentTokenImpl.TokenBuilder().withValue(token).withDisplayValue(displayValue).build();
	}

	/**
	 * Get the ipAddress of the user from the Order Payment.
	 * 
	 * @return the ipAddress
	 */
	@Transient
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * Set the users ip Address into the Order Payment.
	 * 
	 * @param ipAddress the ipAddress of the user.
	 */
	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * Get the gift certificate for the payment.
	 * 
	 * @return the giftCertificate
	 */
	@ManyToOne(targetEntity = GiftCertificateImpl.class)
	@JoinColumn(name = "GIFTCERTIFICATE_UID")
	public GiftCertificate getGiftCertificate() {
		return giftCertificate;
	}

	/**
	 * Set the gift certificate for the payment.
	 * 
	 * @param giftCertificate the giftCertificate to set
	 */
	public void setGiftCertificate(final GiftCertificate giftCertificate) {
		this.giftCertificate = giftCertificate;
	}

	/**
	 * Get Payer Authentication Validate value for transaction.
	 * 
	 * @return the payerAuthValidationValue.
	 */
	@Transient
	public PayerAuthValidationValue getPayerAuthValidationValue() {
		return this.payerAuthValidationValue;
	}

	/**
	 * Set the payerAuthValidationValue for order payment.
	 * 
	 * @param payerAuthValidationValue the payerAuthValidationValue
	 */
	public void setPayerAuthValidationValue(final PayerAuthValidationValue payerAuthValidationValue) {
		this.payerAuthValidationValue = payerAuthValidationValue;
	}

	/**
	 * Copy orderPayment's credit card info to <code>this</code> Order Payment.
	 * 
	 * @param orderPayment the source of credit card info.
	 */
	public void copyCreditCardInfo(final OrderPayment orderPayment) {
		setShouldStoreEncryptedCreditCard(orderPayment.isShouldStoreEncryptedCreditCard());
		setCardHolderName(orderPayment.getCardHolderName());
		setCardType(orderPayment.getCardType());
		setExpiryMonth(orderPayment.getExpiryMonth());
		setExpiryYear(orderPayment.getExpiryYear());
		setCvv2Code(orderPayment.getCvv2Code());
		
		if (orderPayment.getUnencryptedCardNumber() == null) {
			setDisplayValue(orderPayment.getDisplayValue());
		} else {
			setUnencryptedCardNumber(orderPayment.getUnencryptedCardNumber());
		}

		setStartDate(orderPayment.getStartDate());
		setIssueNumber(orderPayment.getIssueNumber());
	}

	/**
	 * Tells <code>this</code> to use the specified credit card.
	 * 
	 * @param creditCard the card to use for this order payment.
	 */
	public void useCreditCard(final CustomerCreditCard creditCard) {
		setPaymentMethod(PaymentType.CREDITCARD);
		setCardHolderName(creditCard.getCardHolderName());
		setCardType(creditCard.getCardType());
		setExpiryMonth(creditCard.getExpiryMonth());
		setExpiryYear(creditCard.getExpiryYear());
		setCvv2Code(creditCard.getSecurityCode());
		setUnencryptedCardNumber(creditCard.getUnencryptedCardNumber());
		setStartDate(createDate(creditCard.getStartMonth(), creditCard.getStartYear()));
		setIssueNumber(String.valueOf(creditCard.getIssueNumber()));
	}

	/**
	 * Extract the credit card information in this order payment into a credit card object.
	 * 
	 * @return a new credit card instance populated with the credit card information
	 */
	public CustomerCreditCard extractCreditCard() {
		final CustomerCreditCard newCreditCard = getBean(ContextIdNames.CUSTOMER_CREDIT_CARD);
		newCreditCard.setCardHolderName(getCardHolderName());
		newCreditCard.setCardNumber(getUnencryptedCardNumber());
		newCreditCard.setCardType(getCardType());
		newCreditCard.setSecurityCode(getCvv2Code());
		newCreditCard.setExpiryMonth(getExpiryMonth());
		newCreditCard.setExpiryYear(getExpiryYear());
		newCreditCard.setDefaultCard(true);
		if (getStartDate() != null) {
			newCreditCard.setStartMonth(ConverterUtils.date2String(getStartDate(), "MM", Locale.getDefault())); // NOPMD
			newCreditCard.setStartYear(ConverterUtils.date2String(getStartDate(), "yyyy", Locale.getDefault())); // NOPMD
		}
		if (isActual(getIssueNumber())) {
			newCreditCard.setIssueNumber(NumberUtils.toInt(getIssueNumber()));
		}
		newCreditCard.encrypt();
		return newCreditCard;
	}

	/**
	 * Copy orderPayment's Gateway info to <code>this</code> Order Payment.
	 * 
	 * @param orderPayment the source of Gateway info.
	 */
	public void copyTransactionFollowOnInfo(final OrderPayment orderPayment) {
		setPaymentMethod(orderPayment.getPaymentMethod());
		setReferenceId(orderPayment.getReferenceId());
		setAuthorizationCode(orderPayment.getAuthorizationCode());
		setCurrencyCode(orderPayment.getCurrencyCode());
		setEmail(orderPayment.getEmail());
		setDisplayValue(orderPayment.getDisplayValue());
		setRequestToken(orderPayment.getRequestToken());

	}

	/**
	 * Get the total amount money.
	 * 
	 * @return a <code>Money</code> object representing the total amount
	 */
	@Transient
	public Money getAmountMoney() {
		return MoneyFactory.createMoney(getAmount(), Currency.getInstance(getCurrencyCode()));
	}
	
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date that this was last modified on.
	 * 
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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

	@Override
	@Basic
	@Column(name = "PAYMENT_FOR_SUBSCRIPTIONS", nullable = true)
	public boolean isPaymentForSubscriptions() {
		return paymentForSubscriptions;
	}

	@Override
	public void setPaymentForSubscriptions(final boolean paymentForSubscriptions) {
		this.paymentForSubscriptions = paymentForSubscriptions;
	}

	/**
	 * Display info for the order payment.
	 * 
	 * @return the string info
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("paymentMethod", getPaymentMethod())
				.append("transactionType", getTransactionType())
				.append("status", getStatus())				
				.append("amount", getAmount())
				.append("currency", getCurrencyCode())
				.append("createdDate", getCreatedDate())
				.append("email", getEmail())
				.toString();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void checkDate() throws EpNonConsistentDomainFieldException {
		if (startDate == null || !isActual(expiryMonth) || !isActual(expiryYear)) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); // NOPMD
		Date expiryDateAsDate;
		try {
			expiryDateAsDate = dateFormat.parse(FIRST_DAY + DATE_SEPARATOR + expiryMonth + DATE_SEPARATOR + expiryYear);
		} catch (ParseException e) {
			throw new EpNonConsistentDomainFieldException("Expiry month or year is incorrect", e);
		}

		// check expiry date and started date
		if (!startDate.before(expiryDateAsDate)) {
			throw new EpNonConsistentDomainFieldException("Expiry date's month must be greater than start date's month.");
		}
		if (startDate.after(new Date())) {
			throw new EpNonConsistentDomainFieldException("Start date is after the current date.");
		}
	}

	/**
	 * Is string actual.
	 * 
	 * @param str string
	 * @return false if str is null or length of the str equals zero.
	 */
	protected boolean isActual(final String str) {
		return !StringUtils.isEmpty(str);
	}

	/**
	 *  Post load the {@link CreditCardNumber}. This has to be done here because
	 *  {@code CreditCardNumber} is embedded, not an entity, so JPA post-load
	 *  callback will not be called on it.
	 */
	@PostLoad
	protected void postLoad() {
		creditCardNumber.postLoad();
	}

	@Override
	public void copyLastModifiedDate(final OrderPayment payment) {
		this.setLastModifiedDate(payment.getLastModifiedDate());
	}
}