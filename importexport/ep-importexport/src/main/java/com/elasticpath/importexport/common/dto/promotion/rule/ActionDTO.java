package com.elasticpath.importexport.common.dto.promotion.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Contains XML mapping for descriptor of <code>Action</code> domain object. Designed for JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "promotionRuleAction")
public class ActionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "type", required = true)
	private String type;

	@XmlElement(name = "code", required = true)
	private String code;

	/**
	 * Gets rule action type.
	 * 
	 * @return rule action type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets rule action type.
	 * 
	 * @param type the action type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets rule action code.
	 * 
	 * @return rule action code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets rule action code.
	 * 
	 * @param code the action code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}
}
