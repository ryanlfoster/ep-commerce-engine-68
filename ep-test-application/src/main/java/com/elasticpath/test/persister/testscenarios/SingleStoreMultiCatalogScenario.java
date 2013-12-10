package com.elasticpath.test.persister.testscenarios;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.test.util.Utils;

/**
 * This scenario has a single store using a virtual catalog that is derived from two master catalogs.
 */
public class SingleStoreMultiCatalogScenario extends ImportJobScenario {

	public static final String DEFAULT_SECOND_CATALOG_BRAND_CODE = "F00003";

	public final static String SECOND_CATALOG_CATEGORY_CODE = "second_catalog_category_code";

	public final static String SECOND_CATALOG_NO_LINK_CATEGORY_CODE = "second_catalog_no_link_category_code";

	/** The second master catalog used by the store. */
	private Catalog secondMasterCatalog;

	/** The virtual catalog used by the store. */
	private Catalog virtualCatalog;

	/** A category on the second master catalog */
	private Category secondCatalogCategory;

	/** A category on the second master catalog that shouldn't be linked */
	private Category secondCatalogNoLinkCategory;

	/** A linked category liking to the first master catalog */
	private Category firstLinkedCategory;

	/** A linked category liking to the second master catalog */
	private Category secondLinkedCategory;

	@Override
	public void initialize() {
		super.initialize();

		secondMasterCatalog = getDataPersisterFactory().getCatalogTestPersister().persistDefaultMasterCatalog();
		virtualCatalog = getDataPersisterFactory().getCatalogTestPersister().createPersistedVirtualCatalog();

		final CategoryType categoryType = getDataPersisterFactory().getCatalogTestPersister().persistCategoryType(Utils.uniqueCode("catType"),
				Utils.uniqueCode("catTypeTemplate"), catalog);
		secondCatalogCategory = getDataPersisterFactory().getCatalogTestPersister().persistCategory(SECOND_CATALOG_CATEGORY_CODE, secondMasterCatalog,
				categoryType, null, null);
		secondCatalogNoLinkCategory = getDataPersisterFactory().getCatalogTestPersister().persistCategory(SECOND_CATALOG_NO_LINK_CATEGORY_CODE,
				secondMasterCatalog, categoryType, null, null);

		firstLinkedCategory = getDataPersisterFactory().getCatalogTestPersister().persistLinkedCategory(virtualCatalog, category);
		secondLinkedCategory = getDataPersisterFactory().getCatalogTestPersister().persistLinkedCategory(virtualCatalog, secondCatalogCategory);

		getDataPersisterFactory().getCatalogTestPersister().persistProductBrand(secondMasterCatalog, DEFAULT_SECOND_CATALOG_BRAND_CODE);
	}

	/**
	 * @return the secondMasterCatalog
	 */
	public Catalog getSecondMasterCatalog() {
		return secondMasterCatalog;
	}

	/**
	 * @return the virtualCatalog
	 */
	public Catalog getVirtualCatalog() {
		return virtualCatalog;
	}

	/**
	 * @return the secondCatalogCategory
	 */
	public Category getSecondCatalogCategory() {
		return secondCatalogCategory;
	}

	/**
	 * @return the secondCatalogNoLinkCategory
	 */
	public Category getSecondCatalogNoLinkCategory() {
		return secondCatalogNoLinkCategory;
	}

	/**
	 * @return the firstLinkedCategory
	 */
	public Category getFirstLinkedCategory() {
		return firstLinkedCategory;
	}

	/**
	 * @return the secondLinkedCategory
	 */
	public Category getSecondLinkedCategory() {
		return secondLinkedCategory;
	}
}
