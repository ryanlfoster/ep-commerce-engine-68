package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.dto.catalogs.MultiSkuDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>ProductType</code> and <code>ProductTypeDTO</code> objects.
 */
public class ProductTypeAdapter extends AbstractDomainAdapterImpl<ProductType, ProductTypeDTO> {

	private AttributeGroupHelper attributeGroupHelper;
	
	@Override
	public void populateDTO(final ProductType productType, final ProductTypeDTO productTypeDTO) {
        productTypeDTO.setGuid(productType.getGuid());
		productTypeDTO.setName(productType.getName());
		productTypeDTO.setTemplate(productType.getTemplate());
		productTypeDTO.setAssignedAttributes(createAssignedAttributes(productType));
		productTypeDTO.setDefaultTaxCode(productType.getTaxCode().getCode());
		productTypeDTO.setNoDiscount(productType.isExcludedFromDiscount());
		
		if (productType.isWithMultipleSkus()) {
			productTypeDTO.setMultiSku(createMultiSkuDTO(productType));
		}
		
		
	}


	private MultiSkuDTO createMultiSkuDTO(final ProductType productType) {
		MultiSkuDTO multiSkuDTO = new MultiSkuDTO();
		
		multiSkuDTO.setAssignedSkuOptions(createAssignedSkuOptions(productType));			
		multiSkuDTO.setAssignedAttributes(createAssignedSkuAttributes(productType));
		
		return multiSkuDTO;
	}

	/**
	 * Creates Assigned Attributes List.
	 * 
	 * @param productType the productType
	 * @return List of assigned attributes.
	 */
	List<String> createAssignedAttributes(final ProductType productType) {
		return attributeGroupHelper.createAssignedAttributes(productType.getProductAttributeGroupAttributes());
	}

	/**
	 * Creates Assigned SKU Attributes List.
	 * 
	 * @param productType the productType
	 * @return List of assigned SKU attributes.
	 */
	List<String> createAssignedSkuAttributes(final ProductType productType) {
		return attributeGroupHelper.createAssignedAttributes(productType.getSkuAttributeGroup().getAttributeGroupAttributes());
	}

	@Override
	public void populateDomain(final ProductTypeDTO productTypeDTO, final ProductType productType) {
        if (StringUtils.isNotBlank(productTypeDTO.getGuid())) {
            productType.setGuid(productTypeDTO.getGuid());
        }
		productType.setName(productTypeDTO.getName());
		productType.setTemplate(productTypeDTO.getTemplate());
		productType.setTaxCode(findDefaultTaxCode(productTypeDTO.getDefaultTaxCode()));
		if (productTypeDTO.getNoDiscount() != null) {
			productType.setExcludedFromDiscount(productTypeDTO.getNoDiscount());
		}
		attributeGroupHelper.populateAttributeGroupAttributes(createAttributeGroupAttributes(productType), 
										 					  productTypeDTO.getAssignedAttributes(), 
										 					  ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
		
		populateWithMultipleSkus(productType, productTypeDTO.getMultiSku());
	}

	private void populateWithMultipleSkus(final ProductType productType, final MultiSkuDTO multiSkuDTO) {
		if (multiSkuDTO != null) {
			productType.setWithMultipleSkus(true);
			for (String skuOptionCode : multiSkuDTO.getAssignedSkuOptions()) {
				productType.addOrUpdateSkuOption(findSkuOption(skuOptionCode));
			}
			
			attributeGroupHelper.populateAttributeGroupAttributes(createSkuAttributeGroupAttributes(productType),
													  		      multiSkuDTO.getAssignedAttributes(),
													  		      ContextIdNames.PRODUCT_TYPE_SKU_ATTRIBUTE);
		}
	}

	/**
	 * Creates SKU Attributes Group Attributes Set.
	 * 
	 * @param productType the productType
	 * @return Set of SKU attributes group attributes.
	 */
	Set<AttributeGroupAttribute> createSkuAttributeGroupAttributes(final ProductType productType) {
		final AttributeGroup skuAttributeGroup = productType.getSkuAttributeGroup();
		Set<AttributeGroupAttribute> skuAttributeGroupAttributes = skuAttributeGroup.getAttributeGroupAttributes();
		if (skuAttributeGroupAttributes == null) {
			skuAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
			skuAttributeGroup.setAttributeGroupAttributes(skuAttributeGroupAttributes);
			productType.setSkuAttributeGroup(skuAttributeGroup);
		}
		return skuAttributeGroupAttributes;
	}

	/**
	 * Creates Attributes Group Attributes Set.
	 * 
	 * @param productType the productType
	 * @return Set of attributes group attributes.
	 */
	Set<AttributeGroupAttribute> createAttributeGroupAttributes(final ProductType productType) {
		Set<AttributeGroupAttribute> productAttributeGroupAttributes = productType.getProductAttributeGroupAttributes();
		if (productAttributeGroupAttributes == null) {
			productAttributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
			productType.setProductAttributeGroupAttributes(productAttributeGroupAttributes);
		}
		return productAttributeGroupAttributes;
	}

	/**
	 * Creates Assigned SkuOptions List.
	 * 
	 * @param productType the skuOptions for creating list
	 * @return List for assigned skuOptions
	 */
	List<String> createAssignedSkuOptions(final ProductType productType) {
		final List<String> assignedSkuOptions = new ArrayList<String>();
		for (SkuOption skuOption : productType.getSkuOptions()) {
			assignedSkuOptions.add(skuOption.getOptionKey());
		}
		return assignedSkuOptions;
	}

	/**
	 * Finds for tax code and checks for inconstancy.
	 *
	 * @param defaultTaxCode the tax code string
	 * @return TaxCode instance or throws exception
	 */
	TaxCode findDefaultTaxCode(final String defaultTaxCode) {
		if (defaultTaxCode == null) {
			throw new PopulationRollbackException("IE-10004");
		}
		
		TaxCode taxCode = getCachingService().findTaxCodeByCode(defaultTaxCode);
		if (taxCode == null) {			
			throw new PopulationRollbackException("IE-10005", defaultTaxCode);
		}		
		return taxCode;
	}

	/**
	 * Finds skuOption by code.
	 * 
	 * @param skuOptionCode the code of the skuOption
	 * @throws PopulationRollbackException if skuOption with skuOptionCode does not exist 
	 * @return SkuOption instance if it was found
	 */
	SkuOption findSkuOption(final String skuOptionCode) {
		SkuOption skuOption = getCachingService().findSkuOptionByKey(skuOptionCode);
		if (skuOption == null) {
			throw new PopulationRollbackException("IE-10006", skuOptionCode);
		}
		return skuOption;
	}

	@Override
	public ProductType createDomainObject() {
		return getBean(ContextIdNames.PRODUCT_TYPE);
	}
	
	@Override
	public ProductTypeDTO createDtoObject() {
		return new ProductTypeDTO();
	}

	/**
	 * Gets AttributeGroupHelper.
	 * 
	 * @return AttributeGroupHelper
	 */
	public AttributeGroupHelper getAttributeGroupHelper() {
		return attributeGroupHelper;
	}

	/**
	 * Sets AttributeGroupHelper.
	 * 
	 * @param attributeGroupHelper the AttributeGroupHelper instance
	 */
	public void setAttributeGroupHelper(final AttributeGroupHelper attributeGroupHelper) {
		this.attributeGroupHelper = attributeGroupHelper;
	}
}
