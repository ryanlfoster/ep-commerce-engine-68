package com.elasticpath.test.persister;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Persister allows to create and save into database Import dependent domain objects.
 */
public class ImportTestPersister {	
	private final ImportService importService;

	/**
	 * Constructor initializes necessary services and beanFactory.
	 * 
	 * @param beanFactory
	 *            the ElasticPath bean factory
	 */
	public ImportTestPersister(final BeanFactory beanFactory) {
		importService = beanFactory.getBean(ContextIdNames.IMPORT_SERVICE);
	}

	/**
	 * 
	 * @param importJob the instance of importJob
	 * @return the instance of ImportJob
	 */
	public ImportJob saveImportJob(final ImportJob importJob) {
		return importService.saveOrUpdateImportJob(importJob);
	}
}
