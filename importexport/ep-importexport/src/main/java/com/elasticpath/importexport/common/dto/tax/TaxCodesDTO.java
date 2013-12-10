package com.elasticpath.importexport.common.dto.tax;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.tax.TaxCodeDTO;

/**
 * This element contains zero or more tax code elements. This class exists mainly for XSD generation.
 */
@XmlRootElement(name = "tax_codes")
@XmlType(name = "taxCodesDTO", propOrder = { })
@XmlAccessorType(XmlAccessType.NONE)
public class TaxCodesDTO {

	@XmlElement(name = "tax_code")
	private final List<TaxCodeDTO> codes = new ArrayList<TaxCodeDTO>();

	public List<TaxCodeDTO> getJurisdictions() {
		return codes;
	}

}
