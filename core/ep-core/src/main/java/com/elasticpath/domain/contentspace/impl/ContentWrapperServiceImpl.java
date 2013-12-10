package com.elasticpath.domain.contentspace.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.domain.contentspace.ContentWrapperService;

/**
 * Default implementation of {@link ContentWrapperService}.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ContentWrapperServiceImpl implements ContentWrapperService {

	private static final long serialVersionUID = 1L;


	@XmlValue
	private String value;

	//The name field is mapped to the name attribute in a content wrapperXML file
	@XmlAttribute(name = "name")
	private String name;

	@Override
	public String getValue() {
		return value;
	}

	/**
	 * Set the value of spring bean id.
	 * @param value spring bean id
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Set the name of value in groovy binding context.
	 * @param name name of value
	 */
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Service: " + getName() + ", value: " + getValue();
	}



}
