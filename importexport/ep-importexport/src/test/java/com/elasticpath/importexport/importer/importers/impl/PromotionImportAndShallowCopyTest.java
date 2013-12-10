package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.BrandConditionImpl;
import com.elasticpath.domain.rules.impl.ProductInCartConditionImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;

/**
 * The test binds together "clear strategy" used for import of rule conditions and shallow copying of rule in <code>RuleWrapperImpl</code>.
 */
public class PromotionImportAndShallowCopyTest {

	private final ConditionRuleImporterImpl ruleImporter = new ConditionRuleImporterImpl();
	private final ConditionRuleImporterImpl.ConditionRuleCollectionStrategy strategy = ruleImporter.new ConditionRuleCollectionStrategy(); 

	private final RuleWrapperImpl ruleWrapper = new RuleWrapperImpl() {
		private static final long serialVersionUID = -1640452464871346278L;

		@Override
		public ElasticPath getElasticPath() {
			return mockElasticPath;
		}
	};

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private Rule mockRule;
	private Rule mockOriginalRule;
	private Rule mockWrappedRule;
	private ElasticPath mockElasticPath;

	private Set<RuleCondition> conditions;
	private RuleCondition condition1;
	private RuleCondition condition2;

	/**
	 * Prepares conditions and eligibilities required for test as well as mock objects.
	 */
	@Before
	public void setUp() {
		mockRule = context.mock(Rule.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockOriginalRule = context.mock(Rule.class, "OriginalRule");
		mockWrappedRule = context.mock(Rule.class, "WrappedRule");

		conditions = new HashSet<RuleCondition>();
		condition1 = new BrandConditionImpl();
		condition2 = new ProductInCartConditionImpl();
		conditions.add(condition1);
		conditions.add(condition2);
	}

	/**
	 * Tests two aspects at one time: ConditionRuleImporterImpl.ConditionRuleCollectionStrategy should clear collection
	 * of conditions in rule because RuleWrapperImpl does shallow copy of Rule.
	 * If one of these changes then the other should probably change too!
	 * Also ensure that if any new fields are added they are covered in this test.
	 */
	@Test
	public void testTakeSnapshotAndPrepareCollectionStrategy() {
		final Set<RuleAction> actions = new HashSet<RuleAction>();
		final Catalog catalog = new CatalogImpl();
		final CmUser cmUser = new CmUserImpl();
		final String ruleCode = "PROMOTION_RULE_12345";
		final boolean conditionOperator = true;
		final long currentLoopNumber = 10;
		final String ruleDescription = "10% subtotal discount to everyone who has items of 3 different colors in shopping cart";
		final boolean eligibilityOperator = false;
		final boolean isEnabled = true;
		final Date endDate = new Date();
		final String ruleGuid = ruleCode;
		final Date lastModifiedDate = new Date();
		final String ruleName = "Color discount";
		final Set<RuleElement> ruleElements = new HashSet<RuleElement>();
		final RuleSet ruleSet = new RuleSetImpl();
		final Date startDate = new Date();
		final Store store = new StoreImpl();
		final long uidPk = 10050L;

		context.checking(new Expectations() { {
			oneOf(mockElasticPath).getBean(ContextIdNames.PROMOTION_RULE); will(returnValue(mockOriginalRule));
			oneOf(mockElasticPath).getBean(ContextIdNames.PROMOTION_RULE); will(returnValue(mockWrappedRule));
			oneOf(mockWrappedRule).getActions(); will(returnValue(actions));
			oneOf(mockWrappedRule).getCatalog(); will(returnValue(catalog));
			oneOf(mockOriginalRule).setCatalog(with(equal(catalog)));
			oneOf(mockWrappedRule).getCmUser(); will(returnValue(cmUser));
			oneOf(mockOriginalRule).setCmUser(with(equal(cmUser)));
			oneOf(mockWrappedRule).getCode(); will(returnValue(ruleCode));
			oneOf(mockOriginalRule).setCode(with(equal(ruleCode)));
			oneOf(mockWrappedRule).getConditions(); will(returnValue(conditions));
			oneOf(mockOriginalRule).addCondition(with(same(condition1)));
			oneOf(mockOriginalRule).addCondition(with(same(condition2)));
			oneOf(mockWrappedRule).getConditionOperator(); will(returnValue(conditionOperator));
			oneOf(mockOriginalRule).setConditionOperator(with(equal(conditionOperator)));
			oneOf(mockWrappedRule).getCurrentLupNumber(); will(returnValue(currentLoopNumber));
			oneOf(mockOriginalRule).setCurrentLupNumber(with(equal(currentLoopNumber)));
			oneOf(mockWrappedRule).getDescription(); will(returnValue(ruleDescription));
			oneOf(mockOriginalRule).setDescription(with(equal(ruleDescription)));
			oneOf(mockWrappedRule).getEligibilityOperator(); will(returnValue(eligibilityOperator));
			oneOf(mockOriginalRule).setEligibilityOperator(with(equal(eligibilityOperator)));
			oneOf(mockWrappedRule).isEnabled(); will(returnValue(isEnabled));
			oneOf(mockOriginalRule).setEnabled(with(equal(isEnabled)));
			oneOf(mockWrappedRule).getEndDate(); will(returnValue(endDate));
			oneOf(mockOriginalRule).setEndDate(with(equal(endDate)));
			oneOf(mockWrappedRule).getGuid(); will(returnValue(ruleGuid));
			oneOf(mockOriginalRule).setGuid(with(equal(ruleGuid)));
			oneOf(mockWrappedRule).getLastModifiedDate(); will(returnValue(lastModifiedDate));
			oneOf(mockOriginalRule).setLastModifiedDate(with(equal(lastModifiedDate)));
			oneOf(mockWrappedRule).getName(); will(returnValue(ruleName));
			oneOf(mockOriginalRule).setName(with(equal(ruleName)));
			oneOf(mockWrappedRule).isCouponEnabled(); will(returnValue(true));
			oneOf(mockOriginalRule).setCouponEnabled(with(true));
			oneOf(mockWrappedRule).getRuleElements(); will(returnValue(ruleElements));
			oneOf(mockOriginalRule).setRuleElements(with(equal(ruleElements)));
			oneOf(mockWrappedRule).getRuleSet(); will(returnValue(ruleSet));
			oneOf(mockOriginalRule).setRuleSet(with(equal(ruleSet)));
			oneOf(mockWrappedRule).getStartDate(); will(returnValue(startDate));
			oneOf(mockOriginalRule).setStartDate(with(equal(startDate)));
			oneOf(mockWrappedRule).getStore(); will(returnValue(store));
			oneOf(mockOriginalRule).setStore(with(equal(store)));
			oneOf(mockWrappedRule).getUidPk(); will(returnValue(uidPk));
			oneOf(mockOriginalRule).setUidPk(with(equal(uidPk)));

			// Expectations for prepareCollections - the important thing is that
			// the condition collection is cleared (otherwise a shallow copy of the
			// rule is insufficient).
			oneOf(mockRule).getConditions(); will(returnValue(conditions));
			oneOf(mockRule).removeCondition(with(same(condition1)));
			oneOf(mockRule).removeCondition(with(same(condition2)));
		} });

		ruleWrapper.takeSnapshot();
		strategy.prepareCollections(mockRule, null);
	}

	/** The import classes should at least contain the DTO class we are operating on. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", RuleDTO.class, ruleImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	@Test
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(ruleImporter.getAuxiliaryJaxbClasses());
	}
}
