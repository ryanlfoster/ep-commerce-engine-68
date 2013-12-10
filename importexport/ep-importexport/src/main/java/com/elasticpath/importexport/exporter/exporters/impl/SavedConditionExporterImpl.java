package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.tag.ConditionalExpressionDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Exporter for Saved TagCondition (ConditionalExpression) objects. 
 * This exporter is fairly simple as ConditionalExpression have no dependent elements.  
 *
 */
public class SavedConditionExporterImpl extends AbstractExporterImpl<ConditionalExpression, ConditionalExpressionDTO, String> {

	private static final Logger LOG = Logger.getLogger(SavedConditionExporterImpl.class);

	private ImportExportSearcher importExportSearcher;
	private TagConditionService tagConditionService;
	private DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> domainAdapter;
	private List<String> savedConditionGuids;

	@Override
	protected List<ConditionalExpression> findByIDs(final List<String> subList) {
		List<ConditionalExpression> csList = new ArrayList<ConditionalExpression>();

		for (String guid : subList) {
			ConditionalExpression current = getTagConditionService().findByGuid(guid);
			
			if (current != null) {
				csList.add(current);
			}
		}
		return csList;
	}

	@Override
	protected List<String> getListExportableIDs() {
		return savedConditionGuids;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		savedConditionGuids = new ArrayList<String>();
		List<String> conditionGuids = new ArrayList<String>();
		conditionGuids.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.SAVED_CONDITION));
		 
		for (String guid : conditionGuids) {
			ConditionalExpression current = getTagConditionService().findByGuid(guid);			
			if (current != null) {
				savedConditionGuids.add(current.getGuid());
			}
		}
		
		LOG.info("Saved Condition Export \n\t" + savedConditionGuids.size() 
				+ " Saved Condition Guids found for export [" + savedConditionGuids + "]");
	}
	
	@Override
	protected Class<? extends ConditionalExpressionDTO> getDtoClass() {
		return ConditionalExpressionDTO.class;
	}
	
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { ConditionalExpression.class };
	}

	public JobType getJobType() {
		return JobType.SAVED_CONDITION;
	}

	private TagConditionService getTagConditionService() {
		return tagConditionService;
	}

	private ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public void setTagConditionService(final TagConditionService tagConditionService) {
		this.tagConditionService = tagConditionService;
	}

	public void setAdapter(final DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}
	
	@Override
	protected DomainAdapter<ConditionalExpression, ConditionalExpressionDTO> getDomainAdapter() {
		return domainAdapter;
	}
	
}