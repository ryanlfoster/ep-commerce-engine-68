package com.elasticpath.importexport.common.dto.targetedselling;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.tag.SellingContextDTO;

/**
 * DTO for DynamicContentDelivery.
 */
@XmlRootElement(name = DynamicContentDeliveryDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class DynamicContentDeliveryDTO implements Dto {

	private static final long serialVersionUID = -8114687423259248774L;

	/**
	 * The name of root element in XML representation of DCD.
	 */
	public static final String ROOT_ELEMENT = "dynamic_content_delivery";

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "description")
	private String description;

	@XmlElement(name = "priority", required = true)
	private int priority;

	@XmlElement(name = "dynamiccontent_guid", required = true)
	private String dynamicContentGuid;

	@XmlElementWrapper(name = "contentspaces", required = true)
	@XmlElement(name = "contentspace_guid")
	private List<String> contentSpaceGuids;

	@XmlElement(name = "selling_context", required = true)
	private SellingContextDTO sellingContext;

	/**
	 * @return name of the DCD
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of the DCD
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return description of the DCD
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description of the DCD
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return name of the DC associated to this DCD
	 */
	public String getDynamicContentGuid() {
		return dynamicContentGuid;
	}

	/**
	 * @param dynamicContentGuid of the DC
	 */
	public void setDynamicContentGuid(final String dynamicContentGuid) {
		this.dynamicContentGuid = dynamicContentGuid;
	}

	/**
	 * @return guids of CSs associated with this DCD
	 */
	public List<String> getContentSpaceGuids() {
		return contentSpaceGuids;
	}

	/**
	 * @param contentSpaceGuids guids
	 */
	public void setContentSpaceGuids(final List<String> contentSpaceGuids) {
		this.contentSpaceGuids = contentSpaceGuids;
	}

	/**
	 * @return guid of this DCD
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid of this DCD
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * @return priority of this DCD
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority of this DCD
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * @return The selling context.
	 */
	public SellingContextDTO getSellingContext() {
		return sellingContext;
	}

	/**
	 * Sets the selling context.
	 * 
	 * @param sellingContext value to set
	 */
	public void setSellingContext(final SellingContextDTO sellingContext) {
		this.sellingContext = sellingContext;
	}
}
