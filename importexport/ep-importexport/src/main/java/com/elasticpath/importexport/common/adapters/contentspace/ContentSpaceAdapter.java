package com.elasticpath.importexport.common.adapters.contentspace;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.contentspace.ContentSpaceDTO;

/**
 * Helper class for mediating data from ContentSpace entities to ContentSpaceDTO. 
 *
 */
public class ContentSpaceAdapter extends AbstractDomainAdapterImpl<ContentSpace, ContentSpaceDTO>  {

	/**
	 * Populate a dto from an entity.
	 * @param source the entity
	 * @param target the dto
	 */
	public void populateDTO(final ContentSpace source, final ContentSpaceDTO target) {
		target.setGuid(source.getGuid());
		target.setDescription(source.getDescription());
		target.setName(source.getTargetId());
	}

	/**
	 * Populate an entity from a dto.
	 * @param source the dto
	 * @param target the entity
	 */
	public void populateDomain(final ContentSpaceDTO source, final ContentSpace target) {
		target.setGuid(source.getGuid());
		target.setTargetId(source.getName());
		target.setDescription(source.getDescription());
	}
	
	@Override
	public ContentSpaceDTO createDtoObject() {
		return new ContentSpaceDTO();
	}

	@Override
	public ContentSpace createDomainObject() {
		return getBean(ContextIdNames.CONTENTSPACE);
	}
}
