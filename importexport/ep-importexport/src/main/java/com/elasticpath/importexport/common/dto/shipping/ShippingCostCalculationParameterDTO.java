package com.elasticpath.importexport.common.dto.shipping;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * ShippingCostCalculationParameterDTO.
 */
@XmlRootElement(name = ShippingCostCalculationParameterDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ShippingCostCalculationParameterDTO implements Dto {

	private static final long serialVersionUID = -358324418035652105L;

	/**
	 * root element.
	 */
	public static final String ROOT_ELEMENT = "shipping_cost_calculation_param";

	@XmlElement(name = "param_key", required = true)
	private String key;

	@XmlElement(name = "value", required = true)
	private String value;

	@XmlElement(name = "display_text", required = true)
	private String displayText;


	@XmlElement(name = "currency", required = true)
	private String currency;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(final String displayText) {
		this.displayText = displayText;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(final String currency) {
		this.currency = currency;
	}
	
}
