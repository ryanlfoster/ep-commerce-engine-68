package com.elasticpath.importexport.common.dto.tax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.tax.TaxJurisdictionDTO;

/**
 * This element contains zero or more jurisdiction elements. This class exists mainly for XSD generation.
 */
@XmlRootElement(name = "tax_jurisdictions")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "taxJurisdictionsDTO", propOrder = { })
public class TaxJurisdictionsDTO {

	@XmlElement(name = "jurisdiction")
	private final List<TaxJurisdictionDTO> jurisdictions = new ArrayList<TaxJurisdictionDTO>();

	public List<TaxJurisdictionDTO> getJurisdictions() {
		return jurisdictions;
	}

}