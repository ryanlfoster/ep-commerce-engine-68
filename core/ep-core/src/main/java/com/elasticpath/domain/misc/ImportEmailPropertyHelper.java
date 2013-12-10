/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc;

import java.util.Locale;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;

/**
 * Helper for constructing email properties.
 */
public interface ImportEmailPropertyHelper {

	/**
	 * Creates new {@link EmailProperties} and sets them.
	 * 
	 * @param runningJob {@link ImportJobStatus}
	 * @param cmUser {@link CmUser}
	 * @param locale of the error messages
	 * @return {@link EmailProperties}
	 */
	EmailProperties getEmailProperties(final ImportJobStatus runningJob, final CmUser cmUser, final Locale locale);

}