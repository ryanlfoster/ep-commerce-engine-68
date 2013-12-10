/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.util.cryptotool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Use an updateable ResultSet (with configurable fetch-size) to perform the update.
 */
@SuppressWarnings({ "PMD.SystemPrintln", "PMD.CyclomaticComplexity", "PMD.DoNotCallSystemExit" })
public class ResultSetUpdater implements ColumnUpdater {

	private int fetchSize = -1;

	// database configuration
	private String jdbcDriver;

	private String dbUrl;

	private String dbUser;

	private String dbPass;

	private String tableName = "TORDERPAYMENT";

	private String srcColumnName = "CARD_NUMBER"; // default is necessary for this field

	private String destColumnName = "MASKED_CARD_NUMBER"; // default is necessary for this field
	
	private boolean nullSrcColumnFlag = false; // by default, do not null the source column

	private List<String> stores;

	private Connection connection;
	
	private ResultSet results;
	
	private Statement statement;

	private boolean dryRun = true;

	private boolean verbose = false;

	private final StringFilter filter;

	@Override
	public void doUpdate() {
		try {
			// Connect to the database
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
			connection.setAutoCommit(false);
			
			results = fetchOrderPaymentRecords();
			if (results == null) {
				System.exit(1);
			}

			System.out.println("Beginning database updates.");

			int index = 0;
			String origSrcValue; 
			String origDestValue;
			String newDestValue;
			String uidPk;
			
			
			while (results.next()) {
				origSrcValue = results.getString(srcColumnName);

				// Error-handling: do not update if the value is null
				if (origSrcValue == null) {
					continue;
				}

				origDestValue = results.getString(destColumnName);
				newDestValue = filter.applyTo(origSrcValue);
				uidPk = results.getString("UIDPK");
				
				// Do not update the DB if this is a dry run
				if (!dryRun) {
					//Update the row in a separate query
					updateOrderPaymentRow(newDestValue, uidPk);
				}
				
				// Detail what's happening if either 'verbose' or 'dryRun' have been specified
				if (verbose || dryRun) {
					String newSrcValue = null;
					if (!nullSrcColumnFlag) {
						newSrcValue = origSrcValue;
					}
					
					System.out.println("Row " + (index + 1));
					if (!srcColumnName.equals(destColumnName)) {
						System.out.println("  " + srcColumnName + ": " + origSrcValue + " > " + newSrcValue);
					}
					System.out.println("  " + destColumnName + ": " + origDestValue + " > " + newDestValue);
				}

				index++;
			}

			this.printSummary(index);
		} catch (final Exception e) {
			System.err.println("doUpdate exception: " + e.getMessage());
			e.printStackTrace(System.err); //NOPMD
			System.exit(1);
		} finally {
			cleanupDb();
		}
	}

	/**
	 * Prints a summary of the update.
	 * 
	 * @param numUpdates the number of rows updated.
	 */
	private void printSummary(final int numUpdates) {
		if (dryRun) {
			System.out.println("\n" + numUpdates + " rows would have been updated had the 'dryRun' option not been specified.\n");
		} else {
			System.out.println("\n" + numUpdates + " rows have been updated.\n");
		}
	}

	/**
	 * Performs cleanup functions on the DB.
	 */
	private void cleanupDb() {
		try {
			results.close();
			statement.close();
		} catch (final SQLException ex) {
			System.err.println("Error in cleanupDb: " + ex.getMessage());
		}
	}

	/**
	 * Create a ResulSetUpdater.
	 * 
	 * @param fetchSize The number of records to fetch at one time.
	 * @param verbose print extra information
	 * @param dryRun don't actually commit anything
	 * @param filter apply the filter to each value in the column
	 */
	public ResultSetUpdater(final StringFilter filter, final int fetchSize, final boolean verbose, final boolean dryRun) {
		this.filter = filter;
		this.fetchSize = fetchSize;
		this.verbose = verbose;
		this.dryRun = dryRun;
	}

	/**
	 * Fetches a {@link ResultSet} from the database. 
	 *
	 * @return a {@link ResultSet} from the database
	 * @throws Exception exception
	 */
	private ResultSet fetchOrderPaymentRecords() {
		try {
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			results = statement.executeQuery(createSelectQuery());

						
			if (statement.getWarnings() != null) { 
				System.out.println("Executed with warnings: " + statement.getWarnings());
			}
			if (fetchSize > 0) {
				results.setFetchSize(fetchSize);
			}

		} catch (final Exception ex) {
			System.err.println("Error connecting to database");
                        ex.printStackTrace(System.err); //NOPMD
		}

		return results;
	}

	
	/**
	 * Update the destination credit card number column with a new value. 
	 * 
	 * Separating this out allows us create query statements with different update properties.
	 * 
	 * @param newValue new encrypted value
	 * @param uidPk key of the record for update
	 */
	void updateOrderPaymentRow(final String newValue, final String uidPk) {
		final String updateQuery1 = String.format("UPDATE " + tableName + "  SET " + destColumnName + "=" + "'%s' WHERE UIDPK = %s", newValue, uidPk);
		final String updateQuery2 = String.format("UPDATE " + tableName + "  SET " + srcColumnName + "=" + "null WHERE UIDPK=%s", uidPk);
		Statement updateStatement = null;
		try {
			updateStatement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			System.out.println("\tExecuting:\t" + updateQuery1);
			updateStatement.executeUpdate(updateQuery1);
			System.out.println("\tExecuting:\t" + updateQuery2);
			updateStatement.executeUpdate(updateQuery2);

			connection.commit();
			if (statement.getWarnings() != null) { 
				System.out.println("Executed with warnings: " + statement.getWarnings());
			}
			updateStatement.close();
		} catch (final Exception ex) {
			System.err.println("Error connecting to database");
                        ex.printStackTrace(System.err); //NOPMD
		} finally {
			try {
				if (updateStatement != null) {
					updateStatement.close();
				}
			} catch (final Exception e) {
				System.out.println("Exception closing statement " + e);
			}
		}
	
	}
	
	/**
	 * Create the query string used to retrieve the table containing the column of interest.
	 * 
	 * @return the database query
	 */
	String createSelectQuery() {
		String result = StringUtils.EMPTY;

		if (CollectionUtils.isNotEmpty(stores)) {
			final StringBuilder query = new StringBuilder();
			query.append("SELECT p." + srcColumnName + ", p." + destColumnName + ", p.UIDPK FROM " + tableName + " p, TSTORE s, TORDER o ");
			query.append("WHERE p.ORDER_UID=o.UIDPK ");
			query.append("AND o.STORE_UID=s.UIDPK ");
			query.append("AND s.STORECODE IN (" + formatList(stores) + ") ");
			result = query.toString();
		} else {
			result = String.format("SELECT p.* FROM %s p", tableName);
		}
		System.out.println("Using query: \n" + result + "\n");
		
		return result;
	}

	/**
	 * Format a list as a string of its comma separated, quoted elements.
	 * 
	 * @param list the list to format
	 * @return string formatted comma separated quoted list of elements
	 */
	String formatList(final List<String> list) {
		if (CollectionUtils.isEmpty(list)) {
			return "''";
		}

		final StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(String.format("'%s'", list.get(0)));
		for (final String item : list.subList(1, list.size())) {
			stringBuilder.append(String.format(",'%s'", item));
		}

		return stringBuilder.toString();
	}

	/**
	 * Sets the database JDBC driver used by the CryptoTool.
	 *
	 * @param jdbcDriver the database JDBC driver used by the CryptoTool
	 */
	public void setJdbcDriver(final String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	/**
	 * Sets the database URL used by the CryptoTool.
	 *
	 * @param dbUrl the database URL used by the CryptoTool
	 */
	public void setDbUrl(final String dbUrl) {
		this.dbUrl = dbUrl;
	}

	/**
	 * Sets the database username used by the CryptoTool.
	 *
	 * @param dbUser the database username used by the CryptoTool
	 */
	public void setDbUser(final String dbUser) {
		this.dbUser = dbUser;
	}

	/**
	 * Sets the database password used by the CryptoTool.
	 *
	 * @param dbPass the database password used by the CryptoTool
	 */
	public void setDbPass(final String dbPass) {
		this.dbPass = dbPass;
	}

	/**
	 * Sets the table name. 
	 *
	 * @param tableName the table name to set
	 */
	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Sets the source column name.
	 * 
	 * @param srcColumnName the source columnName to set
	 */
	public void setSrcColumnName(final String srcColumnName) {
		this.srcColumnName = srcColumnName;
	}

	/**
	 * Sets the destination column name.
	 * 
	 * @param destColumnName the destination columnName to set
	 */
	public void setDestColumnName(final String destColumnName) {
		this.destColumnName = destColumnName;
	}

	/**
	 * Sets the {@link List} of store codes.
	 *
	 * @param stores the {@link List} of store codes
	 */
	public void setStores(final List<String> stores) {
		this.stores = stores;
	}

	/**
	 * Sets the fetchSize value to use when fetching/commiting to the database. 
	 *
	 * @param fetchSize the fetchSize value to use when fetching/commiting to the database
	 */
	public void setFetchSize(final int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Sets the dryRun flag to indicate whether or not the CryptoTool should update the database. 
	 *
	 * @param dryRun set to <code>true</code> to perform a dry run and not write to the database; false otherwise
	 */
	public void setDryRun(final boolean dryRun) {
		this.dryRun = dryRun;
	}

	/**
	 * Sets the verbose flag to indicate whether or not to output informative messages. 
	 *
	 * @param verbose set to <code>true</code> to output informative messages; false otherwise
	 */
	public void setVerbose(final boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sets the <code>boolean</code> flag indicating whether or not the <code>CryptoTool</code> should set to the source
	 * column values to <code>null</code>.
	 * 
	 * @param nullSrcColumn set to <code>true</code> to set the source column values to <code>null</code>; <code>false</code>
	 *            otherwise.
	 */
	public void setNullSrcColumnFlag(final boolean nullSrcColumn) {
		this.nullSrcColumnFlag = nullSrcColumn;
	}
}
