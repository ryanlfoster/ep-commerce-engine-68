package com.elasticpath.importexport.common.dto.shipping.region;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * ShippingRegionDTO.
 */
@XmlRootElement(name = ShippingRegionDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ShippingRegionDTO implements Dto {

	private static final long serialVersionUID = 974095747599127952L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "shipping_region";
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlElement(name = "region")
	private List <ShippingSubRegionsDTO> shippingSubRegions = new ArrayList<ShippingSubRegionsDTO>();

	public List<ShippingSubRegionsDTO> getShippingSubRegions() {
		return shippingSubRegions;
	}

	public void setShippingSubRegions(final List<ShippingSubRegionsDTO> shippingSubRegions) {
		this.shippingSubRegions = shippingSubRegions;
	}

	/**
	 * get guid.
	 * @return guid
	 */ 
	public String getGuid() {
		return guid;
	}

	/**
	 * set guid.
	 * @param guid guid
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * get name.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * set name.
	 * @param name name
	 */
	public void setName(final String name) {
		this.name = name;
	}

}
