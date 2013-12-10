/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.util.cryptotool;

import java.util.List;

/**
 * Update a column (a single attribute) by applying a filter to every record. We turned this functionality into its own class
 * because we needed to swap different update strategies easily during performance testing to optimize the tool for large tables
 * (10^6 - 10^7 records).
 */
interface ColumnUpdater {

	/**
	 * Run the update on the database.
	 */
	void doUpdate();

	/**
	 * Sets the database JDBC driver used by the CryptoTool.
	 * 
	 * @param jdbcDriver the database JDBC driver used by the CryptoTool
	 */
	void setJdbcDriver(final String jdbcDriver);

	/**
	 * Sets the database URL used by the CryptoTool.
	 * 
	 * @param dbUrl the database URL used by the CryptoTool
	 */
	void setDbUrl(final String dbUrl);

	/**
	 * Sets the database username used by the CryptoTool.
	 * 
	 * @param dbUser the database username used by the CryptoTool
	 */
	void setDbUser(final String dbUser);

	/**
	 * Sets the database password used by the CryptoTool.
	 * 
	 * @param dbPass the database password used by the CryptoTool
	 */
	void setDbPass(final String dbPass);

	/**
	 * Sets the table name.
	 * 
	 * @param tableName the table name to set
	 */
	void setTableName(final String tableName);

	/**
	 * Sets the source column name.
	 * 
	 * @param srcColumnName the source columnName to set
	 */
	void setSrcColumnName(final String srcColumnName);

	/**
	 * Sets the destination column name.
	 * 
	 * @param destColumnName the destination columnName to set
	 */
	void setDestColumnName(final String destColumnName);

	/**
	 * Sets the {@link List} of store codes.
	 * 
	 * @param stores the {@link List} of store codes
	 */
	void setStores(final List<String> stores);

	/**
	 * Sets the fetchSize value to use when fetching/commiting to the database.
	 * 
	 * @param fetchSize the fetchSize value to use when fetching/commiting to the database
	 */
	void setFetchSize(final int fetchSize);

	/**
	 * Sets the dryRun flag to indicate whether or not the CryptoTool should update the database.
	 * 
	 * @param dryRun set to <code>true</code> to perform a dry run and not write to the database; false otherwise
	 */
	void setDryRun(final boolean dryRun);

	/**
	 * Sets the verbose flag to indicate whether or not to output informative messages.
	 * 
	 * @param verbose set to <code>true</code> to output informative messages; false otherwise
	 */
	void setVerbose(final boolean verbose);

	/**
	 * Sets the <code>boolean</code> flag indicating whether or not the <code>CryptoTool</code> should set to the source
	 * column values to <code>null</code>.
	 * 
	 * @param nullSrcColumn set to <code>true</code> to set the source column values to <code>null</code>; <code>false</code>
	 *            otherwise.
	 */
	void setNullSrcColumnFlag(final boolean nullSrcColumn);
}