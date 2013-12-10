package com.elasticpath.common.dto.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * The data transfer object for the <code>BaseAmount</code>.
 */
@XmlRootElement(name = PriceAdjustmentDto.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "PriceAdjustmentDTO", propOrder = { })
public class PriceAdjustmentDto implements Dto {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090928L;

	/** The name of root element in XML representation. */
	public static final String ROOT_ELEMENT = "price_adjustment";

	/** If the optional order is unspecified this will be its value. */

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "price_list_guid", required = true)
	private String priceListGuid;

	@XmlElement(name = "adjustment_amount", required = true)
	private BigDecimal adjustmentAmount;

	/**
	 * Do nothing constructor.
	 */
	public PriceAdjustmentDto() {
		// do nothing constructor
	}

	/**
	 * Constructor.
	 * 
	 * @param priceListGuid price list guid.
	 * @param adjustmentAmount adjustment amount.
	 */
	public PriceAdjustmentDto(final String priceListGuid, final BigDecimal adjustmentAmount) {
		this.priceListGuid = priceListGuid;
		this.adjustmentAmount = adjustmentAmount;
	}

	/**
	 * Gets the GUID of the PriceAdjustment.
	 * 
	 * @return the GUID of the PriceAdjustment
	 */
	public String getGuid() {
		return this.guid;
	}

	/**
	 * Set the GUID of the PriceAdjustment.
	 * 
	 * @param guid GUID of the PriceAdjustment
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return the priceListDescriptorGuid
	 */
	public String getPriceListGuid() {
		return priceListGuid;
	}

	/**
	 * @param priceListGuid the priceListGuid to set
	 */
	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}

	/**
	 * @return the adjustmentAmount
	 */
	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	/**
	 * @param adjustmentAmount the adjustmentAmount to set
	 */
	public void setAdjustmentAmount(final BigDecimal adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}
}
