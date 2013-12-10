package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.paymentgateway.PaymentGatewayDTO;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.service.payment.PaymentGatewayService;

/**
 * Implements a {@linkplain CollectionsStrategy} for {@link PaymentGateway}s, allowing payment gateway properties to be cleared.
 */
public class PaymentGatewayCollectionsStrategy implements CollectionsStrategy<PaymentGateway, PaymentGatewayDTO> {

	private final boolean clearProperties;

	private final PaymentGatewayService paymentGatewayService;

	/**
	 * Default constructor.
	 * 
	 * @param importerConfiguration the current importer configuration
	 * @param paymentGatewayService used later to persist cleared collections before importing.
	 */
	public PaymentGatewayCollectionsStrategy(final ImporterConfiguration importerConfiguration, final PaymentGatewayService paymentGatewayService) {
		clearProperties = importerConfiguration.getCollectionStrategyType(DependentElementType.PAYMENT_GATEWAY_PROPERTIES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		this.paymentGatewayService = paymentGatewayService;
	}

	@Override
	public void prepareCollections(final PaymentGateway paymentGateway, final PaymentGatewayDTO dto) {
		if (clearProperties) {
			paymentGateway.getPropertiesMap().clear();
		}

		paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	public boolean isForPersistentObjectsOnly() {
		return true;
	}

}