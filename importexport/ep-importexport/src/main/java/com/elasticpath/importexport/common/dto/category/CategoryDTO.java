package com.elasticpath.importexport.common.dto.category;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.SeoDTO;

/**
 * The implementation of the <code>Dto</code> interface that contains data of Category object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlRootElement(name = CategoryDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class CategoryDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "category";

	@XmlAttribute(name = "mastercatalog", required = true)
	private String catalogCode;

	@XmlElement(name = "parentcategory")
	private String parentCategoryCode = "";

	@XmlElement(name = "code", required = true)
	private String categoryCode;

	@XmlElementWrapper(name = "name", required = true)
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> nameValues;

	@XmlElement(name = "type", required = true)
	private String categoryType;

	@XmlElement(name = "order", required = true)
	private int order;

	@XmlElement(name = "availability", required = true)
	private CategoryAvailabilityDTO categoryAvailabilityDTO;

	@XmlElement(name = "attributes")
	private AttributeGroupDTO attributeGroupDTO;

	@XmlElement(name = "seo", required = true)
	private SeoDTO seoDto;

	@XmlElementWrapper(name = "linkedcategories")
	@XmlElement(name = "linkedcategory", required = true)
	private List<LinkedCategoryDTO> linkedCategoryDTOList;

	/**
	 * Gets the catalogCode.
	 *
	 * @return the catalogCode
	 */
	public String getCatalogCode() {
		return catalogCode;
	}

	/**
	 * Sets the catalogCode.
	 *
	 * @param catalogCode the catalogCode to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 * Gets the parentCategoryCode.
	 *
	 * @return the parentCategoryCode
	 */
	public String getParentCategoryCode() {
		return parentCategoryCode;
	}

	/**
	 * Sets the parentCategoryCode.
	 *
	 * @param parentCategoryCode the parentCategoryCode to set
	 */
	public void setParentCategoryCode(final String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}

	/**
	 * Gets the categoryCode.
	 *
	 * @return the categoryCode
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * Sets the categoryCode.
	 *
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(final String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * Gets the nameValues.
	 *
	 * @return the nameValues
	 */
	public List<DisplayValue> getNameValues() {
		if (nameValues == null) {
			return Collections.emptyList();
		}
		return nameValues;
	}

	/**
	 * Sets the nameValues.
	 *
	 * @param nameValues the nameValues to set
	 */
	public void setNameValues(final List<DisplayValue> nameValues) {
		this.nameValues = nameValues;
	}

	/**
	 * Gets the categoryType.
	 *
	 * @return the categoryType
	 */
	public String getCategoryType() {
		return categoryType;
	}

	/**
	 * Sets the categoryType.
	 *
	 * @param categoryType the categoryType to set
	 */
	public void setCategoryType(final String categoryType) {
		this.categoryType = categoryType;
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order the order to set
	 */
	public void setOrder(final int order) {
		this.order = order;
	}

	/**
	 * Gets the categoryAvailabilityDTO.
	 *
	 * @return the categoryAvailabilityDTO
	 */
	public CategoryAvailabilityDTO getCategoryAvailabilityDTO() {
		return categoryAvailabilityDTO;
	}

	/**
	 * Sets the categoryAvailabilityDTO.
	 *
	 * @param categoryAvailabilityDTO the categoryAvailabilityDTO to set
	 */
	public void setCategoryAvailabilityDTO(final CategoryAvailabilityDTO categoryAvailabilityDTO) {
		this.categoryAvailabilityDTO = categoryAvailabilityDTO;
	}

	/**
	 * Gets the attributeGroupDTO.
	 *
	 * @return the attributeGroupDTO
	 */
	public AttributeGroupDTO getAttributeGroupDTO() {
		return attributeGroupDTO;
	}

	/**
	 * Sets the attributeGroupDTO.
	 *
	 * @param attributeGroupDTO the attributeGroupDTO to set
	 */
	public void setAttributeGroupDTO(final AttributeGroupDTO attributeGroupDTO) {
		this.attributeGroupDTO = attributeGroupDTO;
	}

	/**
	 * Gets the seoDto.
	 *
	 * @return the seoDto
	 */
	public SeoDTO getSeoDto() {
		return seoDto;
	}

	/**
	 * Sets the seoDto.
	 *
	 * @param seoDto the seoDto to set
	 */
	public void setSeoDto(final SeoDTO seoDto) {
		this.seoDto = seoDto;
	}

	/**
	 * Gets the linkedCategoryDTOList.
	 *
	 * @return the linkedCategoryDTOList
	 */
	public List<LinkedCategoryDTO> getLinkedCategoryDTOList() {
		if (linkedCategoryDTOList == null) {
			return Collections.emptyList();
		}
		return linkedCategoryDTOList;
	}

	/**
	 * Sets the linkedCategoryDTOList.
	 *
	 * @param linkedCategoryDTOList the linkedCategoryDTOList to set
	 */
	public void setLinkedCategoryDTOList(final List<LinkedCategoryDTO> linkedCategoryDTOList) {
		this.linkedCategoryDTOList = linkedCategoryDTOList;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("catalogCode", getCatalogCode())
			.append("parentCategoryCode", getParentCategoryCode())
			.append("categoryCode", getCategoryCode())
			.append("nameValues", getNameValues())
			.append("categoryType", getCategoryType())
			.append("order", getOrder())
			.append("categoryAvailabilityDTO", getCategoryAvailabilityDTO())
			.append("attributeGroupDTO", getAttributeGroupDTO())
			.append("seoDto", getSeoDto())
			.append("linkedCategoryDTOList", getLinkedCategoryDTOList())
			.toString();
	}

}
