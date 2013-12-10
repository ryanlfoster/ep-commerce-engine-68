package com.elasticpath.importexport.common.dto.catalogs;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>DTO</code> interface that contains data of Catalog object.
 * <p>
 * This implementation designed for JAXB to working with XML representation of data
 */
@XmlRootElement(name = CatalogDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class CatalogDTO implements Dto {

	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "catalog";

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElement(name = "type", required = true)
	private CatalogType type;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElementWrapper(name = "languages")
	@XmlElement(name = "language")
	private List<String> languages;

	@XmlElement(name = "defaultlanguage", required = true)
	private String defaultLanguage;

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	private List<AttributeDTO> attributes;

	@XmlElementWrapper(name = "categorytypes")
	@XmlElement(name = "categorytype")
	private List<CategoryTypeDTO> categoryTypes;

	@XmlElementWrapper(name = "producttypes")
	@XmlElement(name = "producttype")
	private List<ProductTypeDTO> productTypes;

	@XmlElementWrapper(name = "skuoptions")
	@XmlElement(name = "skuoption")
	private List<SkuOptionDTO> skuOptions;

	@XmlElementWrapper(name = "brands")
	@XmlElement(name = "brand")
	private List<BrandDTO> brands;

	@XmlElementWrapper(name = "synonymgroups")
	@XmlElement(name = "synonymgroup")
	private List<SynonymGroupDTO> synonymGroupDTOs;

	/**
	 * Gets the catalog code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the catalog code.
	 *
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the catalog type.
	 *
	 * @return the CatalogType
	 */
	public CatalogType getType() {
		return type;
	}

	/**
	 * Sets the catalog type.
	 *
	 * @param type the CatalogType to set
	 */
	public void setType(final CatalogType type) {
		this.type = type;
	}

	/**
	 * Gets the catalog name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the catalog name.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the languages.
	 *
	 * @return the languages
	 */
	public List<String> getLanguages() {
		if (languages == null) {
			return Collections.emptyList();
		}
		return languages;
	}

	/**
	 * Sets the languages.
	 *
	 * @param languages the languages to set
	 */
	public void setLanguages(final List<String> languages) {
		this.languages = languages;
	}

	/**
	 * Gets the catalog default language.
	 *
	 * @return the defaultLanguage
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * Sets the catalog default language name.
	 *
	 * @param defaultLanguage the defaultLanguage to set
	 */
	public void setDefaultLanguage(final String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	/**
	 * Gets the categoryTypes.
	 *
	 * @return the categoryTypes
	 */
	public List<CategoryTypeDTO> getCategoryTypes() {
		if (categoryTypes == null) {
			return Collections.emptyList();
		}
		return categoryTypes;
	}

	/**
	 * Sets the categoryTypes.
	 *
	 * @param categoryTypes the categoryTypes to set
	 */
	public void setCategoryTypes(final List<CategoryTypeDTO> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}

	/**
	 * Gets the productTypes.
	 *
	 * @return the productTypes
	 */
	public List<ProductTypeDTO> getProductTypes() {
		if (productTypes == null) {
			return Collections.emptyList();
		}
		return productTypes;
	}

	/**
	 * Sets the productTypes.
	 *
	 * @param productTypes the productTypes to set
	 */
	public void setProductTypes(final List<ProductTypeDTO> productTypes) {
		this.productTypes = productTypes;
	}

	/**
	 * Gets the skuOptions.
	 *
	 * @return the skuOptions
	 */
	public List<SkuOptionDTO> getSkuOptions() {
		if (skuOptions == null) {
			return Collections.emptyList();
		}
		return skuOptions;
	}

	/**
	 * Sets the skuOptions.
	 *
	 * @param skuOptions the skuOptions to set
	 */
	public void setSkuOptions(final List<SkuOptionDTO> skuOptions) {
		this.skuOptions = skuOptions;
	}

	/**
	 * Gets the brands.
	 *
	 * @return the brands
	 */
	public List<BrandDTO> getBrands() {
		if (brands == null) {
			return Collections.emptyList();
		}
		return brands;
	}

	/**
	 * Sets the brands.
	 *
	 * @param brands the brands to set
	 */
	public void setBrands(final List<BrandDTO> brands) {
		this.brands = brands;
	}

	/**
	 * Gets the synonymGroupDTOs.
	 *
	 * @return the synonymGroupDTOs
	 */
	public List<SynonymGroupDTO> getSynonymGroups() {
		return synonymGroupDTOs;
	}

	/**
	 * Sets the synonymGroupDTOs.
	 *
	 * @param synonymGroupDTOs the synonymGroupDTOs to set
	 */
	public void setSynonymGroups(final List<SynonymGroupDTO> synonymGroupDTOs) {
		this.synonymGroupDTOs = synonymGroupDTOs;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public List<AttributeDTO> getAttributes() {
		if (attributes == null) {
			return Collections.emptyList();
		}
		return attributes;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the attributes to set
	 */
	public void setAttributes(final List<AttributeDTO> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("code", getCode())
			.append("type", getType())
			.append("name", getName())
			.append("languages", getLanguages())
			.append("defaultLanguage", getDefaultLanguage())
			.append("categoryTypes", getCategoryTypes())
			.append("productTypes", getProductTypes())
			.append("skuOptions", getSkuOptions())
			.append("brands", getBrands())
			.append("synonymGroups", getSynonymGroups())
			.append("attributes", getAttributes())
			.toString();
	}

}
