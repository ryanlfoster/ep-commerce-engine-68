/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductAssociationRetrieveStrategy;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Provides services for storing and retrieving <code>ProductAssociation</code>s. Note that "update" is purposely not supported. Update product
 * associations by updating the parent product that aggregates them.
 */
public class ProductAssociationServiceImpl extends AbstractEpPersistenceServiceImpl 
	implements ProductAssociationService, ProductAssociationRetrieveStrategy  {

	private FetchPlanHelper fetchPlanHelper;
	
	private ProductLoadTuner productLoadTuner;
	
	private ProductAssociationLoadTuner productAssociationLoadTuner;
	
	/**
	 * Adds the given ProductAssociation.
	 * 
	 * @param productAssociation the ProductAssociation to add
	 * @return the persisted instance of ProductAssociation
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductAssociation add(final ProductAssociation productAssociation) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(productAssociation);
		return productAssociation;
	}

	/**
	 * Updates the given ProductAssociation.
	 * 
	 * @param productAssociation the ProductAssociation to be updated
	 * @return the persisted instance of ProductAssociation
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductAssociation update(final ProductAssociation productAssociation) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().update(productAssociation);
	}

	/**
	 * Delete the ProductAssociation.
	 * 
	 * @param productAssociation the ProductAssociation to remove
	 * @throws EpServiceException - in case of any errors
	 */
	public void remove(final ProductAssociation productAssociation) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(productAssociation);
	}

	/**
	 * Load the ProductAssociation with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 * This implementation does not use a product load tuner.
	 * 
	 * @param productAssociationUid the ProductAssociation UID
	 * @return the productAssociation if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public ProductAssociation load(final long productAssociationUid) throws EpServiceException {
		sanityCheck();
		
		ProductAssociation productAssociation = null;
		if (productAssociationUid <= 0) {
			productAssociation = getBean(ContextIdNames.PRODUCT_ASSOCIATION);
		} else {
			productAssociation = getPersistentBeanFinder().load(ContextIdNames.PRODUCT_ASSOCIATION, productAssociationUid);
		}
		return productAssociation;
	}

	/**
	 * Searches the product associations by given search criteria. Throw an unrecoverable exception if there is no matching database row.
	 * This implementation will use the service's ProductLoadTuner to load the products.
	 * 
	 * @param criteria product associations search criteria.
	 * @return List&lt;ProductAssociation&gt; of ordered by 'ordering' number associations.
	 * @throws EpServiceException - in case of any errors
	 */
	public List<ProductAssociation> findByCriteria(final ProductAssociationSearchCriteria criteria) throws EpServiceException {
		sanityCheck();
		Pair<String, List<Object>> queryData = buildSearchQuery(criteria);

		if (productAssociationLoadTuner == null) {
			fetchPlanHelper.configureProductFetchPlan(productLoadTuner);
		} else {
			fetchPlanHelper.configureProductAssociationFetchPlan(productAssociationLoadTuner);
		}
		List<ProductAssociation> foundAssociations = getPersistenceEngine().retrieve(queryData.getFirst(), queryData.getSecond().toArray());
		fetchPlanHelper.clearFetchPlan();
		return foundAssociations;
	}

	/**
	 * Builds the search query to use.
	 * 
	 * @param criteria the search criteria
	 * @return a pair of the query and the list of parameters to be used
	 */
	protected Pair<String, List<Object>> buildSearchQuery(final ProductAssociationSearchCriteria criteria) {
		criteria.optimize();

		StringBuilder queryBuffer = new StringBuilder("SELECT pa FROM ProductAssociationImpl pa");
		if (criteria.isWithinCatalogOnly()) {
			queryBuffer.append(", IN(pa.sourceProduct.productCategories) spc, IN(pa.targetProduct.productCategories) tpc");
		}

		List<Object> parameterList = new ArrayList<Object>();
		boolean beginWhereClause = false;
		queryBuffer.append(" WHERE ");
		if (criteria.isWithinCatalogOnly()) {
			queryBuffer.append("tpc.category.catalog = pa.catalog AND spc.category.catalog = pa.catalog");
			beginWhereClause = true;
		}
		
		addParameter(queryBuffer, criteria.getAssociationType(), "pa.associationType", parameterList, beginWhereClause);
		addParameter(queryBuffer, criteria.getSourceProduct(), "pa.sourceProduct", parameterList, beginWhereClause);
		addParameter(queryBuffer, criteria.getSourceProductCode(), "pa.sourceProduct.code", parameterList, beginWhereClause);
		addParameter(queryBuffer, criteria.getTargetProduct(), "pa.targetProduct", parameterList, beginWhereClause);
		addParameter(queryBuffer, criteria.getTargetProductCode(), "pa.targetProduct.code", parameterList, beginWhereClause);
		addParameter(queryBuffer, criteria.getCatalogCode(), "pa.catalog.code", parameterList, beginWhereClause);
		
		// order associations in the result by 'ordering' number in an ascending way (1, 2, 3, ...)
		queryBuffer.append(" ORDER BY pa.ordering ASC");
		
		return new Pair<String, List<Object>>(queryBuffer.toString(), parameterList);
	}

	/**
	 * Looks for product associations and loads only the fields supplied by the load tuner.
	 * 
	 * @param criteria the association criteria
	 * @param loadTuner the load tuner to use
	 * @return a list of associations
	 */
	public List<ProductAssociation> findByCriteria(final ProductAssociationSearchCriteria criteria, final LoadTuner loadTuner) {
		sanityCheck();
		Pair<String, List<Object>> queryData = buildSearchQuery(criteria);

		if (loadTuner != null) {
			fetchPlanHelper.configureLoadTuner(loadTuner);
		}
		List<ProductAssociation> foundAssociations = getPersistenceEngine().retrieve(queryData.getFirst(), queryData.getSecond().toArray());

		fetchPlanHelper.clearFetchPlan();
		return foundAssociations;
	}

	private void addParameter(final StringBuilder queryBuffer, final Object object, final String paramName, 
			final List<Object> parameterList, final boolean beginWhereClause) {
		if (object == null) {
			return;
		}
		if (!parameterList.isEmpty() || beginWhereClause) {
			queryBuffer.append(" AND ");
		}
		
		String addProperty = "";
		Object objectParam = object;
		if (object instanceof Persistable) {
			objectParam = ((Entity) object).getUidPk();
			addProperty = ".uidPk";
		}

		parameterList.add(objectParam);
		int index = parameterList.size();
		queryBuffer.append(paramName).append(addProperty).append(" = ?").append(index);
	}


	/**
	 * Generic get method for all persistable domain models.
	 * 
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		return load(uid);
	}
	
	/**
	 * Get the product association with the given UID. Return null if no matching record exists. 
	 * You can give a product load tuner to fine control what data gets populated of the returned target product.
	 * 
	 * @param productAssociationUid the UID of the association
	 * @param loadTuner the product load tuner to use for the product reference
	 * @return the <code>ProductAssociation</code> object
	 * @deprecated pass in a <code>ProductAssoicationLoadTuner</code> instead.
	 */
	@Deprecated
	public ProductAssociation getTuned(final long productAssociationUid, final ProductLoadTuner loadTuner) {
		fetchPlanHelper.configureProductFetchPlan(loadTuner);
		ProductAssociation association = this.load(productAssociationUid);
		fetchPlanHelper.clearFetchPlan();
		return association;
	}

	/**
	 * Get the product association with the given UID. Return null if no matching record exists. 
	 * You can give a product association load tuner to fine control what data gets populated of the returned target product.
	 * If no load tuner is passed as a parameter the one set by the setProductAssociationLoadTuner will be used.
	 * 
	 * @param productAssociationUid the UID of the association
	 * @param loadTuner the product association load tuner to use
	 * @return the <code>ProductAssociation</code> object
	 */
	public ProductAssociation getTuned(final long productAssociationUid, final ProductAssociationLoadTuner loadTuner) {
		if (loadTuner == null) {
			fetchPlanHelper.configureProductAssociationFetchPlan(this.productAssociationLoadTuner);
		} else {
			fetchPlanHelper.configureProductAssociationFetchPlan(loadTuner);
		}
		ProductAssociation association = this.load(productAssociationUid);
		fetchPlanHelper.clearFetchPlan();
		return association;
	}

	/**
	 * Sets the fetch plan helper.
	 * 
	 * @param fetchPlanHelper the fetch plan helper
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	/**
	 * Gets all product associations of a given type for a given source product, in the given catalog.
	 * This implementation constructs a new ProductSearchCriteria object and passes it into a call to
	 * {@link ProductAssociationServiceImpl#findByCriteria(ProductAssociationSearchCriteria)}.
	 * 
	 * Note that this may return associations whose source and/or target products are not in the catalog
	 * that owns the association unless you set the withinCatalogOnly parameter to true.
	 * 
	 * @param sourceProductCode the code for the product that is associated to other products 
	 * @param associationType the type of product association
	 * @param catalogCode the code for the catalog in which the product has associated products
	 * @param withinCatalogOnly limit the results to association's whose source and target product
	 *                           are both in the association's catalog
	 * @return the set of <code>ProductAssociation</code>s which is ordered by 'ordering' number
	 */
	public Set<ProductAssociation> getAssociationsByType(final String sourceProductCode, 
			final int associationType, final String catalogCode, final boolean withinCatalogOnly) {
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(sourceProductCode);
		criteria.setAssociationType(associationType);
		criteria.setCatalogCode(catalogCode);
		criteria.setWithinCatalogOnly(withinCatalogOnly);

		return getAssociations(criteria);
	}
	
	/**
	 * Gets all product associations for a given source product, in the given catalog.
	 * This implementation constructs a new ProductSearchCriteria object and passes it into a call to
	 * {@link ProductAssociationServiceImpl#findByCriteria(ProductAssociationSearchCriteria)}.
	 * 
	 * Note that this may return associations whose source and/or target products are not in the catalog
	 * that owns the association unless you set the withinCatalogOnly parameter to true.
	 * 
	 * @param sourceProductCode the code for the product that is associated to other products
	 * @param catalogCode the code for the catalog in which the product has association products
	 * @param withinCatalogOnly limit the results to association's whose source and target product
	 *                           are both in the association's catalog
	 * @return the set of <code>ProductAssociation</code>s which is ordered by 'ordering' number
	 */
	public Set<ProductAssociation> getAssociations(final String sourceProductCode, final String catalogCode, 
			final boolean withinCatalogOnly) {
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode(sourceProductCode);
		criteria.setCatalogCode(catalogCode);
		criteria.setWithinCatalogOnly(withinCatalogOnly);

		return getAssociations(criteria);
	}

	@Override
	public Set<ProductAssociation> getAssociations(final ProductAssociationSearchCriteria criteria) {
		Set<ProductAssociation> associationSet = new LinkedHashSet<ProductAssociation>();
		associationSet.addAll(this.findByCriteria(criteria));
		return associationSet;
	}
	
	/**
	 * Sets the ProductLoadTuner used by the FindByCriteria method to load ProductAssociations.
	 * The given load tuner dictates which fields from the TargetProduct will be loaded from the
	 * database.
	 * @param productLoadTuner the load tuner
	 * @deprecated A <code>ProductAssociationLoadTuner</code> should be used instead
	 */
	@Deprecated
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}
	
	/**
	 * Sets the ProductLoadTuner used by the FindByCriteria method to load ProductAssociations.
	 * The given load tuner dictates which fields from the TargetProduct will be loaded from the
	 * database.
	 * @param productAssociationLoadTuner the load tuner
	 */
	public void setProductAssociationLoadTuner(final ProductAssociationLoadTuner productAssociationLoadTuner) {
		this.productAssociationLoadTuner = productAssociationLoadTuner;
	}
	
	/**
	 * Filter a set of product associations so that the set only contains associations that are between products that
	 * are currently in the association's catalog.
	 * 
	 * @param associations the set of <code>ProductAssociations</code> to filter.
	 * @return the filtered set.
	 */
	public Set<ProductAssociation> limitAssociationsToCatalog(final Set<ProductAssociation> associations) {
		Set<ProductAssociation> filteredSet = new HashSet<ProductAssociation>();
		for (ProductAssociation association : associations) {
			if (isAssociationInCatalog(association)) {
				filteredSet.add(association);
			}
		}
		return filteredSet;
	}
	
	/**
	 * Determine whether a product association is between products that are currently in the association's catalog.
	 * 
	 * @param association the <code>ProductAssociation</code> to check.
	 * @return true is the association is valid.
	 */
	public boolean isAssociationInCatalog(final ProductAssociation association) {
		if (association.getCatalog() == null || association.getSourceProduct() == null || association.getTargetProduct() == null) {
			List<Long> result = getPersistenceEngine().retrieveByNamedQuery("PRODUCTASSOCIATION_IN_CATALOG", association.getUidPk());
			if (!result.isEmpty() && Long.valueOf(1L).equals(result.get(0))) {
				return true;
			}
		} else {
			Catalog associationCatalog = association.getCatalog();
			if (association.getSourceProduct().getCatalogs().contains(associationCatalog)
				&& association.getTargetProduct().getCatalogs().contains(associationCatalog)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProductAssociation> getProductAssociationsByType(final Set<StoreProduct> storeProducts, final int associationType,
			final int maxAssociations, final Set< ? extends Product> filterTargetProducts) {
		final Set<ProductAssociation> productAssociations = new HashSet<ProductAssociation>();
		final List<Product> targetProducts = new ArrayList<Product>();
		
		for (final StoreProduct storeProduct : storeProducts) {
			for (final ProductAssociation productAssociation : storeProduct.getAssociationsByType(associationType,
					(Set<Product>) filterTargetProducts)) {
				if (productAssociation.isValidProductAssociation()) {
					final Product targetProduct = productAssociation.getTargetProduct();
					if (!targetProducts.contains(targetProduct)) {
						targetProducts.add(targetProduct);
						productAssociations.add(productAssociation);
					}
				}
			}
		}

		return computeTopProductAssociations(productAssociations, maxAssociations);
	}

	/**
	 * Sorts the given product associations in descending order by how well the association's target product is selling, and returns the top X from
	 * the list where X is the max number of associations to return.
	 * 
	 * @param allProductAssociations the set of product associations from which to compute the top selling target products
	 * @param maxAssociations the maximum number of associations to return
	 * @return the list of associations requested
	 */
	protected List<ProductAssociation> computeTopProductAssociations(final Set<ProductAssociation> allProductAssociations, 
			final int maxAssociations) {
		// Return only up to maxAssociations associations
		List<ProductAssociation> topProductAssociations = new ArrayList<ProductAssociation>();
		for (int i = 0; i < maxAssociations; i++) {
			ProductAssociation bestSeller = getAssociationWithBestSellingTargetProduct(allProductAssociations);
			if (bestSeller != null) {
				allProductAssociations.remove(bestSeller);
				topProductAssociations.add(bestSeller);
			}
		}
		return topProductAssociations;
	}

	/**
	 * Return the <code>ProductAssociation</code> with the target product that has the highest sales volume. This is a helper method for
	 * <code>getProductAssociationsByType()</code>.
	 * 
	 * @param productAssociations the set of <code>ProductAssociation</code>s to search. Size must be > 0.
	 * @return the <code>ProductAssociation</code> with the target product that has the highest sales volume
	 */
	private ProductAssociation getAssociationWithBestSellingTargetProduct(final Set<ProductAssociation> productAssociations) {
		ProductAssociation bestSeller = null;
		for (ProductAssociation currAssociation : productAssociations) {
			if (bestSeller == null || currAssociation.getTargetProduct().getSalesCount() > bestSeller.getTargetProduct().getSalesCount()) {
				bestSeller = currAssociation;
			}
		}
		return bestSeller;
	}

	/**
	 *
	 * @param guid The guid to search for.
	 * @param loadTuner The load tuner to use.
	 * @return The product association for guid.
	 */
	public ProductAssociation findByGuid(final String guid, final LoadTuner loadTuner) {
		String query = "SELECT pa FROM ProductAssociationImpl pa WHERE pa.guid = ?1";
		
		fetchPlanHelper.configureLoadTuner(loadTuner);
		List<ProductAssociation> result = getPersistenceEngine().retrieve(query, guid);
		fetchPlanHelper.clearFetchPlan();
		
		if (result.size() == 1) {
			return result.iterator().next();
		} else if (result.size() > 1) {
			throw new EpServiceException("Inconsistent data. Number of product associations having the same GUID: " + result.size());
		}
		return null;
	}

	/**
	 * @param criteria The criteria to search for.
	 */
	public void removeByCriteria(final ProductAssociationSearchCriteria criteria) {
		List<ProductAssociation> productAssociationList = findByCriteria(criteria);
		for (ProductAssociation pa : productAssociationList) {
			remove(pa);
		}
	}
	
}
