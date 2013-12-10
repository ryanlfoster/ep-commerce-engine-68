package com.elasticpath.importexport.common.adapters.promotion;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.importexport.common.dto.promotion.ExceptionDTO;
import com.elasticpath.importexport.common.dto.promotion.ParameterDTO;
import com.elasticpath.importexport.common.dto.promotion.cart.ActionDTO;

/**
 * Tests <code>ActionAdapter</code> class.
 */
public class ActionAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ActionAdapter actionAdapter = new ActionAdapter() {
		/**
		 * Stub method reducing code covered by other Tests.
		 */
		List<ParameterDTO> createElementParameterDTOList(final Set<RuleParameter> ruleParameterSet) {
			return null;
		}

		/**
		 * Stub method reducing code covered by other Tests.
		 */
		Set<RuleParameter> createRuleParameterSet(final List<ParameterDTO> elementParameterDTOList) {
			return null;
		}

		/**
		 * Stub method reducing code covered by other Tests.
		 */
		List<ExceptionDTO> createElementExceptionDTOList(final Set<RuleException> ruleExceptionSet) {
			return null;
		}

		/**
		 * Stub method reducing code covered by other Tests.
		 */
		Set<RuleException> createRuleExceptionSet(final List<ExceptionDTO> elementExceptionDTOList) {
			return null;
		}
	};

	/**
	 * Verifies type of DTO supplied by <code>ActionAdapter</code>.
	 */
	@Test
	public void testCreateDtoObject() {
		assertEquals(ActionDTO.class, actionAdapter.createDtoObject().getClass());
	}

	/**
	 * Tests DTO population method. 
	 */
	@Test
	public void testPopulateDto() {
		final RuleElement mockRuleElement = context.mock(RuleElement.class);

		context.checking(new Expectations() { {
			oneOf(mockRuleElement).getType(); will(returnValue(RuleElementType.CART_N_FREE_SKUS_ACTION.getPropertyKey()));
			oneOf(mockRuleElement).getParameters();
			oneOf(mockRuleElement).getExceptions();
		} });

		ActionDTO target = new ActionDTO();
		actionAdapter.populateDTO(mockRuleElement, target);

		assertEquals(RuleElementType.CART_N_FREE_SKUS_ACTION.getPropertyKey(), target.getType());
	}

	/**
	 * Tests domain population method.
	 */
	@Test
	public void testPopulateDomain() {
		final RuleElement mockRuleElement = context.mock(RuleElement.class);
		ActionDTO source = new ActionDTO();
		source.setType(RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey());
		source.setParameters(null);
		source.setExceptions(null);

		context.checking(new Expectations() { {
			oneOf(mockRuleElement).setType(with(same(RuleElementType.CART_ANY_SKU_AMOUNT_DISCOUNT_ACTION.getPropertyKey())));
			oneOf(mockRuleElement).setParameters(null);
			oneOf(mockRuleElement).setExceptions(null);
		} });

		actionAdapter.populateDomain(source, mockRuleElement);
	}
}
