/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.changesetsupport.BusinessObjectDescriptorLocator;
import com.elasticpath.importexport.importer.changesetsupport.ObjectDescriptorPopulator;

/**
 * The default implementation of the {@link BusinessObjectDescriptorLocator} 
 * which uses a map of class to {@link ObjectDescriptorPopulator} instances.
 */
public class BusinessObjectDescriptorLocatorImpl implements BusinessObjectDescriptorLocator {
	
	private static final Logger LOG = Logger.getLogger(BusinessObjectDescriptorLocatorImpl.class);
	
	
	private Map<Class<Dto>, ObjectDescriptorPopulator> objectDescriptorInfoMap;
	private BeanFactory beanFactory;
	
	@Override
	public BusinessObjectDescriptor locateObjectDescriptor(final Dto dto) {

		try { 
			ObjectDescriptorPopulator populator = objectDescriptorInfoMap.get(dto.getClass());
			if (populator == null) {
				LOG.debug("Could not find object descriptor populator for DTO instance: " + dto);
				return null;
			}
			return createBusinessObjectDescriptor(populator, dto);
		} catch (Exception exception) {
			LOG.error("Could not retrieve the business object descriptor for DTO instance: " + dto);
			throw new ImportRuntimeException("", exception);
		}			
	}

	/**
	 *
	 */
	private BusinessObjectDescriptor createBusinessObjectDescriptor(final ObjectDescriptorPopulator objectDescriptorPopulator, final Dto dto) 
		throws IllegalArgumentException, SecurityException, IllegalAccessException, 
				InvocationTargetException, NoSuchMethodException {
		
		BusinessObjectDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		return objectDescriptorPopulator.populate(descriptor, dto);
	}

	/**
	 *
	 * @param objectDescriptorInfoMap the objectDescriptorInfoMap to set
	 */
	public void setObjectDescriptorInfoMap(final Map<Class<Dto>, ObjectDescriptorPopulator> objectDescriptorInfoMap) {
		this.objectDescriptorInfoMap = objectDescriptorInfoMap;
	}

	/**
	 *
	 * @return the objectDescriptorInfoMap
	 */
	protected Map<Class<Dto>, ObjectDescriptorPopulator> getObjectDescriptorInfoMap() {
		return objectDescriptorInfoMap;
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
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	
}
