package com.elasticpath.importexport.common.dto.tag;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elasticpath.common.dto.Dto;

/**
 * Contains mapping between XML and SellingContext domain object.
 * Designed for JAXB.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SellingContextDTO implements Dto {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "guid", required = true)
	private String guid;
	@XmlElement(name = "name", required = true)
	private String name;
	@XmlElement(name = "description", required = false)
	private String description;
	@XmlElement(name = "priority", required = true)
	private int priority;

	@XmlElementWrapper(name = "conditions")
	@XmlElement(name = "condition")
	private List<ConditionalExpressionDTO> conditions;
	
	@XmlElementWrapper(name = "saved_condition_guids")
	@XmlElement(name = "saved_condition_guid")
	private List<String> savedConditionGuids;

	/**
	 *
	 * @return guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 *
	 * @param guid guid to be set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return selling context conditions.
	 */
	public List<ConditionalExpressionDTO> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions - sets selling context conditions.
	 */
	public void setConditions(final List<ConditionalExpressionDTO> conditions) {
		this.conditions = conditions;
	}

	/**
	 * Gets the saved condition guids.
	 *
	 * @return the saved condition guids
	 */
	public List<String> getSavedConditionGuids() {
		return savedConditionGuids;
	}

	/**
	 * Sets the saved conditions.
	 *
	 * @param savedConditionGuids the new saved condition guids
	 */
	public void setSavedConditionGuids(final List<String> savedConditionGuids) {
		this.savedConditionGuids = savedConditionGuids;
	}

	/**
	 * @return - the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority - the priority to be set.
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

}
