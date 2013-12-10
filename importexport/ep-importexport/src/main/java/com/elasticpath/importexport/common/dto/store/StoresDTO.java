package com.elasticpath.importexport.common.dto.store;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.store.StoreDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of StoreDTOs.
 */
@XmlRootElement(name = "stores")
@XmlType(name = "storesDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class StoresDTO {

	@XmlElement(name = "store")
	private final List<StoreDTO> stores = new ArrayList<StoreDTO>();

	public List<StoreDTO> getStores() {
		return stores;
	}

}
