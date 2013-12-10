package com.elasticpath.importexport.common.dto.promotion.cart;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;

/**
 * Contains XML mapping for <code>RuleAction</code> domain object.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "cartPromotionAction")
public class ActionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "type", required = true)
	private String type;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private List<ParameterDTO> parameters;

	@XmlElementWrapper(name = "exceptions")
	@XmlElement(name = "exception")
	private List<ExceptionDTO> exceptions;

	/**
	 * Gets rule element type.
	 * 
	 * @return rule element type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets rule element type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets rule element parameters.
	 * 
	 * @return the parameters
	 */
	public List<ParameterDTO> getParameters() {
		if (parameters == null) {
			return Collections.emptyList();
		}
		return parameters;
	}

	/**
	 * Sets rule element parameters.
	 * 
	 * @param parameters the parameters to set
	 */
	public void setParameters(final List<ParameterDTO> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Gets rule exceptions.
	 * 
	 * @return the exceptions
	 */
	public List<ExceptionDTO> getExceptions() {
		if (exceptions == null) {
			return Collections.emptyList();
		}
		return exceptions;
	}

	/**
	 * Sets rule exceptions.
	 * 
	 * @param exceptions the exceptions to set
	 */
	public void setExceptions(final List<ExceptionDTO> exceptions) {
		this.exceptions = exceptions;
	}
}
