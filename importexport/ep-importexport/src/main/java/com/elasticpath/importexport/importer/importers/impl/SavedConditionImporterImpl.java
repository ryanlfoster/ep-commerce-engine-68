package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Imports tag conditions. 
 */
public class SavedConditionImporterImpl extends AbstractImporterImpl<ConditionalExpression, ConditionalExpressionDTO> {

	private DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> domainAdapter;
	
	private TagConditionService tagConditionService; 

	@Override
	public String getImportedObjectName() {
		return ConditionalExpressionDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final ConditionalExpressionDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> getDomainAdapter() {
		return domainAdapter;
	}

	@Override
	protected ConditionalExpression findPersistentObject(final ConditionalExpressionDTO dto) {
		return tagConditionService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final ConditionalExpressionDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	/**
	 * Sets domain adapter.
	 * @param domainAdapter domain adapter
	 */
	public void setDomainAdapter(final DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	/**
	 * Sets tag condition service.
	 * @param tagConditionService tag condition service
	 */
	public void setTagConditionService(final TagConditionService tagConditionService) {
		this.tagConditionService = tagConditionService;
	}

	@Override
	public Class<? extends ConditionalExpressionDTO> getDtoClass() {
		return ConditionalExpressionDTO.class;
	}
}
