package com.elasticpath.importexport.common.dto.promotion.cart;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;

/**
 * Contains mapping between XML and Promotion state part of Promotion <code>Rule</code> domain object. Designed for JAXB.
 */
@XmlRootElement(name = PromotionDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "promotion")
public class PromotionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/** The name of root element in XML representation. */
	public static final String ROOT_ELEMENT = "promotion";

	@XmlAttribute(name = "type", required = true)
	private String type;

	/** Choice of either store_code or catalog_code. */

	@XmlElement(name = "store_code")
	private String storeCode;

	@XmlElement(name = "catalog_code")
	private String catalogCode;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElementWrapper(name = "displayName", required = false)
	@XmlElement(name = "value")
	private List<DisplayValue> displayNames;

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "availability", required = true)
	private AvailabilityDTO availability;

	@XmlElementWrapper(name = "actions")
	@XmlElement(name = "action", required = true)
	private List<ActionDTO> actions;

	@XmlElement(name = "selling_context")
	private SellingContextDTO sellingContext;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the display name values for different locales.
	 * 
	 * @return the nameValues
	 */
	public List<DisplayValue> getDisplayNames() {
		if (displayNames == null) {
			return Collections.emptyList();
		}
		return displayNames;
	}

	/**
	 * Sets the display name values for different locales.
	 * 
	 * @param nameValues the nameValues to set
	 */
	public void setDisplayNames(final List<DisplayValue> nameValues) {
		this.displayNames = nameValues;
	}

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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the availability
	 */
	public AvailabilityDTO getAvailability() {
		return availability;
	}

	/**
	 * @param availability the availability to set
	 */
	public void setAvailability(final AvailabilityDTO availability) {
		this.availability = availability;
	}

	/**
	 * Gets the list of actions.
	 * 
	 * @return the actions
	 */
	public List<ActionDTO> getActions() {
		return actions;
	}

	/**
	 * Sets the list of actions.
	 * 
	 * @param actions the actions to set
	 */
	public void setActions(final List<ActionDTO> actions) {
		this.actions = actions;
	}

	/**
	 * Gets promotion type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets promotion type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets promotion code.
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets promotion code.
	 * 
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets catalog code.
	 * 
	 * @return the catalogCode
	 */
	public String getCatalogCode() {
		return catalogCode;
	}

	/**
	 * Sets catalog code.
	 * 
	 * @param catalogCode the catalogCode to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 * Returns selling context.
	 * 
	 * @return - selling context.
	 */
	public SellingContextDTO getSellingContext() {
		return sellingContext;
	}

	/**
	 * Sets selling context.
	 * 
	 * @param sellingContext - selling context to be set.
	 */
	public void setSellingContext(final SellingContextDTO sellingContext) {
		this.sellingContext = sellingContext;
	}
}
