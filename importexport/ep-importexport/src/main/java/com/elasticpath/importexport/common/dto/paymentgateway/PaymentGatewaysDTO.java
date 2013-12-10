package com.elasticpath.importexport.common.dto.paymentgateway;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of PaymentGatewayDTOs.
 */
@XmlRootElement(name = "payment_gateways")
@XmlType(name = "paymentGatewaysDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class PaymentGatewaysDTO {

	@XmlElement(name = "payment_gateway")
	private final List<PaymentGatewayDTO> gateways = new ArrayList<PaymentGatewayDTO>();

	public List<PaymentGatewayDTO> getGateways() {
		return gateways;
	}
}

