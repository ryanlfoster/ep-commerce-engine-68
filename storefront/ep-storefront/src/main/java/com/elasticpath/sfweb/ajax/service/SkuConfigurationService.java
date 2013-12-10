/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.service;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.EpService;
import com.elasticpath.sfweb.ajax.bean.GuidedSkuSelectionBean;

/**
 * Provides services relating to Sku Configuration.
 */
public interface SkuConfigurationService extends EpService {
	
	/**
	 * Returns the set of SkuOptions with option values for which there are skus available given the selected when the primary option value. The
	 * product's first option is considered the "primary" option and all of its option values are always returned.
	 * 
	 * @param productUid the UID of the product whose options are being selected
	 * @param selectedOptionValueCodes the option value codes that have already been selected
	 * @return A set of SkuOptions with each having the available SkuOptionValues in them
	 */
	Set<SkuOption> getAvailableOptionValues(final long productUid, final List<String> selectedOptionValueCodes);
	
	/**
	 * Gets a SKU matches the specifice option value codes. Also returns display information required by the client including the SKU price and the
	 * URL to the SKU's image.
	 * 
	 * @param productUid the SKU's product UID
	 * @param optionValueCodes a list of option value codes the SKU must have
	 * @param currencyCode the code for the currency
	 * @param request {@link HttpServletRequest}
	 * @return a bean containing the selected SKU and other display information.
	 */
	GuidedSkuSelectionBean getSkuWithMatchingOptionValues(final long productUid, final List<String> optionValueCodes, 
			final String currencyCode, final HttpServletRequest request);
	
	/**
	 * Gets a SKU matches the specifice option value codes. Also returns display information required by the client including the SKU price and the
	 * URL to the SKU's image.
	 * 
	 * @param productUid the SKU's product UID
	 * @param optionValueCodes a list of option value codes the SKU must have
	 * @param currencyCode the code for the currency
	 * @param quantity the quantity of the sku item
	 * @param request {@link HttpServletRequest}
	 * @return a bean containing the selected SKU and other display information.
	 */
	GuidedSkuSelectionBean getSkuWithMatchingOptionValuesAndQuantity(final long productUid, final List<String> optionValueCodes, 
			final String currencyCode, final int quantity, final HttpServletRequest request);

}
