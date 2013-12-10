package com.elasticpath.common.dto.pricing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.common.dto.Dto;

/**
 * The data transfer object for the <code>PriceListDescriptor</code>.
 */
@XmlRootElement(name = PriceListDescriptorDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class PriceListDescriptorDTO implements Dto {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** The name of root element in XML representation. */
	public static final String ROOT_ELEMENT = "price_list";

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "description", required = true)
	private String description;

	@XmlElement(name = "currency_code", required = true)
	private String currencyCode;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "hidden")
	private Boolean hidden = Boolean.FALSE;

	/**
	 * Gets the GUID of the price list descriptor.
	 * 
	 * @return the GUID of the price list descriptor
	 */
	public String getGuid() {
		return this.guid;
	}

	/**
	 * Set the GUID of this price list.
	 * 
	 * @param guid the GUID of the price list
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the name of the price list descriptor.
	 * 
	 * @return name of the price list descriptor
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name of this price list.
	 * 
	 * @param name the name of the price list descriptor
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the description of the price list descriptor.
	 * 
	 * @return description of the price list
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the description for the price list.
	 * 
	 * @param description for the price list.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the currency code of the price list descriptor.
	 * 
	 * @return the currency code for the price list descriptor
	 */
	public String getCurrencyCode() {
		return this.currencyCode;
	}

	/**
	 * Set the currency code for the price list descriptor.
	 * 
	 * @param currencyCode currency code for the price list descriptor
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PriceListDescriptorDTO)) {
			return false;
		}

		PriceListDescriptorDTO other = (PriceListDescriptorDTO) obj;
		return new EqualsBuilder().append(guid, other.guid).append(name, other.name).append(description, other.description)
				.append(currencyCode, other.currencyCode).append(hidden, other.hidden).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(guid).append(name).append(description).append(currencyCode).append(hidden).toHashCode();
	}

	@Override
	public String toString() {
		return "PriceListDescriptorDTO [guid=" + guid + ", name=" + name + ", description=" + description + ", currencyCode=" + currencyCode
				+ ", hidden=" + hidden + "]";
	}

}
