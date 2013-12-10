package com.elasticpath.epcoretool.logic;

import com.elasticpath.epcoretool.DataSourceFactory;
import com.elasticpath.epcoretool.EmbeddedEpCore;
import com.elasticpath.epcoretool.LoggerFacade;

/**
 * The Class AbstractEpCore.
 */
public abstract class AbstractEpCore {

	private EmbeddedEpCore embeddedEpCore;

	/**
	 * JDBC URL.
	 */
	private final String jdbcUrl;

	/**
	 * Database Username.
	 */
	private final String jdbcUsername;

	/**
	 * Database Password.
	 */
	private final String jdbcPassword;

	/**
	 * Database JDBC Driver Class.
	 */
	private final String jdbcDriverClass;

	/**
	 * Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle
	 * connections.
	 */
	private final Integer jdbcConnectionPoolMinIdle;

	/**
	 * Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle
	 * to 0 to prohibit idle connections.
	 */
	private final Integer jdbcConnectionPoolMaxIdle;

	/**
	 * Instantiates a new abstract ep core.
	 * 
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractEpCore(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		this.jdbcUrl = jdbcUrl;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
		this.jdbcDriverClass = jdbcDriverClass;
		this.jdbcConnectionPoolMinIdle = jdbcConnectionPoolMinIdle;
		this.jdbcConnectionPoolMaxIdle = jdbcConnectionPoolMaxIdle;
	}

	/**
	 * Gets the logger.
	 * 
	 * @return the logger
	 */
	protected abstract LoggerFacade getLogger();

	/**
	 * Fetch or create an instance. If there isn't one created yet, this method will populate a MavenDataSourceFactory and construct an
	 * EmbeddedEpCore which is expected to do all the initialize it wants in its constructor.
	 * 
	 * @return a fully-activated {@code EpCoreManager}.
	 */
	protected EmbeddedEpCore epCore() {

		synchronized (this) {

			if (embeddedEpCore == null) {

				getLogger().debug("Configuring embedded EP core");
				DataSourceFactory.configure(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle,
						jdbcConnectionPoolMaxIdle);
				embeddedEpCore = new EmbeddedEpCore();

				getLogger().debug(
						String.format("Elastic Path Database Properties: url=%s username=%s password=%s driverClass=%s minIdle=%s maxIdle=%s",
								jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle));
			}

			return embeddedEpCore;
		}
	}
}
