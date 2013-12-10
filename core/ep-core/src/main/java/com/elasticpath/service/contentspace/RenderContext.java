/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.contentspace;

import java.util.Map;

import com.elasticpath.domain.contentspace.ContentWrapper;

/**
 * The context used by the <code>Renderer</code>.
 */
public class RenderContext {

	private ContentWrapper contentWrapper;
	private Map<String, String> params;

	/**
	 * Sets the parameters.
	 * 
	 * @param params the parameters to set
	 */
	public void setParameters(final Map<String, String> params) {
		this.params = params;
	}

	/**
	 *
	 * @param wrapper the wrapper to set
	 */
	public void setContentWrapper(final ContentWrapper wrapper) {
		this.contentWrapper = wrapper;
	}

	/**
	 *
	 * @return the current content wrapper
	 */
	public ContentWrapper getContentWrapper() {
		return contentWrapper;
	}

	/**
	 *
	 * @return all the parameters
	 */
	public Map<String, String> getParameters() {
		return params;
	}

}
