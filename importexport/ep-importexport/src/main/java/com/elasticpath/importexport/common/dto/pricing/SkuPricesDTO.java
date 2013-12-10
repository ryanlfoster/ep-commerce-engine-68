package com.elasticpath.importexport.common.dto.pricing;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of sku prices list object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SkuPricesDTO implements Dto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "code", required = true)
	private String skuCode;
	
	@XmlElement(name = "override")
	private List<CatalogPriceDTO> skuCatalogPrices;

	/**
	 * Gets the product sku code.
	 * 
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Sets the product sku code.
	 * @param skuCode the skuCode to set
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Gets the skuCatalogPrices.
	 * 
	 * @return the skuCatalogPrices
	 */
	public List<CatalogPriceDTO> getSkuCatalogPrices() {
		if (skuCatalogPrices == null) {
			return Collections.emptyList();
		}
		return skuCatalogPrices;
	}

	/**
	 * Sets the skuCatalogPrices.
	 * 
	 * @param skuCatalogPrices the skuCatalogPrices to set
	 */
	public void setSkuCatalogPrices(final List<CatalogPriceDTO> skuCatalogPrices) {
		this.skuCatalogPrices = skuCatalogPrices;
	}	

}
