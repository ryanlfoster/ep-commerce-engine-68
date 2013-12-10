package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.Persistable;

/**
 * Rule for specifying how many constituents of a bundle needs to be included when sold.
 */
public interface SelectionRule extends Persistable {
	
	/**
	 * @return the number assigned for this rule 
	 */
	int getParameter();

	/**
	 * @param value the parameter value to set.
	 */
	void setParameter(int value);
	
    /**
     * get the bundle product this rule is assigned too.
     *
     * @return the bundle
     */
    ProductBundle getBundle();
    
    /**
     * Sets the bundle product this rule is assigned too.
     *
     * @param bundle the bundle to be set
     */
    void setBundle(ProductBundle bundle);
}