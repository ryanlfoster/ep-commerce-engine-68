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
 * The implementation of the <code>Dto</code> interface that contains data of CatalogCategoryType object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CategoryTypeDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "guid")
	private String guid;
	
	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "template", required = true)
	private String template;

	@XmlElementWrapper(name = "assignedattributes")
	@XmlElement(name = "attributekey")
	private List<String> assignedAttributes;

	/**
	 * Gets the guid.
	 *
	 * @return guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid guid to be set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * Sets the template.
	 *
	 * @param template the template to set
	 */
	public void setTemplate(final String template) {
		this.template = template;
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
			.append("name", getName())
			.append("template", getTemplate())
			.append("assignedAttributes", getAssignedAttributes())
			.toString();
	}
}
