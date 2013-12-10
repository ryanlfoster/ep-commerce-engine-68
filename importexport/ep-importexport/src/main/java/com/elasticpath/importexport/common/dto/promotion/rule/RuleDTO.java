package com.elasticpath.importexport.common.dto.promotion.rule;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * Maps promotion <code>Rule</code> on XML. 
 */
@XmlRootElement(name = RuleDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class RuleDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in XML representation.
	 */
	public static final String ROOT_ELEMENT = "rule";
	
	@XmlElement(name = "code", required = true)
	private String code;
	
	@XmlElement(name = "conditions", required = true)
	private ConditionsDTO conditions;

	@XmlElementWrapper(name = "actions")
	@XmlElement(name = "action", required = true)
	private List<ActionDTO> actions;	

	/**
	 * Gets the Code.
	 * 
	 * @return the Code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * Sets The Code.
	 * 
	 * @param code the Code.
	 */
	public final void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Gets the list of actions.
	 * 
	 * @return the actions
	 */
	public List<ActionDTO> getActions() {
		return actions;
	}

	/**
	 * Sets the list of actions.
	 * 
	 * @param actions the actions to set
	 */
	public void setActions(final List<ActionDTO> actions) {
		this.actions = actions;
	}

	/**
	 * Gets conditions.
	 * 
	 * @return the conditions
	 */
	public ConditionsDTO getConditions() {
		return conditions;
	}

	/**
	 * Sets conditions.
	 * 
	 * @param conditions the conditions to set
	 */
	public void setConditions(final ConditionsDTO conditions) {
		this.conditions = conditions;
	}	
}
