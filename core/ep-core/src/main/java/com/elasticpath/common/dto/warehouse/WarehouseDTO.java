package com.elasticpath.common.dto.warehouse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * A DTO representation of Warehouse.
 */
@XmlRootElement(name = WarehouseDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class WarehouseDTO implements Dto {

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "warehouse";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String code;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "pick_delay", required = true)
	private int pickDelay;

	@XmlElement(name = "street1", required = true)
	private String street1;

	@XmlElement(name = "street2")
	private String street2;

	@XmlElement(name = "city", required = true)
	private String city;

	@XmlElement(name = "sub_country")
	private String subCountry;

	@XmlElement(name = "zip_postal_code", required = true)
	private String zipOrPostalCode;

	@XmlElement(name = "country", required = true)
	private String country;

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getPickDelay() {
		return pickDelay;
	}

	public void setPickDelay(final int pickDelay) {
		this.pickDelay = pickDelay;
	}

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(final String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(final String street2) {
		this.street2 = street2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getSubCountry() {
		return subCountry;
	}

	public void setSubCountry(final String subcountry) {
		this.subCountry = subcountry;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

}
