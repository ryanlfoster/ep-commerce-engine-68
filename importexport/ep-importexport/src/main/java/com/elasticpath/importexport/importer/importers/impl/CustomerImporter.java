package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.exporters.impl.CustomerCollectionsStrategy;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.impl.FetchGroupLoadTunerImpl;
import com.elasticpath.service.customer.CustomerService;

/**
 * Importer for {@link Customer} objects.
 */
public class CustomerImporter extends AbstractImporterImpl<Customer, CustomerDTO> {

	private DomainAdapter<Customer, CustomerDTO> customerAdapter;

	private CustomerService customerService;

	@Override
	protected CollectionsStrategy<Customer, CustomerDTO> getCollectionsStrategy() {
		return new CustomerCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.CUSTOMER), customerService);
	}

	@Override
	public String getImportedObjectName() {
		return CustomerDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final CustomerDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<Customer, CustomerDTO> getDomainAdapter() {
		return customerAdapter;
	}

	@Override
	protected Customer findPersistentObject(final CustomerDTO dto) {
		FetchGroupLoadTunerImpl loadtunerAll = new FetchGroupLoadTunerImpl();
		loadtunerAll.addFetchGroup(FetchGroupConstants.ALL);
		return customerService.findByGuid(dto.getGuid(), loadtunerAll);
	}

	@Override
	protected void setImportStatus(final CustomerDTO object) {
		getStatusHolder().setImportStatus("(" + object.getGuid() + ")");
	}

	public void setCustomerAdapter(final DomainAdapter<Customer, CustomerDTO> customerAdapter) {
		this.customerAdapter = customerAdapter;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<Customer, CustomerDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
	}

	@Override
	public Class<? extends CustomerDTO> getDtoClass() {
		return CustomerDTO.class;
	}
}