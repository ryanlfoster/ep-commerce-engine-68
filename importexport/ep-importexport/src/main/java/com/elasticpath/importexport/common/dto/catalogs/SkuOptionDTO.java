package com.elasticpath.importexport.common.dto.catalogs;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>ImportDto</code> interface that contains data of SkuOption.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SkuOptionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElementWrapper(name = "name")
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> nameValues;

	@XmlElementWrapper(name = "skuoptionvalues", required = true)
	@XmlElement(name = "skuoptionvalue")
	private List<SkuOptionValueDTO> skuOptionValues;

	/**
	 * Gets the brand code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the brand code.
	 *
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the display name values for different locales.
	 *
	 * @return the nameValues
	 */
	public List<DisplayValue> getNameValues() {
		if (nameValues == null) {
			return Collections.emptyList();
		}
		return nameValues;
	}

	/**
	 * Sets the display name values for different locales.
	 *
	 * @param nameValues the nameValues to set
	 */
	public void setNameValues(final List<DisplayValue> nameValues) {
		this.nameValues = nameValues;
	}

	/**
	 * Gets the skuOptionValues.
	 *
	 * @return the skuOptionValues
	 */
	public List<SkuOptionValueDTO> getSkuOptionValues() {
		if (skuOptionValues == null) {
			return Collections.emptyList();
		}
		return skuOptionValues;
	}

	/**
	 * Sets the skuOptionValues.
	 *
	 * @param skuOptionValues the skuOptionValues to set
	 */
	public void setSkuOptionValues(final List<SkuOptionValueDTO> skuOptionValues) {
		this.skuOptionValues = skuOptionValues;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("code", getCode())
			.append("nameValues", getNameValues())
			.append("skuOptionValues", getSkuOptionValues())
			.toString();
	}
}
