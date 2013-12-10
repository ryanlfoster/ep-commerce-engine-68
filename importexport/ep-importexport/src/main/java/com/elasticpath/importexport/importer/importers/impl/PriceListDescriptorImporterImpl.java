package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collection;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Price list descriptor importer implementation.
 */
public class PriceListDescriptorImporterImpl extends AbstractImporterImpl<PriceListDescriptor, PriceListDescriptorDTO> {

	private PriceListDescriptorService priceListDescriptorService;

	private DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> priceListDescriptorAdapter;

	private BaseAmountService baseAmountService;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<PriceListDescriptor, PriceListDescriptorDTO> savingStrategy) {
		super.initialize(context, savingStrategy);

		getSavingStrategy().setLifecycleListener(new DefaultLifecycleListener() {
			/**
			 * Memorize updated or saved price lists.
			 */
			@Override
			public void afterSave(final Persistable persistable) {
				context.addChangedPriceLists(((PriceListDescriptor) persistable).getGuid());
			}
		});
	}

	@Override
	protected PriceListDescriptor findPersistentObject(final PriceListDescriptorDTO dto) {
		return priceListDescriptorService.findByGuid(dto.getGuid());
	}

	@Override
	protected String getDtoGuid(final PriceListDescriptorDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected void setImportStatus(final PriceListDescriptorDTO object) {
		getStatusHolder().setImportStatus("(for price list descriptor " + object.getGuid() + ")");
	}

	@Override
	protected DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> getDomainAdapter() {
		return priceListDescriptorAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return PriceListDescriptorDTO.ROOT_ELEMENT;
	}

	/**
	 * Returns the collection which will remove base amounts, referenced by the price list descriptor being imported.
	 * 
	 * @return the collection strategy
	 */
	@Override
	protected CollectionsStrategy<PriceListDescriptor, PriceListDescriptorDTO> getCollectionsStrategy() {
		final PriceListDescriptorCollectionsStrategy priceListDescriptorCollectionsStrategy = new PriceListDescriptorCollectionsStrategy(
				getContext().getImportConfiguration().getImporterConfiguration(JobType.PRICELISTDESCRIPTOR));
		priceListDescriptorCollectionsStrategy.setBaseAmountService(baseAmountService);
		return priceListDescriptorCollectionsStrategy;
	}

	@Override
	public boolean executeImport(final PriceListDescriptorDTO object) {
		checkNameUnique(object);
		return super.executeImport(object);
	}

	private void checkNameUnique(final PriceListDescriptorDTO object) {
		if (!priceListDescriptorService.isPriceListNameUnique(object.getGuid(), object.getName())) {
			throw new PopulationRollbackException("IE-10611", object.getName());
		}
	}

	/**
	 * @param priceListDescriptorService the priceListDescriptorService to set
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	/**
	 * @param priceListDescriptorAdapter the priceListDescriptorAdapter to set
	 */
	public void setPriceListDescriptorAdapter(final DomainAdapter<PriceListDescriptor, PriceListDescriptorDTO> priceListDescriptorAdapter) {
		this.priceListDescriptorAdapter = priceListDescriptorAdapter;
	}

	/**
	 * @param baseAmountService the base amount service
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}
	
	@Override
	public Class<? extends PriceListDescriptorDTO> getDtoClass() {
		return PriceListDescriptorDTO.class;
	}

	/**
	 * PriceListDescriptorCollectionsStrategy implements CLEAR_COLLECTION strategy via removing Base Amounts referenced by Price List Descriptor.
	 */
	public static class PriceListDescriptorCollectionsStrategy implements CollectionsStrategy<PriceListDescriptor, PriceListDescriptorDTO> {

		private final boolean baseAmountClearCollection;

		private BaseAmountService baseAmountService;

		/**
		 * @param importerConfiguration ImporterConfiguration
		 */
		public PriceListDescriptorCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			this.baseAmountClearCollection = importerConfiguration.getCollectionStrategyType(DependentElementType.BASE_AMOUNTS).equals(
					CollectionStrategyType.CLEAR_COLLECTION);
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}

		@Override
		public void prepareCollections(final PriceListDescriptor domainObject, final PriceListDescriptorDTO dto) {
			if (baseAmountClearCollection) {
				final BaseAmountFilter baseAmountFilter = createBaseAmountFilter();
				baseAmountFilter.setPriceListDescriptorGuid(domainObject.getGuid());
				final Collection<BaseAmount> findBaseAmounts = baseAmountService.findBaseAmounts(baseAmountFilter);

				for (final BaseAmount baseAmount : findBaseAmounts) {
					baseAmountService.delete(baseAmount);
				}
			}
		}

		/**
		 * Creates the empty base amount filter.
		 * 
		 * @return empty base amount filter
		 */
		BaseAmountFilter createBaseAmountFilter() {
			// FIXME: probably use the bean factory
			return new BaseAmountFilterImpl();
		}

		/**
		 * Sets the base amount service.
		 * 
		 * @param baseAmountService the base amount service
		 */
		void setBaseAmountService(final BaseAmountService baseAmountService) {
			this.baseAmountService = baseAmountService;
		}
	}
}
