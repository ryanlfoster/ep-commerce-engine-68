package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.contentspace.ContentSpaceDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.contentspace.ContentSpaceService;

/**
 * Exporter for ContentSpace objects. This exporter is fairly simple as ContentSpaces have no dependent elements.
 *
 */
public class ContentSpaceExporterImpl extends AbstractExporterImpl<ContentSpace, ContentSpaceDTO, String> {

	private static final Logger LOG = Logger.getLogger(ContentSpaceExporterImpl.class);

	private ImportExportSearcher importExportSearcher;
	private ContentSpaceService contentSpaceService;
	private DomainAdapter<ContentSpace, ContentSpaceDTO> domainAdapter;
	private List<String> contentSpaceGuids;

	@Override
	protected List<ContentSpace> findByIDs(final List<String> subList) {
		List<ContentSpace> csList = new ArrayList<ContentSpace>();

		for (String guid : subList) {
			ContentSpace current = getContentSpaceService().findByGuid(guid);
			if (current != null) {
				csList.add(current);
			}
		}
		return csList;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(ContentSpace.class)) {
			contentSpaceGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(ContentSpace.class));
		}
		return contentSpaceGuids;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		contentSpaceGuids = new ArrayList<String>();
		contentSpaceGuids.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.CONTENT_SPACE));

		LOG.info("Content Space Export \n\t" + contentSpaceGuids.size() + " Content Space Guids found for export [" + contentSpaceGuids + "]");
	}

	@Override
	protected Class<? extends ContentSpaceDTO> getDtoClass() {
		return ContentSpaceDTO.class;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { ContentSpace.class };
	}

	public JobType getJobType() {
		return JobType.CONTENTSPACE;
	}

	private ContentSpaceService getContentSpaceService() {
		return contentSpaceService;
	}

	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setContentSpaceService(final ContentSpaceService contentSpaceService) {
		this.contentSpaceService = contentSpaceService;
	}

	public void setDomainAdapter(final DomainAdapter<ContentSpace, ContentSpaceDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	@Override
	protected DomainAdapter<ContentSpace, ContentSpaceDTO> getDomainAdapter() {
		return domainAdapter;
	}

}
