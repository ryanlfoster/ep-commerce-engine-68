package com.elasticpath.domain.dataimport.impl;

import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.Persistable;

/**
 * Non-Persistent POJO class used to hold ProductCategory values prior to import.  Override getValueObject() and
 * extend this class if you need to import additional fields in an extension class.
 */
public class ProductCategoryImportBean implements Persistable {
	private static final long serialVersionUID = -5785190998130622765L;

	private static final String NOT_IMPLEMENTED = "Not Implemented.";

	private Category category;
	private int featuredProductOrder;

	/**
	 * Get the category.
	 *
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Set the category.
	 *
	 * @param category the category to set
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * Get the featured product order.
	 *
	 * @return the featured product order
	 */
	public int getFeaturedProductOrder() {
		return featuredProductOrder;
	}

	/**
	 * Set the featured product order.
	 *
	 * @param featuredProductOrder the featured product order to set
	 */
	public void setFeaturedProductOrder(final int featuredProductOrder) {
		this.featuredProductOrder = featuredProductOrder;
	}

	/**
	 * Gets the unique identifier for this domain object. This unique identifier is system-dependent. That means on different systems(like staging
	 * and production environments), different identifiers might be assigned to the same(from business perspective) domain object.
	 * <p/>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifier. They are cascade loaded and
	 * updated through their parents.
	 *
	 * @return the unique identifier.
	 */
	@Override
	public long getUidPk() {
		throw new EpUnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		throw new EpUnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * <code>true</code> if the object has previously been persisted.
	 * <p/>
	 * Notice: not all persistent domain objects has unique identifier. Some value objects don't have unique identifier. They are cascade loaded and
	 * updated through their parents. It doesn't make sense to call this method on those value object.
	 *
	 * @return <code>true</code> if the object has previously been persisted.
	 */
	@Override
	public boolean isPersisted() {
		throw new EpUnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
