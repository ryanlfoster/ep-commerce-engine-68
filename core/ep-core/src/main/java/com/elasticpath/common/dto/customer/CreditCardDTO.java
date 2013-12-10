package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO of a customer's credit card. Remember that the card # may not actually be in here.
 */
@XmlRootElement(name = CreditCardDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class CreditCardDTO implements Comparable<CreditCardDTO>, Dto {

	private static final long serialVersionUID = 1L;

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "card";

	@XmlAttribute(name = "type", required = true)
	private String cardType;

	@XmlAttribute(name = "expiry_year", required = true)
	private String expiryYear;

	@XmlAttribute(name = "expiry_month", required = true)
	private String expiryMonth;

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "holder_name", required = true)
	private String cardHolderName;

	@XmlElement(name = "card_number", required = true)
	private String cardNumber;

	@XmlElement(name = "start_year")
	private String startYear;

	@XmlElement(name = "start_month")
	private String startMonth;

	@XmlElement(name = "issue_number")
	private Integer issueNumber;

	@XmlElement(name = "default_card", required = true)
	private boolean defaultCard;

	@XmlElement(name = "billing_address_guid")
	private String billingAddressGuid;

	public String getCardType() {
		return cardType;
	}

	public void setCardType(final String cardType) {
		this.cardType = cardType;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public void setExpiryYear(final String expiryYear) {
		this.expiryYear = expiryYear;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public void setExpiryMonth(final String expiryMonth) {
		this.expiryMonth = expiryMonth;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(final String holderName) {
		this.cardHolderName = holderName;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(final String startYear) {
		this.startYear = startYear;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(final String startMonth) {
		this.startMonth = startMonth;
	}

	public Integer getIssueNumber() {
		return issueNumber;
	}

	public void setIssueNumber(final Integer issueNumber) {
		this.issueNumber = issueNumber;
	}

	public boolean isDefaultCard() {
		return defaultCard;
	}

	public void setDefaultCard(final boolean defaultCard) {
		this.defaultCard = defaultCard;
	}

	public String getBillingAddressGuid() {
		return billingAddressGuid;
	}

	public void setBillingAddressGuid(final String billingAddressGuid) {
		this.billingAddressGuid = billingAddressGuid;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	/**
	 * Compares CreditCardDTOs via their GUIDs.
	 * 
	 * @param otherCreditCardDTO The other CreditCardDTO.
	 * @return Negative int if this DTO's GUID is less than the given DTO's GUID, zero if they are the same, etc.
	 */
	public int compareTo(final CreditCardDTO otherCreditCardDTO) {
		if (otherCreditCardDTO == null) {
			throw new IllegalArgumentException();
		}
		return getGuid().compareTo(otherCreditCardDTO.getGuid());
	}

}
