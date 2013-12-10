/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * The abstract implement of the filter.
 *
 * @param <T> the type of filter
 */
public abstract class AbstractFilterImpl<T> extends AbstractEpDomainImpl implements Filter<T> {
	private static final long serialVersionUID = -5267643082334790728L;

	private String filterId;
	private boolean localized;
	private String storeCode;
	private String separatorInToken = SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN;

	/**
	 * Sets the filter id and initialize the filter. Calls {@code parseFilterString} to
	 * parse the string into a map of property name to value and then uses this to
	 * initialize the properties.
	 *
	 * @param filterId the id to set
	 * @throws EpCatalogViewRequestBindException when the given filter id is invalid
	 */
	@Override
	public void initialize(final String filterId) throws EpCatalogViewRequestBindException {
		this.initialize(parseFilterString(filterId));
	}

	/**
	 * Check the filter is localized.
	 *
	 * @return the localized
	 */
	@Override
	public boolean isLocalized() {
		return localized;
	}

	/**
	 * Set the filter is localized.
	 *
	 * @param localized the localized to set
	 */
	@Override
	public void setLocalized(final boolean localized) {
		this.localized = localized;
	}

	/**
	 * Returns the id of the filter. Every filter has a unique id.
	 *
	 * @return the id of the filter
	 */
	@Override
	public final String getId() {
		return this.filterId;
	}

	/**
	 * Set the id of the filter.
	 * Every filter has a unique id.
	 *
	 * @param filterId the id of the filter
	 */
	@Override
	public final void setId(final String filterId) {
		this.filterId = filterId;
	}

	/**
	 * @return the code for the {@link com.elasticpath.domain.Store} for which this filter is valid
	 */
	public String getStoreCode() {
		return this.storeCode;
	}

	/**
	 * Set the code representing the {@link com.elasticpath.domain.Store} for which this filter is valid.
	 * @param storeCode the store's code
	 */
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	public String getSeparatorInToken() {
		return separatorInToken;
	}

	@Override
	public void setSeparatorInToken(final String separatorInToken) {
		this.separatorInToken = separatorInToken;
	}

	/**
	 * Returns <code>true</code> if this filter equals to the given object.
	 *
	 * @param object the object to compare
	 * @return <code>true</code> if this filter equals to the given object.
	 */
	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Filter)) {
			return false;
		}

		final Filter< ? > impl = (Filter< ? >) object;
		return getId().equals(impl.getId());
	}

	/**
	 * Returns the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		if (this.getId() == null) {
			return super.hashCode();
		}
		return getId().hashCode();
	}
}
