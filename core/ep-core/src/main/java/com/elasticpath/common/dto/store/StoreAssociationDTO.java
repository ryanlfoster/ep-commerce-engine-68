package com.elasticpath.common.dto.store;

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
 * Data Transfer Object for {@link com.elasticpath.domain.store.Store Store} associations.
 */

@XmlRootElement(name = StoreAssociationDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class StoreAssociationDTO implements Dto {

	private static final long serialVersionUID = 7444734247000273056L;

	/**
	 * Root element name for {@link StoreAssociationDTO}.
	 */
	public static final String ROOT_ELEMENT = "store_association";

	@XmlAttribute(name = "store_code", required = true)
	private String storeCode;
	
	@XmlElementWrapper(name = "associated_store_codes")
	@XmlElement(name = "associated_store_code")
	private List<String> associatedStoreCodes = new ArrayList<String>();

	/**
	 * @return the storeCode
	 */
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * @param storeCode the storeCode to set
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * @return the associatedStoreCodes
	 */
	public List<String> getAssociatedStoreCodes() {
		return associatedStoreCodes;
	}

	/**
	 * @param associatedStoreCodes the associatedStoreCodes to set
	 */
	public void setAssociatedStoreCodes(final List<String> associatedStoreCodes) {
		this.associatedStoreCodes = associatedStoreCodes;
	}

}
