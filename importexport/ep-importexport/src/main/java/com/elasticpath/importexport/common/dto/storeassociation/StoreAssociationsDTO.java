package com.elasticpath.importexport.common.dto.storeassociation;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.store.StoreAssociationDTO;

/**
 * Root element for {@link com.elasticpath.common.dto.store.StoreAssociationDTO}. 
 */
@XmlRootElement(name = "store_associations")
@XmlType(name = "storesDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class StoreAssociationsDTO {

	@XmlElement(name = "store_association")
	private final List<StoreAssociationDTO> storeAssociations = new ArrayList<StoreAssociationDTO>();

	public List<StoreAssociationDTO> getStoreAssociations() {
		return storeAssociations;
	}
}
