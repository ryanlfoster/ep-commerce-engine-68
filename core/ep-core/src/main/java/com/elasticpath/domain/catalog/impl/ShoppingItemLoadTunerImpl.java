/**
 * Copyrighted to Elastic Path Software Inc, 2009.
 */
package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 *
 */
public class ShoppingItemLoadTunerImpl extends AbstractEpDomainImpl implements ShoppingItemLoadTuner {

	private static final long serialVersionUID = 1L;

	private Boolean loadingRecursiveDependentItems = false;

	private Boolean loadingDefaultAssociationQuantity = false;

	private Boolean loadingDependentItems = false;

	private Boolean loadingParentItem = false;

	private Boolean loadingProductSku = false;

	private Boolean loadingQuantity = false;

	private Boolean loadingPrice = false;

	@Override
	public Boolean isLoadingRecursiveDependentItems() {
		return loadingRecursiveDependentItems;
	}

	@Override
	public Boolean isLoadingDefaultAssociationQuantity() {
		return loadingDefaultAssociationQuantity;
	}

	@Override
	public Boolean isLoadingDependentItems() {
		return loadingDependentItems;
	}

	@Override
	public Boolean isLoadingParentItem() {
		return loadingParentItem;
	}

	@Override
	public Boolean isLoadingProductSku() {
		return loadingProductSku;
	}

	@Override
	public Boolean isLoadingQuantity() {
		return loadingQuantity;
	}

	@Override
	public Boolean isLoadingPrice() {
		return loadingPrice;
	}

	@Override
	public void setLoadingDefaultAssociationQuantity(final Boolean flag) {
		loadingDefaultAssociationQuantity = flag;
	}

	@Override
	public void setLoadingDependentItems(final Boolean flag) {
		loadingDependentItems = flag;
	}

	@Override
	public void setLoadingParentItem(final Boolean flag) {
		loadingParentItem = flag;
	}

	@Override
	public void setLoadingProductSku(final Boolean flag) {
		loadingProductSku = flag;
	}

	@Override
	public void setLoadingQuantity(final Boolean flag) {
		loadingQuantity = flag;
	}

	@Override
	public void setLoadingRecursiveDependentItems(final Boolean flag) {
		loadingRecursiveDependentItems = flag;
	}

	@Override
	public void setLoadingPrice(final Boolean flag) {
		loadingPrice = flag;
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		return false;
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		return this;
	}
}
