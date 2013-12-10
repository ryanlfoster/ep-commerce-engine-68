package com.elasticpath.importexport.common.dto.products;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of product sku object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProductSkuDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(name = "guid", required = true)
	private String guid;

	@XmlElement(name = "code", required = true)
	private String skuCode;

	@XmlElementWrapper(name = "skuoptions")
	@XmlElement(name = "skuoption")
	private List<SkuOptionDTO> skuOptionList;

	@XmlElement(name = "availability")
	private ProductSkuAvailabilityDTO productSkuAvailabilityDTO;

	@XmlElement(name = "shippable", required = true)
	private ShippableItemDTO shippableItem;

	@XmlElement(name = "digitalasset", required = true)
	private DigitalAssetItemDTO digitalAssetItem;

	@XmlElement(name = "image")
	private String image;

	@XmlElement(name = "attributes")
	private AttributeGroupDTO attributeGroupDTO;

	/**
	 * Gets the sku guid.
	 * 
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}
	
	/**
	 * Sets the sku guid.
	 * 
	 * @param guid the guid to set
	 */
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	
	/**
	 * Gets the sku code.
	 * 
	 * @return the skuCode
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Sets the sku code.
	 * 
	 * @param skuCode the skuCode to set
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Gets the shippable item dto.
	 * 
	 * @return the shippableItem
	 */
	public ShippableItemDTO getShippableItem() {
		return shippableItem;
	}

	/**
	 * Sets the shippable item dto.
	 * 
	 * @param shippableItem the shippableItem to set
	 */
	public void setShippableItem(final ShippableItemDTO shippableItem) {
		this.shippableItem = shippableItem;
	}

	/**
	 * Gets the digital asset item dto.
	 * 
	 * @return the digitalAssetItem
	 */
	public DigitalAssetItemDTO getDigitalAssetItem() {
		return digitalAssetItem;
	}

	/**
	 * Sets the digital asset item dto.
	 * 
	 * @param digitalAssetItem the digitalAssetItem to set
	 */
	public void setDigitalAssetItem(final DigitalAssetItemDTO digitalAssetItem) {
		this.digitalAssetItem = digitalAssetItem;
	}

	/**
	 * Gets the sku option dto list. If there are no sku option list then empty list will be returned.
	 * 
	 * @return the skuOptionList
	 */
	public List<SkuOptionDTO> getSkuOptionList() {
		if (skuOptionList == null) {
			return Collections.emptyList();
		}
		return skuOptionList;
	}

	/**
	 * Sets the sku option dto list.
	 * 
	 * @param skuOptionList the skuOptionList to set
	 */
	public void setSkuOptionList(final List<SkuOptionDTO> skuOptionList) {
		this.skuOptionList = skuOptionList;
	}

	/**
	 * Gets the image for product sku.
	 * 
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * Sets the image for product sku.
	 * 
	 * @param image the image to set
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Gets the attribute group DTO.
	 * 
	 * @return the attributeGroupDTO
	 */
	public AttributeGroupDTO getAttributeGroupDTO() {
		return attributeGroupDTO;
	}

	/**
	 * Sets the attribute group DTO.
	 * 
	 * @param attributeGroupDTO the attributeGroupDTO to set
	 */
	public void setAttributeGroupDTO(final AttributeGroupDTO attributeGroupDTO) {
		this.attributeGroupDTO = attributeGroupDTO;
	}

	/**
	 * Gets the productSku availabilityDTO.
	 * 
	 * @return the productSkuAvailabilityDTO
	 */
	public ProductSkuAvailabilityDTO getProductSkuAvailabilityDTO() {
		return productSkuAvailabilityDTO;
	}

	/**
	 * Sets the productSku availabilityDTO.
	 * 
	 * @param productSkuAvailabilityDTO the productSkuAvailabilityDTO to set
	 */
	public void setProductSkuAvailabilityDTO(final ProductSkuAvailabilityDTO productSkuAvailabilityDTO) {
		this.productSkuAvailabilityDTO = productSkuAvailabilityDTO;
	}

}
