package com.elasticpath.importexport.common.dto.inventory;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of inventory warehouse object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class InventoryWarehouseDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String code;
	
	@XmlElement(name = "onhand", required = true)
	private Integer onHand;
	
	@XmlElement(name = "available", required = true)
	private Integer avaliable;
	
	@XmlElement(name = "allocated", required = true)
	private Integer allocated;
	
	@XmlElement(name = "reserved", required = true)
	private Integer reserved;
	
	@XmlElement(name = "reordermin", required = true)
	private Integer reorderMin;
	
	@XmlElement(name = "reorderqty", required = true)
	private Integer reorderQty;
	
	@XmlElement(name = "expectedrestockdate")
	private Date expectedRestockDate;

	/**
	 * Gets code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets code.
	 *
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets onHand.
	 *
	 * @return the onHand
	 */
	public Integer getOnHand() {
		return onHand;
	}

	/**
	 * Sets onHand.
	 *
	 * @param onHand the onHand to set
	 */
	public void setOnHand(final Integer onHand) {
		this.onHand = onHand;
	}

	/**
	 * Gets avaliable.
	 *
	 * @return the avaliable
	 */
	public Integer getAvaliable() {
		return avaliable;
	}

	/**
	 * Sets avaliable.
	 *
	 * @param avaliable the avaliable to set
	 */
	public void setAvaliable(final Integer avaliable) {
		this.avaliable = avaliable;
	}

	/**
	 * Gets allocated.
	 *
	 * @return the allocated
	 */
	public Integer getAllocated() {
		return allocated;
	}

	/**
	 * Sets allocated.
	 *
	 * @param allocated the allocated to set
	 */
	public void setAllocated(final Integer allocated) {
		this.allocated = allocated;
	}

	/**
	 * Gets reserved.
	 *
	 * @return the reserved
	 */
	public Integer getReserved() {
		return reserved;
	}

	/**
	 * Sets reserved.
	 *
	 * @param reserved the reserved to set
	 */
	public void setReserved(final Integer reserved) {
		this.reserved = reserved;
	}

	/**
	 * Gets reorderMin.
	 *
	 * @return the reorderMin
	 */
	public Integer getReorderMin() {
		return reorderMin;
	}

	/**
	 * Sets reorderMin.
	 *
	 * @param reorderMin the reorderMin to set
	 */
	public void setReorderMin(final Integer reorderMin) {
		this.reorderMin = reorderMin;
	}

	/**
	 * Gets reorderQty.
	 *
	 * @return the reorderQty
	 */
	public Integer getReorderQty() {
		return reorderQty;
	}

	/**
	 * Sets reorderQty.
	 *
	 * @param reorderQty the reorderQty to set
	 */
	public void setReorderQty(final Integer reorderQty) {
		this.reorderQty = reorderQty;
	}

	/**
	 * Gets expectedRestockDate.
	 *
	 * @return the expectedRestockDate
	 */
	public Date getExpectedRestockDate() {
		return expectedRestockDate;
	}

	/**
	 * Sets expectedRestockDate.
	 *
	 * @param expectedRestockDate the expectedRestockDate to set
	 */
	public void setExpectedRestockDate(final Date expectedRestockDate) {
		this.expectedRestockDate = expectedRestockDate;
	}	
}
