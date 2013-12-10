/**
 * 
 */
package com.elasticpath.tags.domain.impl;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;


/**
 * Tests that demonstrate how to build a conditional expression using
 * domain objects.
 */
public class ConditionalExpressionModelTest  {

	private static final String EQUAL_TO = "equalTo";
	private static final String SHOPPING_START_TIME = "SHOPPING_START_TIME";
	private static final String SELLING_CHANNEL = "SELLING_CHANNEL";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Tests building the following expression with domain objects.
	 * AND 
	 * 	{ refererUrl.contains "google"} 
	 * 	{ AND 
	 * 		{OR 
	 * 			{age.lessThan 5} 
	 * 			{location.notEqualTo "fr"} 
	 * 		}
	 * 		{memberType.equalTo "bum"}
	 * 		{OR
	 * 			{AND
	 * 				{memberType.notEqualTo "poor"}
	 * 				{memberType.notEqualTo "platinum"}
	 * 			}
	 * 		}
	 * 	}
	 */
	@Test
	public void testExpression1() {
		//Create root level LogicalOperator
		LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);		
				
		final TagDefinition refererUrl = context.mock(TagDefinition.class, "refererUrl");
		
		// add location contains something (leaves)
		root.addCondition(new Condition(refererUrl,  "includes" , 
				"google")); // http://google.com/q?s=fasdfasdfasdfasdf		
		
		// add second level AND operation and add this operator to the root node
		LogicalOperator secondLevel = new LogicalOperator(LogicalOperatorType.AND);
		root.addLogicalOperator(secondLevel);
		
		// add third level OR operation and add this operator to the second level AND operator
		LogicalOperator ageOrLocation = new LogicalOperator(LogicalOperatorType.OR);
		secondLevel.addLogicalOperator(ageOrLocation);
		// we're doing setParent call just because in current implementation we've to explicitly set parent node of the tree
		ageOrLocation.setParentLogicalOperator(secondLevel); 
		
		final TagDefinition memberType = context.mock(TagDefinition.class, "memberType");
		
		// add membertype = bum condition to the second level AND operator too
		secondLevel.addCondition(new Condition(memberType, EQUAL_TO, "bum"));
		
		// add third level OR operation and add this operator to the second level AND, just like the OR one
		LogicalOperator notPoorAndPlatinum = new LogicalOperator(LogicalOperatorType.OR);
		secondLevel.addLogicalOperator(notPoorAndPlatinum);
		notPoorAndPlatinum.setParentLogicalOperator(secondLevel);
		
		final TagDefinition age = context.mock(TagDefinition.class, "age");
		final TagDefinition location = context.mock(TagDefinition.class, "location");
		
		// let's add some conditions to the third level OR operator
		ageOrLocation.addCondition(new Condition(age, "lessThan", "5"));
		ageOrLocation.addCondition(new Condition(location, "notEqualTo", "fr"));
		
		// let's NOT the AND operation on the fourth level
		// first start adding AND operator to the third level NOT
		LogicalOperator poorAndPlatinum = new LogicalOperator(LogicalOperatorType.AND);
		notPoorAndPlatinum.addLogicalOperator(poorAndPlatinum);
		poorAndPlatinum.setParentLogicalOperator(notPoorAndPlatinum);
		
		// add two conditions to fourth level AND
		poorAndPlatinum.addCondition(new Condition(memberType, "notEqualTo", "poor"));
		poorAndPlatinum.addCondition(new Condition(memberType, "notEqualTo" , "platinum"));
		
	}
	
	/**
	 * WHEN and WHERE test.
	 */
	@Test
	public void testSimpleWhenAndWhere() {
		
		final TagDefinition sellingChannel = context.mock(TagDefinition.class, SELLING_CHANNEL);
		final TagDefinition shoppingStartTime = context.mock(TagDefinition.class, SHOPPING_START_TIME);
		
		Condition store1 = new Condition(sellingChannel, 
				EQUAL_TO, "1"); //store code #1
		Condition store2 = new Condition(sellingChannel, 
				EQUAL_TO, "2"); //store code #2
		Condition store3 = new Condition(sellingChannel, 
				EQUAL_TO, "3"); //store code #3
		
		// date is always in timestamp format
		Condition dateFrom = new Condition(shoppingStartTime, 
				"greaterThan", "124567898765678");
		Condition dateTo   = new Condition(shoppingStartTime, 
				"lessThan", "124567898782342");
				
		LogicalOperator root = new LogicalOperator(LogicalOperatorType.AND);

		LogicalOperator stores = new LogicalOperator(LogicalOperatorType.OR);
		root.addLogicalOperator(stores);
		stores.setParentLogicalOperator(root);
		
		stores.addCondition(store1);
		stores.addCondition(store2);
		stores.addCondition(store3);
		
		root.addCondition(dateFrom);
		root.addCondition(dateTo);
	}
	
	
	
}
