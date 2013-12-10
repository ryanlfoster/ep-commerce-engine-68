/**
 * 
 */
package com.elasticpath.sfweb.ajax.bean.impl;

import com.elasticpath.sfweb.ajax.bean.AutocompletionRequest;

/**
 * Implementation of <code>AutocompletionRequest</code>.
 *
 */
public class AutocompletionRequestImpl implements AutocompletionRequest {

	private String searchText;
	
	private String categoryUid;
	
	@Override
	public String getSearchText() {
		return this.searchText;
	}

	@Override
	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}
	
	@Override
	public String getCategoryUid() {
		return categoryUid;
		
	}

	@Override
	public void setCategoryUid(final String categoryUid) {
		this.categoryUid = categoryUid;		
	}
	

}
