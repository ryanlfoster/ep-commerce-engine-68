package com.elasticpath.common.dto.tax;

import java.util.ArrayList;
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
 * JAXB DTO for Tax Region.
 */
@XmlRootElement(name = "region")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class TaxRegionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "region_name", required = true)
	private String regionName;

	@XmlElementWrapper(name = "values")
	@XmlElement(name = "value")
	private final List<TaxValueDTO> values = new ArrayList<TaxValueDTO>();

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRegionName() {
		return regionName;
	}

	public List<TaxValueDTO> getValues() {
		return values;
	}

	public void setRegionName(final String name) {
		this.regionName = name;
	}

}
