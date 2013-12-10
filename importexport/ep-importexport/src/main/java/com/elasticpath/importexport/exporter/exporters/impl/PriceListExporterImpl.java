package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Prepares the list of price list objects based on EPQL query and performs export.
 */
public class PriceListExporterImpl extends AbstractExporterImpl<PriceListDescriptor, PriceListDescriptorDTO, String> {

	private static final Logger LOG = Logger.getLogger(PriceListExporterImpl.class);

	private List<String> priceListGUIDs = Collections.emptyList();

	private PriceListDescriptorService priceListDescriptorService;

	private DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> priceListDescriptorAdapter;

	private ImportExportSearcher importExportSearcher;

	private BaseAmountService baseAmountService;

	/**
	 * {@inheritDoc} throws RuntimeException can be thrown if rule GUID list could not be initialized.
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		List<String> searchGuids = importExportSearcher.searchGuids(getContext().getSearchConfiguration(), EPQueryType.PRICELIST);
		priceListGUIDs = new ArrayList<String>(searchGuids);
		LOG.info("The GUIDs list for " + priceListGUIDs.size() + " price list objects are retrieved from database.");
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(PriceListDescriptor.class)) {
			priceListGUIDs.addAll(getContext().getDependencyRegistry().getDependentGuids(PriceListDescriptor.class));
		}

		return priceListGUIDs;
	}

	@Override
	protected List<PriceListDescriptor> findByIDs(final List<String> subList) {
		List<PriceListDescriptor> priceListDescriptorGuids = new ArrayList<PriceListDescriptor>(subList.size());
		for (String guid : subList) {
			PriceListDescriptor foundPriceListDescriptor = priceListDescriptorService.findByGuid(guid);
			if (foundPriceListDescriptor == null) {
				LOG.error("Can not retrieve price list decriptor by guid:" + guid);
				continue;
			}

			priceListDescriptorGuids.add(foundPriceListDescriptor);
		}
		return priceListDescriptorGuids;
	}

	@Override
	protected DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> getDomainAdapter() {
		return priceListDescriptorAdapter;
	}

	@Override
	protected Class<? extends PriceListDescriptorDTO> getDtoClass() {
		return PriceListDescriptorDTO.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] { PriceListDescriptor.class, BaseAmount.class };
	}

	@Override
	public JobType getJobType() {
		return JobType.PRICELISTDESCRIPTOR;
	}

	/**
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	@Override
	protected void addDependencies(final List<PriceListDescriptor> objects, final DependencyRegistry dependencyRegistry) {
		addBaseAmountsToDependencyList(objects, dependencyRegistry);
	}

	private void addBaseAmountsToDependencyList(final List<PriceListDescriptor> objects, final DependencyRegistry dependencyRegistry) {
		final Set<String> dependentBaseAmountGuids = new HashSet<String>();
		for (PriceListDescriptor pld : objects) {
			Collection<BaseAmount> findBaseAmounts = getBaseAmountsOfPriceList(pld);
			addBaseAmountsToDependencyList(dependentBaseAmountGuids, findBaseAmounts);
		}
		dependencyRegistry.addGuidDependencies(BaseAmount.class, dependentBaseAmountGuids);
	}

	private void addBaseAmountsToDependencyList(final Set<String> dependentGuids, final Collection<BaseAmount> findBaseAmounts) {
		for (BaseAmount baseAmount : findBaseAmounts) {
			dependentGuids.add(baseAmount.getGuid());
		}
	}

	private Collection<BaseAmount> getBaseAmountsOfPriceList(final PriceListDescriptor rule) {
		BaseAmountFilter baseAmountFilter = getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		baseAmountFilter.setPriceListDescriptorGuid(rule.getGuid());
		return baseAmountService.findBaseAmounts(baseAmountFilter);
	}

	/**
	 * @param priceListDescriptorService the priceListDescriptorService to set
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	/**
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 * @param priceListGUIDs the priceListGUIDs to set
	 */
	public void setPriceListGUIDs(final List<String> priceListGUIDs) {
		this.priceListGUIDs = priceListGUIDs;
	}

	/**
	 * @param priceListDescriptorAdapter the priceListDescriptorAdapter to set
	 */
	public void setPriceListDescriptorAdapter(final DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> priceListDescriptorAdapter) {
		this.priceListDescriptorAdapter = priceListDescriptorAdapter;
	}
}
