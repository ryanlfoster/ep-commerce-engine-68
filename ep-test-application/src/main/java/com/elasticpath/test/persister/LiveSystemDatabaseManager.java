package com.elasticpath.test.persister;


public class LiveSystemDatabaseManager extends MultiDatabaseManager {

	private static final MultiDatabaseManager INSTANCE = new LiveSystemDatabaseManager();

	/**
	 * Return a {@code LiveSystemDatabaseManager}.
	 */
	public static MultiDatabaseManager getInstance() {
		return INSTANCE;
	}

	/**
	 * We override creating a {@code DBConfig} to indicate that even freshly created
	 * databases do <b>not</b> need to be initialized since this is a live system.
	 */
	@Override
	DBConfig createDBConfig() {
		DBConfig cfg = super.createDBConfig();

		cfg.setShouldReinitialize(false);
		return cfg;
	}

}
