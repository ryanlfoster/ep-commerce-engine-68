package com.elasticpath.importexport.common.dto.shipping.region;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * ShippingSubRegionsDTO.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ShippingSubRegionsDTO implements Dto {

	private static final long serialVersionUID = -189297868560925393L;
	
	@XmlAttribute(name = "country_code")
	private String countrycode;
	
	@XmlElement(name = "sub_region")
	private List <String> regionCodes = new ArrayList<String>();
	
	
	public String getCountryCode() {
		return countrycode;
	}

	public void setCountryCode(final String countrycode) {
		this.countrycode = countrycode;
	}

	public List<String> getRegionCodes() {
		return regionCodes;
	}

	public void setRegionCodes(final List<String> regionCodes) {
		this.regionCodes = regionCodes;
	}
	
}
