package com.elasticpath.importexport.common.adapters.promotion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.AnySkuInCartConditionImpl;
import com.elasticpath.domain.rules.impl.BrandConditionImpl;
import com.elasticpath.domain.rules.impl.CartContainsItemsOfCategoryConditionImpl;
import com.elasticpath.domain.rules.impl.CartCurrencyConditionImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.promotion.rule.ActionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.AndDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.BooleanComponentDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.OrDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Tests population of <code>RuleDTO</code> from <code>Rule</code> and back to front.
 */
public class RuleAdapterTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final RuleAdapter ruleAdapter = new RuleAdapter();

	private final ElasticPath elasticpath = context.mock(ElasticPath.class);

	private final CachingService cachingService = context.mock(CachingService.class);

	@SuppressWarnings("unchecked")
	private final DomainAdapter<RuleElement, ConditionDTO> conditionAdapter = context.mock(DomainAdapter.class);

	private int numberOfCalls;

	private int numberOfRetrievals;

	/**
	 * Prepares <code>RuleAdapter</code>.
	 * 
	 * @throws Exception in case it fails to initialize rule adapter
	 */
	@Before
	public void setUp() throws Exception {
		ruleAdapter.setElasticPath(elasticpath);
		ruleAdapter.setCachingService(cachingService);
		ruleAdapter.setConditionAdapter(conditionAdapter);
	}

	/**
	 * Tests population of Action DTO objects from <code>Rule</code> domain object.
	 */
	@Test
	public void testCreateActionDTOList() {
		final Rule rule = context.mock(Rule.class);
		final RuleSet ruleSet = context.mock(RuleSet.class);
		final String promotionCode = "1234-4321";

		context.checking(new Expectations() { {
			oneOf(rule).getCode(); will(returnValue(promotionCode));
			oneOf(rule).getRuleSet(); will(returnValue(ruleSet));
			oneOf(ruleSet).getScenario(); will(returnValue(RuleScenarios.CART_SCENARIO));
		} });

		List<ActionDTO> actionDTOList = ruleAdapter.createActionDTOList(rule);

		assertEquals(1, actionDTOList.size());
		assertEquals(promotionCode, actionDTOList.get(0).getCode());
		assertEquals(RuleAdapter.SHOPPING_CART_PROMOTION, actionDTOList.get(0).getType());
	}

	/**
	 * Checks that getBooleanClauseDto returns AndDTO for conjunction (true) and OrDTO for disjunction (false).
	 */
	@Test
	public void testGetBooleanClauseDto() {
		assertTrue(ruleAdapter.getBooleanClauseDTO(true) instanceof AndDTO);
		assertTrue(ruleAdapter.getBooleanClauseDTO(false) instanceof OrDTO);
	}

	/**
	 * Checks that pinchCondition method removes one condition from the list and asks condition adapter to populate condition DTO.
	 */
	@Test
	public void testRetrieveCondition() {
		final RuleElement firstCondition = new AnySkuInCartConditionImpl();
		final RuleElement secondCondition = new BrandConditionImpl();
		final List<RuleElement> ruleElements = new ArrayList<RuleElement>();
		ruleElements.add(firstCondition);
		ruleElements.add(secondCondition);

		context.checking(new Expectations() { {
			oneOf(conditionAdapter).populateDTO(with(same(firstCondition)), with(any(ConditionDTO.class)));
		} });

		ruleAdapter.retrieveCondition(ruleElements);
		assertEquals(1, ruleElements.size());
	}

	/**
	 * Tests guard restricting empty lists with conditions and eligibilities.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testCreateConditionComposition01() {
		ruleAdapter.createConditionComposition(Collections.<RuleElement>emptyList(), true);
	}

	/**
	 * Tests number of recursive calls and retrievals of <code>RuleElement</code> objects from the list.
	 * Only one call occurs if list size is equal to 1 or 2. It is recursion finish criterion.
	 * All elements should be retrieved from the list (check number of retrievals for that purpose).
 	 */
	@Test
	public void testCreateConditionComposition02() {

		final RuleAdapter ruleAdapter = new RuleAdapter() {

			BooleanComponentDTO retrieveCondition(final List< ? extends RuleElement> ruleElements) {
				++numberOfRetrievals;
				ruleElements.remove(0);
				return null;
			}

			BooleanComponentDTO createConditionComposition(final List< ? extends RuleElement> ruleElements, final boolean makeConjunction) {
				++numberOfCalls;
				return super.createConditionComposition(ruleElements, makeConjunction);
			}
		};

		RuleElement firstCondition = new AnySkuInCartConditionImpl();
		RuleElement secondCondition = new BrandConditionImpl();
		RuleElement thirdCondition = new CartContainsItemsOfCategoryConditionImpl();
		RuleElement fourthCondition = new CartCurrencyConditionImpl();
		final List<RuleElement> ruleElements = new ArrayList<RuleElement>();

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		int expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, true);
		assertEquals(1, numberOfCalls);
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false); // logical operator doesn't matter
		assertEquals(1, numberOfCalls);
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition); ruleElements.add(secondCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, true);
		assertEquals(1, numberOfCalls);
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition); ruleElements.add(secondCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false); // logical operator doesn't matter
		assertEquals(1, numberOfCalls);
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition); ruleElements.add(secondCondition); ruleElements.add(thirdCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false);
		assertEquals(2, numberOfCalls);		// one recursive call of the method from itself
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);

		numberOfCalls = 0;
		numberOfRetrievals = 0;
		ruleElements.clear();
		ruleElements.add(firstCondition); ruleElements.add(secondCondition);
		ruleElements.add(thirdCondition); ruleElements.add(fourthCondition);
		expectedNumberOfRetrievals = ruleElements.size();
		ruleAdapter.createConditionComposition(ruleElements, false);
		final int expectedNumberOfCalls = 3; 		// two recursive calls
		assertEquals(expectedNumberOfCalls, numberOfCalls);
		assertEquals(expectedNumberOfRetrievals, numberOfRetrievals);
	}

	/**
	 * Tests that adapter gets conditions, eligibilities and its operators from <code>Rule</code>.
	 */
	@Test
	public void testCreateConditionsDTO() {

		final RuleAdapter ruleAdapter = new RuleAdapter() {
			BooleanComponentDTO createConditionComposition(final List< ? extends RuleElement> ruleElements, final boolean makeConjunction) {
				return null;
			}
		};

		final Rule rule = context.mock(Rule.class);
		final Set<RuleCondition> conditions = new HashSet<RuleCondition>();
		conditions.add(new CartContainsItemsOfCategoryConditionImpl());

		context.checking(new Expectations() { {
			oneOf(rule).getConditions(); will(returnValue(conditions));
			oneOf(rule).getConditionOperator(); will(returnValue(true));
		} });

		ruleAdapter.createConditionsDTO(rule);
	}

	/**
	 * Checks that <code>BooleanComponentDTO</code> is used to populate domain object.
	 * This behavior is predefined by Composite design pattern 
	 */
	@Test
	public void testPopulateRuleElements() {
		final BooleanComponentDTO source = context.mock(BooleanComponentDTO.class);
		final Rule target = context.mock(Rule.class);
		final boolean conditionOperator = true;

		context.checking(new Expectations() { {
			oneOf(source).getCompositeOperator(); will(returnValue(conditionOperator));
			oneOf(target).setConditionOperator(conditionOperator);
			oneOf(source).populateDomainObject(target, conditionAdapter);
		} });

		ruleAdapter.populateRuleElements(source, target);
	}

	/**
	 * Verifies that type of DTO created by <code>RuleAdapter</code> is <code>RuleDTO</code>.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(RuleDTO.class, ruleAdapter.createDtoObject().getClass());
	}
}
