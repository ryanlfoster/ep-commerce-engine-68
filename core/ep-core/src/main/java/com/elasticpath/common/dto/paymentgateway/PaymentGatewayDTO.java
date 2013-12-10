package com.elasticpath.common.dto.paymentgateway;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.PropertyDTO;

/**
 * JAXB DTO for Payment Gateways (and their properties).
 */
@XmlRootElement(name = PaymentGatewayDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class PaymentGatewayDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/** Root XML element name. */
	public static final String ROOT_ELEMENT = "payment_gateway";

	@XmlElement(required = true)
	private String name;

	@XmlElement(required = true)
	private String type;

	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "property")
	private List<PropertyDTO> properties = new ArrayList<PropertyDTO>();

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public List<PropertyDTO> getProperties() {
		return properties;
	}

	public void setProperties(final List<PropertyDTO> properties) {
		this.properties = properties;
	}
}
