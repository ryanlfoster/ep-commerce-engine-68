package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionDTO;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * This class is responsible for exporting {@link SkuOption}s.
 */
public class SkuOptionDependentExporterImpl extends AbstractDependentExporterImpl<SkuOption, SkuOptionDTO, CatalogDTO> {
	private SkuOptionService skuOptionService;
	private ApplicationPropertiesHelper applicationPropertiesHelper;

	/** {@inheritDoc} */
	public List<SkuOption> findDependentObjects(final long primaryObjectUid) {
		final DependencyRegistry dependencyRegistry = getContext().getDependencyRegistry();
		List<SkuOption> resultList = new ArrayList<SkuOption>();

		if (getFilter().isFiltered(primaryObjectUid)) {
			resultList = getByCatalog(primaryObjectUid);
		} else {
			Iterator<Long> iter = dependencyRegistry.getDependentUids(SkuOption.class).iterator();
			while (iter.hasNext()) {
				final Long uid = iter.next();
				SkuOption skuOption = skuOptionService.get(uid);
				if (skuOption.getCatalog().getUidPk() == primaryObjectUid) {
					resultList.add(skuOption);
					iter.remove();
				}
			}
		}
		addAssetsIntoRegistry(resultList, dependencyRegistry);
		return resultList;
	}

	private void addAssetsIntoRegistry(final List<SkuOption> skuOptionList, final DependencyRegistry dependencyRegistry) {
		if (dependencyRegistry.supportsDependency(DigitalAsset.class)) {
			String imagesFolder = getAssetProperties().get(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER);

			for (SkuOption skuOption : skuOptionList) {
				for (SkuOptionValue skuOptionValue : skuOption.getOptionValues()) {
					dependencyRegistry.addAsset(imagesFolder, skuOptionValue.getImage());
				}
			}
		}
	}

	private List<SkuOption> getByCatalog(final long primaryObjectUid) {
		return skuOptionService.findAllSkuOptionFromCatalog(primaryObjectUid);
	}

	/** {@inheritDoc} */
	public void bindWithPrimaryObject(final List<SkuOptionDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setSkuOptions(dependentDtoObjects);
	}

	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}

	protected Map<String, String> getAssetProperties() {
		return applicationPropertiesHelper.getPropertiesWithNameStartsWith("asset");
	}
}