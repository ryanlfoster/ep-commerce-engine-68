package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.catalogs.AttributeAdapter;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.attribute.AttributeService;

/**
 * Exporter for {@link CustomerProfileAttribute} objects; used in importexport.
 */
public class CustomerProfileAttributeExporter extends AbstractExporterImpl<Attribute, AttributeDTO, String> {

	private static final Logger LOG = Logger.getLogger(CustomerProfileAttributeExporter.class);

	private List<String> attributeKeys;

	private AttributeAdapter attributeAdapter;

	private AttributeService attributeService;

	private ImportExportSearcher importExportSearcher;

	public JobType getJobType() {
		return JobType.CUSTOMERPROFILE_ATTRIBUTE;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { CustomerProfileAttribute.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		attributeKeys = new ArrayList<String>();
		attributeKeys.addAll(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.CUSTOMERPROFILE_ATTRIBUTE));
		LOG.info("The list for " + attributeKeys.size() + " customer profile attribute keys retrieved from the database.");
	}

	@Override
	protected Class<? extends AttributeDTO> getDtoClass() {
		return AttributeDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(CustomerProfileAttribute.class)) {
			attributeKeys.addAll(getContext().getDependencyRegistry().getDependentGuids(CustomerProfileAttribute.class));
		}

		return attributeKeys;
	}

	@Override
	protected List<Attribute> findByIDs(final List<String> subList) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (String key : subList) {
			Attribute attribute = attributeService.findByKey(key);
			if (attribute != null && attribute.getAttributeUsage().getValue() == AttributeUsageImpl.CUSTOMERPROFILE_USAGE.getValue()) {
				attributes.add(attribute);
			}
		}
		return attributes;
	}

	@Override
	protected DomainAdapter<Attribute, AttributeDTO> getDomainAdapter() {
		return attributeAdapter;
	}

	public void setAttributeAdapter(final AttributeAdapter attributeAdapter) {
		this.attributeAdapter = attributeAdapter;
	}

	public void setAttributeService(final AttributeService attributeService) {
		this.attributeService = attributeService;
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