package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDTO;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Implements an exporter for a {@link PaymentGateway}.
 */
public class PaymentGatewayExporter extends AbstractExporterImpl<PaymentGateway, PaymentGatewayDTO, String> {

	private static final Logger LOG = Logger.getLogger(PaymentGatewayExporter.class);

	private ImportExportSearcher importExportSearcher;
	
	private PaymentGatewayService paymentGatewayService;

	private DomainAdapter<PaymentGateway, PaymentGatewayDTO> paymentGatewayAdapter;

	private Set<String> gatewayNames;

	public JobType getJobType() {
		return JobType.PAYMENTGATEWAY;
	}

	public Class<?>[] getDependentClasses() {
		return new Class<?>[] { PaymentGateway.class };
	}

	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		gatewayNames = new HashSet<String>(getImportExportSearcher().searchGuids(getContext().getSearchConfiguration(), EPQueryType.PAYMENTGATEWAY));
		LOG.info("The list for " + this.gatewayNames.size() + " payment gateways is retrievedfrom the database.");

	}

	@Override
	protected Class<? extends PaymentGatewayDTO> getDtoClass() {
		return PaymentGatewayDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		if (getContext().getDependencyRegistry().supportsDependency(PaymentGateway.class)) {
			gatewayNames.addAll(getContext().getDependencyRegistry().getDependentGuids(PaymentGateway.class));
		}

		return new ArrayList<String>(gatewayNames);
	}

	@Override
	protected List<PaymentGateway> findByIDs(final List<String> subList) {
		return paymentGatewayService.findByNames(subList);
	}

	@Override
	protected DomainAdapter<PaymentGateway, PaymentGatewayDTO> getDomainAdapter() {
		return this.paymentGatewayAdapter;
	}

	public void setPaymentGatewayService(final PaymentGatewayService service) {
		this.paymentGatewayService = service;
	}

	public void setPaymentGatewayAdapter(final DomainAdapter<PaymentGateway, PaymentGatewayDTO> paymentGatewayAdapter) {
		this.paymentGatewayAdapter = paymentGatewayAdapter;
	}

	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

}