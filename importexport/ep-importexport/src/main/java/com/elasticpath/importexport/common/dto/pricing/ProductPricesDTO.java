package com.elasticpath.importexport.common.dto.pricing;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of catalog prices list object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = ProductPricesDTO.ROOT_ELEMENT)
public class ProductPricesDTO implements Dto {
	
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
	
	@XmlElement(name = "base", required = true)
	private CatalogPriceDTO baseCatalogPriceDTO;

	@XmlElement(name = "override")
	private List<CatalogPriceDTO> catalogPriceList;
	
	@XmlElementWrapper(name = "skus")
	@XmlElement(name = "sku", required = true)
	private List<SkuPricesDTO> skuPricesDTOList;

	/**
	 * Gets the catalog price list belongs to this product.
	 * 
	 * @return the catalog price list
	 */
	public List<CatalogPriceDTO> getOverridenCatalogPriceList() {
		if (catalogPriceList == null) {
			return Collections.emptyList();
		}
		return catalogPriceList;
	}

	/**
	 * Sets the catalog price list belongs to this product.
	 * 
	 * @param catalogPriceList the catalogPriceList to set
	 */
	public void setOverridenCatalogPriceList(final List<CatalogPriceDTO> catalogPriceList) {
		this.catalogPriceList = catalogPriceList;
	}

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
	 * @param productCode the productCode to set
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	/**
	 * Gets the baseCatalogPriceDTO.
	 * 
	 * @return the baseCatalogPriceDTO
	 */
	public CatalogPriceDTO getBaseCatalogPriceDTO() {
		return baseCatalogPriceDTO;
	}

	/**
	 * Sets the baseCatalogPriceDTO.
	 * 
	 * @param baseCatalogPriceDTO the baseCatalogPriceDTO to set
	 */
	public void setBaseCatalogPriceDTO(final CatalogPriceDTO baseCatalogPriceDTO) {
		this.baseCatalogPriceDTO = baseCatalogPriceDTO;
	}

	/**
	 * Gets the skuPricesDTOList.
	 * 
	 * @return the skuPricesDTOList
	 */
	public List<SkuPricesDTO> getSkuPricesDTOList() {
		if (skuPricesDTOList == null) {
			return Collections.emptyList();
		}
		return skuPricesDTOList;
	}

	/**
	 * Sets the skuPricesDTOList.
	 * 
	 * @param skuPricesDTOList the skuPricesDTOList to set
	 */
	public void setSkuPricesDTOList(final List<SkuPricesDTO> skuPricesDTOList) {
		this.skuPricesDTOList = skuPricesDTOList;
	}
}
