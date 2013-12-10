package com.elasticpath.importexport.common.adapters.promotion;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.dto.promotion.rule.ConditionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * Tests population of <code>ConditionDTO</code> from <code>RuleElement</code> and back to front.
 */
public class ConditionAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final RuleElement ruleElement = context.mock(RuleElement.class);

	private final ConditionAdapter conditionAdapter = new ConditionAdapter() {
		/**
		 * Stub method covered in other tests.
		 */
		List<ParameterDTO> createElementParameterDTOList(final Set<RuleParameter> ruleParameterSet) {
			return null;
		}

		/**
		 * Stub method covered in other tests.
		 */
		List<ExceptionDTO> createElementExceptionDTOList(final Set<RuleException> ruleExceptionSet) {
			return null;
		}

		/**
		 * Stub method covered in other tests.
		 */
		Set<RuleParameter> createRuleParameterSet(final List<ParameterDTO> elementParameterDTOList) {
			return null;
		}

		/**
		 * Stub method covered in other tests.
		 */
		Set<RuleException> createRuleExceptionSet(final List<ExceptionDTO> elementExceptionDTOList) {
			return null;
		}
	};

	/**
	 * Tests that all required getters are called during DTO population.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() { {
			oneOf(ruleElement).getKind(); will(returnValue(RuleCondition.CONDITION_KIND));
			oneOf(ruleElement).getType(); will(returnValue(RuleElementType.BRAND_CONDITION.getPropertyKey()));
			oneOf(ruleElement).getParameters();
			oneOf(ruleElement).getExceptions();
		} });

		final ConditionDTO conditionDto = new ConditionDTO();
		conditionAdapter.populateDTO(ruleElement, conditionDto);

		assertEquals(RuleCondition.CONDITION_KIND, conditionDto.getKind());
		assertEquals(RuleElementType.BRAND_CONDITION.getPropertyKey(), conditionDto.getType());
	}

	/**
	 * Exception should be thrown if kind of source DTO object is equal to null.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainException1() {
		ConditionDTO conditionDto = new ConditionDTO();
		conditionDto.setKind(null);

		conditionAdapter.populateDomain(conditionDto, ruleElement);
	}

	/**
	 * Exception should be thrown if kind of target <code>RuleElement</code> doesn't match with kind of DTO object.
	 */
	@Test(expected = PopulationRuntimeException.class)
	public void testPopulateDomainException2() {
		ConditionDTO conditionDto = new ConditionDTO();

		final RuleElement conditionElement = context.mock(RuleCondition.class);

		conditionAdapter.populateDomain(conditionDto, conditionElement);
	}

	/**
	 * Tests population of <code>RuleElement</code> domain object.
	 */
	@Test
	public void testPopulateDomain() {
		ConditionDTO conditionDto = new ConditionDTO();
		conditionDto.setKind(RuleCondition.CONDITION_KIND);
		conditionDto.setType(RuleElementType.BRAND_CONDITION.getPropertyKey());
		conditionDto.setParameters(null);
		conditionDto.setExceptions(null);

		context.checking(new Expectations() { {
			oneOf(ruleElement).getKind(); will(returnValue(RuleCondition.CONDITION_KIND));
			oneOf(ruleElement).setParameters(null);
			oneOf(ruleElement).setExceptions(null);
		} });

		conditionAdapter.populateDomain(conditionDto, ruleElement);
	}

	/**
	 * Check the class of DTO object created by <code>ConditionAdapter</code>.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(ConditionDTO.class, conditionAdapter.createDtoObject().getClass());
	}
}
