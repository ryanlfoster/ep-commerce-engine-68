package com.elasticpath.importexport.common.adapters.category;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>Category</code> and <code>LinkedCategoryDTO</code> objects.
 */
public class LinkedCategoryAdapter extends AbstractDomainAdapterImpl<Category, LinkedCategoryDTO> {

	@Override
	public void populateDTO(final Category linkedCategory, final LinkedCategoryDTO linkedCategoryDTO) {
		linkedCategoryDTO.setVirtualCatalogCode(linkedCategory.getCatalog().getCode());
		linkedCategoryDTO.setOrder(linkedCategory.getOrdering());
		linkedCategoryDTO.setExcluded(!linkedCategory.isIncluded());
	}

	@Override
	public void populateDomain(final LinkedCategoryDTO linkedCategoryDTO, final Category linkedCategory) {
		linkedCategory.setOrdering(linkedCategoryDTO.getOrder());
		linkedCategory.setCatalog(getCachingService().findCatalogByCode(linkedCategoryDTO.getVirtualCatalogCode()));
		linkedCategory.setIncluded(!linkedCategoryDTO.getExcluded());
	}
	
	@Override
	public LinkedCategoryDTO createDtoObject() {
		return new LinkedCategoryDTO();
	}
	
	@Override
	public Category createDomainObject() {
		return getBean(ContextIdNames.LINKED_CATEGORY);
	}
}
