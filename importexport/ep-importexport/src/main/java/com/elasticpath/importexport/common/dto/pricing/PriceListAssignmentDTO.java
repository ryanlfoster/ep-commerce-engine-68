package com.elasticpath.importexport.common.dto.pricing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;

/**
 * The data transfer object for the <code>PriceListAssignments</code>.
 */
@XmlRootElement(name = PriceListAssignmentDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class PriceListAssignmentDTO implements Dto {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090814L;

	/** The name of root element in XML representation. */
	public static final String ROOT_ELEMENT = "price_list_assignment";

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "priority", required = true)
	private int priority;

	@XmlElement(name = "catalog_guid", required = true)
	private String catalogGuid;

	@XmlElement(name = "price_list_guid", required = true)
	private String priceListGuid;

	@XmlElement(name = "selling_context")
	private SellingContextDTO sellingContext;
	
	/**
	 * Get price list assignment guid.
	 * 
	 * @return PLA guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Set price list assignment guid.
	 * 
	 * @param guid to set.
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Get price list assignment name.
	 * 
	 * @return price list assignment name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set price list assignment name.
	 * 
	 * @param name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get price list assignment description.
	 * 
	 * @return price list assignment description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set price list assignment description.
	 * 
	 * @param description to set.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Get price list assignment priority.
	 * 
	 * @return price list assignment priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set price list assignment priority.
	 * 
	 * @param priority to set.
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * Get catalog guid for price list assignment.
	 * 
	 * @return catalog guid.
	 */
	public String getCatalogGuid() {
		return catalogGuid;
	}

	/**
	 * Set catalog name for price list assignment.
	 * 
	 * @param catalogGuid to set.
	 */
	public void setCatalogGuid(final String catalogGuid) {
		this.catalogGuid = catalogGuid;
	}

	/**
	 * Get price list guid for price list assignment.
	 * 
	 * @return price list guid
	 */
	public String getPriceListGuid() {
		return priceListGuid;
	}

	/**
	 * Set price list guid for price list assignment.
	 * 
	 * @param priceListGuid price list guid
	 */
	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}
	
	public SellingContextDTO getSellingContext() {
		return sellingContext;
	}

	public void setSellingContext(final SellingContextDTO sellingContext) {
		this.sellingContext = sellingContext;
	}
}
