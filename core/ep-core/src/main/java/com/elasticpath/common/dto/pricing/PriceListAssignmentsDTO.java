package com.elasticpath.common.dto.pricing;

import java.util.Currency;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;


/**
 * The data transfer object for the <code>PriceListAssignments</code>.
 */
@XmlRootElement(name = PriceListAssignmentsDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class PriceListAssignmentsDTO implements Dto {
	
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
	
	@XmlElement(name = "description", required = true)
	private String description;
	
	@XmlElement(name = "priority", required = true)
	private int priority;
	
	@XmlElement(name = "catalog_name", required = true)
	private String catalogName;
	
	@XmlElement(name = "catalog_guid", required = true)
	private String catalogGuid;

	@XmlElement(name = "price_list_name", required = true)
	private String priceListName;

	@XmlElement(name = "price_list_guid", required = true)
	private String priceListGuid;
	
	@XmlElement(name = "start_date", required = true)
	private Date startDate;
	
	@XmlElement(name = "end_date", required = true)
	private Date endDate;
	
	@XmlElement(name = "hidden")
	private Boolean hidden = Boolean.FALSE;

	private Currency priceListCurrency;
	
	/**
	 * Get price list assignment guid.
	 * @return PLA guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Set price list assignment guid.
	 * @param guid to set.
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	/**
	 * Get price list assignment name.
	 * @return price list assignment name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set price list assignment name.
	 * @param name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get price list assignment description.
	 * @return price list assignment description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set price list assignment description.
	 * @param description to set.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Get price list assignment priority.
	 * @return price list assignment priority.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Set price list assignment priority.
	 * @param priority to set.
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * Get catalog name for price list assignment.
	 * @return catalog name.
	 */
	public String getCatalogName() {
		return catalogName;
	}

	/**
	 * Set catalog name for price list assignment.
	 * @param catalogName catalog name to set.
	 */	
	public void setCatalogName(final String catalogName) {
		this.catalogName = catalogName;
	}

	/**
	 * Get catalog guid for price list assignment.
	 * @return catalog guid.
	 */	
	public String getCatalogGuid() {
		return catalogGuid;
	}

	/**
	 * Set catalog name for price list assignment.
	 * @param catalogGuid to set.
	 */
	public void setCatalogGuid(final String catalogGuid) {
		this.catalogGuid = catalogGuid;
	}

	/**
	 * Get price list name for price list assignment.
	 * @return price list name
	 */
	public String getPriceListName() {
		return priceListName;
	}

	/**
	 * Set price list name for price list assignment.
	 * @param priceListName price list name
	 */	
	public void setPriceListName(final String priceListName) {
		this.priceListName = priceListName;
	}

	/**
	 * Get price list guid for price list assignment.
	 * @return price list guid
	 */	
	public String getPriceListGuid() {
		return priceListGuid;
	}

	/**
	 * Set price list guid for price list assignment.
	 * @param  priceListGuid price list guid
	 */	
	public void setPriceListGuid(final String priceListGuid) {
		this.priceListGuid = priceListGuid;
	}

	/**
	 * Get price list assignment start date.
	 * @return start date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set price list assignment start date.
	 * @param startDate date to set.
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get price list assignment end date.
	 * @return end date
	 */	
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Set price list assignment end date.
	 * @param endDate date to set
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return price list currency
	 */
	public Currency getPriceListCurrency() {
		return priceListCurrency;
	}

	/**
	 * Sets price list currency.
	 * @param priceListCurrency price list currency
	 */
	public void setPriceListCurrency(final Currency priceListCurrency) {
		this.priceListCurrency = priceListCurrency;
	}

	/**
	 * @param hidden the hidden to set
	 */
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * @return the hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	
}
