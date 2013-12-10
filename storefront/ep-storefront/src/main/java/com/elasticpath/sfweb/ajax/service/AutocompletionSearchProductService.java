/**
 * 
 */
package com.elasticpath.sfweb.ajax.service;

import java.util.List;

import com.elasticpath.sfweb.ajax.bean.AutocompletionSearchResult;
import com.elasticpath.sfweb.ajax.bean.impl.AutocompletionRequestImpl;


/**
 * A service to get products list for UI autocompletion.
 *
 */
public interface AutocompletionSearchProductService {

	/**
	 * get the list of product for autocompletion request.
	 * @param autocompletionRequest autocompletion reuest object
	 * @return list of products
	 */
	List<AutocompletionSearchResult> findProducts(AutocompletionRequestImpl autocompletionRequest);
}
