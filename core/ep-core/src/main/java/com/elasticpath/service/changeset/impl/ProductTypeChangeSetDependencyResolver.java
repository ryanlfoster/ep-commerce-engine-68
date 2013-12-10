package com.elasticpath.service.changeset.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;

/**
 * Dependency Resolver for product type.
 */
public class ProductTypeChangeSetDependencyResolver implements ChangeSetDependencyResolver {
	
	
	private ProductTypeService productTypeService;

	@Override
	public Set<?> getChangeSetDependency(final Object object) {
		if (object instanceof ProductType) {
			ProductType productType = (ProductType) object;
			Set<Object> dependencies = new LinkedHashSet<Object>();
			dependencies.addAll(getAttributes(productType));
			dependencies.addAll(getSkuOption(productType));
			
			return dependencies;
		}
		
		return Collections.emptySet();
	}


	private Collection<? extends Object> getSkuOption(final ProductType productType) {
		return productType.getSkuOptions();
	}

	private Collection<? extends Object> getAttributes(final ProductType productType) {
		Set<Object> dependentAttributes = new LinkedHashSet<Object>();
		Set<AttributeGroupAttribute> productAttributeGroupAttributes = productType.getProductAttributeGroupAttributes();
		
		for (AttributeGroupAttribute attributeGroupAttribute : productAttributeGroupAttributes) {
			dependentAttributes.add(attributeGroupAttribute.getAttribute());
		}
		
		AttributeGroup skuAttributeGroup = productType.getSkuAttributeGroup();
		
		for (AttributeGroupAttribute attributeGroupeAttribute : skuAttributeGroup.getAttributeGroupAttributes()) {
			dependentAttributes.add(attributeGroupeAttribute.getAttribute());
		}
	
		AttributeGroup productAttributeGroup = productType.getProductAttributeGroup();
		for (AttributeGroupAttribute attributeGroupAttribute : productAttributeGroup.getAttributeGroupAttributes()) {
			dependentAttributes.add(attributeGroupAttribute.getAttribute());
		}
		
		return dependentAttributes;
	}


	@Override
	public Object getObject(final BusinessObjectDescriptor object, final Class<?> objectClass) {
		if (ProductType.class.isAssignableFrom(objectClass)) {
			return getProductTypeService().findByGuid(object.getObjectIdentifier());
		}
		return null;
	}


	/**
	 *
	 * @param productTypeService the productTypeService to set
	 */
	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}


	/**
	 *
	 * @return the productTypeService
	 */
	public ProductTypeService getProductTypeService() {
		return productTypeService;
	}

}
