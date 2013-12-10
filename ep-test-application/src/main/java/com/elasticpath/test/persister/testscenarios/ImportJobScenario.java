package com.elasticpath.test.persister.testscenarios;

import com.elasticpath.domain.cmuser.CmUser;

public class ImportJobScenario extends SimpleStoreScenario {
	public static final String DEFAULT_BRAND_CODE = "F00004";

	public static final String SINGLE_SKU_PRODUCT_TYPE = "SingleSkuProductType";

	public static final String MULTI_SKU_PRODUCT_TYPE = "MultiSkuProductType";

	protected CmUser cmUser;

	@Override
	public void initialize() {
		super.initialize();
		category = getDataPersisterFactory().getCatalogTestPersister().updateDefaultCategoryAttributes(category);

		cmUser = getDataPersisterFactory().getStoreTestPersister().persistDefaultCmUser();

		getDataPersisterFactory().getCatalogTestPersister().persistDefaultMultiSkuProductType(catalog);
		getDataPersisterFactory().getCatalogTestPersister().persistDefaultSingleSkuProductType(catalog);
		getDataPersisterFactory().getCatalogTestPersister().persistProductBrand(catalog, DEFAULT_BRAND_CODE);
	}

	public CmUser getCmUser() {
		return cmUser;
	}
}
