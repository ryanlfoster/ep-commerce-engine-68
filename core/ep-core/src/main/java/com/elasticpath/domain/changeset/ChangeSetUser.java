package com.elasticpath.domain.changeset;


/**
 * Represents a change set user in the system.
 */
public interface ChangeSetUser {

	/**
	 * Get the user guid.
	 * @return the user guid
	 */
	String getUserGuid();

	/**
	 * Set user guid.
	 * @param userGuid is the user guid
	 */
	void setUserGuid(final String userGuid);
}
