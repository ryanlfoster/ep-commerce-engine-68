package com.elasticpath.importexport.common.dto.contentspace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * An instance of a content space dto.
 */
@XmlRootElement(name = ContentSpaceDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class ContentSpaceDTO implements Dto {

	/**
	 * generated.
	 */
	private static final long serialVersionUID = 7355988433095865185L;

	@XmlElement(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;
	
	@XmlElement(name = "description")
	private String description;
	
	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "contentspace";

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
}
