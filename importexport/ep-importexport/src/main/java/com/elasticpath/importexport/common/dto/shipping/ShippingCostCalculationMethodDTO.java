package com.elasticpath.importexport.common.dto.shipping;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * ShippingCostCalculationMethodDTO.
 */
@XmlRootElement(name = ShippingCostCalculationMethodDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ShippingCostCalculationMethodDTO implements Dto {

	private static final long serialVersionUID = -8541471446613988472L;

	/**
	 * root xml element.
	 */
	public static final String ROOT_ELEMENT = "shipping_cost_calculation_method";

	@XmlAttribute(name = "type")
	private String type;
	
	@XmlElement(name = "displayText")
	private String displayText;
	
	@XmlElementWrapper(name = "shipping_cost_calculation_params")
	@XmlElement(name = ShippingCostCalculationParameterDTO.ROOT_ELEMENT)
	private List <ShippingCostCalculationParameterDTO> shippingCostCalculationParams;
	
	/**
	 * return shippingCostCalculationParams.
	 * @return shippingCostCalculationParams 
	 */
	public List<ShippingCostCalculationParameterDTO> getShippingCostCalculationParams() {
		return shippingCostCalculationParams;
	}

	/**
	 * set shippingCostCalculationParams.
	 * @param shippingCostCalculationParams shippingCostCalculationParams
	 */
	public void setShippingCostCalculationParams(final List<ShippingCostCalculationParameterDTO> shippingCostCalculationParams) {
		this.shippingCostCalculationParams = shippingCostCalculationParams;
	}

	/**
	 * return displayText.
	 * @return displayText 
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * set displayText.
	 * @param displayText displayText
	 */
	public void setDisplayText(final String displayText) {
		this.displayText = displayText;
	}

	/**
	 * get type.
	 * @return type 
	 */
	public String getType() {
		return type;
	}

	/**
	 * set type.
	 * @param type type
	 */
	public void setType(final String type) {
		this.type = type;
	}
	
}
