package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricelist.BaseAmountAdapter;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Base amount importer implementation.
 */
public class BaseAmountImporterImpl extends AbstractImporterImpl<BaseAmount, BaseAmountDTO> {

	private BaseAmountService baseAmountService;

	private BaseAmountAdapter baseAmountAdapter;
	
	private SavingStrategy<BaseAmount, BaseAmountDTO> baseAmountSavingStrategy;
	
	@Override
	public void initialize(final ImportContext context, final SavingStrategy<BaseAmount, BaseAmountDTO> savingStrategy) {
		baseAmountSavingStrategy = AbstractSavingStrategy.createStrategy(
				getImportStrategyType(context), 
				savingStrategy.getSavingManager());
		super.initialize(context, baseAmountSavingStrategy);
		baseAmountSavingStrategy.setLifecycleListener(new BaseAmountLifecycleListener());		
	}


	/**
	 * Get the ImportStrategyType for dependent collection.
	 * Retain collection. This option keeps existing dependencies and adds any new dependencies to
	 * the target object. If the import package contains updates to existing dependencies the updates are applied.
	 * @param context the import context
	 * @return ImportStrategyType
	 */
	private ImportStrategyType getImportStrategyType(final ImportContext context) {
		ImportStrategyType importStrategyType = ImportStrategyType.IMMUTABLE;		
		ImporterConfiguration importedConfiguration = context.getImportConfiguration().getImporterConfiguration(JobType.PRICELISTDESCRIPTOR);
		if (importedConfiguration.getImportStrategyType().equals(ImportStrategyType.UPDATE) 
				&&
			importedConfiguration.getCollectionStrategyType(DependentElementType.BASE_AMOUNTS).equals(CollectionStrategyType.RETAIN_COLLECTION)
			) {
			importStrategyType = ImportStrategyType.INSERT_OR_UPDATE;
		}
		return importStrategyType;
	}
	
 
	
	@Override
	public boolean executeImport(final BaseAmountDTO object) {
		sanityCheck();
		if (getContext().getImportConfiguration().getImporterConfiguration(
				JobType.PRICELISTDESCRIPTOR).getImportStrategyType().equals(ImportStrategyType.INSERT)
				&& !getContext().isChangedPriceLists(object.getPriceListDescriptorGuid())) {
			return false;
		}
		
		return super.executeImport(object);
	}	

	@Override
	protected BaseAmount findPersistentObject(final BaseAmountDTO dto) {
		return baseAmountService.findByGuid(dto.getGuid());
	}

	@Override
	protected String getDtoGuid(final BaseAmountDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected void setImportStatus(final BaseAmountDTO object) {
		getStatusHolder().setImportStatus("(for base amount " + object.getGuid() + ")");
	}

	@Override
	protected DomainAdapter<BaseAmount, BaseAmountDTO> getDomainAdapter() {
		return baseAmountAdapter;
	}

	@Override
	public String getImportedObjectName() {
		return BaseAmountDTO.ROOT_ELEMENT;
	}

	/**
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 * @param baseAmountAdapter the baseAmountAdapter to set
	 */
	public void setBaseAmountAdapter(final BaseAmountAdapter baseAmountAdapter) {
		this.baseAmountAdapter = baseAmountAdapter;
	}

	@Override
	public Class<? extends BaseAmountDTO> getDtoClass() {
		return BaseAmountDTO.class;
	}

	/**
	 * As <code>BaseAmount</code> is immutable object, it have to be removed before importing new one.
	 */
	class BaseAmountLifecycleListener implements LifecycleListener {

		@Override
		public void afterSave(final Persistable persistable) {
			//empty
		}

		/**
		 * Removes old BaseAmount from DB in case if the BaseAmount existed.
		 * @param persistable BaseAmount
		 */
		@Override
		public void beforeSave(final Persistable persistable) {
			BaseAmount baseAmount = ((BaseAmount) persistable);
			if (baseAmount == null) {
				return;
			}
			
			// filters are set according to the TBASEAMOUNT_UNIQUE index constraint in db
			final BaseAmountFilter baseAmountFilter = new BaseAmountFilterImpl();
			baseAmountFilter.setPriceListDescriptorGuid(baseAmount.getPriceListDescriptorGuid());
			baseAmountFilter.setObjectGuid(baseAmount.getObjectGuid());
			baseAmountFilter.setObjectType(baseAmount.getObjectType());
			baseAmountFilter.setQuantity(baseAmount.getQuantity());
			
			final Collection<BaseAmount> findBaseAmounts = baseAmountService.findBaseAmounts(baseAmountFilter);

			for (final BaseAmount baseAmountToRemove : findBaseAmounts) {
				if (StringUtils.equals(baseAmount.getGuid(), baseAmountToRemove.getGuid())) {
					baseAmountService.delete(baseAmountToRemove);
				} else {
					throw new PopulationRuntimeException("IE-10805", baseAmount.getGuid()); 
				}
			}

		}

		@Override
		public void beforePopulate(final Persistable persistable) {
			//do nothing			
		}		
	}
}
