package com.elasticpath.importexport.exporter.exporters.impl;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;

/**
 * Populates dependence registry with catalog UIDs in situation when 'real' category contains in
 * virtual catalog but depends on data contained in other master catalogs. Thus, all these master
 * catalogs should be added into dependency registry in order to export them as well.
 */
public class VirtualCatalogDependencyHelper {
	/**
	 * Adds UIDs of catalogs influencing on the given category.
	 * 
	 * @param category to add influencing catalogs for
	 * @param dependencyRegistry containing UIDs of objects other exported objects depend on
	 */
	public final void addInfluencingCatalogs(final Category category, final DependencyRegistry dependencyRegistry) {
		if (category.getCatalog().isMaster()) {
			return;
		}
		addCatalogContainingCategoryType(category.getCategoryType(), dependencyRegistry); 
	}

	/*
	 * Puts catalog UID into dependency registry.
	 */
	private void addCatalogContainingCategoryType(final CategoryType categoryType, final DependencyRegistry dependencyRegistry) {
		dependencyRegistry.addUidDependency(Catalog.class, categoryType.getCatalog().getUidPk());
	}
}
