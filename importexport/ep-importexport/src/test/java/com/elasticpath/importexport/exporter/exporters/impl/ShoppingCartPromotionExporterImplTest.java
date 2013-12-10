package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.rules.RuleService;

/**
 * Tests <code>ShoppingCartPromotionExporterImpl</code>.
 */
public class ShoppingCartPromotionExporterImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShoppingCartPromotionExporterImpl exporter;

	private final ImportExportSearcher importExportSearcher = context.mock(ImportExportSearcher.class);
	private RuleService ruleService;

	private final SearchConfiguration searchConfiguration = new SearchConfiguration();

	private ExportContext exportContext;

	/**
	 * Prepares EPQL search query and export context.
	 */
	@Before
	public void setUp() {
		ruleService =  context.mock(RuleService.class);
		searchConfiguration.setEpQLQuery("FIND Promotion WHERE RuleName='Christmas Discount'");

		final ExportConfiguration exportConfiguration = new ExportConfiguration();
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);

		exporter = new ShoppingCartPromotionExporterImpl();
		exporter.setImportExportSearcher(importExportSearcher);
		exporter.setRuleService(ruleService);
	}
	/**
	 * Clean up after tests.
	 */
	@After
	public void tearDown() {
		ruleService = null;
	}
	/**
	 * Tests that exporter prepares the list of promotion UIDs during initialization.
	 * 
	 * @throws ConfigurationException in case exporter is not configured properly
	 */
	@Test
	public void testInitialize() throws ConfigurationException {
		final List<Long> ruleUidPkList = Arrays.asList(1234L, 4321L);
		context.checking(new Expectations() { {
			oneOf(importExportSearcher).searchUids(searchConfiguration, EPQueryType.PROMOTION);
			will(returnValue(ruleUidPkList));
			oneOf(ruleService).retrievePromotionDependencies(new LinkedHashSet<Long>(ruleUidPkList));
				will(returnValue(new LinkedHashSet<Long>(ruleUidPkList)));
		} });

		exporter.initialize(exportContext);
		assertEquals(ruleUidPkList, exporter.getListExportableIDs());
	}
}
