package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.assembler.customer.BuiltinFilters;
import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.customer.CustomerAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.customer.CustomerService;

/**
 * Exporter for {@link Customer} objects. Used in importexport. 
 */
public class CustomerExporter extends AbstractExporterImpl<Customer, CustomerDTO, String> {

	private static final Logger LOG = Logger.getLogger(CustomerExporter.class);

	private List<String> customerGuids;

	private CustomerAdapter customerAdapter;

	private CustomerService customerService;

	private ImportExportSearcher importExportSearcher;
	
	/** Name for the card number filter. */
	public static final String CARD_NUMBER_FILTER = "CARD_NUMBER_FILTER";

	public JobType getJobType() {
		return JobType.CUSTOMER;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { Customer.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		configureCardFilter(getCardFilterOption(context));
		
		List<Long> customerUids = getImportExportSearcher().searchUids(getContext().getSearchConfiguration(), EPQueryType.CUSTOMER);
		customerGuids = new ArrayList<String>(customerUids.size());
		
		for (Customer customer : customerService.findByUids(customerUids)) {
			if (!customer.isAnonymous()) {
				customerGuids.add(customer.getGuid());
			}
		}
		
		LOG.info("The list for " + customerGuids.size() + " customers retrieved from the database.");
	}

	private String getCardFilterOption(final ExportContext context) {

		// any of these methods may return null, so a check is necessary at each call.
		// otherwise a try-catch block would be necessary to catch the possible null pointer exceptions.
		String emptyString = "";
		
		if (context == null) {
			return emptyString;
		}
		
		if (context.getExportConfiguration() == null) {
			return emptyString;
		}

		if (context.getExportConfiguration().getExporterConfiguration() == null) {
			return emptyString;
		}

		String cardNumberFilterOption = context.getExportConfiguration().getExporterConfiguration().getOption(CARD_NUMBER_FILTER);
		
		if (cardNumberFilterOption == null) {
			return emptyString;
		} 

		return cardNumberFilterOption;
		
	}

	/**
	 * Pass the desired {@code CreditCardFilter} to the DtoAssembler.
	 * If the given option is "STATIC" then use BuiltinFilters.STATIC.
	 * Else use BuiltinFilters.EMPTYING.
	 * 
	 * @param option The credit card filter option.
	 * @see BuiltinFilters
	 */
	public void configureCardFilter(final String option) {
		if (option.equalsIgnoreCase("STATIC")) {
			customerAdapter.setCardFilteringStyle(BuiltinFilters.STATIC);
		} else {
			customerAdapter.setCardFilteringStyle(BuiltinFilters.EMPTYING);
		}
	}

	@Override
	protected Class<? extends CustomerDTO> getDtoClass() {
		return CustomerDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(Customer.class)) {
			customerGuids.addAll(getContext().getDependencyRegistry().getDependentGuids(Customer.class));
		}

		return customerGuids;
	}

	@Override
	protected List<Customer> findByIDs(final List<String> subList) {
		List<Customer> customers = new ArrayList<Customer>();
		for (String guid : subList) {
			Customer customer = customerService.findByGuid(guid);
			if (customer != null) {
				customers.add(customer);
			}
		}

		return customers;
	}

	@Override
	protected void addDependencies(final List<Customer> customers, final DependencyRegistry dependencyRegistry) {
		for (Customer customer : customers) {
			if (dependencyRegistry.supportsDependency(Store.class)) {
				dependencyRegistry.addGuidDependency(Store.class, customer.getStoreCode());
			}

			if (dependencyRegistry.supportsDependency(CustomerGroup.class)) {
				for (CustomerGroup customerGroup : customer.getCustomerGroups()) {
					dependencyRegistry.addGuidDependency(CustomerGroup.class, customerGroup.getGuid());
				}
			}
		}
	}

	@Override
	protected DomainAdapter<Customer, CustomerDTO> getDomainAdapter() {
		return customerAdapter;
	}

	public void setCustomerAdapter(final CustomerAdapter customerAdapter) {
		this.customerAdapter = customerAdapter;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
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