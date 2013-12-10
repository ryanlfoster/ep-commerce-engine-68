package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides a service for generating product data in xml format.
 */
public interface ProductXmlService {
	/**
	 * Returns the minimal product data required by PowerReviews in xml format.
	 * 
	 * @param catalog - the catalog that the product is should be in.
	 * @param baseUrl - the base url of the request.
	 * @param productUid - the product uid of the product to get data from.
	 * @param isSeoEnabled - True if SEO is enabled for the store.
	 * @return the product data in xml format
	 * @throws EpServiceException - in case of errors
	 */
	String getProductMinimalXml(Catalog catalog, String baseUrl, String productUid, boolean isSeoEnabled) throws EpServiceException;
}
