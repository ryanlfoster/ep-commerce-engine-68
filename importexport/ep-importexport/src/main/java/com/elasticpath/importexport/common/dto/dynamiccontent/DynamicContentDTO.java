package com.elasticpath.importexport.common.dto.dynamiccontent;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * An instance of dynamic content.
 */
@XmlRootElement(name = DynamicContentDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class DynamicContentDTO implements Dto {

	private static final long serialVersionUID = -7627240237204084766L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "dynamiccontent";

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "description")
	private String description;

	// content wrapper id
	@XmlElement(name = "contentwrapperid", required = true)
	private String contentWrapperId;

	// parameter values
	@XmlElementWrapper(name = "parametervalues")
	@XmlElement(name = "parametervalue")
	private List<ParameterValueDTO> parameterValues;

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
	 * @return the contentWrapperId
	 */
	public String getContentWrapperId() {
		return contentWrapperId;
	}

	/**
	 * @param contentWrapperId the contentWrapperId to set
	 */
	public void setContentWrapperId(final String contentWrapperId) {
		this.contentWrapperId = contentWrapperId;
	}

	/**
	 * @return the parameterValues
	 */
	public List<ParameterValueDTO> getParameterValues() {
		if (parameterValues == null) {
			return Collections.emptyList();
		}
		return parameterValues;
	}

	/**
	 * @param parameterValues the parameterValues to set
	 */
	public void setParameterValues(final List<ParameterValueDTO> parameterValues) {
		this.parameterValues = parameterValues;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("name", getName())
				.append("guid", getGuid())
				.append("description", getDescription())
				.append("contentWrapperId", getContentWrapperId())
				.append("parameterValues", getParameterValues())
				.toString();
	}


}