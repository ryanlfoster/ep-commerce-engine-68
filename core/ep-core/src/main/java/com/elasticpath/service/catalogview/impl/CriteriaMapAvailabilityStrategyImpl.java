/**
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.service.catalogview.impl;

import java.util.Map;

import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalogview.AvailabilityStrategy;

/**
 * An {@link AvailabilityStrategy} that uses an injected map from {@link AvailabilityCriteria} to {@link Availability}.
 */
public class CriteriaMapAvailabilityStrategyImpl implements AvailabilityStrategy {

	private Map<AvailabilityCriteria, Availability> criteriaMap;
	
	@Override
	public Availability getAvailability(final Product product, final boolean isAvailable, final boolean isDisplayable, final boolean isPuchaseable) {
		return getCriteriaMap().get(product.getAvailabilityCriteria());
	}

	protected Map<AvailabilityCriteria, Availability> getCriteriaMap() {
		return criteriaMap;
	}

	public void setCriteriaMap(final Map<AvailabilityCriteria, Availability> criteriaMap) {
		this.criteriaMap = criteriaMap;
	}
	
	

}
