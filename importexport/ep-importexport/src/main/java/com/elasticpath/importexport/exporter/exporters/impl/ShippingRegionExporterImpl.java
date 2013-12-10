package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.shipping.region.ShippingRegionDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Exporter for Shipping Regions. 
 */
public class ShippingRegionExporterImpl extends AbstractExporterImpl<ShippingRegion, ShippingRegionDTO, String> {

	private static final Logger LOG = Logger.getLogger(ShippingRegionExporterImpl.class);

	private ImportExportSearcher importExportSearcher;
	private ShippingRegionService shippingRegionService;
	private DomainAdapter<ShippingRegion, ShippingRegionDTO> domainAdapter;

	private List<String> shippingRegionNames;

	@Override
	protected List<ShippingRegion> findByIDs(final List<String> subList) {
		List<ShippingRegion> regionList = new ArrayList<ShippingRegion>();

		for (String name : subList) {
			//name is unique so no worries doing this
			ShippingRegion current = getShippingRegionService().findByName(name); 
			if (current != null) {
				regionList.add(current);
			}
		}
		return regionList;
	}

	@Override
	protected DomainAdapter<ShippingRegion, ShippingRegionDTO> getDomainAdapter() {
		return domainAdapter;
	}

	@Override
	protected Class<? extends ShippingRegionDTO> getDtoClass() {
		return ShippingRegionDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(ShippingRegion.class)) {
			shippingRegionNames.addAll(getContext().getDependencyRegistry().getDependentGuids(ShippingRegion.class));
		}
		return shippingRegionNames;
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		shippingRegionNames = new ArrayList<String>();
		shippingRegionNames.addAll(
				getImportExportSearcher().searchGuids(
						getContext().getSearchConfiguration(),
						EPQueryType.SHIPPING_REGION));
		
		LOG.info("Shipping Region Export \n\t" + shippingRegionNames.size()
				+ " Shipping Region Names found for export [" + shippingRegionNames + "]");
}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	public ShippingRegionService getShippingRegionService() {
		return shippingRegionService;
	}

	public void setShippingRegionService(final ShippingRegionService shippingRegionService) {
		this.shippingRegionService = shippingRegionService;
	}

	public void setDomainAdapter(
			final DomainAdapter<ShippingRegion, ShippingRegionDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { ShippingRegion.class };
	}

	public JobType getJobType() {
		return JobType.SHIPPING_REGION;
	}


}
