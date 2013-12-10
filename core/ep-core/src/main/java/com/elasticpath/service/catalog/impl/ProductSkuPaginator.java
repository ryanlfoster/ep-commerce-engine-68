/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.catalog.impl;

import java.util.List;

import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginatorImpl;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Adapter for providing the required functionality to deliver product SKUs
 * in separate pages instead of in a full set.
 */
public class ProductSkuPaginator extends PaginatorImpl<ProductSku> {

	private static final long serialVersionUID = 1L;

	private ProductSkuService skuService;

	@Override
	protected List<ProductSku> findItems(final Page<ProductSku> page) {
		return getProductSkuService().findSkusByProductCode(getProductCode(), page.getPageStartIndex() - 1,
				page.getPageSize(), page.getOrderingFields(), getLoadTuner());
	}

	/**
	 *
	 * @return the product SKU service
	 */
	protected ProductSkuService getProductSkuService() {
		return skuService;
	}

	/**
	 *
	 * @return the total product SKUs available
	 */
	@Override
	public long getTotalItems() {
		return getProductSkuService().getProductSkuCount(getProductCode());
	}

	/**
	 *
	 * @return the productCode
	 */
	protected String getProductCode() {
		return super.getObjectId();
	}

	/**
	 *
	 * @param skuService the skuService to set
	 */
	public void setProductSkuService(final ProductSkuService skuService) {
		this.skuService = skuService;
	}


}
