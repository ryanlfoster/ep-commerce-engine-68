package com.elasticpath.importexport.example;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Extension of {@link ExampleDTO} for extensibility tests.
 */
@XmlRootElement(name = ExampleExtDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ExampleExtDTO extends ExampleDTO {
	private static final long serialVersionUID = 1L;
	/** Root element of this dto. */
	public static final String ROOT_ELEMENT = "example-ext";

	@XmlElement(name = "code", required = true)
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}