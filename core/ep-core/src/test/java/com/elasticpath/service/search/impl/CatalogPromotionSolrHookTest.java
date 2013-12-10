package com.elasticpath.service.search.impl;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.service.search.CatalogPromoQueryComposerHelper;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.query.FilteredSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Test case for {@link CatalogPromotionSolrHook}.
 */
public class CatalogPromotionSolrHookTest {

	private static final String RULE2 = "Rule2";

	private CatalogPromotionSolrHook catalogPromotionSolrHook;

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private IndexNotificationService mockIndexNotificationService;

	private CatalogPromoQueryComposerHelper mockCatalogPromoQueryComposerHelper;

	private FilteredSearchCriteria<SearchCriteria> filteredSearchCriteria;

	private FilteredSearchCriteria<SearchCriteria> filteredSearchCriteria2;

	private Rule mockRule;
	
	private RuleSet mockRuleSet;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		mockIndexNotificationService = context.mock(IndexNotificationService.class);
		mockCatalogPromoQueryComposerHelper = context.mock(CatalogPromoQueryComposerHelper.class);

		filteredSearchCriteria = new FilteredSearchCriteria<SearchCriteria>();
		filteredSearchCriteria.addCriteria(new ProductSearchCriteria());

		filteredSearchCriteria2 = new FilteredSearchCriteria<SearchCriteria>();
		filteredSearchCriteria2.addCriteria(new ProductSearchCriteria());
		
		mockRuleSet = context.mock(RuleSet.class);
		context.checking(new Expectations() {
			{
				allowing(mockRuleSet).getScenario();
				will(returnValue(RuleScenarios.CATALOG_BROWSE_SCENARIO));
			}
		});

		mockRule = context.mock(Rule.class, "Rule1");
		context.checking(new Expectations() {
			{
				allowing(mockRule).getRuleSet();
				will(returnValue(mockRuleSet));
			}
		});

		catalogPromotionSolrHook = new CatalogPromotionSolrHook();
		catalogPromotionSolrHook.setIndexNotificationService(mockIndexNotificationService);
		catalogPromotionSolrHook
				.setCatalogPromoQueryComposerHelper(mockCatalogPromoQueryComposerHelper);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postAdd(com.elasticpath.domain.Persistence)} where the
	 * promotion rule is not enabled.
	 */
	@Test
	public void testAddDisabledRule() {
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(false));
			}
		});

		// nothing should be invoked
		catalogPromotionSolrHook.postAdd(mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postAdd(com.elasticpath.domain.Persistence)} where the
	 * promotion rule is expected to be active in the future.
	 */
	@Test
	public void testAddFutureRule() {
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MAX_VALUE)));
			}
		});

		// nothing should be invoked
		catalogPromotionSolrHook.postAdd(mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postAdd(com.elasticpath.domain.Persistence)} where the
	 * promotion rule is enabled and within date range.
	 */
	@Test
	public void testAddActiveRule() {
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule).getEndDate();
				will(returnValue(new Date(Long.MAX_VALUE)));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postAdd(mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postDelete(com.elasticpath.domain.Persistence)}.
	 */
	@Test
	public void testRemoveRule() {
		context.checking(new Expectations() {
			{
				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postDelete(mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the both promotion rules are {@link com.elasticpath.domain.rules.RuleState#EXPIRED}.
	 */
	@Test
	public void testUpdateExpiredExpiredRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule).getEndDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule2).isEnabled();
				will(returnValue(true));

				oneOf(mockRule2).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule2).getEndDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));
			}
		});
		

		// nothing should be invoked
		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where both promotion rules are {@link com.elasticpath.domain.rules.RuleState#DISABLED}.
	 */
	@Test
	public void testUpdateDisabledDisabledRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(false));

				oneOf(mockRule2).isEnabled();
				will(returnValue(false));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));
			}
		});

		// nothing should be invoked
		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the old promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE} and
	 * the new promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE}.
	 */
	@Test
	public void testUpdateActiveActiveRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule).getEndDate();
				will(returnValue(null));

				oneOf(mockRule2).isEnabled();
				will(returnValue(true));

				oneOf(mockRule2).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule2).getEndDate();
				will(returnValue(new Date(Long.MAX_VALUE)));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));


				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule2));
				will(returnValue(filteredSearchCriteria2));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria2), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the old promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE} and
	 * the new promotion rule is {@link com.elasticpath.domain.rules.RuleState#EXPIRED}.
	 */
	@Test
	public void testUpdateActiveExpiredRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule).getEndDate();
				will(returnValue(null));

				oneOf(mockRule2).isEnabled();
				will(returnValue(true));

				oneOf(mockRule2).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule2).getEndDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the old promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE} and
	 * the new promotion rule is {@link com.elasticpath.domain.rules.RuleState#DISABLED}.
	 */
	@Test
	public void testUpdateActiveDisabledRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule).getEndDate();
				will(returnValue(null));

				oneOf(mockRule2).isEnabled();
				will(returnValue(false));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the old promotion rule is {@link com.elasticpath.domain.rules.RuleState#DISABLED} and
	 * the new promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE}.
	 */
	@Test
	public void testUpdateDisabledActiveRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(false));

				oneOf(mockRule2).isEnabled();
				will(returnValue(true));

				oneOf(mockRule2).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule2).getEndDate();
				will(returnValue(null));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule2));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});


		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the old promotion rule is {@link com.elasticpath.domain.rules.RuleState#EXPIRED} and
	 * the new promotion rule is {@link com.elasticpath.domain.rules.RuleState#ACTIVE}.
	 */
	@Test
	public void testUpdateExpiredActiveRule() {
		final Rule mockRule2 = context.mock(Rule.class, RULE2);
		context.checking(new Expectations() {
			{
				oneOf(mockRule).isEnabled();
				will(returnValue(true));

				oneOf(mockRule).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				exactly(2).of(mockRule).getEndDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule2).isEnabled();
				will(returnValue(true));

				oneOf(mockRule2).getStartDate();
				will(returnValue(new Date(Long.MIN_VALUE)));

				oneOf(mockRule2).getEndDate();
				will(returnValue(null));

				allowing(mockRule2).getRuleSet();
				will(returnValue(mockRuleSet));

				oneOf(mockCatalogPromoQueryComposerHelper).constructSearchCriteria(Arrays.asList(mockRule2));
				will(returnValue(filteredSearchCriteria));

				oneOf(mockIndexNotificationService).addViaQuery(with(UpdateType.UPDATE), with(filteredSearchCriteria), with(any(boolean.class)));
			}
		});

		catalogPromotionSolrHook.postUpdate(mockRule2, mockRule);
	}

	/**
	 * Test method for
	 * {@link CatalogPromotionSolrHook#postUpdate(com.elasticpath.domain.Persistence, com.elasticpath.domain.Persistence)}
	 * where the service passes in {@code null}s. By the interface, only the first argument could
	 * possibly be {@code null}.
	 */
	@Test
	public void testUpdateWithNulls() {
		try {
			catalogPromotionSolrHook.postUpdate(null, mockRule);
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException e) { // NOPMD -- AvoidEmptyCatchBlocks
			// success
		}

		try {
			catalogPromotionSolrHook.postUpdate(null, null);
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException e) { // NOPMD -- AvoidEmptyCatchBlocks
			// success
		}
	}
}
