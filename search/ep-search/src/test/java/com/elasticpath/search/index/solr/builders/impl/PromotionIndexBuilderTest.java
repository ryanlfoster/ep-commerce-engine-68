package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.LimitedUseCouponCodeConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.document.impl.RuleSolrInputDocumentCreator;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>PromotionIndexBuilder</code>.
 */
public class PromotionIndexBuilderTest {

	private RuleSolrInputDocumentCreator ruleDocumentCreator;
	
	private PromotionIndexBuilder promotionIndexBuilder;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private RuleService mockRuleService;

	private AnalyzerImpl analyzer;

	private ElasticPath mockElasticPath;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {

		this.promotionIndexBuilder = new PromotionIndexBuilder();
		this.analyzer = new AnalyzerImpl();

		this.mockRuleService = context.mock(RuleService.class);
		this.mockElasticPath = context.mock(ElasticPath.class);
		this.promotionIndexBuilder.setRuleService(mockRuleService);
		
		this.ruleDocumentCreator = new RuleSolrInputDocumentCreator();
		ruleDocumentCreator.setAnalyzer(analyzer);
		ruleDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());

	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.promotionIndexBuildServiceImpl.getName()'.
	 */
	@Test
	public void testGetName() {
		assertNotNull(this.promotionIndexBuilder.getName());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.index.impl.promotionIndexBuildServiceImpl.findDeletedUids(Date)'.
	 */
	@Test
	public void testFindDeletedUids() {
		assertEquals(0, this.promotionIndexBuilder.findDeletedUids(new Date()).size());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.index.impl.promotionIndexBuildServiceImpl.findAddedOrModifiedUids(Date)'.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockRuleService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.promotionIndexBuilder.findAddedOrModifiedUids(new Date()));
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.index.impl.promotionIndexBuildServiceImpl.findAllUids()'.
	 */
	@Test
	public void testFindAllUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockRuleService).findAllUids();
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.promotionIndexBuilder.findAllUids());
	}

	/**
	 * Test that the search index document is populated with the correct fields from a Rule.
	 */
	@Test
	public void testCreateDocument() {
		final com.elasticpath.domain.rules.Rule rule = createRule();

		final long ruleUid = 1L;
		rule.setUidPk(ruleUid);


		ruleDocumentCreator.setEntity(rule);
		final SolrInputDocument document = this.ruleDocumentCreator.createDocument();
		assertNotNull(document);
		assertEquals(String.valueOf(rule.getUidPk()), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(analyzer.analyze(rule.getName()), document.getFieldValue(SolrIndexConstants.PROMOTION_NAME));
		assertEquals("If the promotion state is non-null, it should be added to the index.", 
				analyzer.analyze(String.valueOf(rule.isEnabled())), document.getFieldValue(SolrIndexConstants.PROMOTION_STATE));
		assertEquals("If the ruleset is non-null, its uid should be added to the index.", 
				analyzer.analyze(rule.getRuleSet().getUidPk()), document.getFieldValue(SolrIndexConstants.PROMOTION_RULESET_UID));
		assertEquals("If the ruleset is non-null, its name should be added to the index.", 
				analyzer.analyze(rule.getRuleSet().getName()), document.getFieldValue(SolrIndexConstants.PROMOTION_RULESET_NAME));
		assertEquals("If the catalog is null, the store code should be added to the index.", 
				analyzer.analyze(rule.getStore().getCode()), document.getFieldValue(SolrIndexConstants.STORE_CODE));
	}
	
	/**
	 * Test that if the rule's Catalog is not null, createDocument adds the Catalog's UID and GUID to the index.
	 */
	@Test
	public void testCreateDocumentAddsCatalogUid() {
		final long catalogUid = 1000;
		final String catalogCode = "catalogCode";
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(mockCatalog).getUidPk();
				will(returnValue(catalogUid));
				allowing(mockCatalog).getCode();
				will(returnValue(catalogCode));
			}
		});
		final com.elasticpath.domain.rules.Rule rule = createRule();
		rule.setCatalog(mockCatalog);
		
		final long ruleUid = 1L;
		rule.setUidPk(ruleUid);

		ruleDocumentCreator.setEntity(rule);
		final SolrInputDocument document = this.ruleDocumentCreator.createDocument();
		assertNotNull(document);
		assertEquals("The rule's catalog's UID should be added to the index if the rule has an associated catalog.", 
				analyzer.analyze(rule.getCatalog().getUidPk()), document.getFieldValue(SolrIndexConstants.CATALOG_UID));
		assertEquals("The rule's catalog's code should be added to the index if the rule has an associated catalog.",
				analyzer.analyze(rule.getCatalog().getCode()), document.getFieldValue(SolrIndexConstants.CATALOG_CODE));
		assertNull("The store code should not be added to the index if the rule has a non-null catalog.",
				document.getFieldValue(SolrIndexConstants.STORE_CODE));
	}
	
	/**
	 * Test that if a rule with the given UID cannot be found, createDocument returns null.
	 */
	@Test
	public void testCreateDocumentWithNoRuleReturnsNull() {
		context.checking(new Expectations() {
			{
				allowing(mockRuleService).get(with(any(long.class)));
				will(returnValue(null));
			}
		});
		
		ruleDocumentCreator.setEntity(null);
		assertNull(this.ruleDocumentCreator.createDocument());
	}	

	private com.elasticpath.domain.rules.Rule createRule() {
		final RuleCondition couponCodeCondition = new LimitedUseCouponCodeConditionImpl();
		couponCodeCondition.addParameter(new RuleParameterImpl(RuleParameter.COUPON_CODE, null));

		context.checking(new Expectations() {
			{
				oneOf(mockElasticPath).getBean(ContextIdNames.LIMITED_USE_COUPON_CODE_COND);
				will(returnValue(couponCodeCondition));
			}
		});

		final com.elasticpath.domain.rules.Rule rule = new PromotionRuleImpl() {
			private static final long serialVersionUID = 8881197735731543251L;

			@Override
			public ElasticPath getElasticPath() {
				return mockElasticPath;
			}
		};

		rule.setName("Promo Name");
		rule.setCouponEnabled(true);
		rule.setEnabled(true);
		rule.setStartDate(new Date());
		rule.setEndDate(new Date());
		
		RuleSet ruleSet = new RuleSetImpl();
		ruleSet.setUidPk(1);
		ruleSet.setName("myRuleSet");
		rule.setRuleSet(ruleSet);
		
		Store store = new StoreImpl();
		store.setUidPk(1);
		store.setCode("some code");
		rule.setStore(store);

		return rule;
	}
}
