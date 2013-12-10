/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.contentspace;

import java.util.Locale;


/**
 * Responsible for handling a call from a velocity template 
 * and rendering a content space into content.
 */
public interface RenderMediator {

	/**
	 * Renders a content space.
	 * 
	 * @param contentSpaceName the content space name
	 * @param locale the locale to use
	 * @return localized rendered contents
	 */
	String render(final String contentSpaceName, final Locale locale);

}
