package com.elasticpath.importexport.common.dto.tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * Contains mapping between XML and TagCondition domain object. Designed for JAXB.
 */
@XmlRootElement(name = ConditionalExpressionDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ConditionalExpressionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "condition";
	
	@XmlElement(name = "guid", required = true)
	private String guid;
	
	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "condition_string", required = true)
	private String conditionString;

	@XmlElement(name = "dictionary_guid", required = true)
	private String dictionaryGuid;

	@XmlAttribute(name = "is_named")
	private Boolean named;
	
	/**
	 * @return guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
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
	 * @return the condition string
	 */
	public String getConditionString() {
		return conditionString;
	}

	/**
	 * @param conditionString - condition string to be set.
	 */
	public void setConditionString(final String conditionString) {
		this.conditionString = conditionString;
	}

	/**
	 * @return - dictionary guid.
	 */
	public String getDictionaryGuid() {
		return dictionaryGuid;
	}

	/**
	 * @param dictionaryGuid - dictionary guid to be set.
	 */
	public void setDictionaryGuid(final String dictionaryGuid) {
		this.dictionaryGuid = dictionaryGuid;
	}

	/**
	 * @return true if condition is named, false otherwise.
	 */
	public Boolean isNamed() {
		return named;
	}

	/**
	 * @param named - set named condition flag.
	 */
	public void setNamed(final Boolean named) {
		this.named = named;
	}

}
