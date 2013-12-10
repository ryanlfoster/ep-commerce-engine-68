package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.dynamiccontent.DynamicContentAdapter;
import com.elasticpath.importexport.common.adapters.dynamiccontent.ParameterValueAdapter;
import com.elasticpath.importexport.common.dto.dynamiccontent.DynamicContentDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * Exporter for DynamicContent objects, also exports the ParameterValue objects. Because ParameterValue objects are only referenced and persisted
 * through DynamicContent they are managed directly here instead of in a DependentExporter.
 */
public class DynamicContentExporterImpl extends AbstractExporterImpl<DynamicContent, DynamicContentDTO, String> {

	private static final Logger LOG = Logger.getLogger(DynamicContentExporterImpl.class);

	private ImportExportSearcher importExportSearcher;

	private DynamicContentAdapter dynamicContentAdapter;

	private ParameterValueAdapter parameterValueAdapter;

	private DynamicContentService dynamicContentService;

	private Set<String> dynamicContentNames;

	@Override
	protected List<DynamicContent> findByIDs(final List<String> subList) {
		List<DynamicContent> dynamicContentList = new ArrayList<DynamicContent>();
		for (String guid : subList) {
			DynamicContent dynamicContent = dynamicContentService.findByGuid(guid);
			dynamicContentList.add(dynamicContent);
		}

		return dynamicContentList;
	}

	@Override
	protected DomainAdapter<DynamicContent, DynamicContentDTO> getDomainAdapter() {
		return dynamicContentAdapter;
	}

	@Override
	protected Class<? extends DynamicContentDTO> getDtoClass() {
		return DynamicContentDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {

		if (getContext().getDependencyRegistry().supportsDependency(DynamicContent.class)) {
			dynamicContentNames.addAll(getContext().getDependencyRegistry().getDependentGuids(DynamicContent.class));
		}

		return new ArrayList<String>(dynamicContentNames);
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		dynamicContentNames = new HashSet<String>();
		dynamicContentNames.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.DYNAMICCONTENT));
		LOG.info("Found " + dynamicContentNames.size() + " dynamicContent objects for export.");
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { DynamicContent.class };
	}

	public JobType getJobType() {
		return JobType.DYNAMICCONTENT;
	}

	/**
	 * @return the dynamicContentAdapter
	 */
	public DynamicContentAdapter getDynamicContentAdapter() {
		return dynamicContentAdapter;
	}

	/**
	 * @param dynamicContentAdapter the dynamicContentAdapter to set
	 */
	public void setDynamicContentAdapter(final DynamicContentAdapter dynamicContentAdapter) {
		this.dynamicContentAdapter = dynamicContentAdapter;
	}

	/**
	 * @return the parameterValueAdapter
	 */
	public ParameterValueAdapter getParameterValueAdapter() {
		return parameterValueAdapter;
	}

	/**
	 * @param parameterValueAdapter the parameterValueAdapter to set
	 */
	public void setParameterValueAdapter(final ParameterValueAdapter parameterValueAdapter) {
		this.parameterValueAdapter = parameterValueAdapter;
	}

	/**
	 * @return the dynamicContentService
	 */
	public DynamicContentService getDynamicContentService() {
		return dynamicContentService;
	}

	/**
	 * @param dynamicContentService the dynamicContentService to set
	 */
	public void setDynamicContentService(final DynamicContentService dynamicContentService) {
		this.dynamicContentService = dynamicContentService;
	}

	/**
	 * @return The importExportSearcher.
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * @param importExportSearcher The ImportExportSearcher.
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
