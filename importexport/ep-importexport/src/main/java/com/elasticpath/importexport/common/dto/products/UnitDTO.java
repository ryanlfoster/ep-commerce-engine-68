package com.elasticpath.importexport.common.dto.products;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * This class is holder for various shippable parameters (e.g weigh, height, width, length).
 * <p>
 * It designed for JAXB to working with xml representation of data  
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UnitDTO {

	@XmlAttribute(name = "units")
	private String units;

	@XmlValue
	private BigDecimal value;
	
	/**
	 * Constructs the empty object.
	 */
	public UnitDTO() {
		//empty constructor
	}

	/**
	 * Constructs the object with given parameters.
	 * 
	 * @param units the units
	 * @param value the value
	 */
	public UnitDTO(final String units, final BigDecimal value) {
		super();
		this.value = BigDecimal.ZERO;
		this.units = units;
		if (value != null) {
			this.value = value;
		}
	}

	/**
	 * Gets the units.
	 * 
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * Sets the units.
	 * 
	 * @param units the units to set
	 */
	public void setUnits(final String units) {
		this.units = units;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value to set
	 */
	public void setValue(final BigDecimal value) {
		this.value = value;
	}
}
