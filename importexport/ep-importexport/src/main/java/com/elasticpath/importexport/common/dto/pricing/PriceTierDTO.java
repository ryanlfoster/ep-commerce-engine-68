package com.elasticpath.importexport.common.dto.pricing;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of price tier object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PriceTierDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "qty", required = true)
	private int qty;
	
	@XmlElement(name = "price")
	private List<PriceDTO> priceDtoList;

	/**
	 * Gets the minimum quantity of the price tier.
	 *
	 * @return the minimum quantity of the price tier
	 */
	public int getQty() {
		return qty;
	}

	/**
	 * Sets the minimum quantity for the price tier.
	 *
	 * @param qty the minimum quantity of the price tier
	 */
	public void setQty(final int qty) {
		this.qty = qty;
	}

	/**
	 * Gets the price dto list.
	 * 
	 * @return the priceDtoList
	 */
	public List<PriceDTO> getPriceDtoList() {
		if (priceDtoList == null) {
			return Collections.emptyList();
		}
		return priceDtoList;
	}

	/**
	 * Sets the price dto list.
	 * 
	 * @param priceDtoList the priceDtoList to set
	 */
	public void setPriceDtoList(final List<PriceDTO> priceDtoList) {
		this.priceDtoList = priceDtoList;
	}
}
