package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.job.TransactionJobBuilder;

/**
 * The Export Controller, is used to read data from the source system, save to the database.
 */
public class ExportController extends FullController {

	private static final Logger LOG = Logger.getLogger(ExportController.class);

	/**
	 * Logs a message into the log for the complete export.
	 */
	@Override
	public void synchronizationCompleted() {
		LOG.debug("Export completed");
	}

	/**
	 * Initializes the source system configuration.
	 *
	 * @param sourceSystem the source system configuration
	 * @param targetSystem the target system configuration
	 */
	@Override
	protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		// only the source system is required when doing export
		sourceSystem.initSystem();
	}

    /**
     *Gets a non caching transaction job builder.
     * @return a non caching transaction job builder.
     */
    @Override
    protected TransactionJobBuilder getTransactionJobFromBean() {
        return getSyncBeanFactory().getSourceBean("transactionJobBuilder");
    }






}
