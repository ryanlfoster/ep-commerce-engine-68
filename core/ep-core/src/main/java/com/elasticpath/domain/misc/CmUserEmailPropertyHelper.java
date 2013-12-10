/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.misc;

import java.util.Locale;

import com.elasticpath.domain.cmuser.CmUser;

/**
 * Helper for constructing email properties.
 */
public interface CmUserEmailPropertyHelper {

	/**
	 * Returns email properties for create password.
	 * 
	 * @param cmUser the {@link CmUser}
	 * @param newPassword the new password
	 * @param locale the locale of the email messages
	 * 
	 * @return {@link EmailProperties}
	 */
	EmailProperties getCreateEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale);

	/**
	 * Returns email properties for reset password.
	 * 
	 * @param cmUser the {@link CmUser}
	 * @param newPassword the new password
	 * @param locale the locale of the email messages
	 * 
	 * @return {@link EmailProperties}
	 */
	EmailProperties getResetEmailProperties(final CmUser cmUser, final String newPassword, final Locale locale);
}