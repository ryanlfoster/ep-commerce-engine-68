package com.elasticpath.importexport.common.dto.promotion;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elasticpath.common.dto.Dto;

/**
 * Contains XML mapping for <code>RuleException</code>.
 * Designed for JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ExceptionDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "type", required = true)
	private String exceptionType;

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	private List<ParameterDTO> exceptionParameters;

	/**
	 * Gets exception type.
	 * 
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}

	/**
	 * Sets exception type.
	 * 
	 * @param exceptionType the exceptionType to set
	 */
	public void setExceptionType(final String exceptionType) {
		this.exceptionType = exceptionType;
	}

	/**
	 * Gets exception parameters.
	 * 
	 * @return the exceptionParameters
	 */
	public List<ParameterDTO> getExceptionParameters() {
		if (exceptionParameters == null) {
			return Collections.emptyList();
		}
		return exceptionParameters;
	}

	/**
	 * Sets exception parameters.
	 * 
	 * @param exceptionParameters the exceptionParameters to set
	 */
	public void setExceptionParameters(final List<ParameterDTO> exceptionParameters) {
		this.exceptionParameters = exceptionParameters;
	}
}
