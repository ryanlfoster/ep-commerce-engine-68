package com.elasticpath.test.integration.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.impl.TagValueTypeImpl;
import com.elasticpath.tags.service.ConditionValidationFacade;
import com.elasticpath.tags.service.InvalidConditionTreeException;
import com.elasticpath.tags.service.TagDefinitionService;
import com.elasticpath.tags.service.impl.GroovyConditionDSLBuilderImpl;
import com.elasticpath.tags.service.impl.TagDefinitionServiceImpl;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.validation.domain.ValidationResult;

/**
 * {@link GroovyConditionDSLBuilderImpl} integration test.
 */
public class GroovyConditionDSLBuilderTest extends BasicSpringContextTest {
	
	private static final Logger LOG = Logger.getLogger(GroovyConditionDSLBuilderTest.class);
	
	private GroovyConditionDSLBuilderImpl groovyDSLBuilder;

	@Autowired
	private BeanFactory beanFactory;

	/**
	 * This class mocks actual TagDefinitionService. 
	 *
	 */
	class TagDefinitionServiceMock extends TagDefinitionServiceImpl {
		@Override
		public List<TagDefinition> getTagDefinitions() {
			final List<TagDefinition> tagDefinitions = new ArrayList<TagDefinition>();
			String[] tags = {"refererUrl", "refererUrl", "location", "age", "memberType"};
			TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
			tagValueType.setJavaType(String.class.getCanonicalName());
			for (String tagGuid : tags) {
			TagDefinition tagDefinition = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
			tagDefinition.setGuid(tagGuid);
			tagDefinition.setValueType(tagValueType);
			tagDefinitions.add(tagDefinition);			
			}
			return tagDefinitions;
		}
	}
	
	/**
	 * Mocked validation.
	 */
	class TagConditionValidationMock implements ConditionValidationFacade {
		@Override
		public ValidationResult validate(final Condition condition,
				final Object newValue) throws IllegalArgumentException {
			return ValidationResult.VALID;
		}

		@Override
		public ValidationResult validate(final Condition condition)
				throws IllegalArgumentException {
			return ValidationResult.VALID;
		}

		@Override
		public ValidationResult validateTree(
				final LogicalOperator logicalOperatorTreeRootNode)
				throws IllegalArgumentException {
			return ValidationResult.VALID;
		}
	}
	
	/**
	 * Initialize test case.
	 */
	@Before
	public void initialize() {
		groovyDSLBuilder = new GroovyConditionDSLBuilderImpl();
		TagDefinitionService tagDefinitionService = new TagDefinitionServiceMock();
		groovyDSLBuilder.setTagDefinitionService(tagDefinitionService);
		groovyDSLBuilder.setValidationFacade(new TagConditionValidationMock());
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding empty condition.
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingEmptyCondition() {		
		StringBuffer conditionalExpression = new StringBuffer("");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertNull(rootNode);
	}

	/**
	 * Test getting condition string by seeding null LogicalOperator Tree.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingNullLogicalOperatorTree() throws InvalidConditionTreeException {		
		String conditionString = groovyDSLBuilder.getConditionalDSLString(null);
		
		assertEquals(StringUtils.EMPTY, conditionString);
	}

	/**
	 * Test getting LogicalOperator Tree by seeding only one condition.
	 * 
	 * { AND { refererUrl.includes 'google' } }
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingOnlyOneCondition() {		
		StringBuffer conditionalExpression = new StringBuffer("{ ");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		
		// refererUrl contains google
		conditionalExpression.append("\n{ refererUrl.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'google' }\n}");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertNotNull(rootNode);
		assertEquals(1, rootNode.getConditions().size());
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding a simple AND operation.
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingSimpleANDOperation() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertNotNull(rootNode);
		assertEquals(2, rootNode.getConditions().size());
	}

	/**
	 * Add two conditions to the given expression.
	 * 
	 * @param conditionalExpression an expression that conditions to be added
	 */
	private void addSomeConditions(final StringBuffer conditionalExpression) {
		// refererUrl contains google
		conditionalExpression.append("\n{ refererUrl.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'google' }\n");
		
		// memberType equals to Gold
		conditionalExpression.append("{ memberType.");
		conditionalExpression.append("equalTo");
		conditionalExpression.append(" 'GOLD' }\n");
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding a simple OR operation.
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingSimpleOROperation() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.OR.toString());
		
		addSomeConditions(conditionalExpression);				
		conditionalExpression.append("}");

		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());

		assertNotNull(rootNode);
		assertEquals(2, rootNode.getConditions().size());
	}
		
	/**
	 * Test getting LogicalOperator Tree by seeding a simple AND operation nested by a NOT operation.
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingSimpleAND() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		
		addSomeConditions(conditionalExpression);
		
		conditionalExpression.append("}\n}");			
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertNotNull(rootNode);
		assertEquals(1, rootNode.getLogicalOperators().size());
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding a simple OR operation nested by a NOT operation.
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingSimpleOROperationNestedByAND() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.OR.toString());
		
		addSomeConditions(conditionalExpression);
		
		conditionalExpression.append("}\n}");				
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertNotNull(rootNode);
		assertEquals(1, rootNode.getLogicalOperators().size());
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding a nested operators.
	 * 
	 * AND
	 * 	{ refererUrl.includes 'google' }
	 * 	{ memberType.equalTo 'GOLD' }
	 * 	{
	 * 		OR
	 * 			{ refererUrl.includes 'google' }
	 * 			{ memberType.equalTo 'GOLD' }
	 * 			{
	 * 				AND
	 * 					{ refererUrl.includes 'google' }
	 * 					{ memberType.equalTo 'GOLD' }
	 * 			}
	 * 	}
	 * 	{
	 * 		AND {
	 * 			AND
	 * 				{ refererUrl.includes 'google' }
	 * 				{ memberType.equalTo 'GOLD' }
	 * 		}
	 * 	}
	 *
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingNestedOperators() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.OR.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}\n}\n");		

		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}\n}\n}");		

		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());

		assertNotNull(rootNode);

		// root level checks
		assertEquals(2, rootNode.getLogicalOperators().size());
		assertEquals(2, rootNode.getConditions().size());

		// level one checks
		List<LogicalOperator> levelOneOperators = new ArrayList<LogicalOperator>(rootNode.getLogicalOperators());

		assertEquals(2, levelOneOperators.get(0).getConditions().size());
		assertEquals(1, levelOneOperators.get(0).getLogicalOperators().size());
		assertEquals(1, levelOneOperators.get(1).getLogicalOperators().size());
		assertEquals(0, levelOneOperators.get(1).getConditions().size());

		// third level checks
		List<LogicalOperator> levelTwoOperators = new ArrayList<LogicalOperator>(levelOneOperators.get(0).getLogicalOperators());
		assertEquals(2, levelTwoOperators.get(0).getConditions().size());
		assertEquals(0, levelTwoOperators.get(0).getLogicalOperators().size());

		levelTwoOperators = new ArrayList<LogicalOperator>(levelOneOperators.get(1).getLogicalOperators());
		assertEquals(2, levelTwoOperators.get(0).getConditions().size());
		assertEquals(0, levelTwoOperators.get(0).getLogicalOperators().size());
	}
	
	/**
	 * Test getting LogicalOperator Tree by seeding a nested operators.
	 * 
	 * AND
	 * 	{ refererUrl.includes 'google' }
	 * 	{ memberType.equalTo 'GOLD' }
	 * 	{
	 * 		OR
	 * 			{ refererUrl.includes 'google' }
	 * 			{ memberType.equalTo 'GOLD' }
	 * 			{
	 * 				AND
	 * 					{ refererUrl.includes 'google' }
	 * 					{ memberType.equalTo 'GOLD' }
	 * 			}
	 * 	}
	 * 	{
	 * 		AND {
	 * 			AND
	 * 				{ refererUrl.includes 'google' }
	 * 				{ memberType.equalTo 'GOLD' }
	 * 		}
	 * 	}
	 *  {
	 *      AND
	 * 			{ refererUrl.includes 'google' }
	 * 			{ memberType.equalTo 'GOLD' }
	 * 			{
	 * 				OR
	 * 					{ refererUrl.includes 'google' }
	 * 					{ memberType.equalTo 'GOLD' }
	 * 			}
	 *  }
	 */
	
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingMoreNestedOperators() {		
		StringBuffer conditionalExpression = new StringBuffer("{");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.OR.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}\n}\n");		

		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}\n}\n{\n");		
		conditionalExpression.append(LogicalOperatorType.AND.toString());
		addSomeConditions(conditionalExpression);
		
		conditionalExpression.append("{\n");
		conditionalExpression.append(LogicalOperatorType.OR.toString());
		addSomeConditions(conditionalExpression);
		conditionalExpression.append("}\n}\n}");		

		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());

		assertNotNull(rootNode);

		// root level checks
		assertEquals(Integer.parseInt("3"), rootNode.getLogicalOperators().size());
		assertEquals(2, rootNode.getConditions().size());

		// level one checks
		List<LogicalOperator> levelOneOperators = new ArrayList<LogicalOperator>(rootNode.getLogicalOperators());

		assertEquals(2, levelOneOperators.get(0).getConditions().size());
		assertEquals(1, levelOneOperators.get(0).getLogicalOperators().size());
		assertEquals(1, levelOneOperators.get(1).getLogicalOperators().size());
		assertEquals(0, levelOneOperators.get(1).getConditions().size());
		assertEquals(1, levelOneOperators.get(2).getLogicalOperators().size());
		assertEquals(2, levelOneOperators.get(2).getConditions().size());

		// third level checks
		List<LogicalOperator> levelTwoOperators = new ArrayList<LogicalOperator>(levelOneOperators.get(0).getLogicalOperators());
		assertEquals(2, levelTwoOperators.get(0).getConditions().size());
		assertEquals(0, levelTwoOperators.get(0).getLogicalOperators().size());

		levelTwoOperators = new ArrayList<LogicalOperator>(levelOneOperators.get(1).getLogicalOperators());
		assertEquals(2, levelTwoOperators.get(0).getConditions().size());
		assertEquals(0, levelTwoOperators.get(0).getLogicalOperators().size());

		levelTwoOperators = new ArrayList<LogicalOperator>(levelOneOperators.get(2).getLogicalOperators());
		assertEquals(2, levelTwoOperators.get(0).getConditions().size());
		assertEquals(0, levelTwoOperators.get(0).getLogicalOperators().size());
	}
	
	/**
	 * Test getting ConditionalExpression string by seeding a LogicalOperator which has just one condition.
	 * 
	 * { AND { refererUrl.includes 'google' }  }
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingLogicalOperatorTreeThatHasOneCondition() throws InvalidConditionTreeException {		
		StringBuffer conditionalExpression = new StringBuffer(" { ");
		conditionalExpression.append(LogicalOperatorType.AND);
		conditionalExpression.append(" { refererUrl.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'google' }  } ");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertEquals(conditionalExpression.toString(), groovyDSLBuilder.getConditionalDSLString(rootNode));
	}
	
	/**
	 * Test getting ConditionalExpression string by seeding a LogicalOperator which has two conditions.
	 * 
	 * { AND { refererUrl.includes 'google' }  { memberType.equalTo 'gold' } }
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingLogicalOperatorTreeThatHasTwoConditions() throws InvalidConditionTreeException {		
		StringBuffer conditionalExpression = new StringBuffer(" { ");
		conditionalExpression.append(LogicalOperatorType.AND);
		conditionalExpression.append(" { refererUrl.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'google' }  { age.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'abc' }  } ");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		assertEquals(conditionalExpression.toString(), groovyDSLBuilder.getConditionalDSLString(rootNode));
	}
	
	/**
	 * Test getting ConditionalExpression string by seeding a LogicalOperator which has nested structure.
	 * 
	 * { AND { refererUrl.includes 'google' }  { memberType.equalTo 'gold' } 
	 *       { OR { AND { memberType.equalTo 'basic' }  { location.equalTo 'ca' } } }
	 * }
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testConditionalExpressionBySeedingLogicalOperatorTreeThatHasNestedStructure() throws InvalidConditionTreeException {		
		StringBuffer conditionalExpression = new StringBuffer(" { ");
		conditionalExpression.append(LogicalOperatorType.AND);
		conditionalExpression.append(" { refererUrl.");
		conditionalExpression.append("includes");
		conditionalExpression.append(" 'google' }  { memberType.");
		conditionalExpression.append("equalTo");
		conditionalExpression.append(" 'gold' }  { ");
		conditionalExpression.append(LogicalOperatorType.OR);
		conditionalExpression.append(" { ");
		conditionalExpression.append(LogicalOperatorType.AND);
		conditionalExpression.append(" { memberType.");
		conditionalExpression.append("equalTo");
		conditionalExpression.append(" 'gold' }  { location.");
		conditionalExpression.append("equalTo");
		conditionalExpression.append(" 'ca' }  }  }  } ");
		
		LogicalOperator rootNode = groovyDSLBuilder.getLogicalOperationTree(conditionalExpression.toString());
		
		assertEquals(conditionalExpression.toString(), groovyDSLBuilder.getConditionalDSLString(rootNode));
	}
	
	/**
	 * Test a simple condition which right operand of the condition is not wrapped with quotes. The condition string
	 * should be automatically quoted since the type of the object is String.
	 * 
	 * { AND { memberType.includes google }  }
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testAddingRightOperandsWithoutQuotingThem() throws InvalidConditionTreeException {
		LogicalOperator rootNode = new LogicalOperator(LogicalOperatorType.AND);
		
		final TagDefinition memberType = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		memberType.setGuid("memberType");
		memberType.setName("memberType");
		TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
		tagValueType.setJavaType(String.class.getCanonicalName());
		memberType.setValueType(tagValueType);
		
		rootNode.addCondition(new Condition(memberType, "includes", "google"));
		
		assertEquals(" { AND { memberType.includes 'google' }  } ", groovyDSLBuilder.getConditionalDSLString(rootNode));
	}
	
	/**
	 * Test a simple condition which right operand of the condition is an integer. The condition string
	 * should be created without quotes since the type of the object is integer.
	 * 
	 * { AND { age.equalTo 3 }  }
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test
	public void testAddingIntegerRightOperands() throws InvalidConditionTreeException {
		LogicalOperator rootNode = new LogicalOperator(LogicalOperatorType.AND);
		
		final TagDefinition age = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		age.setGuid("age");
		age.setName("age");
		TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
		tagValueType.setJavaType(Integer.class.getCanonicalName());
		age.setValueType(tagValueType);
		
		rootNode.addCondition(new Condition(age, "equalTo" , Integer.parseInt("3")));

		final String condition = groovyDSLBuilder.getConditionalDSLString(rootNode);
		LOG.debug("-[act]->>" + condition);
		assertEquals(" { AND { age.equalTo (3i) }  } ", condition);
	}
	
	/**
	 * Test a simple condition which right operand of the condition is a collection. An unsupported operation exception
	 * should be thrown.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test(expected = UnsupportedOperationException.class)
	public void testAddingCollectionRightOperands() throws InvalidConditionTreeException {
		LogicalOperator rootNode = new LogicalOperator(LogicalOperatorType.AND);
		
		final TagDefinition age = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		age.setGuid("age");
		age.setName("age");
		TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
		tagValueType.setJavaType(String.class.getCanonicalName());
		age.setValueType(tagValueType);

		rootNode.addCondition(new Condition(age, "includes", Collections.singletonList("a")));

		groovyDSLBuilder.getConditionalDSLString(rootNode);
	}

	/**
	 * Test a simple condition which right operand of the condition is a map. An unsupported operation exception
	 * should be thrown.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test(expected = UnsupportedOperationException.class)
	public void testAddingMapRightOperands() throws InvalidConditionTreeException {
		LogicalOperator rootNode = new LogicalOperator(LogicalOperatorType.AND);
		
		final TagDefinition age = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		age.setGuid("age");
		age.setName("age");
		TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
		tagValueType.setJavaType(String.class.getCanonicalName());
		age.setValueType(tagValueType);

		rootNode.addCondition(new Condition(age, "equalTo", Collections.singletonMap("a", "b")));

		groovyDSLBuilder.getConditionalDSLString(rootNode);
	}

	/**
	 * Test a simple condition which right operand of the condition is an array. An unsupported operation exception
	 * should be thrown.
	 * @throws InvalidConditionTreeException if the condition tree has an invalid format or data
	 */
	@DirtiesDatabase
	@Test(expected = UnsupportedOperationException.class)
	public void testAddingArrayRightOperands() throws InvalidConditionTreeException {
		LogicalOperator rootNode = new LogicalOperator(LogicalOperatorType.AND);
		
		final TagDefinition age = getBeanFactory().getBean(ContextIdNames.TAG_DEFINITION);
		age.setGuid("age");
		age.setName("age");
		TagValueTypeImpl tagValueType = getBeanFactory().getBean(ContextIdNames.TAG_VALUE_TYPE);
		tagValueType.setJavaType(String.class.getCanonicalName());
		age.setValueType(tagValueType);

		
		rootNode.addCondition(new Condition(age, "equalTo", new String[] { "a" }));
		
		groovyDSLBuilder.getConditionalDSLString(rootNode);
	}
}
