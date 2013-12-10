package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * This is the default implementation of <code>BrandService</code>.
 */
public class BrandServiceImpl extends AbstractEpPersistenceServiceImpl implements BrandService {
	
	private static final String PLACEHOLDER_FOR_LIST = "list";

	private static final Object DUMMY_BRAND_GUID = "~Others";
	
	private ProductService productService;
	
	private CatalogService catalogService;

	/**
	 * Adds the given brand.
	 *
	 * @param brand the brand to add
	 * @return the persisted instance of brand
	 * @throws EpServiceException - in case of any errors
	 */
	public Brand add(final Brand brand) throws EpServiceException {
		sanityCheck();
		if (codeExists(brand)) {
			throw new EpServiceException("Brand with the code \"" + brand.getCode() + "\" already exists");
		}
		getPersistenceEngine().save(brand);
		return brand;
	}

	/**
	 * Get the brand with the given UID. Return a new instance if the given UID is less than 0. Return <code>null</code> the given UID is bigger
	 * than 0 and no matching record exists.
	 *
	 * @param brandUid the Brand UID.
	 * @return a brand if the given UID is less than 0 or exists, otherwise <code>null</code>
	 * @throws EpServiceException - in case of any errors
	 */
	public Brand get(final long brandUid) throws EpServiceException {
		sanityCheck();
		Brand brand = null;
		if (brandUid <= 0) {
			brand = getBean(ContextIdNames.BRAND);
		} else {
			brand = getPersistentBeanFinder().get(ContextIdNames.BRAND, brandUid);
		}
		return brand;
	}

	/**
	 * Generic get method for all persistable domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	/**
	 * Save or update the given brand.
	 *
	 * @param brand the brand to save or update
	 * @return the persisted or updated brand
	 * @throws EpServiceException - in case of any errors
	 */
	public Brand saveOrUpdate(final Brand brand) throws EpServiceException {
		final Brand updatedBrand = getPersistenceEngine().saveOrUpdate(brand);
		this.productService.notifyBrandUpdated(updatedBrand);
		return updatedBrand;
	}

	/**
	 * Deletes the brand.
	 *
	 * @param brand the brand to remove
	 * @throws EpServiceException in case of any errors
	 */
	public void remove(final Brand brand) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(brand);
	}

	/**
	 * Retrieve the dummy brand. The dummy brand can be used as a brand filter for those products whose brands hasn't been specified.
	 *
	 * @return the dummy brand
	 * @throws EpServiceException in case of any error
	 */
	public Brand getDummyBrand() throws EpServiceException {
		final List<Brand> brands = getPersistenceEngine().retrieveByNamedQuery("BRAND_SELECT_BY_GUID", DUMMY_BRAND_GUID);
		final int size = brands.size();
		if (size > 1) {
			throw new EpServiceException("Duplicate guid found:" + DUMMY_BRAND_GUID);
		}
		if (size == 0) {
			return null;
		}
		return brands.get(0);
	}

	/**
	 * Lists all brand stored in the database.
	 *
	 * @return a list of brand
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public List<Brand> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("BRAND_SELECT_ALL");
	}
	
	/**
	 * Checks whether the given UID is in use.
	 *
	 * @param uidToCheck the UID to check that is in use
	 * @return whether the UID is currently in use or not
	 * @throws EpServiceException in case of any errors
	 */
	public boolean isInUse(final long uidToCheck) throws EpServiceException {
		sanityCheck();
		return !getPersistenceEngine().retrieveByNamedQuery("BRAND_IN_USE", uidToCheck).isEmpty();
	}
	
	/**
	 * Finds all the {@link Brand}s for the specified catalog UID.
	 *
	 * @param catalogUid the catalog UID
	 * @return a {@link List} of {@link Brand}s
	 * @throws EpServiceException in case of any errors
	 */
	public List<Brand> findAllBrandsFromCatalog(final long catalogUid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("BRAND_SELECT_CATALOG_ALL", catalogUid);
	}

	/**
	 * Finds all the {@link Brand}s for the specified catalog UIDs.
	 *
	 * @param catalogUids the catalog UIDs to check
	 * @return a {@link List} of {@link Brand}s
	 * @throws EpServiceException in case of any errors
	 */
	public List<Brand> findAllBrandsFromCatalogList(final Collection<Catalog> catalogUids) throws EpServiceException {
		sanityCheck();

		List<Long> masterCatalogUids = new ArrayList<Long>();
		for (Catalog catalog : catalogUids) {
			if (catalog.isMaster()) {
				masterCatalogUids.add(catalog.getUidPk());
			} else {
				List<Catalog> masters = catalogService.findMastersUsedByVirtualCatalog(catalog.getCode());
				masterCatalogUids.addAll(getCatalogUidsFromCatalogList(masters));		
			}
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("BRAND_SELECT_CATALOG_IN_LIST", PLACEHOLDER_FOR_LIST, masterCatalogUids);
	}
	
	/**
	 * Convenience method to get a list of all catalog uidpks from a list of Catalogs.
	 * 
	 * @param list of catalog objects
	 * @return list of catalog uids
	 */
	private List<Long> getCatalogUidsFromCatalogList(final List<Catalog> catalogs) {
		List<Long> catalogUids = new ArrayList<Long>();
		for (Catalog catalog : catalogs) {
			catalogUids.add(catalog.getUidPk());
		}
		return catalogUids;
	}
	
	
	/**
	 * Return a list of uids for all brands in use.
	 *
	 * @return a list of uids for all brands in use
	 */
	public List<Long> getBrandInUseUidList() {
		return getPersistenceEngine().retrieveByNamedQuery("BRAND_IN_USE_UIDPK_LIST");
	}
	
	/**
	 * Return a {@link List} of brand UIDs for only those brand UIDs that are in use.
	 * 
	 * @param brandUids brand UIDs to check
	 * @return {@link List} of brand UIDs for only those brand UIDs that are in use
	 * @throws EpServiceException in case of any errors
	 */
	public List<Long> getBrandInUseUidList(final Collection<Long> brandUids) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQueryWithList("BRAND_IN_USE_LIST", PLACEHOLDER_FOR_LIST, brandUids);
	}

	/**
	 * Return a list of all brands in use.
	 *
	 * @return a list of all brands in use
	 */
	public List<Brand> getBrandInUseList() {
		return getPersistenceEngine().retrieveByNamedQuery("BRANDS_IN_USE_LIST");
	}

	/**
	 * Updates the given brand.
	 *
	 * @param brand the brand to update
	 * @return the persisted or updated brand
	 *
	 * @throws EpServiceException - in case of any errors
	 */
	public Brand update(final Brand brand) throws EpServiceException {
		sanityCheck();
		if (codeExists(brand)) {
			throw new EpServiceException("Brand with the code \"" + brand.getCode() + "\" already exists");
		}
		final Brand updatedBrand = getPersistenceEngine().merge(brand);
		this.productService.notifyBrandUpdated(updatedBrand);
		return updatedBrand;
	}


	/**
	 * Checks whether the given brand code exists or not.
	 *
	 * @param code the brand code.
	 * @return true if the given code exists.
	 * @throws EpServiceException -
	 *             in case of any errors
	 */
	public boolean codeExists(final String code) throws EpServiceException {
		if (code == null) {
			return false;
		}
		final Brand brand = this.findByCode(code);
		boolean codeExists = false;
		if (brand != null) {
			codeExists = true;
		}
		return codeExists;
	}

	/**
	 * Check whether the given brand's code exists or not.
	 *
	 * @param brand the brand to check
	 * @return true if a different brand with the given brand's code exists
	 * @throws EpServiceException - in case of any errors
	 */
	public boolean codeExists(final Brand brand) throws EpServiceException {
		if (brand.getCode() == null) {
			return false;
		}
		final Brand existingBrand = this.findByCode(brand.getCode());
		boolean codeExists = false;
		if (existingBrand != null && existingBrand.getUidPk() != brand.getUidPk()) {
			codeExists = true;
		}
		return codeExists;
	}

	/**
	 * Find the brand with the given code.
	 *
	 * @param code the brand code.
	 * @return the brand that matches the given code, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Brand findByCode(final String code) throws EpServiceException {
		sanityCheck();
		if (code == null) {
			throw new EpServiceException("Cannot retrieve null code.");
		}

		final List<Brand> results = getPersistenceEngine().retrieveByNamedQuery("BRAND_FIND_BY_CODE", code);
		Brand brand = null;
		if (results.size() == 1) {
			brand = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate brand code exist -- " + code);
		}
		return brand;
	}

	/**
	 * Return a list of brand code and default locale display name of all brands.
	 *
	 * @return a list of brand code and default locale display name of all brands
	 */
	public List<String[]> getBrandCodeNameList() {

		//return getPersistenceEngine().retrieve(Criteria.BRAND_CODE_LIST);
		List<Brand> brandList = this.list();
		List<String[]> arrayList = new ArrayList<String[]>(brandList.size());
		for (int i = 0; i < brandList.size(); i++) {
			arrayList.add(getCodeNameArray(brandList.get(i)));
		}
		return arrayList;

	}

	private String[] getCodeNameArray(final Brand brand) {
		String brandCode = brand.getCode();

		String displayNameForDefaultLocale = brand.getLocalizedProperties().getValueWithoutFallBack(
											"brandDisplayName", brand.getCatalog().getDefaultLocale());
		return new String[] {brandCode, displayNameForDefaultLocale};
	}
	
	/**
	 * Set the <code>ProductService</code>.
	 *
	 * @param productService the <code>ProductService</code>
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Set the <code>CatalogService</code>.
	 *
	 * @param catalogService the <code>CatalogService</code>
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	
}
