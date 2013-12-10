package com.elasticpath.importexport.common.dto.catalogs;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of Catalog Product Type MultiSku.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class MultiSkuDTO implements Dto {


	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "assignedskuoptions")
	@XmlElement(name = "skuoption")
	private List<String> assignedSkuOptions;

	@XmlElementWrapper(name = "assignedattributes")
	@XmlElement(name = "attributekey")
	private List<String> assignedAttributes;

	/**
	 * Gets the assignedSkuOptions.
	 *
	 * @return the assignedSkuOptions
	 */
	public List<String> getAssignedSkuOptions() {
		if (assignedSkuOptions == null) {
			return Collections.emptyList();
		}
		return assignedSkuOptions;
	}

	/**
	 * Sets the assignedSkuOptions.
	 *
	 * @param assignedSkuOptions the assignedSkuOptions to set
	 */
	public void setAssignedSkuOptions(final List<String> assignedSkuOptions) {
		this.assignedSkuOptions = assignedSkuOptions;
	}

	/**
	 * Gets the assignedAttributes.
	 *
	 * @return the assignedAttributes
	 */
	public List<String> getAssignedAttributes() {
		if (assignedAttributes == null) {
			return Collections.emptyList();
		}
		return assignedAttributes;
	}

	/**
	 * Sets the assignedAttributes.
	 *
	 * @param assignedAttributes the assignedAttributes to set
	 */
	public void setAssignedAttributes(final List<String> assignedAttributes) {
		this.assignedAttributes = assignedAttributes;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("assignedSkuOptions", getAssignedSkuOptions())
			.append("assignedAttributes", getAssignedAttributes())
			.toString();
	}

}
