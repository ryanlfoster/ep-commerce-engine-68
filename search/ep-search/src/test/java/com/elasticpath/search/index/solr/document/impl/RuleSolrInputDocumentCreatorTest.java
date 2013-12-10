package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.LimitedUseCouponCodeConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test construction of {@link SolrInputDocument}s from {@link Rule}s using {@link RuleSolrInputDocumentCreator}.
 */
public class RuleSolrInputDocumentCreatorTest {
	
	private RuleSolrInputDocumentCreator ruleSolrInputDocumentCreator;
	
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final Analyzer analyzer = new AnalyzerImpl();  
	
	/**
	 * Set up {@link CustomerSolrInputDocumentCreator}.
	 */
	@Before
	public void setUp() {
		ruleSolrInputDocumentCreator = new RuleSolrInputDocumentCreator();
		ruleSolrInputDocumentCreator.setAnalyzer(analyzer);
	}
	
	/**
	 * Test that the search index document is populated with the correct fields from a Rule.
	 */
	@Test
	public void testCreateDocument() {
		final Rule rule = createRule();

		ruleSolrInputDocumentCreator.setEntity(rule);
		final SolrInputDocument document = ruleSolrInputDocumentCreator.createDocument();
		assertNotNull(document);
		assertEquals(String.valueOf(rule.getUidPk()), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(analyzer.analyze(rule.getName()), document.getFieldValue(SolrIndexConstants.PROMOTION_NAME));
		assertEquals("If the ruleset is non-null, its uid should be added to the index.", 
				analyzer.analyze(rule.getRuleSet().getUidPk()), document.getFieldValue(SolrIndexConstants.PROMOTION_RULESET_UID));
		assertEquals("If the ruleset is non-null, its name should be added to the index.", 
				analyzer.analyze(rule.getRuleSet().getName()), document.getFieldValue(SolrIndexConstants.PROMOTION_RULESET_NAME));
		assertEquals("If the catalog is null, the store code should be added to the index.", 
				analyzer.analyze(rule.getStore().getCode()), document.getFieldValue(SolrIndexConstants.STORE_CODE));
		assertEquals("Promotion state not added to index", analyzer.analyze(String.valueOf(rule.isEnabled())),
				document.getFieldValue(SolrIndexConstants.PROMOTION_STATE));
	}
	
	/**
	 * Test that if the rule's Catalog is not null, createDocument adds the Catalog's UID and GUID to the index.
	 */
	@Test
	public void testCreateDocumentAddsCatalogUid() {
		final long catalogUid = 1000;
		final String catalogCode = "catalogCode";
		
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() { {
			allowing(mockCatalog).getUidPk(); will(returnValue(catalogUid));
			allowing(mockCatalog).getCode(); will(returnValue(catalogCode));

		} });
		final Rule rule = createRule();
		rule.setCatalog(mockCatalog);
		
		ruleSolrInputDocumentCreator.setEntity(rule);
		final SolrInputDocument document = ruleSolrInputDocumentCreator.createDocument();
		assertNotNull(document);
		assertEquals("The rule's catalog's UID should be added to the index if the rule has an associated catalog.", 
				analyzer.analyze(rule.getCatalog().getUidPk()), document.getFieldValue(SolrIndexConstants.CATALOG_UID));
		assertEquals("The rule's catalog's code should be added to the index if the rule has an associated catalog.",
				analyzer.analyze(rule.getCatalog().getCode()), document.getFieldValue(SolrIndexConstants.CATALOG_CODE));
		assertNull("The store code should not be added to the index if the rule has a non-null catalog.",
				document.getFieldValue(SolrIndexConstants.STORE_CODE));
		assertEquals("Promotion state not added to index", analyzer.analyze(String.valueOf(rule.isEnabled())),
				document.getFieldValue(SolrIndexConstants.PROMOTION_STATE));
	}
	
	/**
	 * Test that if a null {@link Rule} is submitted, createDocument returns null.
	 */
	@Test
	public void testCreateDocumentWithNoRuleReturnsNull() {
		assertNull(ruleSolrInputDocumentCreator.createDocument());
	}	
	
	private Rule createRule() {
		final RuleCondition couponCodeCondition = new LimitedUseCouponCodeConditionImpl();
		couponCodeCondition.addParameter(new RuleParameterImpl(RuleParameter.COUPON_CODE, null));

		final ElasticPath mockElasticPath = context.mock(ElasticPath.class);
		
		context.checking(new Expectations() { {
			allowing(mockElasticPath).getBean(with(ContextIdNames.LIMITED_USE_COUPON_CODE_COND)); will(returnValue(couponCodeCondition));
		} });
		

		final Rule rule = new PromotionRuleImpl() {
			private static final long serialVersionUID = 6946126655293880883L;

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
