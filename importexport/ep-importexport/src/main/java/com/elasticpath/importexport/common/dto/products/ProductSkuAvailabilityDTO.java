package com.elasticpath.importexport.common.dto.products;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product sku availability object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductSkuAvailabilityDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "enabledate", required = true)
	private Date startDate;

	@XmlElement(name = "disabledate")
	private Date endDate;

	/**
	 * Gets the start date that this product sku will become available to customers.
	 * 
	 * @return the start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date that this product sku will become valid.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date. After the end date, the product sku will change to unavailable to customers.
	 * 
	 * @return the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date. Precondition: endDate is after the start date
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

}
