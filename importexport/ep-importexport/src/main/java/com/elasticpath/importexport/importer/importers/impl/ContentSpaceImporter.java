package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.contentspace.ContentSpaceDTO;
import com.elasticpath.service.contentspace.ContentSpaceService;

/**
 * An importer for {@link ContentSpace}.
 */
public class ContentSpaceImporter extends AbstractImporterImpl<ContentSpace, ContentSpaceDTO> {

	private DomainAdapter<ContentSpace, ContentSpaceDTO> domainAdapter;

	private ContentSpaceService contentSpaceService;

	@Override
	public String getImportedObjectName() {
		return ContentSpaceDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final ContentSpaceDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<ContentSpace, ContentSpaceDTO> getDomainAdapter() {
		return domainAdapter;
	}

	@Override
	protected ContentSpace findPersistentObject(final ContentSpaceDTO dto) {
		return contentSpaceService.findByGuid(dto.getGuid());
	}

	@Override
	protected void setImportStatus(final ContentSpaceDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	public void setDomainAdapter(final DomainAdapter<ContentSpace, ContentSpaceDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	public void setContentSpaceService(final ContentSpaceService contentSpaceService) {
		this.contentSpaceService = contentSpaceService;
	}

	@Override
	public Class<? extends ContentSpaceDTO> getDtoClass() {
		return ContentSpaceDTO.class;
	}
}