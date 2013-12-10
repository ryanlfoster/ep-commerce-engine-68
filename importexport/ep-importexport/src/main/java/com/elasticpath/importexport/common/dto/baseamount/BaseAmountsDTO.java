package com.elasticpath.importexport.common.dto.baseamount;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Element which contains zero or more amount elements. This class exists mainly to be generated in the XSD.
 */
@XmlRootElement(name = "amounts")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "baseAmountsDTO", propOrder = { })
public class BaseAmountsDTO {

	@XmlElement(name = "base_amount")
	private final List<BaseAmountDTO> baseAmounts = new ArrayList<BaseAmountDTO>();

	public List<BaseAmountDTO> getBaseAmounts() {
		return baseAmounts;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
			.append("baseAmounts", getBaseAmounts())
			.toString();
	}
}
