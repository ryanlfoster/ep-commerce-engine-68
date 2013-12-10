package com.elasticpath.sfweb.controller;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;

/**
 * Factory for creating {@code BillingAndReviewFormBean}s which are used by the {@code BillingAndReviewFormBeanController}.
 */
public interface BillingAndReviewFormBeanFactory {

	/**
	 * Creates a {@code BillingAndRevieweFormBean}.
	 * 
	 * @param request The request to use for reference.
	 * @return The new bean.
	 */
	BillingAndReviewFormBean createBillingAndReviewFormBean(final HttpServletRequest request);

}
