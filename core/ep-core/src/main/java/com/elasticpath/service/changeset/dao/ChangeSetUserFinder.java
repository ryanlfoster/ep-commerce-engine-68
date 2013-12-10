/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.changeset.dao;

/**
 * This class is used for finding change set users.
 */
public interface ChangeSetUserFinder {
	
	/**
	 * Find change set user guid by user name.
	 *
	 * @param userName user name
	 * @return user guid
	 */
	String findUserGuidByUserName(String userName);
}
