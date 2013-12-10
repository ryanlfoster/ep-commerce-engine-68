package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricelist.BaseAmountAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Prepares the list of base amount objects based on EPQL query and performs export.
 */
public class BaseAmountExporterImpl extends AbstractExporterImpl<BaseAmount, BaseAmountDTO, String> {
	
	private static final Logger LOG = Logger.getLogger(BaseAmountExporterImpl.class);
	
	private BaseAmountAdapter baseAmountAdapter;

	private BaseAmountService baseAmountService;

	/**
	 * {@inheritDoc} throws RuntimeException can be thrown if rule GUID list could not be initialized.
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		//do nothing
	}

	@Override
	protected List<String> getListExportableIDs() {
		return new ArrayList<String>(getContext().getDependencyRegistry().getDependentGuids(BaseAmount.class));		
	}

	@Override
	protected List<BaseAmount> findByIDs(final List<String> subList) {
		List<BaseAmount> baseAmountGuids = new ArrayList<BaseAmount>(subList.size());
		for (String guid : subList) {
			BaseAmount foundBaseAmount = baseAmountService.findByGuid(guid);
			if (foundBaseAmount == null) {
				LOG.error("Can not retrieve base amount by guid:" + guid);
				continue;
			}
			
			baseAmountGuids.add(foundBaseAmount);
		}
		return baseAmountGuids;
	}

	@Override
	protected DomainAdapter<BaseAmount, BaseAmountDTO> getDomainAdapter() {
		return baseAmountAdapter;
	}

	@Override
	protected Class<? extends BaseAmountDTO> getDtoClass() {
		return BaseAmountDTO.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] {};
	}

	@Override
	public JobType getJobType() {
		return JobType.BASEAMOUNT;
	}

	/**
	 * @param baseAmountAdapter the baseAmountAdapter to set
	 */
	public void setBaseAmountAdapter(final BaseAmountAdapter baseAmountAdapter) {
		this.baseAmountAdapter = baseAmountAdapter;
	}

	/**
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}	
}
