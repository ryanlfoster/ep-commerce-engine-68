package com.elasticpath.importexport.common.dto.productcategory;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of catalog categories objects.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CatalogCategoriesDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String catalogCode;

	@XmlElement(name = "category", required = true)
	private List<ProductCategoryDTO> productCategoryDTOList;

	/**
	 * Gets the catalog code.
	 * 
	 * @return the catalogCode
	 */
	public String getCatalogCode() {
		return catalogCode;
	}

	/**
	 * Sets the catalog code.
	 * 
	 * @param catalogCode the catalogCode to set
	 */
	public void setCatalogCode(final String catalogCode) {
		this.catalogCode = catalogCode;
	}

	/**
	 * Gets the product category DTO list.
	 * 
	 * @return the productCategoryDTOList
	 */
	public List<ProductCategoryDTO> getProductCategoryDTOList() {
		if (productCategoryDTOList == null) {
			return Collections.emptyList();
		}
		return productCategoryDTOList;
	}

	/**
	 * Sets the product category DTO list.
	 * 
	 * @param productCategoryDTOList the productCategoryDTOList to set
	 */
	public void setProductCategoryDTOList(final List<ProductCategoryDTO> productCategoryDTOList) {
		this.productCategoryDTOList = productCategoryDTOList;
	}
}
