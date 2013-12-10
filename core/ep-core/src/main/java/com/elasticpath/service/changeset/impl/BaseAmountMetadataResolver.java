/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.changeset.BusinessObjectMetadataResolver;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Resolves metadata for base amount objects.
 * 
 * @since 6.2.2
 */
public class BaseAmountMetadataResolver extends AbstractMetadataResolverImpl {

	private List<BusinessObjectMetadataResolver> partResolvers;
	
	private BaseAmountService baseAmountService;
	
	private BeanFactory beanFactory;
	
	/**
	 * This resolver is only valid for "Base Amount" objects.
	 * 
	 * @param objectType the type of object being resolved
	 * @return true if this resolver is valid
	 */
	@Override
	protected boolean isValidResolverForObjectType(final String objectType) {
		return "Base Amount".equals(objectType);
	}

	/**
	 * Resolve the name for a Base Amount by retrieving the base amount from the system, and using other
	 * resolvers to get the relevant names.
	 * 
	 * @param objectDescriptor the descriptor of the base amount
	 * @return the metadata
	 */
	@Override
	protected Map<String, String> resolveMetaDataInternal(final BusinessObjectDescriptor objectDescriptor) {
		BaseAmount baseAmount = getBaseAmountService().findByGuid(objectDescriptor.getObjectIdentifier());
		if (baseAmount == null) {
			return Collections.emptyMap();
		}
		String relatedObjectName = getRelatedObjectName(baseAmount.getObjectGuid(), getObjectTypeMap().get(baseAmount.getObjectType()));
		StringBuilder name = new StringBuilder();
		if (!StringUtils.isEmpty(relatedObjectName)) {
			name.append(relatedObjectName);
		}
		name.append(" @ ");
		name.append(baseAmount.getQuantity());
		String priceListName = getRelatedObjectName(baseAmount.getPriceListDescriptorGuid(), "Price List Descriptor");
		if (!StringUtils.isEmpty(priceListName)) {
			name.append(" - ");
			name.append(priceListName);
		}
		
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("objectName", name.toString());
		return metadata;
	}

	/**
	 * Get the name of the related object. This may be the object that the base amount is for
	 * (i.e. Product or Sku) or the price list that the base amount belongs to.
	 * 
	 * @param objectGuid the guid of the related object
	 * @param objectType the object type
	 * @return the name of the related object
	 */
	protected String getRelatedObjectName(final String objectGuid, final String objectType) {
		BusinessObjectDescriptor targetObjectDescriptor = getBeanFactory().getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		targetObjectDescriptor.setObjectIdentifier(objectGuid);
		targetObjectDescriptor.setObjectType(objectType);
		String name = null;
		for (BusinessObjectMetadataResolver resolver : getPartResolvers()) {
			Map<String, String> partMetadata = resolver.resolveMetaData(targetObjectDescriptor);
			if (MapUtils.isNotEmpty(partMetadata)) {
				name = partMetadata.get("objectName");
				break;
			}
		}
		return name;
	}
	
	/**
	 * Get the map of Base Amount object types to Business Object types.
	 * 
	 * @return a map of base amount "object type" to business object type
	 */
	protected Map<String, String> getObjectTypeMap() {
		Map<String, String> resolverMap = new HashMap<String, String>();
		resolverMap.put("PRODUCT", "Product");
		resolverMap.put("SKU", "Product SKU");
		return resolverMap;
	}

	/**
	 *
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 *
	 * @return the baseAmountService
	 */
	public BaseAmountService getBaseAmountService() {
		return baseAmountService;
	}

	/**
	 *
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 *
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 *
	 * @param partResolvers the partResolvers to set
	 */
	public void setPartResolvers(final List<BusinessObjectMetadataResolver> partResolvers) {
		this.partResolvers = partResolvers;
	}

	/**
	 *
	 * @return the partResolvers
	 */
	public List<BusinessObjectMetadataResolver> getPartResolvers() {
		return partResolvers;
	}

}
