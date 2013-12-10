package com.elasticpath.epcoretool.logic;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * The Class AbstractSetCmUserPassword.
 */
public abstract class AbstractSetCmUserPassword extends AbstractEpCore {

	/**
	 * Instantiates a new abstract set cm user password.
	 *
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractSetCmUserPassword(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}

	/**
	 * Execute.
	 *
	 * @param username the username
	 * @param password the password
	 */
	public void execute(final String username, final String password) {
		CmUserService cmUserService = epCore().getCmUserService();

		CmUser cmUser = cmUserService.findByUserName(username);
		if (cmUser == null) {
			throw new IllegalArgumentException("Unable to find user " + username);
		}
		cmUser.setCheckedClearTextPassword(password);
		cmUserService.update(cmUser);
	}
}
