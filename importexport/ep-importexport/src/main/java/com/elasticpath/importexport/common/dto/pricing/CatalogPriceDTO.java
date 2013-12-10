package com.elasticpath.importexport.common.dto.pricing;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of catalog price object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CatalogPriceDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "catalog", required = true)
	private String catalogCode;

	@XmlElement(name = "currency")
	private List<CurrencyPriceDTO> pricesList;

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
	 * Gets the prices dto list.
	 * 
	 * @return the pricesList
	 */
	public List<CurrencyPriceDTO> getPricesList() {
		if (pricesList == null) {
			return Collections.emptyList();
		}
		return pricesList;
	}

	/**
	 * Sets the prices dto list.
	 * 
	 * @param pricesList the pricesList to set
	 */
	public void setPricesList(final List<CurrencyPriceDTO> pricesList) {
		this.pricesList = pricesList;
	}
}
