package com.elasticpath.importexport.exporter.exporters.impl;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporter;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.persistence.api.Persistable;

/**
 * Helper impelmentation of {@link DependentExporter}.
 * 
 * @param <DOMAIN> the dependent domain object that should be exported
 * @param <DTO> the dto object that is corresponded to {@code DOMAIN} object
 * @param <PARENT> parent {@link Dto} this exporter is dependent on
 */
public abstract class AbstractDependentExporterImpl<DOMAIN extends Persistable, DTO extends Dto, PARENT extends Dto> implements
		DependentExporter<DOMAIN, DTO, PARENT> {
	private ExportContext context;
	private DomainAdapter<DOMAIN, DTO> domainAdapter;
	private DependentExporterFilter filter;

	@Override
	public DomainAdapter<DOMAIN, DTO> getDomainAdapter() {
		return domainAdapter;
	}

	public void setDomainAdapter(final DomainAdapter<DOMAIN, DTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	@Override
	public void initialize(final ExportContext context, final DependentExporterFilter filter) throws ConfigurationException {
		this.context = context;
		this.filter = filter;
	}

	protected ExportContext getContext() {
		return context;
	}

	public DependentExporterFilter getFilter() {
		return filter;
	}
}
