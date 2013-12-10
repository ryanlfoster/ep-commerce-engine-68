package com.elasticpath.importexport.common.dto.productcategory;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product categories objects.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlRootElement(name = ProductCategoriesDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ProductCategoriesDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "product";
	
	@XmlAttribute(name = "code", required = true)
	private String productCode;

	@XmlElement(name = "catalog", required = true)
	private List<CatalogCategoriesDTO> catalogCategoriesDTOList;

	/**
	 * Gets the product code.
	 * 
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * Sets the product code.
	 * 
	 * @param productCode the productCode to set
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	/**
	 * Gets the catalog categories DTO list.
	 * 
	 * @return the catalogCategoryDTOList
	 */
	public List<CatalogCategoriesDTO> getCatalogCategoriesDTOList() {
		if (catalogCategoriesDTOList == null) {
			return Collections.emptyList();
		}
		return catalogCategoriesDTOList;
	}

	/**
	 * Sets the catalog category DTO list.
	 * 
	 * @param catalogCategoriesDTOList the catalogCategoryDTOList to set
	 */
	public void setCatalogCategoriesDTOList(final List<CatalogCategoriesDTO> catalogCategoriesDTOList) {
		this.catalogCategoriesDTOList = catalogCategoriesDTOList;
	}
}