package com.elasticpath.importexport.common.dto.pricing;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product price object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CurrencyPriceDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "currency", required = true)
	private String currencyCode;

	@XmlElement(name = "tier")
	private List<PriceTierDTO> tierList;

	/**
	 * Gets the currency code.
	 * 
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Sets the currency code.
	 * 
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * Gets the price tier dto list.
	 * 
	 * @return the tierList
	 */
	public List<PriceTierDTO> getTierList() {
		if (tierList == null) {
			return Collections.emptyList();
		}
		return tierList;
	}

	/**
	 * Sets the price tier dto list.
	 * 
	 * @param tierList the tierList to set
	 */
	public void setTierList(final List<PriceTierDTO> tierList) {
		this.tierList = tierList;
	}

}
