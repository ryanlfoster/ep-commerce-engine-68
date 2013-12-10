package com.elasticpath.importexport.common.dto.pricing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * This element contains zero or more price_list elements. This class exists mainly for XSD generation.
 */
@XmlRootElement(name = "price_lists")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "priceListsDTO", propOrder = { })
public class PriceListsDTO {

	@XmlElement(name = "price_list")
	private final List<PriceListDescriptorDTO> priceLists = new ArrayList<PriceListDescriptorDTO>();

	public List<PriceListDescriptorDTO> getPriceLists() {
		return priceLists;
	}
}
