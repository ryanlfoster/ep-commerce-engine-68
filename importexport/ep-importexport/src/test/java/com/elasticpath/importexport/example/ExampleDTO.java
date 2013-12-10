package com.elasticpath.importexport.example;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * Example {@Link Dto} class for extensibility tests.
 */
@XmlRootElement(name = ExampleDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ExampleDTO implements Dto {
	private static final long serialVersionUID = 1L;
	/** Root element of this dto. */
	public static final String ROOT_ELEMENT = "example";

	@XmlElement(name = "name", required = true)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}