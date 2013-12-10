package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.domain.shipping.impl.ShippingServiceLevelImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.document.impl.ShippingServiceLevelSolrInputDocumentCreator;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Tests that <code>ShippingServiceLevelIndexBuilder</code> creates proper index documents for shipping service levels.
 */
public class ShippingServiceLevelIndexBuilderTest {

	private final ShippingServiceLevelIndexBuilder shippingServiceLevelIndexBuilder = new ShippingServiceLevelIndexBuilder();

	private ShippingServiceLevelSolrInputDocumentCreator sslDocumentCreator;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShippingServiceLevelService mockShippingServiceLevelService;

	private Store mockStore;

	private Catalog mockCatalog;

	private AnalyzerImpl analyzer;

	/**
	 * Prepares mock objects.
	 */
	@Before
	public void setUp() {
		mockStore = context.mock(Store.class);
		mockCatalog = context.mock(Catalog.class);
		mockShippingServiceLevelService = context.mock(ShippingServiceLevelService.class);
		shippingServiceLevelIndexBuilder.setShippingServiceLevelService(mockShippingServiceLevelService);

		this.analyzer = new AnalyzerImpl();

		sslDocumentCreator = new ShippingServiceLevelSolrInputDocumentCreator();
		sslDocumentCreator.setAnalyzer(analyzer);

		sslDocumentCreator.setIndexUtility(new IndexUtilityImpl());
		sslDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());
	}

	private ShippingServiceLevel createShippingServiceLevel() {
		ShippingServiceLevelImpl shippingServiceLevel = new ShippingServiceLevelImpl();
		final LocalizedProperties localizedProperties = context.mock(LocalizedProperties.class);

		// CHECKSTYLE:OFF
		shippingServiceLevel.setUidPk(10050);
		shippingServiceLevel.setCode("ROYAL_POST");
		shippingServiceLevel.setEnabled(true);
		shippingServiceLevel.setCarrier("Royal Post");
		shippingServiceLevel.setShippingRegion(new ShippingRegionImpl());
		final Locale defaultLocale = Locale.CANADA;
		final String storeName = "Snap It All";
		// CHECKSTYLE:ON

		context.checking(new Expectations() {
			{

				allowing(mockStore).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockStore).getName();
				will(returnValue(storeName));

				allowing(mockCatalog).getDefaultLocale();
				will(returnValue(defaultLocale));
				allowing(localizedProperties).getLocalizedPropertiesMap();
				will(returnValue(new HashMap<String, LocalizedPropertyValue>()));
				allowing(localizedProperties).getValue(ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, defaultLocale);
				will(returnValue("Royal Post, eh"));
			}
		});
		shippingServiceLevel.setStore(mockStore);

		Map<String, LocalizedPropertyValue> map = new HashMap<String, LocalizedPropertyValue>();
		shippingServiceLevel.setLocalizedPropertiesMap(map);
		shippingServiceLevel.setLocalizedProperties(localizedProperties);

		return shippingServiceLevel;
	}

	/**
	 * Checks supported <code>IndexType</code>.
	 */
	@Test
	public void testGetIndexType() {
		assertEquals(IndexType.SHIPPING_SERVICE_LEVEL, shippingServiceLevelIndexBuilder.getIndexType());
	}

	/**
	 * Should always throw an exception.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testFindUidsByNotification() {
		shippingServiceLevelIndexBuilder.findUidsByNotification(null);
	}

	/**
	 * If <code>ShippingServiceLevel</code> cannot be found then <code>createDocument</code> returns null.
	 */
	@Test
	public void testCreateDocument() {

		sslDocumentCreator.setEntity(null);

		assertNull(sslDocumentCreator.createDocument());
	}

	/**
	 * <code>ShippingServiceLevel</code> was found and document can be created.
	 */
	@Test
	public void testCreateDocumentForReal() {
		sslDocumentCreator.setEntity(createShippingServiceLevel());
		assertEquals(SolrInputDocument.class, sslDocumentCreator.createDocument().getClass());
	}

	/**
	 * Checks a name of index.
	 */
	@Test
	public void testGetName() {
		assertEquals(SolrIndexConstants.SHIPPING_SERVICE_LEVEL_CORE, shippingServiceLevelIndexBuilder.getName());
	}

	/**
	 * Verifies that builder knows about UIDs of deleted shipping service levels.
	 */
	@Test
	public void testFindDeletedUids() {
		final Date lastBuildDate = new Date();
		context.checking(new Expectations() {
			{
				oneOf(mockShippingServiceLevelService).findUidsByDeletedDate(lastBuildDate);
				will(returnValue(Collections.emptyList()));
			}
		});
		assertEquals(Collections.emptyList(), shippingServiceLevelIndexBuilder.findDeletedUids(lastBuildDate));
	}

	/**
	 * Verifies that builder knows about UIDs of added or updated shipping service levels.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final Date lastBuildDate = new Date();
		context.checking(new Expectations() {
			{
				oneOf(mockShippingServiceLevelService).findUidsByModifiedDate(lastBuildDate);
				will(returnValue(Collections.emptyList()));
			}
		});
		assertEquals(Collections.emptyList(), shippingServiceLevelIndexBuilder.findAddedOrModifiedUids(lastBuildDate));
	}

	/**
	 * Checks that builder uses appropriate service to find all shipping service level UIDs.
	 */
	@Test
	public void testFindAllUids() {
		context.checking(new Expectations() {
			{
				oneOf(mockShippingServiceLevelService).findAllUids();
				will(returnValue(Collections.emptyList()));
			}
		});
		assertEquals(Collections.emptyList(), shippingServiceLevelIndexBuilder.findAllUids());
	}
}
