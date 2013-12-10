package com.elasticpath.importexport.common.dto.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of sku option object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SkuOptionDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String code;
	
	@XmlElement(name = "skuoptionvalue", required = true)
	private String skuOptionValue;

	/**
	 * Gets the sku option key.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the sku option key.
	 * 
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the sku option value.
	 * 
	 * @return the skuOptionValue
	 */
	public String getSkuOptionValue() {
		return skuOptionValue;
	}

	/**
	 * Sets the sku option value.
	 * 
	 * @param skuOptionValue the skuOptionValue to set
	 */
	public void setSkuOptionValue(final String skuOptionValue) {
		this.skuOptionValue = skuOptionValue;
	}

}
