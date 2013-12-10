/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc.impl;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.apache.openjpa.meta.FetchGroup;
import org.apache.openjpa.persistence.FetchPlan;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.jdbc.JDBCFetchPlan;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.catalog.impl.AbstractCategoryImpl;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.openjpa.JpaPersistenceSession;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchMode;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * The OpenJPA implementation of <code>FetchPlanHelper</code>.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
public class OpenJPAFetchPlanHelperImpl extends AbstractEpPersistenceServiceImpl implements FetchPlanHelper {
	private static final String ATTRIBUTE_VALUE_MAP = "attributeValueMap";
	private static final Logger LOG = Logger.getLogger(OpenJPAFetchPlanHelperImpl.class);
	private static final String LOCALE_DEPENDANT_FIELDS = "localeDependantFieldsMap";
	private org.apache.openjpa.persistence.jdbc.FetchMode eagerFetchMode;
	private org.apache.openjpa.persistence.jdbc.FetchMode subclassFetchMode;
	
	/**
	 * Clear the fetch plan configuration by popping the new plan off the stack.
	 */
	@Override
	public void clearFetchPlan() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Clearing fetch plan fields");
		}

		JDBCFetchPlan fetchPlan = (JDBCFetchPlan) getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		fetchPlan.clearFields();
		fetchPlan.resetFetchGroups();
		if (eagerFetchMode != null) {
			fetchPlan.setEagerFetchMode(eagerFetchMode);
		}
		if (subclassFetchMode != null) {
			fetchPlan.setSubclassFetchMode(subclassFetchMode);
		}
		eagerFetchMode = null;
		subclassFetchMode = null;
	}

	/**
	 * Configure the Category fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureCategoryFetchPlan(final CategoryLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring category fetch plan");
		}

		if (loadTuner.isLoadingAttributeValue()) {
			fetchPlan.addField(CategoryImpl.class, ATTRIBUTE_VALUE_MAP);
		}
		if (loadTuner.isLoadingChildren()) {

			switch (loadTuner.getChildCategoryLevel()) {
				case -1:
					fetchPlan.addFetchGroup(FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH);
					break;
				case 1:
					//TODO: May need to find a better way to define fetch group, so we can be easily configure recursion level
					// Currently we only have -1 or 1
					fetchPlan.addFetchGroup(FetchGroupConstants.CATEGORY_CHILD_LEVEL_1);
					break;
				default:
					fetchPlan.addFetchGroup(FetchGroupConstants.CATEGORY_CHILD_LEVEL_1);
			}

		}
		if (loadTuner.isLoadingParent()) {
			if (loadTuner.getParentCategoryLevel() == -1) {
				fetchPlan.addFetchGroup(FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH);
			} else {
				fetchPlan.addField(AbstractCategoryImpl.class, "parentInternal");
			}
		}
		if (loadTuner.isLoadingMaster()) {
			fetchPlan.addField(LinkedCategoryImpl.class, "masterCategory");
		}
		if (loadTuner.isLoadingCategoryType()) {
			fetchPlan.addField(CategoryImpl.class, "categoryType");
		}
		if (loadTuner.isLoadingLocaleDependantFields()) {
			fetchPlan.addField(CategoryImpl.class, LOCALE_DEPENDANT_FIELDS);
		}

		configureCategoryTypeFetchPlan(loadTuner.getCategoryTypeLoadTuner());

	}

	/**
	 * Configure the CategoryType fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureCategoryTypeFetchPlan(final CategoryTypeLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring category type fetch plan");
		}

		if (loadTuner.isLoadingAttributes()) {
			fetchPlan.addField(CategoryTypeImpl.class, "categoryAttributeGroupAttributes");
		}

	}

	/**
	 * Configure the Product fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureProductFetchPlan(final ProductLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring product fetch plan");
		}

		// Assume we'll always want locale dependant fields
		fetchPlan.addField(ProductImpl.class, LOCALE_DEPENDANT_FIELDS);

		// Assume we'll always want Brand localized properties
		fetchPlan.addField(BrandImpl.class, "localizedPropertiesMap");

		// Assume we'll always want bundle constituents if the product is a bundle
		fetchPlan.addFetchGroup(FetchGroupConstants.BUNDLE_CONSTITUENTS);

		if (loadTuner.isLoadingAttributeValue()) {
			fetchPlan.addField(ProductImpl.class, ATTRIBUTE_VALUE_MAP);
		}
		if (loadTuner.isLoadingCategories()) {
			fetchPlan.addField(ProductImpl.class, "productCategories");
			configureCategoryFetchPlan(loadTuner.getCategoryLoadTuner());
		}
		if (loadTuner.isLoadingDefaultSku()) {
			configureProductSkuFetchPlan(loadTuner.getProductSkuLoadTuner());
		}
		if (loadTuner.isLoadingProductType()) {
			fetchPlan.addField(ProductImpl.class, "productType");
			configureProductTypeFetchPlan(loadTuner.getProductTypeLoadTuner());
		}
		if (loadTuner.isLoadingSkus()) {
			fetchPlan.addField(ProductImpl.class, "productSkusInternal");
			configureProductSkuFetchPlan(loadTuner.getProductSkuLoadTuner());
		}
	}

	/**
	 * Configure the ProducSku fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureProductSkuFetchPlan(final ProductSkuLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring product sku fetch plan");
		}

		if (loadTuner.isLoadingAttributeValue()) {
			fetchPlan.addField(ProductSkuImpl.class, ATTRIBUTE_VALUE_MAP);
		}
		if (loadTuner.isLoadingOptionValue()) {
			fetchPlan.addField(ProductSkuImpl.class, "optionValueMap");
		}
		if (loadTuner.isLoadingProduct()) {
			fetchPlan.addField(ProductSkuImpl.class, "productInternal");
		}
		if (loadTuner.isLoadingDigitalAsset()) {
			fetchPlan.addField(ProductSkuImpl.class, "digitalAssetInternal");
		}
	}

	/**
	 * Configure the ProductType fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureProductTypeFetchPlan(final ProductTypeLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring product type fetch plan");
		}

		if (loadTuner.isLoadingAttributes()) {
			fetchPlan.addField(ProductTypeImpl.class, "productAttributeGroupAttributes");
			fetchPlan.addField(ProductTypeImpl.class, "skuAttributeGroupAttributes");
		}
		if (loadTuner.isLoadingSkuOptions()) {
			fetchPlan.addField(ProductTypeImpl.class, "skuOptions");
			fetchPlan.addField(ProductSkuImpl.class, "optionValueMap");
		}
	}

	/**
	 * Configure the ProductAssociation fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureProductAssociationFetchPlan(final ProductAssociationLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring product association fetch plan");
		}

		if (loadTuner.isLoadingCatalog()) {
			fetchPlan.addField(ProductAssociationImpl.class, "catalog");
		}

		configureProductFetchPlan(loadTuner.getProductLoadTuner());
	}

	/**
	 * This method is not used and not implemented in this service.
	 *
	 * @param uid not used
	 * @return nothing
	 * @throws EpServiceException - in case it gets called
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("Should never be called.");
	}

	/**
	 * Get the fetch plan by pushing a new plan onto the stack.
	 *
	 * @return the fetch plan
	 */
	protected FetchPlan getFetchPlan() {
		OpenJPAEntityManager oem = getOpenJPAEntityManager();
		return oem.getFetchPlan();
	}

	/**
	 * Get the OpenJPA Entity Manager.
	 *
	 * @return The {@link OpenJPAEntityManager}.
	 */
	protected OpenJPAEntityManager getOpenJPAEntityManager() {
		PersistenceSession session = getPersistenceEngine().getSharedPersistenceSession();
		try {
			EntityManager entityManager = ((JpaPersistenceSession) session).getEntityManager();
			return OpenJPAPersistence.cast(entityManager);
		} catch (ClassCastException ex) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Persistence session cannot be cast to OpenJPA, can't get Fetch Plan");
			}
			return null;
		}
	}

	/**
	 * Configure the fetch plan based on the given fields requested.
	 * @param clazz class of the object on which to request the specified fields
	 * @param fieldsToLoad the fields to load in requested objects
	 */
	@Override
	public void addFields(final Class< ? > clazz, final Collection<String> fieldsToLoad) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		fetchPlan.addFields(clazz, fieldsToLoad);
	}

	/**
	 * Add a single field to the fetch plan.
	 * @param clazz class of the object on which to request the specified fields
	 * @param fieldToLoad the fields to load in requested objects
	 */
	@Override
	public void addField(final Class< ? > clazz, final String fieldToLoad) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		fetchPlan.addField(clazz, fieldToLoad);
	}

	/**
	 * Configures the fetch plan based on the given {@link FetchGroupLoadTuner}.
	 * This method will clean the existing fetch plan.
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureFetchGroupLoadTuner(final FetchGroupLoadTuner loadTuner) {
		configureFetchGroupLoadTuner(loadTuner, true);
	}

	/**
	 * Configures the fetch plan based on the given {@link FetchGroupLoadTuner}.
	 *
	 * @param groupLoadTuner the load tuner
	 * @param cleanExistingGroups indicate whether to clean the existing groups. Set to true for cleaning up,
	 * otherwise keep the active fetch groups.
	 */
	@Override
	public void configureFetchGroupLoadTuner(final FetchGroupLoadTuner groupLoadTuner, final boolean cleanExistingGroups) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null) {
			return;
		}
		if (groupLoadTuner == null && cleanExistingGroups) {
			clearFetchPlan();
			return;
		}

		if (cleanExistingGroups) {
			fetchPlan.clearFetchGroups();
			fetchPlan.removeFetchGroup(FetchGroup.NAME_DEFAULT);
		}
		
		if (groupLoadTuner != null) {
			for (String fetchGroup : groupLoadTuner) {
				fetchPlan.addFetchGroup(fetchGroup);
			}
		}
	}

	/**
	 * Configure the fetch plan based on the given {@link ShoppingItemLoadTuner}.
	 *
	 * @param loadTuner {@link ShoppingItemLoadTuner}
	 */
	protected void configureShoppingItemLoadTuner(final ShoppingItemLoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("configuring shopping item fetch plan");
		}

		// always load the following fields
		fetchPlan.addField("guid");

		if (loadTuner.isLoadingRecursiveDependentItems()) {
			fetchPlan.addFetchGroup(FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS);
		}
		if (loadTuner.isLoadingDefaultAssociationQuantity()) {
			fetchPlan.addField(ShoppingItemImpl.class, "defaultAssociationQuantity");
		}
		if (loadTuner.isLoadingQuantity()) {
			fetchPlan.addField(ShoppingItemImpl.class, "quantityInternal");
		}
		if (loadTuner.isLoadingDependentItems()) {
			fetchPlan.addField(ShoppingItemImpl.class, "dependentItemsInternal");
		}
		if (loadTuner.isLoadingParentItem()) {
			fetchPlan.addField(ShoppingItemImpl.class, "parentItem");
		}
		if (loadTuner.isLoadingProductSku()) {
			fetchPlan.addField(ShoppingItemImpl.class, "productSku");
			fetchPlan.addField(ProductImpl.class, "defaultSku");
			fetchPlan.addField(ProductImpl.class, "productSkusInternal");
		}
		if (loadTuner.isLoadingPrice()) {
			fetchPlan.addField(ShoppingItemImpl.class, "price");
		}
	}

	/**
	 * Configures a load tuner depending on its identity.
	 *
	 * @param loadTuner the load tuner
	 */
	@Override
	public void configureLoadTuner(final LoadTuner loadTuner) {
		FetchPlan fetchPlan = getFetchPlan();
		if (fetchPlan == null || loadTuner == null) {
			return;
		}

		// always load the following fields
		fetchPlan.addField("guid");
		fetchPlan.addField("code");
		
		if (loadTuner instanceof FetchGroupLoadTuner) {
			configureFetchGroupLoadTuner((FetchGroupLoadTuner) loadTuner);
		} else if (loadTuner instanceof ProductLoadTuner) {
			configureProductFetchPlan((ProductLoadTuner) loadTuner);
		} else if (loadTuner instanceof CategoryLoadTuner) {
			configureCategoryFetchPlan((CategoryLoadTuner) loadTuner);
		} else if (loadTuner instanceof CategoryTypeLoadTuner) {
			configureCategoryTypeFetchPlan((CategoryTypeLoadTuner) loadTuner);
		} else if (loadTuner instanceof ProductAssociationLoadTuner) {
			configureProductAssociationFetchPlan((ProductAssociationLoadTuner) loadTuner);
		} else if (loadTuner instanceof ProductSkuLoadTuner) {
			configureProductSkuFetchPlan((ProductSkuLoadTuner) loadTuner);
		} else if (loadTuner instanceof ProductTypeLoadTuner) {
			configureProductTypeFetchPlan((ProductTypeLoadTuner) loadTuner);
		} else if (loadTuner instanceof ShoppingItemLoadTuner) {
			configureShoppingItemLoadTuner((ShoppingItemLoadTuner) loadTuner);
		}
	}

	@Override
	public void setFetchMode(final FetchMode fetchMode) {
		JDBCFetchPlan fetchPlan = (JDBCFetchPlan) getFetchPlan();

		eagerFetchMode = fetchPlan.getEagerFetchMode();
		subclassFetchMode = fetchPlan.getSubclassFetchMode();
		
		switch (fetchMode) {
		case NONE:
			fetchPlan.setEagerFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.NONE);
			fetchPlan.setSubclassFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.NONE);
			break;
		case JOIN:
			fetchPlan.setEagerFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.JOIN);
			fetchPlan.setSubclassFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.JOIN);
			break;
		case PARALLEL:
			fetchPlan.setEagerFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.PARALLEL);
			fetchPlan.setSubclassFetchMode(org.apache.openjpa.persistence.jdbc.FetchMode.PARALLEL);
			break;
		default:
			break;
		}
	}

}
