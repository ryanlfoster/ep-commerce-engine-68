package com.elasticpath.validation.service.valang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;

/**
 * Test the condition type check function for valang validator.
 */
public class TagConditionTypeOfFunctionTest  {

	private static final String OPERATOR = "operator";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagConditionTypeOfFunction isValidConditionType;
	
	/**
	 * Test that if a null condition is supplied to the function is returns false.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void  testDoGetResultForNullCondition() throws Exception {
		
		isValidConditionType = new TagConditionTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return null;
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(isValidConditionType.doGetResult(null));
		
	}
	
	private Condition setUpConditionWithFullChainOfRelationsAndSetExpectations(final String valueJavaType, final Object value) {
		final TagDefinition tagDefinition = context.mock(TagDefinition.class, "conditionTag");
		final TagValueType tagValueType = context.mock(TagValueType.class, "conditionTagValueType");
		
		context.checking(new Expectations() { { 
			allowing(tagDefinition).getValueType(); will(returnValue(tagValueType));
			allowing(tagValueType).getJavaType(); will(returnValue(valueJavaType));
		} });
		
		return new Condition(tagDefinition, OPERATOR, value);
	}
	
	/**
	 * Test that if a valid condition with a valid value is provided the function returns true as the 
	 * result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void  testDoGetResultForSuccess() throws Exception {
		
		isValidConditionType = new TagConditionTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return setUpConditionWithFullChainOfRelationsAndSetExpectations("java.lang.Integer", Integer.valueOf(1));
				default:
					return null;
				}
			}
			
		};
		
		assertTrue(isValidConditionType.doGetResult(null));
		
	}
	
	private Condition setUpConditionWithTagDefinitionNullAndSetExpectations(final Object value) {
		return new Condition(null, OPERATOR, value);
	}
	
	/**
	 * Test that if a valid condition with a valid value is provided the function returns true as the 
	 * result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void  testDoGetResultWithNullTagDefinition() throws Exception {
		
		isValidConditionType = new TagConditionTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return setUpConditionWithTagDefinitionNullAndSetExpectations(Integer.valueOf(1));
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(isValidConditionType.doGetResult(null));
		
	}
	
	
	private Condition setUpConditionWithTagValueTypeNullAndSetExpectations(final Object value) {
		
		final TagDefinition tagDefinition = context.mock(TagDefinition.class, "conditionTag");
		
		context.checking(new Expectations() { { 
			allowing(tagDefinition).getValueType(); will(returnValue(null));
		} });
		
		return new Condition(tagDefinition, OPERATOR, value);
	}
	
	/**
	 * Test that if a valid condition with a valid value is provided the function returns true as the 
	 * result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void  testDoGetResultWithNullTagValueType() throws Exception {
		
		isValidConditionType = new TagConditionTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return setUpConditionWithTagValueTypeNullAndSetExpectations(Integer.valueOf(1));
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(isValidConditionType.doGetResult(null));
		
	}
	
	private Condition setUpConditionWithJavaTypeNullAndSetExpectations(final Object value) {
		
		final TagDefinition tagDefinition = context.mock(TagDefinition.class, "conditionTag");
		final TagValueType tagValueType = context.mock(TagValueType.class, "conditionTagValueType");
		
		context.checking(new Expectations() { { 
			allowing(tagDefinition).getValueType(); will(returnValue(tagValueType));
			allowing(tagValueType).getJavaType(); will(returnValue(null));
		} });
		
		return new Condition(tagDefinition, OPERATOR, value);
	}
	
	/**
	 * Test that if a valid condition with a valid value is provided the function returns true as the 
	 * result.
	 * @throws Exception if valang encounter an error
	 */
	@Test
	public void  testDoGetResultWithNullJavaType() throws Exception {
		
		isValidConditionType = new TagConditionTypeOfFunction() {
			
			Object getFunctionArgument(final int argumentIndex, final Object target) {
				switch (argumentIndex) {
				case 0:
					return setUpConditionWithJavaTypeNullAndSetExpectations(Integer.valueOf(1));
				default:
					return null;
				}
			}
			
		};
		
		assertFalse(isValidConditionType.doGetResult(null));
		
	}

	
}
