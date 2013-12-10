package com.elasticpath.importexport.common.dto.category;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of linked category object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class LinkedCategoryDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "virtualcatalog", required = true)
	private String virtualCatalogCode;

	@XmlElement(name = "order", required = true)
	private Integer order;

	@XmlElement(name = "excluded")
	private Boolean excluded = Boolean.FALSE;

	/**
	 * Gets the virtualCatalogCode.
	 *
	 * @return the virtualCatalogCode
	 */
	public String getVirtualCatalogCode() {
		return virtualCatalogCode;
	}

	/**
	 * Sets the virtualCatalogCode.
	 *
	 * @param virtualCatalogCode the virtualCatalogCode to set
	 */
	public void setVirtualCatalogCode(final String virtualCatalogCode) {
		this.virtualCatalogCode = virtualCatalogCode;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order the order to set
	 */
	public void setOrder(final Integer order) {
		this.order = order;
	}

	/**
	 * Sets the excluded option.
	 *
	 * @param excluded the excluded option to set
	 */
	public void setExcluded(final Boolean excluded) {
		this.excluded = excluded;
	}

	/**
	 * Gets the excluded option.
	 *
	 * @return Boolean
	 */
	public Boolean getExcluded() {
		return excluded;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("virtualCatalogCode", getVirtualCatalogCode())
			.append("order", getOrder())
			.append("excluded", getExcluded())
			.toString();
	}
}
