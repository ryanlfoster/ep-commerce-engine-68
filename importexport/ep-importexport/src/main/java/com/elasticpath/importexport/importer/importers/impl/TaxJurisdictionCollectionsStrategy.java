package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.common.dto.tax.TaxJurisdictionDTO;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;

/**
 * A {@link CollectionsStrategy} for {@link TaxJurisdiction}, clearing values, regions and categories.
 */
public class TaxJurisdictionCollectionsStrategy implements CollectionsStrategy<TaxJurisdiction, TaxJurisdictionDTO> {

	private final boolean clearValues, clearRegions, clearCategories;

	/**
	 * Default constructor.
	 * 
	 * @param importerConfiguration current importer configuration.
	 */
	public TaxJurisdictionCollectionsStrategy(final ImporterConfiguration importerConfiguration) {

		clearValues = importerConfiguration.getCollectionStrategyType(DependentElementType.TAX_VALUES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearRegions = importerConfiguration.getCollectionStrategyType(DependentElementType.TAX_REGIONS).equals(
				CollectionStrategyType.CLEAR_COLLECTION);
		clearCategories = importerConfiguration.getCollectionStrategyType(DependentElementType.TAX_CATEGORIES).equals(
				CollectionStrategyType.CLEAR_COLLECTION);

	}

	@Override
	public void prepareCollections(final TaxJurisdiction domainObject, final TaxJurisdictionDTO dto) {

		if (clearCategories) {
			domainObject.getTaxCategorySet().clear();
			return;
		}

		if (clearRegions) {
			for (TaxCategory category : domainObject.getTaxCategorySet()) {
				category.getTaxRegionSet().clear();
				return;
			}
		}

		if (clearValues) {
			for (TaxCategory category : domainObject.getTaxCategorySet()) {
				for (TaxRegion region : category.getTaxRegionSet()) {
					region.getTaxValuesMap().clear();
				}
			}
		}
	}

	public boolean isForPersistentObjectsOnly() {
		return true;
	}
}
