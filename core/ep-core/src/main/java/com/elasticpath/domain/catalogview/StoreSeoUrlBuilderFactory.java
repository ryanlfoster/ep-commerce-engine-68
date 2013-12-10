package com.elasticpath.domain.catalogview;

import com.elasticpath.commons.beanframework.BeanFactory;

/**
 * StoreSeoUrlBuilderFactory builds SeoUrlBuilder instances. 
 */
public interface StoreSeoUrlBuilderFactory {

	/**
	 * Set bean factory.
	 * @param beanFactory is an instance of the bean factory
	 */
	void setBeanFactory(final BeanFactory beanFactory);

	/**
	 * Returns a specific store SeoUrlBuilder from the current list.
	 * 
	 * @return SeoUrlBuilder based on the store code
	 */
	 SeoUrlBuilder getStoreSeoUrlBuilder();	
	 
	 /**
	  * Resets the token separator for each builder.
	  *
	  * @param newTokenSeparator - the new value for the token separator
	  */
	 void resetFieldSeparator(String newTokenSeparator);
}
