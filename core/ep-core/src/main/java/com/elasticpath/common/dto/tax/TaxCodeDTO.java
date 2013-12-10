package com.elasticpath.common.dto.tax;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO for Tax Code.
 */
@XmlRootElement(name = TaxCodeDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class TaxCodeDTO implements Dto {

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "code";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlAttribute(name = "code", required = true)
	private String code;

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
