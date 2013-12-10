package com.elasticpath.importexport.common.adapters.promotion;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleExceptionType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.impl.AbstractRuleExceptionImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Tests common methods of abstract class <code>AbstractElementAdapter</code>.
 */
public class AbstractElementAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final AbstractElementAdapter<ParameterDTO> elementAdapter = new AbstractElementAdapter<ParameterDTO>() {

		@Override
		public void populateDTO(final RuleElement source, final ParameterDTO target) {
			// stub
		}

		@Override
		public void populateDomain(final ParameterDTO source, final RuleElement target) {
			// stub
		}
	};

	/**
	 * Verifies that element parameter population involves <code>RuleParameter</code> key and value. 
	 */
	@Test
	public void testCreateElementParameterDTO() {
		final RuleParameter mockRuleParameter = context.mock(RuleParameter.class);
		final String ruleParameterKey = RuleParameter.DISCOUNT_PERCENT_KEY;
		final String ruleParameterValue = "10";

		context.checking(new Expectations() { {
			oneOf(mockRuleParameter).getKey(); will(returnValue(ruleParameterKey));
			oneOf(mockRuleParameter).getValue(); will(returnValue(ruleParameterValue));
		} });

		final ParameterDTO parameterDto = elementAdapter.createElementParameterDTO(mockRuleParameter);

		assertEquals(ruleParameterKey, parameterDto.getKey());
		assertEquals(ruleParameterValue, parameterDto.getValue());
	}

	/**
	 * Verifies population of a list with several <code>ParameterDTO</code> objects.
	 */
	@Test
	public void testCreateElementParameterDTOList() {
		final RuleParameter mockRuleParameter1 = context.mock(RuleParameter.class, "parameter1");
		final String ruleParameterKey1 = RuleParameter.DISCOUNT_AMOUNT_KEY;
		final String ruleParameterValue1 = "100";

		final RuleParameter mockRuleParameter2 = context.mock(RuleParameter.class, "parameter2");
		final String ruleParameterKey2 = RuleParameter.DISCOUNT_PERCENT_KEY;
		final String ruleParameterValue2 = "5";

		context.checking(new Expectations() { {
			oneOf(mockRuleParameter1).getKey(); will(returnValue(ruleParameterKey1));
			oneOf(mockRuleParameter1).getValue(); will(returnValue(ruleParameterValue1));
			oneOf(mockRuleParameter2).getKey(); will(returnValue(ruleParameterKey2));
			oneOf(mockRuleParameter2).getValue(); will(returnValue(ruleParameterValue2));
		} });

		final Set<RuleParameter> ruleParameters = new HashSet<RuleParameter>(Arrays.asList(mockRuleParameter1, mockRuleParameter2));

		final List<ParameterDTO> parameterDtoList = elementAdapter.createElementParameterDTOList(ruleParameters);

		assertEquals(2, parameterDtoList.size());
	}

	/**
	 * Tests population of <code>ExceptionDTO</code> object from <code>RuleException</code>.
	 */
	@Test
	public void testCreateElementExceptionDTO() {
		final RuleException ruleException = context.mock(RuleException.class);

		final RuleParameter mockRuleParameter1 = context.mock(RuleParameter.class, "parameter1");
		final String ruleParameterKey1 = RuleParameter.PRODUCT_CODE_KEY;
		final String ruleParameterValue1 = "BIG_JOYFUL_TELESCOPE";

		final Set<RuleParameter> ruleParameters = new HashSet<RuleParameter>(Arrays.asList(mockRuleParameter1));

		context.checking(new Expectations() { {
			oneOf(ruleException).getParameters(); will(returnValue(ruleParameters));
			oneOf(ruleException).getType(); will(returnValue(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey()));

			oneOf(mockRuleParameter1).getKey(); will(returnValue(ruleParameterKey1));
			oneOf(mockRuleParameter1).getValue(); will(returnValue(ruleParameterValue1));
		} });

		final ExceptionDTO exceptionDto = elementAdapter.createElementExceptionDTO(ruleException);

		assertEquals(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey(), exceptionDto.getExceptionType());
		assertEquals(1, exceptionDto.getExceptionParameters().size());
		assertEquals(ruleParameterKey1, exceptionDto.getExceptionParameters().get(0).getKey());
		assertEquals(ruleParameterValue1, exceptionDto.getExceptionParameters().get(0).getValue());
	}

	/**
	 * Tests population of a list with several <code>ExceptionDTO</code> objects.
	 */
	@Test
	public void testCreateElementExceptionDTOList() {
		final RuleException ruleException1 = context.mock(RuleException.class, "ruleException1");

		final RuleParameter mockRuleParameter1 = context.mock(RuleParameter.class, "parameter1");
		final String ruleParameterKey1 = RuleParameter.PRODUCT_CODE_KEY;
		final String ruleParameterValue1 = "BIG_JOYFUL_TELESCOPE";

		final Set<RuleParameter> ruleParameters1 = new HashSet<RuleParameter>(Arrays.asList(mockRuleParameter1));

		final RuleException ruleException2 = context.mock(RuleException.class, "ruleException2");

		final RuleParameter mockRuleParameter2 = context.mock(RuleParameter.class, "parameter2");
		final String ruleParameterKey2 = RuleParameter.CATEGORY_CODE_KEY;
		final String ruleParameterValue2 = "TRIPODS";

		final Set<RuleParameter> ruleParameters2 = new HashSet<RuleParameter>(Arrays.asList(mockRuleParameter2));

		final Set<RuleException> ruleExceptions = new HashSet<RuleException>(Arrays.asList(ruleException1, ruleException2));

		context.checking(new Expectations() { {
			oneOf(ruleException1).getParameters(); will(returnValue(ruleParameters1));
			oneOf(ruleException1).getType(); will(returnValue(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey()));

			oneOf(mockRuleParameter1).getKey(); will(returnValue(ruleParameterKey1));
			oneOf(mockRuleParameter1).getValue(); will(returnValue(ruleParameterValue1));

			oneOf(ruleException2).getParameters(); will(returnValue(ruleParameters2));
			oneOf(ruleException2).getType(); will(returnValue(RuleExceptionType.CATEGORY_EXCEPTION.getPropertyKey()));

			oneOf(mockRuleParameter2).getKey(); will(returnValue(ruleParameterKey2));
			oneOf(mockRuleParameter2).getValue(); will(returnValue(ruleParameterValue2));
		} });

		final List<ExceptionDTO> exceptionDtoList = elementAdapter.createElementExceptionDTOList(ruleExceptions);

		assertEquals(2, exceptionDtoList.size());
	}

	/**
	 * Tests successful instantiation of <code>RuleException</code> object.
	 */
	@Test
	public void testCreateRuleException() {
		final ElasticPath mockElasticPath = context.mock(ElasticPath.class);
		elementAdapter.setElasticPath(mockElasticPath);
		final RuleException ruleException = new AbstractRuleExceptionImpl();

		context.checking(new Expectations() { {
			oneOf(mockElasticPath).getBean(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey());
			will(returnValue(ruleException));
		} });

		assertEquals(ruleException,
				elementAdapter.createRuleException(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey()));
	}

	/**
	 * Tests that exception is thrown when bean factory cannot instantiate <code>RuleException</code> object.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testCreateNullRuleException() {
		final ElasticPath mockElasticPath = context.mock(ElasticPath.class);
		elementAdapter.setElasticPath(mockElasticPath);

		context.checking(new Expectations() { {
			oneOf(mockElasticPath).getBean(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey());
			will(returnValue(null));
		} });

		elementAdapter.createRuleException(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey());
	}

	/**
	 * Tests instantiation of <code>RuleParameter</code> set and its population from DTO objects.
	 */
	@Test
	public void testCreateRuleParameterSet() {
		final ParameterDTO parameterDto1 = new ParameterDTO();
		parameterDto1.setKey(RuleParameter.PRODUCT_CODE_KEY);
		parameterDto1.setValue("ELECTRIC_GUITAR");

		final ParameterDTO parameterDto2 = new ParameterDTO();
		parameterDto2.setKey(RuleParameter.BRAND_CODE_KEY);
		parameterDto2.setValue("YAMAHA");

		final ElasticPath mockElasticPath = context.mock(ElasticPath.class);
		elementAdapter.setElasticPath(mockElasticPath);

		context.checking(new Expectations() { {
			oneOf(mockElasticPath).getBean(ContextIdNames.RULE_PARAMETER);
			will(returnValue(new RuleParameterImpl()));
			oneOf(mockElasticPath).getBean(ContextIdNames.RULE_PARAMETER);
			will(returnValue(new RuleParameterImpl()));
		} });

		Set<RuleParameter> ruleParameters = elementAdapter.createRuleParameterSet(Arrays.asList(parameterDto1, parameterDto2));

		assertEquals(2, ruleParameters.size());
	}

	/**
	 * Tests instantiation of a list of <code>RuleException</code> objects based on DTO objects.
	 */
	@Test
	public void testCreateRuleExceptionSet() {
		final ElasticPath mockElasticPath = context.mock(ElasticPath.class);
		elementAdapter.setElasticPath(mockElasticPath);

		final ExceptionDTO exceptionDto1 = new ExceptionDTO();
		exceptionDto1.setExceptionType(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey());
		exceptionDto1.setExceptionParameters(Arrays.asList(new ParameterDTO()));

		final ExceptionDTO exceptionDto2 = new ExceptionDTO();
		exceptionDto2.setExceptionType(RuleExceptionType.CATEGORY_EXCEPTION.getPropertyKey());
		exceptionDto2.setExceptionParameters(Arrays.asList(new ParameterDTO()));

		final RuleException ruleException1 = new AbstractRuleExceptionImpl();
		final RuleException ruleException2 = new AbstractRuleExceptionImpl();

		context.checking(new Expectations() { {
			oneOf(mockElasticPath).getBean(RuleExceptionType.PRODUCT_EXCEPTION.getPropertyKey());
			will(returnValue(ruleException1));
			oneOf(mockElasticPath).getBean(RuleExceptionType.CATEGORY_EXCEPTION.getPropertyKey());
			will(returnValue(ruleException2));
			exactly(2).of(mockElasticPath).getBean(ContextIdNames.RULE_PARAMETER);
			will(returnValue(new RuleParameterImpl()));
		} });

		Set<RuleException> ruleExceptions = elementAdapter.createRuleExceptionSet(Arrays.asList(exceptionDto1, exceptionDto2));

		assertEquals(2, ruleExceptions.size());
	}
}
