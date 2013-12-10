/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.misc;

import java.util.Collection;

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a helper which can set a fetch plan for object loading.
 */
public interface FetchPlanHelper {

	/**
	 * Clear the fetch plan configuration.
	 */
	void clearFetchPlan();

	/**
	 * Configure the Category fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureCategoryFetchPlan(final CategoryLoadTuner loadTuner);

	/**
	 * Configure the CategoryType fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureCategoryTypeFetchPlan(final CategoryTypeLoadTuner loadTuner);

	/**
	 * Configure the Product fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureProductFetchPlan(final ProductLoadTuner loadTuner);

	/**
	 * Configure the ProducSku fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureProductSkuFetchPlan(final ProductSkuLoadTuner loadTuner);

	/**
	 * Configure the ProductType fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureProductTypeFetchPlan(final ProductTypeLoadTuner loadTuner);

	/**
	 * Configure the ProductAssociation fetch plan based on the given tuner.
	 * @param loadTuner the load tuner
	 */
	void configureProductAssociationFetchPlan(final ProductAssociationLoadTuner loadTuner);

	/**
	 * Configure the fetch plan based on the given fields requested.
	 * @param clazz class of the object on which to request the specified fields
	 * @param fieldsToLoad the fields to load in requested objects
	 */
	void addFields(final Class< ? > clazz, final Collection<String> fieldsToLoad);

	/**
	 * Add a single field to the fetch plan.
	 * @param clazz class of the object on which to request the specified fields
	 * @param fieldToLoad the fields to load in requested objects
	 */
	void addField(final Class< ? > clazz, final String fieldToLoad);
	
	/**
	 * Configures the fetch plan based on the given {@link FetchGroupLoadTuner}.
	 * This method will clean the existing fetch plan.
	 *
	 * @param loadTuner the load tuner
	 */
	void configureFetchGroupLoadTuner(FetchGroupLoadTuner loadTuner);
	

	/**
	 * Configures the fetch plan based on the given {@link FetchGroupLoadTuner}.
	 *
	 * @param groupLoadTuner the load tuner
	 * @param cleanExistingGroups indicate whether to clean the existing groups. Set to true for cleaning up, 
	 * otherwise keep the active fetch groups.
	 */
	void configureFetchGroupLoadTuner(final FetchGroupLoadTuner groupLoadTuner, final boolean cleanExistingGroups);

	/**
	 * Configures a load tuner.
	 * 
	 * @param loadTuner the load tuner
	 */
	void configureLoadTuner(LoadTuner loadTuner);
	
	/**
	 * Configure which type of fetch mode to use.
	 *
	 * @param fetchMode the fetch mode to use
	 */
	void setFetchMode(final FetchMode fetchMode);
}
