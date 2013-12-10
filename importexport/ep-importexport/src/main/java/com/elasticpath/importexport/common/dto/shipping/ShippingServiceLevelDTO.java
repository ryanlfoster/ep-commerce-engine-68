package com.elasticpath.importexport.common.dto.shipping;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * An instance of a ShippingServiceLevelDTO.
 */
@XmlRootElement(name = ShippingServiceLevelDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ShippingServiceLevelDTO implements Dto {
	
	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "shipping_service_level";

	private static final long serialVersionUID = 5897718318454540640L;
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;
	
	@XmlElement(name = "shipping_region")
	private String shippingRegionName;
	
	@XmlElement(name = "shipping_cost_calculation_method")
	private ShippingCostCalculationMethodDTO shippingCostCalculationMethodDto; 
	
	@XmlElement(name = "storecode")
	private String storeCode;

	@XmlElement(name = "carrier")
	private String carrier;
	
	@XmlElementWrapper(name = "localized_names")
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> nameValues;
	
	/**
	 * Gets the display name values for different locales.
	 * 
	 * @return the nameValues
	 */
	public List<DisplayValue> getNameValues() {
		if (nameValues == null) {
			nameValues = Collections.emptyList();
		}
		return nameValues;
	}

	/**
	 * gets the display value for the given locale.
	 * @param locale locale
	 * @return the value for the given locale or an empty String.
	 */
	public String getLocalizedDisplayValue(final Locale locale) {
		for (DisplayValue value : nameValues) {
			if (StringUtils.equals(value.getLanguage(), locale.getLanguage())) {
				return value.getValue();
			}
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * Sets the display name values for different locales.
	 * 
	 * @param nameValues the nameValues to set
	 */
	public void setNameValues(final List<DisplayValue> nameValues) {
		this.nameValues = nameValues;
	}
	
	public String getShippingRegionName() {
		return shippingRegionName;
	}

	public void setShippingRegionName(final String shippingRegionName) {
		this.shippingRegionName = shippingRegionName;
	}

	@XmlElement(name = "code")
	private String code;

	@XmlElement(name = "active")
	private boolean enabled;
	
	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	public ShippingCostCalculationMethodDTO getShippingCostCalculationMethodDto() {
		return shippingCostCalculationMethodDto;
	}

	public void setShippingCostCalculationMethodDto(
			final ShippingCostCalculationMethodDTO shippingCostCalculationMethodDto) {
		this.shippingCostCalculationMethodDto = shippingCostCalculationMethodDto;
	}
	
	
}
