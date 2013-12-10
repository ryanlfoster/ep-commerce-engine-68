package com.elasticpath.domain.shipping.evaluator.impl;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shipping.evaluator.InvalidShoppingItemException;
import com.elasticpath.domain.shipping.evaluator.MultipleMatchingShipmentTypesFoundException;
import com.elasticpath.domain.shipping.evaluator.NoMatchingShipmentTypeFoundException;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Test {@link SingleShipmentTypeEvaluatorStrategy} functionality.
 */
public class SingleShipmentTypeEvaluatorStrategyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SingleShipmentTypeEvaluatorStrategy singleShipmentTypeEvaluatorStrategy;

	private Predicate electronicPredicate;

	private Predicate physicalPredicate;

	/**
	 * Initialize mocks.
	 */
	@Before
	public void initializeMocks() {
		electronicPredicate = context.mock(Predicate.class, "electronicPredicate");
		physicalPredicate = context.mock(Predicate.class, "physicalPredicate");

		final Map<ShipmentType, Predicate> shipmentTypeMap = new LinkedHashMap<ShipmentType, Predicate>();
		shipmentTypeMap.put(ShipmentType.ELECTRONIC, electronicPredicate);
		shipmentTypeMap.put(ShipmentType.PHYSICAL, physicalPredicate);

		singleShipmentTypeEvaluatorStrategy = new SingleShipmentTypeEvaluatorStrategy(shipmentTypeMap);
	}

	/**
	 * Test no matching shipment types.
	 */
	@Test(expected = NoMatchingShipmentTypeFoundException.class)
	public void testNoMatchingShipmentTypes() {
		final ShoppingItem item = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(item).isBundle();
				will(returnValue(false));

				oneOf(item).getGuid();
				will(returnValue("itemGuid"));

				oneOf(electronicPredicate).evaluate(item);
				will(returnValue(false));

				oneOf(physicalPredicate).evaluate(item);
				will(returnValue(false));
			}
		});
		singleShipmentTypeEvaluatorStrategy.evaluate(item);
	}

	/**
	 * Test multiple matching shipment types.
	 */
	@Test(expected = MultipleMatchingShipmentTypesFoundException.class)
	public void testMultipleMatchingShipmentTypes() {
		final ShoppingItem item = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(item).isBundle();
				will(returnValue(false));

				oneOf(item).getGuid();
				will(returnValue("itemGuid"));

				oneOf(electronicPredicate).evaluate(item);
				will(returnValue(true));

				oneOf(physicalPredicate).evaluate(item);
				will(returnValue(true));
			}
		});
		singleShipmentTypeEvaluatorStrategy.evaluate(item);
	}

	/**
	 * Test bundle evaluation.
	 */
	@Test(expected = InvalidShoppingItemException.class)
	public void testBundleEvaluation() {
		final ShoppingItem item = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(item).isBundle();
				will(returnValue(true));

				oneOf(item).getGuid();
				will(returnValue("itemGuid"));
			}
		});
		singleShipmentTypeEvaluatorStrategy.evaluate(item);
	}

	/**
	 * Test matching electronic shipment type.
	 */
	@Test
	public void testMatchingElectronicShipmentType() {
		final ShoppingItem item = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(item).isBundle();
				will(returnValue(false));

				oneOf(electronicPredicate).evaluate(item);
				will(returnValue(true));

				oneOf(physicalPredicate).evaluate(item);
				will(returnValue(false));
			}
		});
		ShipmentType actualShipmentType = singleShipmentTypeEvaluatorStrategy.evaluate(item);
		assertEquals("Incorrect ShipmentType.", ShipmentType.ELECTRONIC, actualShipmentType);
	}


	/**
	 * Test matching physical shipment type.
	 */
	@Test
	public void testMatchingPhysicalShipmentType() {
		final ShoppingItem item = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				oneOf(item).isBundle();
				will(returnValue(false));

				oneOf(electronicPredicate).evaluate(item);
				will(returnValue(false));

				oneOf(physicalPredicate).evaluate(item);
				will(returnValue(true));
			}
		});
		ShipmentType actualShipmentType = singleShipmentTypeEvaluatorStrategy.evaluate(item);
		assertEquals("Incorrect ShipmentType.", ShipmentType.PHYSICAL, actualShipmentType);
	}

}
