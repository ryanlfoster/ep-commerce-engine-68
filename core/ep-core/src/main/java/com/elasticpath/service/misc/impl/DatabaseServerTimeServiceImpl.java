package com.elasticpath.service.misc.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;

/**
 * Provides an implementation of <code>TimeService</code> to retrieve a timestamp from the database server.
 */
public class DatabaseServerTimeServiceImpl extends AbstractEpPersistenceServiceImpl implements TimeService {

	private static final Logger LOG = Logger.getLogger(DatabaseServerTimeServiceImpl.class);
	private static String databaseType;
	private static final String ORACLE_DB_NAME = "oracle";
	private static final String TIME_RETRIEVE_QUERY = "SELECT CURRENT_TIMESTAMP FROM JPA_GENERATED_KEYS WHERE ID='DEFAULT'";
	private static final String ORACLE_TIME_RETRIEVE_QUERY = "SELECT LOCALTIMESTAMP FROM DUAL";

	private ElasticPath elasticpath;

	/**
	 * Returns the current date and time from the database.
	 *
	 * @return the current date and time from the database.
	 */
	public Date getCurrentTime() {
		Date now;
		try {
			final String databaseType = getDatabaseType();
			final List <Date> results = executeTimeQuery(databaseType);
			if (results.size() < 1) {
				throw new EpSystemException("No result returned.");
			}

			// Get the current date/time from the first result
			now = results.get(0);
		} catch (final Exception e) {
			LOG.warn("Could not retrieve db server time!", e);
			LOG.warn("Will use application server timestamp instead.");
			now = new Date();
		}
		
		// WARNING: Do no close the session in this method, since this service may
		// get injected in other services which still need to use the session.
		// This won't cause a session leak, since this service is always in a transaction proxy.
		
		return now;
	}
	
	/**
	 * If the database is Oracle, use the Oracle specific query to get the current time.
	 * this is required, because Oracle returns an Oracle JDBC specific object which can't
	 * be converted to Date if the standard CURRENT_TIMESTAMP DB function is used.
	 * @param dbType the database type
	 * @return list of date results
	 */
	protected List<Date> executeTimeQuery(final String dbType) {
		String queryString = TIME_RETRIEVE_QUERY;
		if (dbType.equalsIgnoreCase(ORACLE_DB_NAME)) {
			queryString = ORACLE_TIME_RETRIEVE_QUERY;
		} else {
			queryString = TIME_RETRIEVE_QUERY;
		}
		final PersistenceSession session = getPersistenceEngine().getPersistenceSession();
		final Query query = session.createSQLQuery(queryString);

		final List<Date> results = query.list();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Got " + results.size() + " rows for time query: " + queryString);
		}

		return results;
	}

	/**
	 * Gets the type of database that is currently being used by the persistence engine.
	 *
	 * @return The database type returned from the database connection. 
	 * @throws SQLException
	 */
	private String getDatabaseType() throws SQLException {
		
		// Get the database type from the database connection if it has not yet been set
		if (databaseType == null) {
			Connection connection = null;
			try {
				connection = getPersistenceEngine().getConnection();
				final String databaseName = connection.getMetaData().getDatabaseProductName();
				databaseType = databaseName;
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		}
		
		return databaseType;
	}
	
	/**
	 * Sets the database that is currently being used by the persistence engine.
	 * This method is only for testing purposes and should not be called by
	 * any code other then test code.
	 *
	 * @param type - The name of the database type.
	 */
	void setDatabaseType(final String type) {
		databaseType = type;
	}
	
	/**
	 * Returns the elastic path context.
	 *
	 * @return the elastic path context
	 */
	@Override
	public ElasticPath getElasticPath() {
		return this.elasticpath;
	}

	/**
	 * Sets the elastic path context.
	 *
	 * @param elasticpath sets the elastic path
	 */
	@Override
	public void setElasticPath(final ElasticPath elasticpath) {
		this.elasticpath = elasticpath;
	}

	/**
	 * Not implmented.
	 *
	 * @param uid not used
	 * @return never returns
	 * @throws EpServiceException when it is called
	 */
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpUnsupportedOperationException("Unimplemented!");
	}
}
