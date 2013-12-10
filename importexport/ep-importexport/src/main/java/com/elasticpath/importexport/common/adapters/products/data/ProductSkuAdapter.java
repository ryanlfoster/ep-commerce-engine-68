package com.elasticpath.importexport.common.adapters.products.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.AttributeGroupDTO;
import com.elasticpath.importexport.common.dto.products.DigitalAssetItemDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuAvailabilityDTO;
import com.elasticpath.importexport.common.dto.products.ProductSkuDTO;
import com.elasticpath.importexport.common.dto.products.ShippableItemDTO;
import com.elasticpath.importexport.common.dto.products.SkuOptionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>ProductSku</code> interface. It is responsible for data transformation between <code>Product</code> and
 * <code>ProductSkuDTO</code> objects.
 */
public class ProductSkuAdapter extends AbstractDomainAdapterImpl<ProductSku, ProductSkuDTO> {

	private ShippableItemAdapter shippableItemAdapter;

	private DigitalAssetItemAdapter digitalAssetItemAdapter;

	private ProductSkuOptionAdapter productSkuOptionAdapter;

	private AttributeGroupAdapter attributeGroupAdapter;

	@Override
	public void populateDomain(final ProductSkuDTO source, final ProductSku target) {
		target.setSkuCode(source.getSkuCode());
		target.setGuid(source.getGuid());
		target.setImage(source.getImage());

		final ShippableItemDTO shippableItem = source.getShippableItem();
		final DigitalAssetItemDTO digitalAssetItem = source.getDigitalAssetItem();

		checkDigitalShippable(shippableItem, digitalAssetItem);

		shippableItemAdapter.populateDomain(shippableItem, target);
		digitalAssetItemAdapter.populateDomain(digitalAssetItem, target);

		populateDomainSkuOptions(target, source.getSkuOptionList());

		attributeGroupAdapter.populateDomain(source.getAttributeGroupDTO(), target.getAttributeValueGroup());

		populateProductSkuAvailability(target, source.getProductSkuAvailabilityDTO());		
	}

	/**
	 * Checks if product sku is Shippable and Digital at the same time.
	 *
	 * @param shippableItem the ShippableItemDTO
	 * @param digitalAssetItem the DigitalAssetItemDTO
	 */
	void checkDigitalShippable(final ShippableItemDTO shippableItem, final DigitalAssetItemDTO digitalAssetItem) {
		if (shippableItem.isEnabled() && digitalAssetItem.isEnabled()) {
			throw new PopulationRuntimeException("IE-10318");
		}
	}

	/**
	 * Populates ProductSku with ProductSkuAvailabilityDTO.
	 * 
	 * @param productSku the ProductSku
	 * @param productSkuAvailabilityDTO the ProductSkuAvailabilityDTO
	 */
	void populateProductSkuAvailability(final ProductSku productSku, final ProductSkuAvailabilityDTO productSkuAvailabilityDTO) {
		if (productSkuAvailabilityDTO != null) {
			checkProductSkuAvailability(productSkuAvailabilityDTO);
			
			productSku.setStartDate(productSkuAvailabilityDTO.getStartDate());
			productSku.setEndDate(productSkuAvailabilityDTO.getEndDate());
		}
	}

	private void checkProductSkuAvailability(final ProductSkuAvailabilityDTO source) {
		if (source.getStartDate() != null && source.getEndDate() != null && source.getStartDate().after(source.getEndDate())) {
			throw new PopulationRuntimeException("IE-10321");
		}
	}

	/**
	 * Populates ProductSku with List of SkuOptionDTOs.
	 * 
	 * @param productSku the ProductSku
	 * @param skuOptionDtoList the List of SkuOptionDTO
	 */
	void populateDomainSkuOptions(final ProductSku productSku, final List<SkuOptionDTO> skuOptionDtoList) {
		for (SkuOptionDTO skuOptionDTO : skuOptionDtoList) {
			productSkuOptionAdapter.populateDomain(skuOptionDTO, createSkuOptionValue(productSku, skuOptionDTO.getCode()));
		}
	}

	/**
	 * Creates the SkuOptionValue (JPA_Adaper) using ProductSku and SkuOptionCode.
	 * First of all we trying to find SkuOptionValue in current productSku's OptionValueMap 
	 * and then if it is not found new instance of SkuOptionValue will be created.    
	 * 
	 * @param productSku the ProductSku
	 * @param skuOptionCode the SkuOption Code
	 * @return JpaAdaptorOfSkuOptionValueImpl instance
	 */
	JpaAdaptorOfSkuOptionValueImpl createSkuOptionValue(final ProductSku productSku, final String skuOptionCode) {
		JpaAdaptorOfSkuOptionValueImpl skuOptionValue = (JpaAdaptorOfSkuOptionValueImpl) productSku.getOptionValueMap().get(skuOptionCode);
		if (skuOptionValue == null) {
			skuOptionValue = getBean(ContextIdNames.SKU_OPTION_VALUE_JPA_ADAPTOR);
			productSku.getOptionValueMap().put(skuOptionCode, skuOptionValue);
		}
		return skuOptionValue;
	}

	@Override
	public void populateDTO(final ProductSku source, final ProductSkuDTO target) {
		target.setSkuCode(source.getSkuCode());
		target.setGuid(source.getGuid());

		ShippableItemDTO shippableItemDTO = new ShippableItemDTO();
		shippableItemAdapter.populateDTO(source, shippableItemDTO);
		target.setShippableItem(shippableItemDTO);

		DigitalAssetItemDTO digitalAssetItemDTO = new DigitalAssetItemDTO();
		digitalAssetItemAdapter.populateDTO(source, digitalAssetItemDTO);
		target.setDigitalAssetItem(digitalAssetItemDTO);

		if (source.getProduct().hasMultipleSkus()) {
			populateDTOSkuOptions(source, target);
			target.setImage(source.getImage());

			AttributeGroupDTO attributeGroupDto = new AttributeGroupDTO();
			attributeGroupAdapter.populateDTO(source.getAttributeValueGroup(), attributeGroupDto);
			target.setAttributeGroupDTO(attributeGroupDto);

			ProductSkuAvailabilityDTO availabilityDTO = new ProductSkuAvailabilityDTO();			
			availabilityDTO.setStartDate(source.getStartDate());
			availabilityDTO.setEndDate(source.getEndDate());			
			target.setProductSkuAvailabilityDTO(availabilityDTO);
		}
	}

	/**
	 * Populates DTO SkuOptions.
	 * 
	 * @param productSku the ProductSku
	 * @param productSkuDTO the ProductSkuDTO
	 */
	void populateDTOSkuOptions(final ProductSku productSku, final ProductSkuDTO productSkuDTO) {
		Map<String, SkuOptionValue> skuOptionValueMap = productSku.getOptionValueMap();
		List<SkuOptionDTO> skuOptionDtoList = new ArrayList<SkuOptionDTO>();
		for (Entry<String, SkuOptionValue> entry : skuOptionValueMap.entrySet()) {
			SkuOptionDTO skuOptionDTO = new SkuOptionDTO();
			productSkuOptionAdapter.populateDTO((JpaAdaptorOfSkuOptionValueImpl) entry.getValue(), skuOptionDTO);
			skuOptionDtoList.add(skuOptionDTO);
		}
		productSkuDTO.setSkuOptionList(skuOptionDtoList);
	}

	/**
	 * Gets the shippableItemAdapter.
	 * 
	 * @return the shippableItemAdapter
	 * @see ShippableItemAdapter
	 */
	public ShippableItemAdapter getShippableItemAdapter() {
		return shippableItemAdapter;
	}

	/**
	 * Sets the shippableItemAdapter.
	 * 
	 * @param shippableItemAdapter the shippableItemAdapter to set
	 */
	public void setShippableItemAdapter(final ShippableItemAdapter shippableItemAdapter) {
		this.shippableItemAdapter = shippableItemAdapter;
	}

	/**
	 * Gets the digitalAssetItemAdapter.
	 * 
	 * @return the digitalAssetItemAdapter
	 * @see DigitalAssetItemAdapter
	 */
	public DigitalAssetItemAdapter getDigitalAssetItemAdapter() {
		return digitalAssetItemAdapter;
	}

	/**
	 * Sets the digitalAssetItemAdapter.
	 * 
	 * @param digitalAssetItemAdapter the digitalAssetItemAdapter to set
	 * @see DigitalAssetItemAdapter
	 */
	public void setDigitalAssetItemAdapter(final DigitalAssetItemAdapter digitalAssetItemAdapter) {
		this.digitalAssetItemAdapter = digitalAssetItemAdapter;
	}

	/**
	 * Gets the productSkuOptionAdapter.
	 * 
	 * @return the productSkuOptionAdapter
	 * @see ProductSkuOptionAdapter
	 */
	public ProductSkuOptionAdapter getProductSkuOptionAdapter() {
		return productSkuOptionAdapter;
	}

	/**
	 * Sets the productSkuOptionAdapter.
	 * 
	 * @param productSkuOptionAdapter the productSkuOptionAdapter to set
	 * @see ProductSkuOptionAdapter
	 */
	public void setProductSkuOptionAdapter(final ProductSkuOptionAdapter productSkuOptionAdapter) {
		this.productSkuOptionAdapter = productSkuOptionAdapter;
	}

	/**
	 * Gets the attributeGroupAdapter.
	 * 
	 * @return the attributeGroupAdapter
	 * @see AttributeGroupAdapter
	 */
	public AttributeGroupAdapter getAttributeGroupAdapter() {
		return attributeGroupAdapter;
	}

	/**
	 * Sets the attributeGroupAdapter.
	 * 
	 * @param attributeGroupAdapter the attributeGroupAdapter to set
	 * @see AttributeGroupAdapter
	 */
	public void setAttributeGroupAdapter(final AttributeGroupAdapter attributeGroupAdapter) {
		this.attributeGroupAdapter = attributeGroupAdapter;
	}
}
