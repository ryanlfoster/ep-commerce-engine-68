/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration.audit;


import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.ThreadLocalMap;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * This class tests the auditing functionality while create/delete/update catalog.
 */
public class CatalogAuditTest extends AbstractAuditTestSupport {

	@Autowired
	private CatalogService catalogService;

	/**
	 * Test auditing for creating catalog.
	 */
	@DirtiesDatabase
	@Test
	public void testAuditingForCreateCatalog() {
		ThreadLocalMap<String, Object> metadata = getBeanFactory().getBean("persistenceListenerMetadataMap");
		metadata.put("changeSetGuid", "changeSetGuid1");
		metadata.put("userGuid", "userGuid1");

		Catalog masterCatalog = getSampleCatalog();

		Catalog createdCatalog = catalogService.saveOrUpdate(masterCatalog);
		
		int expectedChangeOperationNumber = 1;
		verifyAuditData(null, createdCatalog, createdCatalog.getGuid(), ChangeType.CREATE, expectedChangeOperationNumber);
	}

	/**
	 * Test auditing for deleting catalog. 
	 */
	@DirtiesDatabase
	@Test
	public void testAuditingForDeleteCatalog() {
		ThreadLocalMap<String, Object> metadata = getBeanFactory().getBean("persistenceListenerMetadataMap");
		metadata.put("changeSetGuid", "changeSetGuid1");
		metadata.put("userGuid", "userGuid1");

		Catalog masterCatalog = getSampleCatalog();

		Catalog createdCatalog = catalogService.saveOrUpdate(masterCatalog);

		catalogService.remove(createdCatalog);

		int expectedChangeOperationNumber = 1;
		verifyAuditData(createdCatalog, null, createdCatalog.getGuid(), ChangeType.DELETE, expectedChangeOperationNumber);
	}

	/**
	 * Test auditing for update catalog.
	 */
	@DirtiesDatabase
	@Test
	public void testAuditingForUpdateCatalog() {
		ThreadLocalMap<String, Object> metadata = getBeanFactory().getBean("persistenceListenerMetadataMap");
		metadata.put("changeSetGuid", "changeSetGuid1");
		metadata.put("userGuid", "userGuid1");

		Catalog masterCatalog = getSampleCatalog();

		Catalog createdCatalog = catalogService.saveOrUpdate(masterCatalog);

		CatalogImpl catalogBack = (CatalogImpl) catalogService.findByCode(createdCatalog.getCode());
		
		createdCatalog.setName("catalogName1Updated");

		Catalog updatedCatalog = catalogService.saveOrUpdate(createdCatalog);

		int expectedChangeOperationNumber = 1;
		verifyAuditData(catalogBack, updatedCatalog, updatedCatalog.getGuid(), ChangeType.UPDATE, expectedChangeOperationNumber);
	}

	private Catalog getSampleCatalog() {
		Set<String> supportedCurrencies = new HashSet<String>();
		supportedCurrencies.add("CAD");
		supportedCurrencies.add("CNY");

		return getCatalog("catalogCode1", "catalogName1", true, null, null);
	}

	private Catalog getCatalog(final String code, final String name, final boolean master, final String defaultLocale,
			final Collection<String> locales) {

		final Catalog catalog = new CatalogImpl();

		catalog.setMaster(master);
		catalog.setCode(code);
		if (name == null) {
			catalog.setName(code);
		} else {
			catalog.setName(name);
		}

		// Setup supported locales
		if (master && locales != null) {
			for (String locale : locales) {
				catalog.addSupportedLocale(new Locale(locale));
			}
		}

		if (defaultLocale == null) {
			catalog.setDefaultLocale(TestDataPersisterFactory.DEFAULT_LOCALE);
		} else {
			catalog.setDefaultLocale(new Locale(defaultLocale));
		}
		
		return catalog;
	}
}
