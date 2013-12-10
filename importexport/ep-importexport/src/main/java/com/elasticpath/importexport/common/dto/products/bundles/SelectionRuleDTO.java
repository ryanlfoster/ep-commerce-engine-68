package com.elasticpath.importexport.common.dto.products.bundles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data for Bundle SelectionRule.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SelectionRuleDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "selectionparameter", required = true)
	private Integer parameter;

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(final Integer parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the parameter
	 */
	public Integer getParameter() {
		return parameter;
	}
}
