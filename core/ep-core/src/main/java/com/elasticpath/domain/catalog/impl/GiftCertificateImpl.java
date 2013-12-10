/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.security.CardEncrypter;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * This is the default implementation of the <code>GiftCertificate</code>.
 */

@Entity
@Table(name = GiftCertificateImpl.TABLE_NAME)
@DataCache(enabled = false)
public class GiftCertificateImpl extends AbstractLegacyEntityImpl implements GiftCertificate, DatabaseLastModifiedDate {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String giftCertificateCode;

	private Date creationDate;

	private Date lastModifiedDate;

	private Customer purchaser;

	private String recipientName = StringUtils.EMPTY;

	private String recipientEmail;

	private String senderName = StringUtils.EMPTY;

	private String message;

	private String theme;

	private BigDecimal purchaseAmount;

	private String currencyCode;

	private long uidPk;

	private Store store;
	
	private GiftCertificateService giftCertificateService;
	
	private String orderGuid;

	private String guid;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TGIFTCERTIFICATE";
	
	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
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

	/**
	 * Get the gift certificate service.
	 * 
	 * @return GiftCertificateService
	 */
	@Transient
	private GiftCertificateService getGiftCertificateService() {
		if (giftCertificateService == null) {
			giftCertificateService = getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
		}
		return giftCertificateService;
	}

	/**
	 * @return the giftCertificateCode
	 */
	@Basic
	@Column(name = "GIFT_CERTIFICATE_CODE")
	public String getGiftCertificateCode() {
		return giftCertificateCode;
	}

	/**
	 * @param giftCertificateCode the giftCertificateCode to set
	 */
	public void setGiftCertificateCode(final String giftCertificateCode) {
		this.giftCertificateCode = giftCertificateCode;
	}

	/**
	 * @return the creationDate
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the lastModifiedDate
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE")
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the purchaser
	 */
	@ManyToOne(targetEntity = CustomerImpl.class)
	@JoinColumn(name = "CUSTOMER_UID")
	public Customer getPurchaser() {
		return this.purchaser;
	}

	/**
	 * @param purchaser the purchaser to set
	 */
	public void setPurchaser(final Customer purchaser) {
		this.purchaser = purchaser;
	}

	/**
	 * <p>This implementation is annotated with OpenJPA annotations for persistence purposes.
	 * Of particular note are the two annotations:
	 * <ul><li>{@code @Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")}</li>
	 * <li>{@code @Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")}</li></ul>
	 * which ensure that even if the underlying database (e.g. Oracle) converts empty strings to null values,
	 * JPA won't complain.</p>
	 * @return the recipientName
	 */
	@Basic
	@Column(name = "RECIPIENT_NAME")
	@Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")
	@Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")
	public String getRecipientName() {
		return recipientName;
	}

	/**
	 * @param recipientName the recipientName to set
	 */
	public void setRecipientName(final String recipientName) {
		this.recipientName = recipientName;
	}

	/**
	 * <p>This implementation is annotated with OpenJPA annotations for persistence purposes.
	 * Of particular note are the two annotations:
	 * <ul><li>{@code @Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")}</li>
	 * <li>{@code @Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")}</li></ul>
	 * which ensure that even if the underlying database (e.g. Oracle) converts empty strings to null values,
	 * JPA won't complain.</p>
	 * @return the senderName
	 */
	@Basic
	@Column(name = "SENDER_NAME")
	@Externalizer("com.elasticpath.persistence.api.StringExternalizer.toExternalForm")
	@Factory("com.elasticpath.persistence.api.StringExternalizer.toInternalForm")
	public String getSenderName() {
		return senderName;
	}

	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(final String senderName) {
		this.senderName = senderName;
	}

	/**
	 * @return the message
	 */
	@Basic
	@Column(name = "MESSAGE")
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the theme
	 */
	@Basic
	@Column(name = "THEME")
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(final String theme) {
		this.theme = theme;
	}

	/**
	 * @return the purchaseAmount
	 */
	@Basic
	@Column(name = "PURCHASE_AMOUNT")
	public BigDecimal getPurchaseAmount() {
		return purchaseAmount;
	}

	/**
	 * @param purchaseAmount the purchaseAmount to set
	 */
	public void setPurchaseAmount(final BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	/**
	 * @return the currencyCode
	 */
	@Basic
	@Column(name = "CURRENCY")
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the recipientEmail
	 */
	@Basic
	@Column(name = "RECEPIENT_EMAIL")
	public String getRecipientEmail() {
		return recipientEmail;
	}

	/**
	 * @param recipientEmail the recipientEmail to set
	 */
	public void setRecipientEmail(final String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	/**
	 * Get the balance money of gift certificate.
	 * 
	 * @return a <code>Money</code> object representing the balance money
	 */
	@Transient
	public Money getPurchaseAmountMoney() {
		return MoneyFactory.createMoney(getPurchaseAmount(), getGiftCertificateCurrency());
	}

	/**
	 * Get the currency.
	 * 
	 * @return a <code>Currency</code> object
	 */
	@Transient
	private Currency getGiftCertificateCurrency() {
		return Currency.getInstance(this.getCurrencyCode());
	}

	/**
	 * Equals method.
	 * 
	 * @param obj target object
	 * @return true when their code matches.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (!(obj instanceof GiftCertificate)) {
			return false;
		}

		GiftCertificate otherCertificate = (GiftCertificate) obj;

		if (isPersisted() && otherCertificate.isPersisted()) {
			// Equality of persistent instances is determined by uidPk
			return getUidPk() == otherCertificate.getUidPk();
		}

		
		return ObjectUtils.equals(this.getGuid(), ((GiftCertificate) obj).getGuid());
	}

	/**
	 * Hash code method.
	 * 
	 * @return the hash code of the gift certificate code
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.getGuid());

	}

	/**
	 * Set the default values.
	 */
	@Override
	public void initialize() {
		super.initialize();
		setPurchaseAmount(BigDecimal.ZERO);
	}

	/**
	 * @return a gift certificate code
	 */
	public String displayGiftCertificateCode() {
		final String gcCode = getGiftCertificateCode();
		if (gcCode == null || gcCode.length() <= 0) {
			throw new EpDomainException("Gift Certificate Code is not set or not correct length.");
		}
		return gcCode;
	}
	
	/**
	 * @return a masked gift certificate code
	 */
	public String displayMaskedGiftCertificateCode() {
		return getCardEncrypter().mask(getGiftCertificateCode());
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
	 * Gets the {@link Store} this object belongs to.
	 * 
	 * @return the {@link Store}
	 */
	//  WARNING!  Do NOT add a CascadeType.MERGE / CascadeType.REFRESH to this relationship or you will add a race
	//  condition to the storefront.
	@ManyToOne(targetEntity = StoreImpl.class, optional = false)
	@JoinColumn(name = "STORE_UID", nullable = false)
	@ForeignKey
	public Store getStore() {
		return store;
	}

	/**
	 * Sets the {@link Store} this object belongs to.
	 * 
	 * @param store the {@link Store} to set
	 */
	public void setStore(final Store store) {
		this.store = store;
	}

	/**
	 * Retrieves and returns the balance money of gift certificate.
	 * 
	 * @return a <code>Money</code> object representing the balance money.
	 */
	@Transient
	public Money retrieveBalanceMoney() {
		final BigDecimal balance = getGiftCertificateService().getBalance(this);
		return MoneyFactory.createMoney(balance, getGiftCertificateCurrency());
	}
	
	/**
	 * Gets Card Encrypter. This gets the bean from the factory each time to avoid serialization
	 * of the encrypter field during remoting.
	 * 
	 * @return <code>CardEncrypter</code>
	 */
	@Transient
	protected CardEncrypter getCardEncrypter() {
		return getBean(ContextIdNames.CARD_ENCRYPTER);
	}

	@Override
	public void setOrderGuid(final String orderGuid) {
		this.orderGuid = orderGuid;
	}

	@Override
	@Basic
	@Column(name = "ORDER_GUID")
	public String getOrderGuid() {
		return orderGuid;
	}
}
