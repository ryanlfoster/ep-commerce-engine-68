package com.elasticpath.importexport.common.dto.customer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.customer.CustomerDTO;

/**
 * Wrapper JAXB entity for schema generation to collect a group of CustomerDTOs.
 */
@XmlRootElement(name = "customers")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "customersDTO", propOrder = { })
public class CustomersDTO {

	@XmlElement(name = "customer")
	private final List<CustomerDTO> customers = new ArrayList<CustomerDTO>();

	public List<CustomerDTO> getCustomers() {
		return customers;
	}

}

