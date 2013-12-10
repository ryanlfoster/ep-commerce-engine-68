package com.elasticpath.common.dto.tax;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO for Tax Jurisdictions.
 */
@XmlRootElement(name = TaxJurisdictionDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class TaxJurisdictionDTO implements Dto {

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "jurisdiction";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "region_code", required = true)
	private String regionCode;

	@XmlElement(name = "priceCalculationMethod", required = true)
	private boolean priceCalculationMethod;

	@XmlElementWrapper(name = "tax_categories")
	@XmlElement(name = "tax_category")
	private final Set<TaxCategoryDTO> categories = new HashSet<TaxCategoryDTO>();

	public Set<TaxCategoryDTO> getTaxCategories() {
		return this.categories;
	}

	public boolean isPriceCalculationMethod() {
		return priceCalculationMethod;
	}

	public String getRegionCode() {
		return this.regionCode;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public void setRegionCode(final String regionCode) {
		this.regionCode = regionCode;
	}

	public void setPriceCalculationMethod(final boolean priceCalculationMethod) {
		this.priceCalculationMethod = priceCalculationMethod;
	}

}
