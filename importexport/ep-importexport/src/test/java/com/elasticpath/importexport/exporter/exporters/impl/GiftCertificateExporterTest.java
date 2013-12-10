package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * Tests for {@link GiftCertificateExporter}.
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class GiftCertificateExporterTest {
	private GiftCertificateExporter giftCertificateExporter;
	private ImportExportSearcher importExportSearcher;
	private GiftCertificateService giftCertificateService;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();

	/** Test initialization. */
	@Before
	public void setUp() {
		giftCertificateExporter = new GiftCertificateExporter();

		importExportSearcher = context.mock(ImportExportSearcher.class);
		giftCertificateService = context.mock(GiftCertificateService.class);

		giftCertificateExporter.setImportExportSearcher(importExportSearcher);
		giftCertificateExporter.setGiftCertificateService(giftCertificateService);
	}

	/**
	 * All guids found during initialization should be exportable.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testInitialize() throws ConfigurationException {
		final SearchConfiguration searchConfiguration = new SearchConfiguration();
		exportContext = new ExportContext(new ExportConfiguration(), searchConfiguration);

		final List<String> foundGuids = Arrays.asList("GUID1", "GUID3", "GUID2");
		context.checking(new Expectations() {
			{
				allowing(importExportSearcher).searchGuids(searchConfiguration, EPQueryType.GIFT_CERTIFICATE);
				will(returnValue(foundGuids));
			}
		});

		giftCertificateExporter.initialize(exportContext);
		assertEquals(foundGuids, giftCertificateExporter.getListExportableIDs());
	}

	/**
	 * Searching for a guids should return all guids regardless whether they were found during initializatino.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Test
	public void testFindById() throws ConfigurationException {
		testInitialize();

		final GiftCertificate giftCertificate1 = context.mock(GiftCertificate.class, "giftCertificate-1");
		final GiftCertificate giftCertificate2 = context.mock(GiftCertificate.class, "giftCertificate-2");
		context.checking(new Expectations() {
			{
				allowing(giftCertificateService).findByGuid("GUID1");
				will(returnValue(giftCertificate1));
				allowing(giftCertificateService).findByGuid("GUID-DNE");
				will(returnValue(giftCertificate2));
			}
		});

		List<GiftCertificate> results = giftCertificateExporter.findByIDs(Arrays.asList("GUID1", "GUID-DNE"));
		@SuppressWarnings("unchecked")
		Matcher<Iterable<GiftCertificate>> allReturnedGiftCertificates = allOf(hasItem(giftCertificate1), hasItem(giftCertificate2));
		assertThat("Missing returned certificates", results, allReturnedGiftCertificates);
		assertEquals("Extra gift certificates returned", 2, results.size());
	}

	/** Ensures the proper {@link JobType} is returned. */
	@Test
	public void testJobType() {
		assertEquals(JobType.GIFT_CERTIFICATE, giftCertificateExporter.getJobType());
	}
}
