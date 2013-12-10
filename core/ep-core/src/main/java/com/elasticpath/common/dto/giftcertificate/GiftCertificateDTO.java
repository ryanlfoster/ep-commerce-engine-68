package com.elasticpath.common.dto.giftcertificate;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Data Transfer Object for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO}.
 */
@XmlRootElement(name = GiftCertificateDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class GiftCertificateDTO implements Dto {
	
	/** Root element name for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO}. */
	public static final String ROOT_ELEMENT = "gift_certificate";
	
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "code")
	private String code;

	@XmlElement(name = "creation_date", required = true)
	private Date creationDate;

	@XmlElement(name = "last_modified", required = true)
	private Date lastModifiedDate;
	
	@XmlElement(name = "recipient_name")
	private String recipientName;
	
	@XmlElement(name = "recipient_email")
	private String recipientEmail;
	
	@XmlElement(name = "sender_name")
	private String senderName;
	
	@XmlElement(name = "message")
	private String message;
	
	@XmlElement(name = "theme")
	private String theme;
	
	@XmlElement(name = "purchase_amount")
	private BigDecimal purchaseAmount;
	
	@XmlElement(name = "store_code", required = true)
	private String storeCode;
	
	@XmlElement(name = "currency_code")
	private String currencyCode;
	
	@XmlElement(name = "purchaser_guid")
	private String purchaserGuid;
	
	@XmlElement(name = "order_guid")
	private String orderGuid;

	@XmlElementWrapper(name = "gift_certificate_transactions")
	@XmlElement(name = "gift_certificate_transaction")
	private List<GiftCertificateTransactionDTO> giftCertificateTransactions;
				 
	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * @return the creationDate
	 */
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
	 * @return the recipientName
	 */
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
	 * @return the recipientEmail
	 */
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
	 * @return the senderName
	 */
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
	 * @return the currencyCode
	 */
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
	 * @return the purchaserGuid
	 */
	public String getPurchaserGuid() {
		return purchaserGuid;
	}

	/**
	 * @param purchaserGuid the purchaserGuid to set
	 */
	public void setPurchaserGuid(final String purchaserGuid) {
		this.purchaserGuid = purchaserGuid;
	}

	/**
	 * @return the orderGuid
	 */
	public String getOrderGuid() {
		return orderGuid;
	}

	/**
	 * @param orderGuid the orderGuid to set
	 */
	public void setOrderGuid(final String orderGuid) {
		this.orderGuid = orderGuid;
	}

	/**
	 * @return the giftCertificateTransactions
	 */
	public List<GiftCertificateTransactionDTO> getGiftCertificateTransactions() {
		return giftCertificateTransactions;
	}

	/**
	 * @param giftCertificateTransactions the giftCertificateTransactions to set
	 */
	public void setGiftCertificateTransactions(
			final List<GiftCertificateTransactionDTO> giftCertificateTransactions) {
		this.giftCertificateTransactions = giftCertificateTransactions;
	}
		
}
