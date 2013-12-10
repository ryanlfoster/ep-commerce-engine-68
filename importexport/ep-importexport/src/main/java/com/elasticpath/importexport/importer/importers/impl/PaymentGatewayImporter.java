package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDTO;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Implements an importer for {@link PaymentGateway}s.
 */
public class PaymentGatewayImporter extends AbstractImporterImpl<PaymentGateway, PaymentGatewayDTO> {

	private DomainAdapter<PaymentGateway, PaymentGatewayDTO> paymentGatewayAdapter;

	private PaymentGatewayService paymentGatewayService;

	@Override
	public String getImportedObjectName() {
		return PaymentGatewayDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final PaymentGatewayDTO dto) {
		return dto.getName();
	}

	@Override
	protected DomainAdapter<PaymentGateway, PaymentGatewayDTO> getDomainAdapter() {
		return this.paymentGatewayAdapter;
	}

	@Override
	protected PaymentGateway findPersistentObject(final PaymentGatewayDTO dto) {
		PaymentGateway gateway;
		try {
			gateway = paymentGatewayService.getGatewayByName(dto.getName());
		} catch (EpServiceException e) {
			gateway = null;
		}
		return gateway;
	}

	@Override
	protected void setImportStatus(final PaymentGatewayDTO object) {
		getStatusHolder().setImportStatus("(" + object.getName() + ")");
	}

	public void setPaymentGatewayAdapter(final DomainAdapter<PaymentGateway, PaymentGatewayDTO> paymentGatewayAdapter) {
		this.paymentGatewayAdapter = paymentGatewayAdapter;
	}

	public void setPaymentGatewayService(final PaymentGatewayService paymentGatewayService) {
		this.paymentGatewayService = paymentGatewayService;
	}

	@Override
	protected CollectionsStrategy<PaymentGateway, PaymentGatewayDTO> getCollectionsStrategy() {
		return new PaymentGatewayCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.PAYMENTGATEWAY),
				paymentGatewayService);
	}
	
	@Override
	public Class<? extends PaymentGatewayDTO> getDtoClass() {
		return PaymentGatewayDTO.class;
	}
}
