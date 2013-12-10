package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.catalog.BrandService;

/**
 * This class is responsible for exporting {@link Brand}.
 */
public class BrandDependentExporterImpl extends AbstractDependentExporterImpl<Brand, BrandDTO, CatalogDTO> {
	private BrandService brandService;
	private ApplicationPropertiesHelper applicationPropertiesHelper;

	/** {@inheritDoc} */
	public List<Brand> findDependentObjects(final long primaryObjectUid) {
		DependencyRegistry dependencyRegistry = getContext().getDependencyRegistry();
		List<Brand> brandList = new ArrayList<Brand>();

		if (getFilter().isFiltered(primaryObjectUid)) {
			brandList = getByCatalog(primaryObjectUid);
		} else {
			Set<Long> brandUids = dependencyRegistry.getDependentUids(Brand.class);
			List<Long> tempBrandUids = new ArrayList<Long>(brandUids);
			for (Long brandUid : tempBrandUids) {
				Brand brand = brandService.get(brandUid);
				if (brand.getCatalog().getUidPk() == primaryObjectUid) {
					brandList.add(brand);
					brandUids.remove(brandUid);
				}
			}
		}

		addAssetsIntoRegistry(brandList, dependencyRegistry);
		return brandList;
	}

	private void addAssetsIntoRegistry(final List<Brand> brandList, final DependencyRegistry dependencyRegistry) {
		if (dependencyRegistry.supportsDependency(DigitalAsset.class)) {
			String imagesFolder = getAssetProperties().get(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER);

			for (Brand brand : brandList) {
				dependencyRegistry.addAsset(imagesFolder, brand.getImageUrl());
			}
		}
	}

	/** {@inheritDoc} */
	public void bindWithPrimaryObject(final List<BrandDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setBrands(dependentDtoObjects);
	}

	private List<Brand> getByCatalog(final Long catalogUid) {
		return brandService.findAllBrandsFromCatalog(catalogUid);
	}

	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}

	protected Map<String, String> getAssetProperties() {
		return applicationPropertiesHelper.getPropertiesWithNameStartsWith("asset");
	}
}