package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.targetedselling.DynamicContentDeliveryDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * An exporter for DCDs.
 */
public class DynamicContentDeliveryExporter extends AbstractExporterImpl<DynamicContentDelivery, DynamicContentDeliveryDTO, String> {

	private ImportExportSearcher importExportSearcher;

	private List<String> dcdGuids;

	private static final Logger LOG = Logger.getLogger(DynamicContentDeliveryExporter.class);

	private DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> adapter;

	private DynamicContentDeliveryService dcdService;

	@Override
	protected List<DynamicContentDelivery> findByIDs(final List<String> subList) {
		List<DynamicContentDelivery> dcdList = new ArrayList<DynamicContentDelivery>();

		for (String guid : subList) {
			DynamicContentDelivery dcd = dcdService.findByGuid(guid);
			dcdList.add(dcd);
		}
		return dcdList;
	}

	@Override
	protected void addDependencies(final List<DynamicContentDelivery> dcds, final DependencyRegistry dependencyRegistry) {
		for (DynamicContentDelivery dcd : dcds) {
			if (dependencyRegistry.supportsDependency(DynamicContent.class)) {
				dependencyRegistry.addGuidDependency(DynamicContent.class, dcd.getDynamicContent().getGuid());
			}
		}
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		dcdGuids = new ArrayList<String>();
		dcdGuids.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.DYNAMICCONTENTDELIVERY));
		LOG.info(dcdGuids.size() + " Dynamic Content Delivery Guids found for export [" + dcdGuids + "]");
	}

	public JobType getJobType() {
		return JobType.DYNAMICCONTENTDELIVERY;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { DynamicContentDelivery.class };
	}

	@Override
	protected Class<? extends DynamicContentDeliveryDTO> getDtoClass() {
		return DynamicContentDeliveryDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(DynamicContentDelivery.class)) {
			dcdGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(DynamicContentDelivery.class));
		}

		return dcdGuids;
	}

	@Override
	protected DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> getDomainAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(final DomainAdapter<DynamicContentDelivery, DynamicContentDeliveryDTO> adapter) {
		this.adapter = adapter;
	}

	/**
	 * @param dcdService the dcdService to set
	 */
	public void setDynamicContentDeliveryService(final DynamicContentDeliveryService dcdService) {
		this.dcdService = dcdService;
	}

	/**
	 * @return the dcdService
	 */
	public DynamicContentDeliveryService getDynamicContentDeliveryService() {
		return dcdService;
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
