/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport.impl;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.changesetsupport.ObjectDescriptorPopulator;

/**
 * The default object descriptor populator.
 */
public class MethodObjectDescriptorPopulator implements ObjectDescriptorPopulator {
	
	private String objectIdGetter;
	private String objectType;
	
	/**
	 *
	 * @return the objectIdGetter
	 */
	protected String getObjectIdGetter() {
		return objectIdGetter;
	}
	
	/**
	 *
	 * @param objectIdGetter the objectIdGetter to set
	 */
	public void setObjectIdGetter(final String objectIdGetter) {
		this.objectIdGetter = objectIdGetter;
	}
	
	/**
	 *
	 * @return the objectType
	 */
	protected String getObjectType() {
		return objectType;
	}
	
	/**
	 *
	 * @param objectType the objectType to set
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getObjectType()).append(getObjectIdGetter()).toString();
	}

	@Override
	public BusinessObjectDescriptor populate(final BusinessObjectDescriptor descriptor, final Dto dto) {
		descriptor.setObjectType(getObjectType());
		try {
			descriptor.setObjectIdentifier((String) dto.getClass().getMethod(getObjectIdGetter()).invoke(dto));
		} catch (Exception exc) {
			throw new ImportRuntimeException("IE-30510", exc);
		}
		return descriptor;
	}

}
