package com.elasticpath.importexport.common.dto.customer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of AttributeDTOs.
 */
@XmlRootElement(name = "customerprofile_attributes")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "customerProfileAttributesDTO", propOrder = { })
public class CustomerProfileAttributesDTO {

	@XmlElement(name = "attribute")
	private final List<AttributeDTO> customerProfileAttributes = new ArrayList<AttributeDTO>();

	public List<AttributeDTO> getCustomerProfileAttributes() {
		return customerProfileAttributes;
	}

}