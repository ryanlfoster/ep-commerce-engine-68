package com.elasticpath.importexport.common.dto.inventory;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of inventory sku object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlRootElement(name = InventorySkuDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class InventorySkuDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in XML representation of product association.
	 */
	public static final String ROOT_ELEMENT = "sku";
	
	@XmlAttribute(name = "code", required = true)
	private String code;
	
	@XmlElement(name = "warehouse")
	private List<InventoryWarehouseDTO> warehouses;

	/**
	 * Gets code.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets code.
	 * 
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets warehouses.
	 * 
	 * @return the warehouses
	 */
	public List<InventoryWarehouseDTO> getWarehouses() {
		if (warehouses == null) {
			return Collections.emptyList();
		}
		return warehouses;
	}

	/**
	 * Sets warehouses.
	 * 
	 * @param warehouses the warehouses to set
	 */
	public void setWarehouses(final List<InventoryWarehouseDTO> warehouses) {
		this.warehouses = warehouses;
	}
}
