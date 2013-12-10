package com.elasticpath.common.dto.tax;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * JAXB DTO for Tax Value.
 */
@XmlRootElement(name = "value")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class TaxValueDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String code;

	@XmlAttribute(name = "percent", required = true)
	private BigDecimal percent;

	public String getCode() {
		return code;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setPercent(final BigDecimal percent) {
		this.percent = percent;
	}

}
