package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.tags.Tag;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.impl.ConditionalExpressionImpl;

/**
 * Test the groovy ConditionalExpressional evaluator evaluates logical operations written in groovy dsl.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
public class GroovyConditionEvaluatorImplTest {

	private static final String CS_LOCATION_CONTAINS_US = "{location.contains \"US\"} \n";
	private static final String MEMBER = "member";
	private static final int MINUS_TEN_INTEGER = -10;
	private static final int MINUS_NINE_INTEGER = -9;
	
	private static final float MINUS_TEN_FLOAT = -10.0f;
	private static final float MINUS_NINE_FLOAT = -9.0f;
	
	private static final long MINUS_TEN_LONG = -10L;
	private static final long MINUS_NINE_LONG = -9L;
	
	private static final BigDecimal MINUS_TEN_BIGDECIMAL = new BigDecimal("-10.0");
	private static final BigDecimal MINUS_NINE_BIGDECIMAL = new BigDecimal("-9.0");

	
	private static final int TEN = 10;
	private static final int THREE = 3;
	private static final int FIFTY = 50;
	private static final int THIRTY = 30;
	private static final int FIVE = 5;
	private static final String FOO = "foo";
	private static final String AGE = "age";
	private static final String NAME = "name";
	private static final String LOCATION = "location";
	private static final String SEARCH_TERMS = "SEARCH_TERMS";
	private static final String INSTORE_SEARCH_TERMS = "INSTORE_SEARCH_TERMS";
	
	private static final float FLOAT_ONE = 1.1f;
	private static final float FLOAT_TWO = 2.2f;
	private static final float FLOAT_THREE = 3.3f;

	private static final BigDecimal BIGDECIMAL_ONE = new BigDecimal("11.111");
	private static final BigDecimal BIGDECIMAL_TWO = new BigDecimal("22.222");
	private static final BigDecimal BIGDECIMAL_THREE = new BigDecimal("33.333");

	private OpenEvaluator evaluator;
	
	@Before
	public void setUp() {
		evaluator = new OpenEvaluator() {
			{
				initialize();
			}
		};
	}

	/**
	 * Test the expression builder merges groovy expression with dsl expression.
	 */
	@Test
	public void testExpressionBuilder() {
		GroovyConditionEvaluatorServiceImpl.GroovyExpressionBuilder builder = evaluator.new GroovyExpressionBuilder();
		String expression = builder.buildExpression(StringUtils.EMPTY);
		assertTrue(expression.startsWith(GroovyConditionEvaluatorServiceImpl.GroovyExpressionBuilder.DEF));
		assertTrue(expression.endsWith(GroovyConditionEvaluatorServiceImpl.GroovyExpressionBuilder.CLOSE));
	}

	/**
	 * Test an empty condition.
	 */
	@Test
	public void testEmptyCondition() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));
		String conditionString = StringUtils.EMPTY;
		condition.setConditionString(conditionString);
		final Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(TEN)
		);
		
		assertTrue(evaluator.evaluate(trueMap, condition));
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testLessThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("age.lessThan 9");
		Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(0)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		final Map <String, Tag> falseMap = Collections.singletonMap(
				AGE, new Tag(FIFTY)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	
	/**
	 * Test negative integer values support in tag framework. 
	 */
	@Test
	public void testNegativeInteger() {
		ConditionalExpression condition = generateTestWithConditionString("age.equalTo (-9)");
		final Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(AGE, new Tag(MINUS_TEN_INTEGER));
		assertFalse(evaluator.evaluate(trueMap, condition));
		
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_INTEGER));
		assertTrue(evaluator.evaluate(trueMap, condition)); 
		
		condition = generateTestWithConditionString("age.lessThan (-9)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_INTEGER));
		assertTrue(evaluator.evaluate(trueMap, condition));
	}
	
	
	/**
	 * Test negative float values support in tag framework. 
	 */
	@Test
	public void testNegativeFloat() {
		ConditionalExpression condition = generateTestWithConditionString("age.equalTo (-9f)");
		final Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(AGE, new Tag(MINUS_TEN_FLOAT));
		assertFalse(evaluator.evaluate(trueMap, condition));
		
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_FLOAT));
		assertTrue(evaluator.evaluate(trueMap, condition)); 
		
		condition = generateTestWithConditionString("age.lessThan (-9f)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_FLOAT));
		assertTrue(evaluator.evaluate(trueMap, condition));
	}
	
	/**
	 * Test negative float values support in tag framework. 
	 */
	@Test
	public void testNegativeLong() {
		ConditionalExpression condition = generateTestWithConditionString("age.equalTo (-9L)");
		final Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(AGE, new Tag(MINUS_TEN_LONG));
		assertFalse(evaluator.evaluate(trueMap, condition));
		
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_LONG));
		assertTrue(evaluator.evaluate(trueMap, condition)); 
		
		condition = generateTestWithConditionString("age.lessThan (-9L)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_LONG));
		assertTrue(evaluator.evaluate(trueMap, condition));
	}
	
	/**
	 * Test negative big decimal values support in tag framework. 
	 * WARNING !!! See BigDecimal equals method for more details about big decimal compare. 
	 */
	@Test
	public void testNegativeBigDecimal() {
		ConditionalExpression condition = generateTestWithConditionString("age.equalTo (-9.0G)");
		Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(AGE, new Tag(MINUS_TEN_BIGDECIMAL));
		assertFalse(evaluator.evaluate(trueMap, condition));
		
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_NINE_BIGDECIMAL));
		assertTrue(evaluator.evaluate(trueMap, condition)); 
		
		condition = generateTestWithConditionString("age.lessThan (-9.0G)");
		trueMap.clear();
		trueMap.put(AGE, new Tag(MINUS_TEN_BIGDECIMAL));
		assertTrue(evaluator.evaluate(trueMap, condition));
	}
	

	/**
	 * Test the operator lessThan.
	 * This test show string usage instead of digits for compare digits values.
	 */
	@Test
	public void testLessThanOperator2() {
		// Looks like tag framework still compare strings instead of digits
		ConditionalExpression condition = generateTestWithConditionString("age.lessThan (9)");
		Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(TEN)
		);
		assertFalse(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			AGE, new Tag(FIVE)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> trueMap3 = Collections.singletonMap(
			AGE, new Tag(THIRTY) 
		);

		assertFalse(evaluator.evaluate(trueMap3, condition)); //here must be assertFalse


	}
	
	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testGreaterThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("age.greaterThan 2");
		Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(THREE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			AGE, new Tag(1)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testLessThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("age.lessThanOrEqualTo 30");
		Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(THIRTY)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			AGE, new Tag(FIVE)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			AGE, new Tag(FIFTY)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("age.greaterThanOrEqualTo 30");
		Map <String, Tag> trueMap = Collections.singletonMap(
			AGE, new Tag(THIRTY)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			AGE, new Tag(FIFTY)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			AGE, new Tag(FIVE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.equalTo 'bar'");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag("bar")
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FOO)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator notEqualTo.
	 */
	@Test
	public void testNotEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.notEqualTo \"bar\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FOO)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag("bar")
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/**
	 * Test includes operation on list values.
	 * Intersection between lists will be used 
	 * in VisitedCategoryTagger.
	 */
	@Test
	public void testIntersection() {

		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setConditionString("{ AND { AND { SEARCH_TERMS.includes 'one' }  }  { AND { SEARCH_TERMS.includes 'two' }  }  }");
		condition.setGuid(String.valueOf((System.currentTimeMillis()) + "_1"));
		
		Map <String, Tag> searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("one,two") 
		);
		
		assertTrue(evaluator.evaluate(searchTermMap, condition));
		
		searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("two,one") 
		);
		
		assertTrue(evaluator.evaluate(searchTermMap, condition));

		searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("two, zzz, one") 
		);
		
		assertTrue(evaluator.evaluate(searchTermMap, condition));
		
		searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("'two', 'zzz', 'one'") 
		);
		
		assertTrue(evaluator.evaluate(searchTermMap, condition));
		
		condition = new ConditionalExpressionImpl();
		condition.setConditionString("{ AND { SEARCH_TERMS.includes 'category code 1' }  { SEARCH_TERMS.includes 'category code 2' }  }");
		condition.setGuid(String.valueOf((System.currentTimeMillis()) + "_2"));
		
		searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("[category code 1, category code 2]") 
		);
		
		condition = new ConditionalExpressionImpl();
		
		condition.setConditionString("{ AND { SEARCH_TERMS.includes 'category' }  { SEARCH_TERMS.includes 'code' }  }");
		condition.setGuid(String.valueOf((System.currentTimeMillis()) + "_3"));
		
		searchTermMap = Collections.singletonMap(
			SEARCH_TERMS, new Tag("category,code") 
		);
		
		
		assertTrue(evaluator.evaluate(searchTermMap, condition));
		
	}
	
	/**
	 * Test the operator includes.
	 */
	@Test
	public void testIncludesOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.includes \"bar\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag("foobar")
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FOO)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/**
	 * Test the operator includesIgnoreCase.
	 */
	@Test
	public void testIncludesIgnoreCase() {
		ConditionalExpression condition = generateTestWithConditionString("foo.includesIgnoreCase \"Foo\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag("OnefOOTwo")
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag("zoo")
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/**
	 * Test the operator equalsIgnoreCase.
	 */
	@Test
	public void testEqualsIgnoreCase() {
		ConditionalExpression condition = generateTestWithConditionString("foo.equalsIgnoreCase 'bAr'");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag("bar")
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FOO)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	
	/**
	 * Test evaluation of nested logical conditions produced by UI.
	 */
	@Test
	public void testConditionAfterUI() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		// This condition produced by UI. Test will be failed.
		/*condition.setConditionString(
				"{ AND " 
				+
				"{ AND { INSTORE_SEARCH_TERMS.includesIgnoreCase 'Julia' }  }  " 
				+
				"{ OR  { INSTORE_SEARCH_TERMS.equalsIgnoreCase 'zzz' }  }  " 
				+
				"}");*/
		condition.setConditionString(
				"{ OR " 
				+
				"{ INSTORE_SEARCH_TERMS.includesIgnoreCase 'Julia' }   " 
				+
				"{ INSTORE_SEARCH_TERMS.equalsIgnoreCase 'zzz' }   " 
				+
				"}");
		
		condition.setGuid(String.valueOf((System.currentTimeMillis()) + "_1"));
		
		Map <String, Tag> trueMap = Collections.singletonMap(
			INSTORE_SEARCH_TERMS, new Tag("Julia")
		);
		
		assertTrue(evaluator.evaluate(trueMap, condition));
		
	}

	
	//  
	

	/**
	 * Test the operator notIncludes.
	 */
	@Test
	public void testNotIncludesOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.notIncludes \"bar\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FOO)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag("foobar")
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}



	/**
	 * Empty conditions should evaluate to true.
	 */
	@Test
	public void testExpressionEvaluationTrueIfEmpty() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));
		condition.setConditionString(StringUtils.EMPTY);
		assertTrue(evaluator.evaluate(new HashMap <String, Tag>(), condition));
	}


	/**
	 * Test evaluation of a singular contains condition in an AND clause
	 * (all non nested conditions are assumed to be AND-ed in the end)
	 * location.contains "US".
	 */
	@Test
	public void testExpressionEvaluationSingularContains() {
		ConditionalExpression condition = generateTestWithConditionString("location.contains \"US\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			LOCATION, new Tag("US")
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			LOCATION, new Tag("CANADA")
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}


	/**
	 * Test evaluation of a singular IS condition in an AND clause
	 * (all non nested conditions are assumed to be AND-ed in the end)
	 * name.is "smith".
	 */
	@Test
	public void testExpressionNonExistantOperatorIs() {
		ConditionalExpression condition = generateTestWithConditionString("name.is \"smith\"");
		Map <String, Tag> trueMap = Collections.singletonMap(
			NAME, new Tag("smith")
		);

		assertFalse(evaluator.evaluate(trueMap, condition));
	}

	/**
	 * Test evaluation of multiple conditions in an AND clause.
	 * {AND
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {title.is "president"}
	 * }
	 */
	@Test
	public void testExpressionEvaluationAND() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));

		String conditionString = "\t{AND \n"
			+ CS_LOCATION_CONTAINS_US
			+ "{name.equalTo \"obama\"} \n"
			+ "{title.equalTo \"president\"}}\n";
		condition.setConditionString(conditionString);


		Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(LOCATION, new Tag("US"));
		trueMap.put(NAME, new Tag("obama"));
		trueMap.put("title", new Tag("president"));

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			LOCATION, new Tag("CANADA")
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}


	/**
	 * Test evaluation of multiple conditions in an OR clause.
	 * {OR
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {title.is "president"}
	 * }
	 */
	@Test
	public void testExpressionEvaluationOR() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));

		String conditionString = "\t{OR \n"
			+ CS_LOCATION_CONTAINS_US
			+ "{name.equalTo \"jones\"} \n"
			+ "{title.equalTo \"president\"}}\n";
		condition.setConditionString(conditionString);

		Map <String, Tag> trueMap = Collections.singletonMap(
			"title", new Tag("president")
		);
		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			LOCATION, new Tag("US")
		);
		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> trueMap3 = Collections.singletonMap(
			NAME, new Tag("jones")
		);
		assertTrue(evaluator.evaluate(trueMap3, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			NAME, new Tag("foobar")
		);
		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test evaluation of nested logical conditions.
	 *
	 * User is in the US AND is named obama, AND is either gold or platinum member,
	 * AND is out of the age 16-60 range
	 * {AND
	 * 	{location.contains "US"}
	 *  {name.is "obama"}
	 *  {OR
	 *  	{member.is "gold"}
	 *  	{member.is "platinum"}
	 *  }
	 *  {OR
	 *  	{AND
	 *  		{ age.lessThan 16 }
	 *  		{ age.greaterThan 60 }
	 *  	}
	 *  }
	 * }
	 */
	@Test
	public void testExpressionEvaluationNested() {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));
		final String name = "obama";
		final String age = AGE;

		String conditionString =
			"{AND \n"
			+ CS_LOCATION_CONTAINS_US
			+ "{name.equalTo \"obama\"} \n"
			+ "{OR \n"
			+ "  {member.contains \"gold\"} \n"
			+ "  {member.contains \"platinum\"}} \n"
			+ "{AND \n"
			+ "  {AND \n"
			+ "    { age.lessThan 60 } \n"
			+ "    { age.greaterThan 16 } }\n"
			+ "} }\n";
		condition.setConditionString(conditionString);

		Map<String, Tag> trueMap = new HashMap<String, Tag>();
		trueMap.put(LOCATION, new Tag("US"));
				trueMap.put(NAME, new Tag(name));
						trueMap.put(MEMBER, new Tag("gold"));
								trueMap.put(age, new Tag(FIFTY));

		assertTrue(evaluator.evaluate(trueMap, condition));

		//No member
		Map <String, Tag> falseMap = new HashMap<String, Tag>();
		falseMap.put(LOCATION, new Tag("US"));
		falseMap.put(NAME, new Tag(name));
		falseMap.put(age, new Tag(FIVE));

		assertFalse(evaluator.evaluate(falseMap, condition));

		//Age in 16-60 range
		Map <String, Tag> falseMap2 = new HashMap<String, Tag>();
		falseMap2.put(LOCATION, new Tag("US"));
		falseMap2.put(NAME, new Tag(name));
		falseMap2.put(MEMBER, new Tag("gold"));
		falseMap2.put(age, new Tag(FIFTY));

		assertTrue(evaluator.evaluate(falseMap2, condition));
	}

	private ConditionalExpression generateTestWithConditionString(final String stringCondition) {
		ConditionalExpression condition = new ConditionalExpressionImpl();
		condition.setGuid(String.valueOf((System.currentTimeMillis())));
		String conditionString = "\t{AND {\n\t" + stringCondition + "\n\t} }\n";
		condition.setConditionString(conditionString);
		return condition;
	}
	
	/**
	 * Allow maps to be evaluated instead of tag clouds for testing.
	 */
	class OpenEvaluator extends GroovyConditionEvaluatorServiceImpl {
		/**
		 * Ease of use for testing.
		 * @param map string to string map
		 * @param condition object with string rule
		 * @return true if condition evaluates to true
		 */
		public boolean evaluate(final Map <String, Tag> map, final ConditionalExpression condition) {
			return evaluateConditionOnMap(map, condition);
		};
	}
	
	/******************** Float tests ******************************/
	
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testFloatEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.equalTo 2.2f");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testFloatGreaterThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThan 2.2f");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testFloatLessThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThan 2.2f");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testFloatGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThanOrEqualTo 2.2f");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testFloatLessThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThanOrEqualTo 2.2f");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_ONE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			FOO, new Tag(FLOAT_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(FLOAT_THREE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/****************** BigDecimal tests *********************/
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testBigDecimalEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.equalTo 22.222G");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testBigDecimalGreaterThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThan 22.222G");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testBigDecimalLessThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThan 22.222G");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testBigDecimalGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThanOrEqualTo 22.222G");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testBigDecimalLessThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThanOrEqualTo 22.222G");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_ONE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> trueMap2 = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_TWO)
		);

		assertTrue(evaluator.evaluate(trueMap2, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(BIGDECIMAL_THREE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/****************** Boolean tests *********************/
	/**
	 * Test the operator equalTo.
	 */
	@Test
	public void testBooleanEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.equalTo true");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(Boolean.TRUE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(Boolean.FALSE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/**
	 * Test the operator greaterThan.
	 */
	@Test
	public void testBooleanGreaterThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThan true");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(Boolean.TRUE)
		);

		assertFalse(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(Boolean.FALSE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}
	
	/**
	 * Test the operator lessThan.
	 */
	@Test
	public void testBooleanLessThanOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThan true");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(Boolean.TRUE)
		);

		assertFalse(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(Boolean.FALSE)
		);

		assertTrue(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator greaterThanOrEqualTo.
	 */
	@Test
	public void testBooleanGreaterThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.greaterThanOrEqualTo true");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(Boolean.TRUE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(Boolean.FALSE)
		);

		assertFalse(evaluator.evaluate(falseMap, condition));
	}

	/**
	 * Test the operator lessThanOrEqualTo.
	 */
	@Test
	public void testBooleanLessThanOrEqualToOperator() {
		ConditionalExpression condition = generateTestWithConditionString("foo.lessThanOrEqualTo true");
		Map <String, Tag> trueMap = Collections.singletonMap(
			FOO, new Tag(Boolean.TRUE)
		);

		assertTrue(evaluator.evaluate(trueMap, condition));

		Map <String, Tag> falseMap = Collections.singletonMap(
			FOO, new Tag(Boolean.FALSE)
		);

		assertTrue(evaluator.evaluate(falseMap, condition));
	}
}
