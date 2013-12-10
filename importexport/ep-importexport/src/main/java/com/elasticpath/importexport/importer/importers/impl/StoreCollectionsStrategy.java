package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.store.StoreDTO;
import com.elasticpath.domain.store.Store;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;

/**
 * A {@link CollectionsStrategy} for {@link Store}s. Clear out references to warehouses, tax codes, tax jurisdictions, payment gateways and supported
 * credit card types.
 */
public class StoreCollectionsStrategy implements CollectionsStrategy<Store, StoreDTO> {

	private final boolean clearWarehouses, clearTaxCodes, clearTaxJurisdictions, clearPaymentGateways, clearCreditCardTypes;

	/**
	 * Default constructor.
	 * @param importerConfiguration current import configuration.
	 */
	public StoreCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
		clearWarehouses = importerConfiguration.getCollectionStrategyType(DependentElementType.WAREHOUSES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearTaxCodes = importerConfiguration.getCollectionStrategyType(DependentElementType.TAX_CODES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearTaxJurisdictions = importerConfiguration.getCollectionStrategyType(DependentElementType.TAX_JURISDICTIONS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearPaymentGateways = importerConfiguration.getCollectionStrategyType(DependentElementType.PAYMENT_GATEWAYS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearCreditCardTypes = importerConfiguration.getCollectionStrategyType(DependentElementType.CREDIT_CARD_TYPES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
	}

	@Override
	public void prepareCollections(final Store store, final StoreDTO dto) {
		if (clearWarehouses) {
			store.getWarehouses().clear();
		}

		if (clearTaxCodes) {
			store.getTaxCodes().clear();
		}
		if (clearTaxJurisdictions) {
			store.getTaxJurisdictions().clear();
		}
		if (clearPaymentGateways) {
			store.getPaymentGateways().clear();
		}
		if (clearCreditCardTypes) {
			store.getCreditCardTypes().clear();
		}

	}

	public boolean isForPersistentObjectsOnly() {
		return false;
	}

}
