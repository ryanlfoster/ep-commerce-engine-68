package com.elasticpath.importexport.importer.importers.objectdescriptorbuilder.impl;

import java.util.Set;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.Persistable;

/**
 * Default implementation of object descriptor builder.
 */
public abstract class AbstractObjectDescriptorBuilder implements ObjectDescriptorBuilder {

	@Override
	public Set<BusinessObjectDescriptor> buildDescriptors(final Persistable persistable, final Dto dto) {
		if (persistable == null) {
			return buildDescriptorsForNewObject(dto);
		}
		return buildDescriptorsForPersistenceObject(persistable, dto);
	}

	/**
	 * Builds descriptors for persistable objects. 
	 * 
	 * @param persistable persistable object
	 * @param dto corresponding data transfer object
	 * @return set of business object descriptors
	 */
	protected abstract Set<BusinessObjectDescriptor> buildDescriptorsForPersistenceObject(final Persistable persistable, final Dto dto);

	/**
	 * Builds descriptors for new object. 
	 * 
	 * @param dto transfer object
	 * @return set of business object descriptors
	 */
	protected abstract Set<BusinessObjectDescriptor> buildDescriptorsForNewObject(final Dto dto);
}
