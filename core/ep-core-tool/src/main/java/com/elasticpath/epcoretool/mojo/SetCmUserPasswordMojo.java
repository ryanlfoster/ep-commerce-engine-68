package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetCmUserPassword;

/** Suppress unknown tag warning. */
@SuppressWarnings("javadoc")

/**
 * Set the password of the specified CM User.
 * 
 * @goal set-cmuser-password
 */
public class SetCmUserPasswordMojo extends AbstractEpCoreMojo {

	/**
	 * Username of the CM User.
	 * 
	 * @parameter expression="${username}"
	 */
	private String username;

	/**
	 * New plaintext password for the CM user.
	 * 
	 * @parameter expression="${password}"
	 */
	private String password;

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractSetCmUserPassword setCmUserPassword = new AbstractSetCmUserPassword(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			setCmUserPassword.execute(username, password);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		}

	}

}
