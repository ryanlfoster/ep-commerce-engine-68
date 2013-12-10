package com.elasticpath.service.changeset.impl;

import java.util.Collections;
import java.util.Set;

import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * The BaseAmount change set dependency resolver class.
 */
public class BaseAmountChangeSetDependencyResolverImpl implements ChangeSetDependencyResolver {
	
	private BaseAmountService baseAmountService;
	private PriceListService priceListService;
	
	@Override
	public Set< ? > getChangeSetDependency(final Object object) {
		if (object instanceof BaseAmount) {
			String pldGuid = ((BaseAmount) object).getPriceListDescriptorGuid();
			return Collections.singleton(priceListService.getPriceListDescriptor(pldGuid));
		} 
		return Collections.emptySet();
	}

	@Override
	public BaseAmount getObject(final BusinessObjectDescriptor businessObjectDescriptor, final Class< ? > objectClass) {
		if (BaseAmount.class.isAssignableFrom(objectClass)) {
			return baseAmountService.findByGuid(businessObjectDescriptor.getObjectIdentifier());
		}
		return null;
	}

	/**
	 * Set BaseAmountService.
	 * 
	 * @param baseAmountService The BaseAmountService.
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 * Set PriceListService.
	 * 
	 * @param priceListService The PriceListService.
	 */
	public void setPriceListService(final PriceListService priceListService) {
		this.priceListService = priceListService;
	}

}
