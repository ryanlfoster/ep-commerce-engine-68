/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import java.util.List;
import java.util.Set;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Provides services for storing and retrieving <code>ProductAssociation</code>s.
 * 
 * Note that "update" is purposely not supported. Update product associations by 
 * updating the parent product that aggregates them.
 *
 */
public interface ProductAssociationService extends EpPersistenceService {

	/**
	 * Adds the given ProductAssociation.
	 *
	 * @param productAssociation the ProductAssociation to add
	 * @return the persisted instance of ProductAssociation
	 * @throws EpServiceException - in case of any errors
	 */
	ProductAssociation add(final ProductAssociation productAssociation) throws EpServiceException;

	/**
	 * Updates the given ProductAssociation.
	 *
	 * @param productAssociation the ProductAssociation to be updated
	 * @return the persisted instance of ProductAssociation
	 * @throws EpServiceException - in case of any errors
	 */
	ProductAssociation update(final ProductAssociation productAssociation) throws EpServiceException;

	/**
	 * Delete the ProductAssociation.
	 *
	 * @param productAssociation the ProductAssociation to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(final ProductAssociation productAssociation) throws EpServiceException;

	/**
	 * Load the ProductAssociation with the given UID.
	 * Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param productAssociationUid the ProductAssociation UID
	 * @return the productAssociation if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	ProductAssociation load(long productAssociationUid) throws EpServiceException;
	
	/**
	 * Searches the product associations by given search criteria.
	 * 
	 * @param criteria product associations search criteria.
	 * @return List&lt;ProductAssociation&gt; of associations.
	 * @throws EpServiceException - in case of any errors
	 */
	List<ProductAssociation> findByCriteria(ProductAssociationSearchCriteria criteria) throws EpServiceException;
	
	
	/**
	 * Searches the product associations by given search criteria and loads the associations with the provided load tuner.
	 *
	 * @param criteria the search criteria
	 * @param loadTuner the load tuner
	 * @return a list of associations
	 */
	List<ProductAssociation> findByCriteria(ProductAssociationSearchCriteria criteria, LoadTuner loadTuner);


	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Object getObject(long uid) throws EpServiceException;
	
	/**
	 * Get the product association with the given UID. Return null if no matching record exists. 
	 * You can give a product load tuner to fine control what data gets populated of the returned target product.
	 * 
	 * @param productAssociationUid the UID of the association
	 * @param loadTuner the product load tuner to use for the product reference
	 * @return the <code>ProductAssociation</code> object
	 * @deprecated pass in a <code>ProductAssociationLoadTuner</code> instead.
	 */
	@Deprecated
	ProductAssociation getTuned(long productAssociationUid, ProductLoadTuner loadTuner);
	
	/**
	 * Get the product association with the given UID. Return null if no matching record exists. 
	 * You can give a product association load tuner to fine control what data gets populated of the returned target product.
	 * 
	 * @param productAssociationUid the UID of the association
	 * @param loadTuner the product association load tuner to use
	 * @return the <code>ProductAssociation</code> object
	 */
	ProductAssociation getTuned(long productAssociationUid, ProductAssociationLoadTuner loadTuner);
		
	/**
	 * Gets all product associations of a given type for a given source product, in the given catalog. 
	 * 
	 * Note that this may return associations whose source and/or target products are not in the catalog
	 * that owns the association unless you set the validInCatalogOnly parameter to true.
	 * 
	 * @param sourceProductCode the code for the product that is associated to other products 
	 * @param associationType the type of product association
	 * @param catalogCode the code for the catalog in which the product has associated products
	 * @param withinCatalogOnly limit the results to association's whose source and target product
	 *                           are both in the association's catalog
	 * @return the set of <code>ProductAssociation</code>s
	 */
	Set<ProductAssociation> getAssociationsByType(String sourceProductCode, int associationType, String catalogCode, boolean withinCatalogOnly);
	
	/**
	 * Gets all product associations for a given source product, in the given catalog.
	 * 
	 * Note that this may return associations whose source and/or target products are not in the catalog
	 * that owns the association unless you set the validInCatalogOnly parameter to true.
	 * 
	 * @param sourceProductCode the code for the product that is associated to other products 
	 * @param catalogCode the code for the catalog in which the product has associated products
	 * @param withinCatalogOnly limit the results to association's whose source and target product
	 *                           are both in the association's catalog
	 * @return the list of <code>ProductAssociation</code>s
	 */
	Set<ProductAssociation> getAssociations(String sourceProductCode, String catalogCode, boolean withinCatalogOnly);
	
	/**
	 * Get product associations based on the criteria.
	 * 
	 * @param criteria the product criteria.
	 * @return a set of product associations
	 */
	Set<ProductAssociation> getAssociations(ProductAssociationSearchCriteria criteria);

	/**
	 * Filter a set of product associations so that the set only contains associations that are between products that
	 * are currently in the association's catalog.
	 * 
	 * @param associations the set of <code>ProductAssociations</code> to filter.
	 * @return the filtered set.
	 */
	Set<ProductAssociation> limitAssociationsToCatalog(Set<ProductAssociation> associations);
	
	/**
	 * Determine whether a product association is between products that are currently in the association's catalog.
	 * 
	 * @param association the <code>ProductAssociation</code> to check.
	 * @return true is the association is valid.
	 */
	boolean isAssociationInCatalog(ProductAssociation association);
	
	/**
	 * Get a list of <code>ProductAssociation</code>s that specify associations between products and other related products. The product
	 * associations are returned in decreasing order of the sales quantity of the targeted product.
	 *
	 * @param products The <code>Product</code>s for which to find associations
	 * @param associationType the type of the <code>ProductAssociation</code>s to be returned. Use a constant defined in the
	 *            <code>ProductAssociation</code> interface
	 * @param maxAssociations the maximum number of associations to return.
	 * @param filterTargetProducts Any associations targeting products in this list will not be returned.
	 * @return the <code>ProductAssociation</code>s, ordered by number of sales.
	 */
	List<ProductAssociation> getProductAssociationsByType(final Set<StoreProduct> products, final int associationType, final int maxAssociations,
			final Set< ? extends Product> filterTargetProducts);

	/**
	 *
	 * @param guid The guid to search for.
	 * @param loadTuner The load tuner to use.
	 * @return The product association for guid.
	 */
	ProductAssociation findByGuid(String guid, LoadTuner loadTuner);

	/**
	 * @param criteria The criteria to search for.
	 */
	void removeByCriteria(final ProductAssociationSearchCriteria criteria);

}
