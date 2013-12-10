package com.elasticpath.importexport.common.dto.products.bundles;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.pricing.PriceAdjustmentDto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of single bundle constituent.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductBundleConstituentDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "code", required = true)
	private ProductBundleConstituentCodeDTO code;
	
	@XmlElement(name = "quantity", required = true)
	private Integer quantity;

	@XmlElement(name = "ordering", required = true)
	private Integer ordering;

	@XmlElementWrapper(name = "price_adjustments")
	@XmlElement(name = "adjustment")
	private List<PriceAdjustmentDto> adjustments;
	
	/**
	 * @return the code
	 */
	public ProductBundleConstituentCodeDTO getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(final ProductBundleConstituentCodeDTO code) {
		this.code = code;
	}

	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(final Integer quantity) {
		this.quantity = quantity;
	}

	/**
	 *
	 * @param ordering the ordering to set
	 */
	public void setOrdering(final Integer ordering) {
		this.ordering = ordering;
	}

	/**
	 *
	 * @return the ordering
	 */
	public Integer getOrdering() {
		return ordering;
	}

	/**
	 * @param adjustments the adjustments to set
	 */
	public void setAdjustments(final List<PriceAdjustmentDto> adjustments) {
		this.adjustments = adjustments;
	}

	/**
	 * @return the adjustments
	 */
	public List<PriceAdjustmentDto> getAdjustments() {
		return adjustments;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public String getGuid() {
		return guid;
	}
}
