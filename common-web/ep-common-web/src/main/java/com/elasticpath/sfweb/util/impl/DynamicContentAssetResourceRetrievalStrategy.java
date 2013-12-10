package com.elasticpath.sfweb.util.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.sfweb.util.FilenameUtils;

/**
 * This retrieval strategy loads files from the Dynamic Content Assets Folder under the Assets directory. 
 */
public class DynamicContentAssetResourceRetrievalStrategy 
	extends AbstractResourceRetrievalStrategy {

	private static final Logger LOG = Logger.getLogger(DynamicContentAssetResourceRetrievalStrategy.class);
	
	/**
	 * Resolve a resource for a piece of dynamic content. If it doesn't exist there is no fallback.
	 *
	 * @param resourcePath the path to the resource (must not be blank)
	 * @return a file system path to the resource, could be null.
	 */
	@Override
	public String getFullPath(final String resourcePath) {
		
		if (StringUtils.isBlank(resourcePath)) {
			return null;
		}
		
		final String encodedPath = encode(resourcePath);
		
		if (StringUtils.isBlank(encodedPath)) {
			return null;
		}		
		
		final String fullPath = FilenameUtils.formPath(
				getAssetRepository().getDynamicContentAssetsPath(), 
				encodedPath.replace(WebConstants.DYNAMIC_CONTENT_REWRITE_PATTERN, "/"));
		LOG.debug(fullPath);
		return fullPath;
	}
	
	private String encode(final String resourcePath) {
		try {
			return URLDecoder.decode(resourcePath, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return resourcePath;
		}
	}
}
