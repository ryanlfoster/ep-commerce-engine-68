package com.elasticpath.importexport.common.dto.warehouse;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.warehouse.WarehouseDTO;

/**
 * This element contains zero or more warehouses. This class exists mainly for XSD generation.
 */
@XmlRootElement(name = "warehouses")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "warehousesDTO", propOrder = { })
public class WarehousesDTO {

	@XmlElement(name = "warehouse")
	private final List<WarehouseDTO> warehouses = new ArrayList<WarehouseDTO>();

	public List<WarehouseDTO> getWarehouses() {
		return warehouses;
	}

}
