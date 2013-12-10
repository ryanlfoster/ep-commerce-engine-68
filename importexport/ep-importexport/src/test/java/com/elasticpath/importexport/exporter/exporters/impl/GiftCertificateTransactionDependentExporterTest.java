package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.payment.GiftCertificateTransactionService;

/**
 * Test for {@link GiftCertificateTransactionDependentExporter}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class GiftCertificateTransactionDependentExporterTest {
	private final GiftCertificateTransactionDependentExporter giftCertificateExporter = new GiftCertificateTransactionDependentExporter();
	private GiftCertificateService giftCertificateService;
	private GiftCertificateTransactionService giftCertificateTransactionService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		giftCertificateService = context.mock(GiftCertificateService.class);
		giftCertificateExporter.setGiftCertificateService(giftCertificateService);

		giftCertificateTransactionService = context.mock(GiftCertificateTransactionService.class);
		giftCertificateExporter.setGiftCertificateTransactionService(giftCertificateTransactionService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		giftCertificateExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when there are no transactions for a {@link GiftCertificate}. */
	@Test
	public void testFindDependentObjectsEmpty() {
		final GiftCertificate giftCertificate = context.mock(GiftCertificate.class);
		context.checking(new Expectations() {
			{
				one(giftCertificateService).get(CATALOG_UID);
				will(returnValue(giftCertificate));

				one(giftCertificateTransactionService).getGiftCertificateTransactions(giftCertificate);
				will(returnValue(Collections.emptyList()));
			}
		});

		List<GiftCertificateTransaction> certificates = giftCertificateExporter.findDependentObjects(CATALOG_UID);
		assertNotNull(certificates);
		assertTrue(certificates.isEmpty());
	}

	/** Tests finding dependent objects when there are transactions for a {@link GiftCertificate}. */
	@Test
	public void testFindDependentObjects() {
		final GiftCertificate giftCertificate = context.mock(GiftCertificate.class);
		final GiftCertificateTransaction transaction1 = context.mock(GiftCertificateTransaction.class, "transaction-1");
		final GiftCertificateTransaction transaction2 = context.mock(GiftCertificateTransaction.class, "transaction-2");
		context.checking(new Expectations() {
			{
				one(giftCertificateService).get(CATALOG_UID);
				will(returnValue(giftCertificate));

				one(giftCertificateTransactionService).getGiftCertificateTransactions(giftCertificate);
				will(returnValue(Arrays.asList(transaction1, transaction2)));
			}
		});

		List<GiftCertificateTransaction> certificates = giftCertificateExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing transaction1", certificates, hasItem(transaction1));
		assertThat("Missing transaction2", certificates, hasItem(transaction2));
		assertEquals("Other transactions in result?", 2, certificates.size());
	}
}
