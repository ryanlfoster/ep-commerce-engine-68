package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.elasticpath.epcoretool.LoggerFacade;

/**
 * Extends {@code AbstractMojo} to include both the jdbc settings for EP and a mechanism to fetch a {@code EpCoreManager}.
 */
public abstract class AbstractEpCoreMojo extends AbstractMojo {

	/**
	 * @parameter default-value="false"
	 * @required
	 */
	private boolean skip;

	/**
	 * JDBC URL.
	 * 
	 * @parameter expression="${epdb.url}"
	 * @required
	 */
	private String jdbcUrl;

	/**
	 * Database Username.
	 * 
	 * @parameter expression="${epdb.username}"
	 */
	private String jdbcUsername;

	/**
	 * Database Password.
	 * 
	 * @parameter expression="${epdb.password}"
	 */
	private String jdbcPassword;

	/**
	 * Database JDBC Driver Class.
	 * 
	 * @parameter expression="${epdb.jdbc.driver}"
	 * @required
	 */
	private String jdbcDriverClass;

	/**
	 * Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle
	 * connections.
	 * 
	 * @parameter expression="${epdb.jdbc.min.idle}"
	 */
	private Integer jdbcConnectionPoolMinIdle;

	/**
	 * Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle
	 * to 0 to prohibit idle connections.
	 * 
	 * @parameter expression="${epdb.jdbc.max.idle}"
	 */
	private Integer jdbcConnectionPoolMaxIdle;

	private final LoggerFacade logger = new LoggerFacade() {

		@Override
		public void error(final String message) {
			getLog().error(message);
		}

		@Override
		public void warn(final String message) {
			getLog().warn(message);
		}

		@Override
		public void info(final String message) {
			getLog().info(message);
		}

		@Override
		public void debug(final String message) {
			getLog().debug(message);
		}
	};

	/**
	 * Execute.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping ep-core-tool execution");
			return;
		}
		executeMojo();
	}

	/**
	 * Execute mojo.
	 * 
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

	/**
	 * Gets the jdbc url.
	 * 
	 * @return the jdbc url
	 */
	protected String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * Gets the jdbc username.
	 * 
	 * @return the jdbc username
	 */
	protected String getJdbcUsername() {
		return jdbcUsername;
	}

	/**
	 * Gets the jdbc password.
	 * 
	 * @return the jdbc password
	 */
	protected String getJdbcPassword() {
		return jdbcPassword;
	}

	/**
	 * Gets the jdbc driver class.
	 * 
	 * @return the jdbc driver class
	 */
	protected String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	/**
	 * Gets the jdbc connection pool min idle.
	 * 
	 * @return the jdbc connection pool min idle
	 */
	protected Integer getJdbcConnectionPoolMinIdle() {
		return jdbcConnectionPoolMinIdle;
	}

	/**
	 * Gets the jdbc connection pool max idle.
	 * 
	 * @return the jdbc connection pool max idle
	 */
	protected Integer getJdbcConnectionPoolMaxIdle() {
		return jdbcConnectionPoolMaxIdle;
	}

	/**
	 * Checks if is skip.
	 *
	 * @return true, if is skip
	 */
	protected boolean isSkip() {
		return skip;
	}

	/**
	 * Gets the logger facade.
	 * 
	 * @return the logger facade
	 */
	protected LoggerFacade getLoggerFacade() {
		return logger;
	}
}
