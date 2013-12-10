package com.elasticpath.importexport.common.dto.products;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.dto.general.PricingMechanismValues;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlRootElement(name = ProductDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
public class ProductDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "product";

	@XmlAttribute(name = "bundle")
	private Boolean bundle;

	@XmlAttribute(name = "pricingMechanism")
	private PricingMechanismValues pricingMechanism;

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElementWrapper(name = "name")
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> nameValues;

	@XmlElement(name = "type", required = true)
	private String type;

	@XmlElement(name = "taxcode")
	private String taxCode;

	@XmlElement(name = "brand")
	private String brand;

	@XmlElement(name = "image")
	private String image;

	@XmlElement(name = "availability", required = true)
	private ProductAvailabilityDTO productAvailability;

	@XmlElement(name = "attributes")
	private AttributeGroupDTO productAttributes;

	@XmlElementWrapper(name = "skus", required = true)
	@XmlElement(name = "sku", required = true)
	private List<ProductSkuDTO> productSkus;

	@XmlElement(name = "seo", required = true)
	private SeoDTO seoDto;

	/**
	 * Gets the product code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the product code.
	 *
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the display name values for different locales.
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
	 * Sets the display name values for different locales.
	 *
	 * @param nameValues the nameValues to set
	 */
	public void setNameValues(final List<DisplayValue> nameValues) {
		this.nameValues = nameValues;
	}

	/**
	 * Gets the product type name.
	 *
	 * @return the type name
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the product type name.
	 *
	 * @param type the type name to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the tax code.
	 *
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	/**
	 * Sets the tax code.
	 *
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(final String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * Gets the brand code.
	 *
	 * @return the brand code
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * Sets the brand code.
	 *
	 * @param brand the brand code to set
	 */
	public void setBrand(final String brand) {
		this.brand = brand;
	}

	/**
	 * Gets the product default image.
	 *
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the product default image.
	 *
	 * @param image the image to set
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Gets the product availability criteria.
	 *
	 * @return the productAvalability
	 */
	public ProductAvailabilityDTO getProductAvailability() {
		return productAvailability;
	}

	/**
	 * Sets the product availability criteria.
	 *
	 * @param productAvailability the productAvailability to set
	 */
	public void setProductAvailability(final ProductAvailabilityDTO productAvailability) {
		this.productAvailability = productAvailability;
	}

	/**
	 * Gets the product skus.
	 *
	 * @return the productSkus
	 */
	public List<ProductSkuDTO> getProductSkus() {
		if (productSkus == null) {
			return Collections.emptyList();
		}
		return productSkus;
	}

	/**
	 * Sets the product skus.
	 *
	 * @param productSkus the productSkus to set
	 */
	public void setProductSkus(final List<ProductSkuDTO> productSkus) {
		this.productSkus = productSkus;
	}

	/**
	 * Gets the seo dto object.
	 *
	 * @return the seoAdapter
	 * @see SeoDTO
	 */
	public SeoDTO getSeoDTO() {
		return seoDto;
	}

	/**
	 * Sets the seo dto objects.
	 *
	 * @param seoDto the seoDto to set
	 */
	public void setSeoDto(final SeoDTO seoDto) {
		this.seoDto = seoDto;
	}

	/**
	 * Gets the product attributes group dto.
	 *
	 * @return the productAttributesGroup dto
	 */
	public AttributeGroupDTO getProductAttributes() {
		return productAttributes;
	}

	/**
	 * Sets the product attributes group dto.
	 *
	 * @param productAttributes the productAttributesGroup dto to set
	 */
	public void setProductAttributes(final AttributeGroupDTO productAttributes) {
		this.productAttributes = productAttributes;
	}

	/**
	 * @return the bundle
	 */
	public Boolean isBundle() {
		if (bundle == null) {
			bundle = Boolean.FALSE;
		}
		return bundle;
	}

	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(final Boolean bundle) {
		this.bundle = bundle;
	}

	 /**
     * Gets the value of the pricingMechanism property.
     *
     * @return
     *     possible object is
     *     {@link PricingMechanismValues }
     *
     */
    public PricingMechanismValues getPricingMechanism() {
        if (pricingMechanism == null) {
            return PricingMechanismValues.ASSIGNED;
        }

        return pricingMechanism;
    }

    /**
     * Sets the value of the pricingMechanism property.
     *
     * @param value
     *     allowed object is
     *     {@link PricingMechanismValues }
     *
     */
    public void setPricingMechanism(final PricingMechanismValues value) {
        this.pricingMechanism = value;
    }

}
