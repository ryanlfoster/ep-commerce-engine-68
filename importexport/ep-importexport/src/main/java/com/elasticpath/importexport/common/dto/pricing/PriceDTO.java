package com.elasticpath.importexport.common.dto.pricing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * The implementation of the <code>Dto</code> interface that contains data of price object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PriceDTO {
	
	@XmlAttribute(name = "alias", required = true)
	private PriceAlias alias;
	
	@XmlValue
	private String value;

	/**
	 * Gets the alias.
	 * 
	 * @return the alias
	 */
	public PriceAlias getAlias() {
		return alias;
	}

	/**
	 * Sets the alias.
	 * 
	 * @param alias the alias to set
	 */
	public void setAlias(final PriceAlias alias) {
		this.alias = alias;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public BigDecimal getValue() {
		if (value == null || "".equals(value)) {
			return null;
		}
		return new BigDecimal(value);
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value to set
	 */
	public void setValue(final BigDecimal value) {
		if (value == null) {
			this.value = null;
			return;
		}
		
		this.value = value.toString();
	}

}
